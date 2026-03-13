package com.hr.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hr.admin.dto.Result;
import com.hr.admin.dto.SupplierDetailVO;
import com.hr.admin.entity.HrContract;
import com.hr.admin.entity.HrPersonnel;
import com.hr.admin.entity.HrProject;
import com.hr.admin.entity.HrSettlement;
import com.hr.admin.entity.HrSupplier;
import com.hr.admin.entity.HrSupplierScore;
import com.hr.admin.mapper.HrSupplierScoreMapper;
import com.hr.admin.service.HrContractService;
import com.hr.admin.service.HrPersonnelService;
import com.hr.admin.service.HrProjectService;
import com.hr.admin.service.HrSettlementService;
import com.hr.admin.service.HrSupplierService;
import com.hr.admin.service.HrSupplierScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierDetailController {
    
    private final HrSupplierService hrSupplierService;
    private final HrSupplierScoreMapper hrSupplierScoreMapper;
    private final HrContractService hrContractService;
    private final HrPersonnelService hrPersonnelService;
    private final HrProjectService hrProjectService;
    private final HrSettlementService hrSettlementService;
    private final HrSupplierScoreService hrSupplierScoreService;
    
    @GetMapping("/{id}/detail")
    public Result<SupplierDetailVO> getDetail(@PathVariable Long id) {
        HrSupplier supplier = hrSupplierService.getById(id);
        if (supplier == null) {
            return Result.error("供应商不存在");
        }
        
        SupplierDetailVO vo = new SupplierDetailVO();
        vo.setId(supplier.getId());
        vo.setSupplierName(supplier.getSupplierName());
        vo.setContactPerson(supplier.getContactPerson());
        vo.setContactPhone(supplier.getContactPhone());
        vo.setContactEmail(supplier.getContactEmail());
        vo.setLevel(supplier.getLevel());
        vo.setDeliveryRate(supplier.getDeliveryRate());
        vo.setCurrentPersonCount(supplier.getCurrentPersonCount());
        vo.setStatus(supplier.getStatus());
        
        Map<String, BigDecimal> avgScores = hrSupplierScoreMapper.getAverageScores(id);
        vo.setAvgScores(avgScores != null ? avgScores : new HashMap<>());
        
        List<Map<String, Object>> monthlyScores = hrSupplierScoreMapper.getMonthlyScores(id);
        vo.setMonthlyScores(monthlyScores != null ? monthlyScores : new ArrayList<>());
        
        List<HrSupplierScore> scoreHistory = hrSupplierScoreService.lambdaQuery()
                .eq(HrSupplierScore::getSupplierId, id)
                .orderByDesc(HrSupplierScore::getScoreDate)
                .last("LIMIT 10")
                .list();
        vo.setScoreHistory(scoreHistory.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("scoreDate", s.getScoreDate());
            map.put("overallScore", s.getOverallScore());
            return map;
        }).collect(Collectors.toList()));
        
        List<HrContract> contracts = hrContractService.list(
                new LambdaQueryWrapper<HrContract>()
                        .eq(HrContract::getSupplierId, id)
                        .orderByDesc(HrContract::getCreateTime));
        
        BigDecimal totalAmount = contracts.stream()
                .map(HrContract::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalContractAmount(totalAmount);
        
        List<Map<String, Object>> contractList = contracts.stream().limit(5).map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("contractName", c.getContractName());
            map.put("contractCode", c.getContractCode());
            map.put("amount", c.getAmount());
            map.put("startDate", c.getStartDate());
            map.put("endDate", c.getEndDate());
            map.put("status", c.getStatus());
            return map;
        }).collect(Collectors.toList());
        vo.setContracts(contractList);
        
        List<HrPersonnel> personnelList = hrPersonnelService.list(
                new LambdaQueryWrapper<HrPersonnel>()
                        .eq(HrPersonnel::getSupplierId, id)
                        .orderByDesc(HrPersonnel::getCreateTime));
        
        int totalPersonnel = personnelList.size();
        int activePersonnel = (int) personnelList.stream().filter(p -> "ON_DUTY".equals(p.getStatus())).count();
        int exitPersonnel = totalPersonnel - activePersonnel;
        
        vo.setTotalPersonnel(totalPersonnel);
        vo.setActivePersonnel(activePersonnel);
        vo.setExitPersonnel(exitPersonnel);
        
        if (totalPersonnel > 0) {
            vo.setTurnoverRate(BigDecimal.valueOf(exitPersonnel * 100.0 / totalPersonnel).setScale(2, RoundingMode.HALF_UP));
        } else {
            vo.setTurnoverRate(BigDecimal.ZERO);
        }
        
        List<Map<String, Object>> personnelMap = personnelList.stream().limit(10).map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("positionName", p.getPositionName());
            map.put("entryDate", p.getEntryDate());
            map.put("status", p.getStatus());
            return map;
        }).collect(Collectors.toList());
        vo.setPersonnelList(personnelMap);
        
        List<Long> projectIds = contracts.stream()
                .map(HrContract::getProjectId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        
        if (!projectIds.isEmpty()) {
            List<HrProject> projects = hrProjectService.listByIds(projectIds);
            List<Map<String, Object>> projectList = projects.stream().map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("projectName", p.getProjectName());
                map.put("projectCode", p.getProjectCode());
                map.put("status", p.getStatus());
                return map;
            }).collect(Collectors.toList());
            vo.setProjects(projectList);
        } else {
            vo.setProjects(new ArrayList<>());
        }
        
        List<HrSettlement> settlements = hrSettlementService.list(
                new LambdaQueryWrapper<HrSettlement>()
                        .eq(HrSettlement::getSupplierId, id)
                        .orderByDesc(HrSettlement::getSettlementYear)
                        .orderByDesc(HrSettlement::getSettlementMonth)
                        .last("LIMIT 12"));
        
        BigDecimal totalSettled = settlements.stream()
                .map(HrSettlement::getFinalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalSettledAmount(totalSettled);
        vo.setPendingSettlement(totalAmount.subtract(totalSettled));
        
        List<Map<String, Object>> settlementList = settlements.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("year", s.getSettlementYear());
            map.put("month", s.getSettlementMonth());
            map.put("finalAmount", s.getFinalAmount());
            map.put("status", s.getStatus());
            return map;
        }).collect(Collectors.toList());
        vo.setSettlements(settlementList);
        
        return Result.success(vo);
    }
    
    @PostMapping("/{id}/scores")
    public Result<Void> addScore(@PathVariable Long id, @RequestBody HrSupplierScore score) {
        score.setSupplierId(id);
        if (score.getOverallScore() == null) {
            int total = (score.getTechAbility() != null ? score.getTechAbility() : 0) +
                       (score.getDeliveryQuality() != null ? score.getDeliveryQuality() : 0) +
                       (score.getResponseSpeed() != null ? score.getResponseSpeed() : 0) +
                       (score.getCommunication() != null ? score.getCommunication() : 0) +
                       (score.getStability() != null ? score.getStability() : 0);
            score.setOverallScore(BigDecimal.valueOf(total / 5.0).setScale(2, RoundingMode.HALF_UP));
        }
        hrSupplierScoreService.save(score);
        return Result.success();
    }
    
    @GetMapping("/{id}/scores")
    public Result<List<HrSupplierScore>> getScores(@PathVariable Long id) {
        List<HrSupplierScore> scores = hrSupplierScoreService.lambdaQuery()
                .eq(HrSupplierScore::getSupplierId, id)
                .orderByDesc(HrSupplierScore::getScoreDate)
                .list();
        return Result.success(scores);
    }
}
