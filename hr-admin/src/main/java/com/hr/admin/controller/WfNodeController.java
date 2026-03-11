package com.hr.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hr.admin.dto.Result;
import com.hr.admin.entity.WfNode;
import com.hr.admin.service.WfNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/workflow/nodes")
@RequiredArgsConstructor
public class WfNodeController {
    
    private final WfNodeService wfNodeService;
    
    @GetMapping("/workflow/{workflowId}")
    public Result<List<WfNode>> getByWorkflowId(@PathVariable Long workflowId) {
        LambdaQueryWrapper<WfNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WfNode::getWorkflowId, workflowId)
               .orderByAsc(WfNode::getSortOrder);
        return Result.success(wfNodeService.list(wrapper));
    }
    
    @PostMapping("/batch")
    public Result<Void> saveBatch(@RequestBody List<WfNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return Result.success();
        }
        Long workflowId = nodes.get(0).getWorkflowId();
        LambdaQueryWrapper<WfNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WfNode::getWorkflowId, workflowId);
        wfNodeService.remove(wrapper);
        wfNodeService.saveBatch(nodes);
        return Result.success();
    }
    
    @PostMapping
    public Result<Void> save(@RequestBody WfNode node) {
        wfNodeService.save(node);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody WfNode node) {
        node.setId(id);
        wfNodeService.updateById(node);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        wfNodeService.removeById(id);
        return Result.success();
    }
}
