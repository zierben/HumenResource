package com.hr.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrPersonnel;
import com.hr.admin.entity.HrProject;
import com.hr.admin.entity.HrSettlement;
import com.hr.admin.entity.HrSupplier;
import com.hr.admin.service.HrPersonnelService;
import com.hr.admin.service.HrProjectService;
import com.hr.admin.service.HrSettlementService;
import com.hr.admin.service.HrSupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final HrProjectService hrProjectService;
    private final HrSupplierService hrSupplierService;
    private final HrPersonnelService hrPersonnelService;
    private final HrSettlementService hrSettlementService;
    
    @GetMapping("/cost-by-project")
    public Result<List<Map<String, Object>>> getCostByProject() {
        List<HrProject> projects = hrProjectService.list();
        List<HrSettlement> settlements = hrSettlementService.list();
        
        Map<Long, BigDecimal> projectCostMap = settlements.stream()
                .filter(s -> s.getProjectId() != null)
                .collect(Collectors.groupingBy(
                        HrSettlement::getProjectId,
                        Collectors.reducing(BigDecimal.ZERO, 
                                s -> s.getFinalAmount() != null ? s.getFinalAmount() : BigDecimal.ZERO,
                                BigDecimal::add)
                ));
        
        List<Map<String, Object>> result = projects.stream()
                .map(p -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", p.getProjectName());
                    item.put("value", projectCostMap.getOrDefault(p.getId(), BigDecimal.ZERO));
                    return item;
                })
                .sorted((a, b) -> ((BigDecimal) b.get("value")).compareTo((BigDecimal) a.get("value")))
                .collect(Collectors.toList());
        
        return Result.success(result);
    }
    
    @GetMapping("/personnel-by-supplier")
    public Result<List<Map<String, Object>>> getPersonnelBySupplier() {
        List<HrSupplier> suppliers = hrSupplierService.list();
        List<HrPersonnel> personnelList = hrPersonnelService.lambdaQuery()
                .eq(HrPersonnel::getStatus, "ON_DUTY")
                .list();
        
        Map<Long, Long> supplierCountMap = personnelList.stream()
                .collect(Collectors.groupingBy(HrPersonnel::getSupplierId, Collectors.counting()));
        
        List<Map<String, Object>> result = suppliers.stream()
                .map(s -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", s.getSupplierName());
                    item.put("value", supplierCountMap.getOrDefault(s.getId(), 0L));
                    return item;
                })
                .sorted((a, b) -> ((Long) b.get("value")).compareTo((Long) a.get("value")))
                .collect(Collectors.toList());
        
        return Result.success(result);
    }
    
    @GetMapping("/monthly-trend")
    public Result<Map<String, Object>> getMonthlyTrend(@RequestParam(defaultValue = "2024") Integer year) {
        List<HrSettlement> settlements = hrSettlementService.lambdaQuery()
                .eq(HrSettlement::getSettlementYear, year)
                .list();
        
        Map<Integer, BigDecimal> monthlyActual = settlements.stream()
                .collect(Collectors.groupingBy(
                        HrSettlement::getSettlementMonth,
                        Collectors.reducing(BigDecimal.ZERO,
                                s -> s.getFinalAmount() != null ? s.getFinalAmount() : BigDecimal.ZERO,
                                BigDecimal::add)
                ));
        
        List<String> months = Arrays.asList("1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月");
        List<BigDecimal> actualData = new ArrayList<>();
        List<BigDecimal> budgetData = new ArrayList<>();
        
        for (int i = 1; i <= 12; i++) {
            actualData.add(monthlyActual.getOrDefault(i, BigDecimal.ZERO)
                    .divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP));
            budgetData.add(new BigDecimal("15"));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("months", months);
        result.put("actual", actualData);
        result.put("budget", budgetData);
        
        return Result.success(result);
    }
    
    @GetMapping("/summary")
    public Result<Map<String, Object>> getSummary() {
        long totalPersonnel = hrPersonnelService.count();
        long onDutyPersonnel = hrPersonnelService.lambdaQuery()
                .eq(HrPersonnel::getStatus, "ON_DUTY")
                .count();
        
        long totalProjects = hrProjectService.count();
        long activeProjects = hrProjectService.lambdaQuery()
                .eq(HrProject::getStatus, 1)
                .count();
        
        long totalSuppliers = hrSupplierService.count();
        
        BigDecimal totalCost = hrSettlementService.list().stream()
                .map(s -> s.getFinalAmount() != null ? s.getFinalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalPersonnel", totalPersonnel);
        result.put("onDutyPersonnel", onDutyPersonnel);
        result.put("totalProjects", totalProjects);
        result.put("activeProjects", activeProjects);
        result.put("totalSuppliers", totalSuppliers);
        result.put("totalCost", totalCost);
        
        return Result.success(result);
    }
}
