package com.hr.admin.controller;

import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrEvaluation;
import com.hr.admin.service.HrEvaluationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
public class HrEvaluationController {
    
    private final HrEvaluationService hrEvaluationService;
    
    @GetMapping
    public Result<Page<HrEvaluation>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long personnelId,
            @RequestParam(required = false) String evaluationMonth) {
        Page<HrEvaluation> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HrEvaluation> wrapper = new LambdaQueryWrapper<>();
        if (personnelId != null) {
            wrapper.eq(HrEvaluation::getPersonnelId, personnelId);
        }
        if (evaluationMonth != null) {
            wrapper.eq(HrEvaluation::getEvaluationMonth, evaluationMonth);
        }
        wrapper.orderByDesc(HrEvaluation::getCreateTime);
        return Result.success(hrEvaluationService.page(page, wrapper));
    }
    
    @GetMapping("/{id}")
    public Result<HrEvaluation> getById(@PathVariable Long id) {
        return Result.success(hrEvaluationService.getById(id));
    }
    
    @PostMapping
    public Result<Void> save(@RequestBody HrEvaluation hrEvaluation) {
        hrEvaluationService.save(hrEvaluation);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody HrEvaluation hrEvaluation) {
        hrEvaluation.setId(id);
        hrEvaluationService.updateById(hrEvaluation);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hrEvaluationService.removeById(id);
        return Result.success();
    }
}
