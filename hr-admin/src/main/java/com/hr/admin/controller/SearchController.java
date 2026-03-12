package com.hr.admin.controller;

import com.hr.admin.dto.Result;
import com.hr.admin.entity.*;
import com.hr.admin.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final HrPersonnelService hrPersonnelService;
    private final HrProjectService hrProjectService;
    private final HrSupplierService hrSupplierService;
    private final HrContractService hrContractService;

    @GetMapping
    public Result<List<Map<String, Object>>> search(@RequestParam String keyword) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(results);
        }

        String kw = keyword.trim().toLowerCase();

        hrPersonnelService.lambdaQuery()
                .like(HrPersonnel::getName, kw)
                .or().like(HrPersonnel::getPersonnelCode, kw)
                .last("LIMIT 5")
                .list()
                .forEach(p -> results.add(createResult("personnel", p.getId(), p.getName(), 
                        "人员 - " + (p.getPositionName() != null ? p.getPositionName() : ""), "/personnel/" + p.getId())));

        hrProjectService.lambdaQuery()
                .like(HrProject::getProjectName, kw)
                .or().like(HrProject::getProjectCode, kw)
                .last("LIMIT 5")
                .list()
                .forEach(p -> results.add(createResult("project", p.getId(), p.getProjectName(),
                        "项目 - " + (p.getManager() != null ? p.getManager() : ""), "/projects/" + p.getId())));

        hrSupplierService.lambdaQuery()
                .like(HrSupplier::getSupplierName, kw)
                .last("LIMIT 5")
                .list()
                .forEach(s -> results.add(createResult("supplier", s.getId(), s.getSupplierName(),
                        "供应商", "/suppliers")));

        hrContractService.lambdaQuery()
                .like(HrContract::getContractName, kw)
                .or().like(HrContract::getContractCode, kw)
                .last("LIMIT 5")
                .list()
                .forEach(c -> results.add(createResult("contract", c.getId(), c.getContractName(),
                        "合同", "/contracts")));

        return Result.success(results);
    }

    private Map<String, Object> createResult(String type, Long id, String name, String desc, String path) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("id", id);
        result.put("name", name);
        result.put("description", desc);
        result.put("path", path);
        return result;
    }
}
