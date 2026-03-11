package com.hr.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.admin.dto.Result;
import com.hr.admin.entity.WfWorkflow;
import com.hr.admin.service.WfWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow/workflows")
@RequiredArgsConstructor
public class WfWorkflowController {
    
    private final WfWorkflowService wfWorkflowService;
    
    @GetMapping
    public Result<Page<WfWorkflow>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String workflowName,
            @RequestParam(required = false) Integer status) {
        Page<WfWorkflow> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WfWorkflow> wrapper = new LambdaQueryWrapper<>();
        if (workflowName != null) {
            wrapper.like(WfWorkflow::getWorkflowName, workflowName);
        }
        if (status != null) {
            wrapper.eq(WfWorkflow::getStatus, status);
        }
        wrapper.orderByDesc(WfWorkflow::getCreateTime);
        return Result.success(wfWorkflowService.page(page, wrapper));
    }
    
    @GetMapping("/{id}")
    public Result<WfWorkflow> getById(@PathVariable Long id) {
        return Result.success(wfWorkflowService.getById(id));
    }
    
    @PostMapping
    public Result<Void> save(@RequestBody WfWorkflow workflow) {
        wfWorkflowService.save(workflow);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody WfWorkflow workflow) {
        workflow.setId(id);
        wfWorkflowService.updateById(workflow);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        wfWorkflowService.removeById(id);
        return Result.success();
    }
}
