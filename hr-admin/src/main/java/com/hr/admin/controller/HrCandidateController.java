package com.hr.admin.controller;

import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrCandidate;
import com.hr.admin.entity.HrPersonnel;
import com.hr.admin.service.HrCandidateService;
import com.hr.admin.service.HrPersonnelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class HrCandidateController {
    
    private final HrCandidateService hrCandidateService;
    private final HrPersonnelService hrPersonnelService;
    
    @GetMapping
    public Result<Page<HrCandidate>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        Page<HrCandidate> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HrCandidate> wrapper = new LambdaQueryWrapper<>();
        if (name != null) {
            wrapper.like(HrCandidate::getName, name);
        }
        if (status != null) {
            wrapper.eq(HrCandidate::getStatus, status);
        }
        wrapper.orderByDesc(HrCandidate::getCreateTime);
        return Result.success(hrCandidateService.page(page, wrapper));
    }
    
    @GetMapping("/{id}")
    public Result<HrCandidate> getById(@PathVariable Long id) {
        return Result.success(hrCandidateService.getById(id));
    }
    
    @PostMapping
    public Result<Void> save(@RequestBody HrCandidate hrCandidate) {
        hrCandidateService.save(hrCandidate);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody HrCandidate hrCandidate) {
        hrCandidate.setId(id);
        hrCandidateService.updateById(hrCandidate);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hrCandidateService.removeById(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/convert")
    public Result<HrPersonnel> convertToPersonnel(@PathVariable Long id) {
        HrCandidate candidate = hrCandidateService.getById(id);
        if (candidate == null) {
            return Result.error("候选人不存在");
        }
        
        HrPersonnel personnel = new HrPersonnel();
        personnel.setName(candidate.getName());
        personnel.setPhone(candidate.getPhone());
        personnel.setEmail(candidate.getEmail());
        personnel.setPositionName(candidate.getPositionName());
        personnel.setSupplierId(candidate.getSupplierId());
        personnel.setDailyRate(candidate.getExpectedRate());
        personnel.setStatus("PENDING_ENTRY");
        
        hrPersonnelService.savePersonnel(personnel);
        
        candidate.setStatus(3);
        hrCandidateService.updateById(candidate);
        
        return Result.success(personnel);
    }
}
