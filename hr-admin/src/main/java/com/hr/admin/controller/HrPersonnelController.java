package com.hr.admin.controller;

import com.hr.admin.annotation.OperationLog;
import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrPersonnel;
import com.hr.admin.entity.HrProject;
import com.hr.admin.entity.HrSupplier;
import com.hr.admin.service.HrPersonnelService;
import com.hr.admin.service.HrProjectService;
import com.hr.admin.service.HrSupplierService;
import com.hr.admin.util.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/personnel")
@RequiredArgsConstructor
public class HrPersonnelController {
    
    private final HrPersonnelService hrPersonnelService;
    private final HrProjectService hrProjectService;
    private final HrSupplierService hrSupplierService;
    
    @GetMapping
    public Result<Page<HrPersonnel>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long projectId) {
        Page<HrPersonnel> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HrPersonnel> wrapper = new LambdaQueryWrapper<>();
        if (name != null) {
            wrapper.like(HrPersonnel::getName, name);
        }
        if (status != null) {
            wrapper.eq(HrPersonnel::getStatus, status);
        }
        if (projectId != null) {
            wrapper.eq(HrPersonnel::getProjectId, projectId);
        }
        wrapper.orderByDesc(HrPersonnel::getCreateTime);
        return Result.success(hrPersonnelService.page(page, wrapper));
    }
    
    @GetMapping("/{id}")
    public Result<HrPersonnel> getById(@PathVariable Long id) {
        return Result.success(hrPersonnelService.getById(id));
    }
    
    @PostMapping
    @OperationLog(module = "人员管理", action = "新增人员", targetType = "人员")
    public Result<Void> save(@RequestBody HrPersonnel hrPersonnel) {
        hrPersonnelService.savePersonnel(hrPersonnel);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    @OperationLog(module = "人员管理", action = "编辑人员", targetType = "人员")
    public Result<Void> update(@PathVariable Long id, @RequestBody HrPersonnel hrPersonnel) {
        hrPersonnel.setId(id);
        hrPersonnelService.updateById(hrPersonnel);
        return Result.success();
    }
    
    @PostMapping("/{id}/entry")
    @OperationLog(module = "人员管理", action = "人员入场", targetType = "人员")
    public Result<Void> entry(@PathVariable Long id) {
        hrPersonnelService.entry(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/exit")
    @OperationLog(module = "人员管理", action = "人员离场", targetType = "人员")
    public Result<Void> exit(@PathVariable Long id) {
        hrPersonnelService.exit(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/re-entry")
    @OperationLog(module = "人员管理", action = "重新入场", targetType = "人员")
    public Result<Void> reEntry(@PathVariable Long id) {
        hrPersonnelService.reEntry(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/transfer")
    @OperationLog(module = "人员管理", action = "人员调配", targetType = "人员")
    public Result<Void> transfer(@PathVariable Long id, @RequestParam Long targetProjectId) {
        hrPersonnelService.transfer(id, targetProjectId);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    @OperationLog(module = "人员管理", action = "删除人员", targetType = "人员")
    public Result<Void> delete(@PathVariable Long id) {
        hrPersonnelService.removeById(id);
        return Result.success();
    }
    
    @GetMapping("/export")
    public void export(@RequestParam(required = false) String name,
                      @RequestParam(required = false) String status,
                      HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<HrPersonnel> wrapper = new LambdaQueryWrapper<>();
        if (name != null) {
            wrapper.like(HrPersonnel::getName, name);
        }
        if (status != null) {
            wrapper.eq(HrPersonnel::getStatus, status);
        }
        List<HrPersonnel> list = hrPersonnelService.list(wrapper);
        
        List<HrSupplier> suppliers = hrSupplierService.list();
        List<HrProject> projects = hrProjectService.list();
        
        Map<Long, String> supplierMap = new HashMap<>();
        for (HrSupplier s : suppliers) {
            supplierMap.put(s.getId(), s.getSupplierName());
        }
        
        Map<Long, String> projectMap = new HashMap<>();
        for (HrProject p : projects) {
            projectMap.put(p.getId(), p.getProjectName());
        }
        
        String[] headers = {"人员编号", "姓名", "岗位", "供应商", "项目", "人天单价", "入场时间", "状态", "联系方式"};
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("PENDING_ENTRY", "待入场");
        statusMap.put("ON_DUTY", "在岗");
        statusMap.put("LEAVE", "请假");
        statusMap.put("OFF_DUTY", "已离场");
        
        for (HrPersonnel p : list) {
            Map<String, Object> row = new HashMap<>();
            row.put("人员编号", p.getPersonnelCode());
            row.put("姓名", p.getName());
            row.put("岗位", p.getPositionName());
            row.put("供应商", supplierMap.getOrDefault(p.getSupplierId(), "-"));
            row.put("项目", projectMap.getOrDefault(p.getProjectId(), "-"));
            row.put("人天单价", p.getDailyRate());
            row.put("入场时间", p.getEntryDate());
            row.put("状态", statusMap.getOrDefault(p.getStatus(), "未知"));
            row.put("联系方式", p.getPhone());
            dataList.add(row);
        }
        
        ExcelUtil.export(response, "人员列表", headers, dataList);
    }
}
