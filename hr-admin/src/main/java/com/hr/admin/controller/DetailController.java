package com.hr.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hr.admin.dto.*;
import com.hr.admin.entity.*;
import com.hr.admin.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/detail")
@RequiredArgsConstructor
public class DetailController {

    private final HrProjectService hrProjectService;
    private final HrPersonnelService hrPersonnelService;
    private final HrSupplierService hrSupplierService;
    private final HrWorkHoursService hrWorkHoursService;
    private final HrSettlementService hrSettlementService;
    private final HrPersonnelAssetService hrPersonnelAssetService;
    private final HrPersonnelSalaryService hrPersonnelSalaryService;
    private final HrProjectMilestoneService hrProjectMilestoneService;

    @GetMapping("/project/{id}")
    public Result<ProjectDetailDTO> getProjectDetail(@PathVariable Long id) {
        HrProject project = hrProjectService.getById(id);
        if (project == null) {
            return Result.error("项目不存在");
        }

        ProjectDetailDTO dto = new ProjectDetailDTO();
        dto.setProject(project);

        List<HrPersonnel> personnelList = hrPersonnelService.lambdaQuery()
                .eq(HrPersonnel::getProjectId, id)
                .list();

        Map<Long, HrSupplier> supplierMap = hrSupplierService.list().stream()
                .collect(Collectors.toMap(HrSupplier::getId, s -> s));

        Map<Long, List<HrPersonnel>> groupedBySupplier = personnelList.stream()
                .filter(p -> p.getSupplierId() != null)
                .collect(Collectors.groupingBy(HrPersonnel::getSupplierId));

        List<ProjectDetailDTO.SupplierPersonnel> supplierPersonnelList = new ArrayList<>();
        for (Map.Entry<Long, List<HrPersonnel>> entry : groupedBySupplier.entrySet()) {
            ProjectDetailDTO.SupplierPersonnel sp = new ProjectDetailDTO.SupplierPersonnel();
            sp.setSupplierId(entry.getKey());
            HrSupplier supplier = supplierMap.get(entry.getKey());
            sp.setSupplierName(supplier != null ? supplier.getSupplierName() : "未知供应商");
            List<HrPersonnel> list = entry.getValue();
            sp.setTotalCount(list.size());
            sp.setActiveCount((int) list.stream().filter(p -> "在岗".equals(p.getStatus())).count());
            sp.setPersonnel(list.stream().map(p -> {
                ProjectDetailDTO.PersonnelInfo pi = new ProjectDetailDTO.PersonnelInfo();
                pi.setId(p.getId());
                pi.setName(p.getName());
                pi.setPositionName(p.getPositionName());
                pi.setEntryDate(p.getEntryDate());
                pi.setStatus(p.getStatus());
                return pi;
            }).collect(Collectors.toList()));
            supplierPersonnelList.add(sp);
        }
        dto.setSupplierPersonnel(supplierPersonnelList);

        List<HrProjectMilestone> milestones = hrProjectMilestoneService.lambdaQuery()
                .eq(HrProjectMilestone::getProjectId, id)
                .orderByAsc(HrProjectMilestone::getPlanDate)
                .list();
        dto.setMilestones(milestones);

        ProjectDetailDTO.BudgetInfo budgetInfo = new ProjectDetailDTO.BudgetInfo();
        budgetInfo.setTotalBudget(project.getBudget() != null ? project.getBudget() : BigDecimal.ZERO);

        BigDecimal settledAmount = hrSettlementService.lambdaQuery()
                .eq(HrSettlement::getProjectId, id)
                .eq(HrSettlement::getStatus, 1)
                .list()
                .stream()
                .map(HrSettlement::getFinalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        budgetInfo.setUsedBudget(settledAmount);

        BigDecimal pendingAmount = hrSettlementService.lambdaQuery()
                .eq(HrSettlement::getProjectId, id)
                .eq(HrSettlement::getStatus, 0)
                .list()
                .stream()
                .map(HrSettlement::getFinalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        budgetInfo.setPendingBudget(pendingAmount);

        if (project.getBudget() != null && project.getBudget().compareTo(BigDecimal.ZERO) > 0) {
            int percent = settledAmount.multiply(new BigDecimal(100))
                    .divide(project.getBudget(), 0, RoundingMode.HALF_UP).intValue();
            budgetInfo.setUsedPercent(Math.min(percent, 100));
        } else {
            budgetInfo.setUsedPercent(0);
        }
        dto.setBudgetInfo(budgetInfo);

        List<ProjectDetailDTO.RiskWarning> risks = new ArrayList<>();
        
        LocalDate today = LocalDate.now();
        LocalDate warningDate = today.plusDays(30);
        
        for (HrPersonnel p : personnelList) {
            if (p.getContractEndDate() != null && p.getContractEndDate().isBefore(warningDate)) {
                ProjectDetailDTO.RiskWarning risk = new ProjectDetailDTO.RiskWarning();
                risk.setType("合同到期");
                risk.setLevel("warning");
                risk.setMessage(p.getName() + "合同将于" + p.getContractEndDate() + "到期");
                risks.add(risk);
            }
        }

        if (budgetInfo.getUsedPercent() > 80) {
            ProjectDetailDTO.RiskWarning risk = new ProjectDetailDTO.RiskWarning();
            risk.setType("预算超支");
            risk.setLevel(budgetInfo.getUsedPercent() > 100 ? "danger" : "warning");
            risk.setMessage("预算已使用" + budgetInfo.getUsedPercent() + "%");
            risks.add(risk);
        }
        dto.setRisks(risks);

        return Result.success(dto);
    }

    @GetMapping("/personnel/{id}")
    public Result<PersonnelDetailDTO> getPersonnelDetail(@PathVariable Long id) {
        HrPersonnel personnel = hrPersonnelService.getById(id);
        if (personnel == null) {
            return Result.error("人员不存在");
        }

        PersonnelDetailDTO dto = new PersonnelDetailDTO();
        dto.setPersonnel(personnel);

        if (personnel.getSupplierId() != null) {
            HrSupplier supplier = hrSupplierService.getById(personnel.getSupplierId());
            dto.setSupplierName(supplier != null ? supplier.getSupplierName() : "");
        }

        if (personnel.getProjectId() != null) {
            HrProject project = hrProjectService.getById(personnel.getProjectId());
            dto.setProjectName(project != null ? project.getProjectName() : "");
        }

        List<HrPersonnelAsset> assets = hrPersonnelAssetService.lambdaQuery()
                .eq(HrPersonnelAsset::getPersonnelId, id)
                .orderByDesc(HrPersonnelAsset::getReceiveDate)
                .list();
        dto.setAssets(assets);

        List<HrPersonnelSalary> salaries = hrPersonnelSalaryService.lambdaQuery()
                .eq(HrPersonnelSalary::getPersonnelId, id)
                .orderByDesc(HrPersonnelSalary::getEffectiveDate)
                .list();
        List<PersonnelDetailDTO.SalaryRecord> salaryRecords = salaries.stream().map(s -> {
            PersonnelDetailDTO.SalaryRecord sr = new PersonnelDetailDTO.SalaryRecord();
            sr.setAmount(s.getAmount());
            sr.setSalaryType("DAILY".equals(s.getSalaryType()) ? "日薪" : "月薪");
            sr.setEffectiveDate(s.getEffectiveDate());
            sr.setReason(s.getReason());
            return sr;
        }).collect(Collectors.toList());
        dto.setSalaryHistory(salaryRecords);

        PersonnelDetailDTO.WorkStatistics stats = new PersonnelDetailDTO.WorkStatistics();
        
        List<HrWorkHours> workHours = hrWorkHoursService.lambdaQuery()
                .eq(HrWorkHours::getPersonnelId, id)
                .list();
        BigDecimal totalHours = workHours.stream()
                .map(HrWorkHours::getHours)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalWorkHours(totalHours);

        stats.setTotalProjects(1);
        stats.setAvgScore(BigDecimal.valueOf(4.5));
        dto.setStatistics(stats);

        return Result.success(dto);
    }

    @PostMapping("/project/{projectId}/milestone")
    public Result<Void> addMilestone(@PathVariable Long projectId, @RequestBody HrProjectMilestone milestone) {
        milestone.setProjectId(projectId);
        hrProjectMilestoneService.save(milestone);
        return Result.success();
    }

    @PutMapping("/milestone/{id}")
    public Result<Void> updateMilestone(@PathVariable Long id, @RequestBody HrProjectMilestone milestone) {
        milestone.setId(id);
        hrProjectMilestoneService.updateById(milestone);
        return Result.success();
    }

    @DeleteMapping("/milestone/{id}")
    public Result<Void> deleteMilestone(@PathVariable Long id) {
        hrProjectMilestoneService.removeById(id);
        return Result.success();
    }

    @PostMapping("/personnel/{personnelId}/asset")
    public Result<Void> addAsset(@PathVariable Long personnelId, @RequestBody HrPersonnelAsset asset) {
        asset.setPersonnelId(personnelId);
        hrPersonnelAssetService.save(asset);
        return Result.success();
    }

    @PutMapping("/asset/{id}")
    public Result<Void> updateAsset(@PathVariable Long id, @RequestBody HrPersonnelAsset asset) {
        asset.setId(id);
        hrPersonnelAssetService.updateById(asset);
        return Result.success();
    }

    @DeleteMapping("/asset/{id}")
    public Result<Void> deleteAsset(@PathVariable Long id) {
        hrPersonnelAssetService.removeById(id);
        return Result.success();
    }

    @PostMapping("/personnel/{personnelId}/salary")
    public Result<Void> addSalary(@PathVariable Long personnelId, @RequestBody HrPersonnelSalary salary) {
        salary.setPersonnelId(personnelId);
        hrPersonnelSalaryService.save(salary);
        return Result.success();
    }
}
