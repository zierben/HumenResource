package com.hr.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.admin.dto.*;
import com.hr.admin.entity.SysUser;
import com.hr.admin.entity.WfInstance;
import com.hr.admin.service.SysUserService;
import com.hr.admin.service.WfInstanceService;
import com.hr.admin.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow/instances")
@RequiredArgsConstructor
public class WfInstanceController {
    
    private final WfInstanceService wfInstanceService;
    private final SysUserService sysUserService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/start")
    public Result<WfInstance> start(
            @RequestBody StartWorkflowDTO dto,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserId(token);
        WfInstance instance = wfInstanceService.startWorkflow(
            dto.getWorkflowId(),
            dto.getBusinessType(),
            dto.getBusinessId(),
            dto.getTitle(),
            userId
        );
        return Result.success(instance);
    }
    
    @PostMapping("/{id}/approve")
    public Result<Void> approve(
            @PathVariable Long id,
            @RequestBody ActionDTO dto,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserId(token);
        SysUser user = sysUserService.getById(userId);
        String userName = user != null ? user.getRealName() : "Unknown";
        wfInstanceService.approve(id, userId, userName, dto.getComment());
        return Result.success();
    }
    
    @PostMapping("/{id}/reject")
    public Result<Void> reject(
            @PathVariable Long id,
            @RequestBody ActionDTO dto,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserId(token);
        SysUser user = sysUserService.getById(userId);
        String userName = user != null ? user.getRealName() : "Unknown";
        wfInstanceService.reject(id, userId, userName, dto.getComment());
        return Result.success();
    }
    
    @GetMapping("/pending")
    public Result<Page<WfInstance>> getPending(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserId(token);
        return Result.success(wfInstanceService.getMyPending(userId, pageNum, pageSize));
    }
    
    @GetMapping("/done")
    public Result<Page<WfInstance>> getDone(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserId(token);
        return Result.success(wfInstanceService.getMyDone(userId, pageNum, pageSize));
    }
    
    @GetMapping("/{id}")
    public Result<InstanceDetailDTO> getDetail(@PathVariable Long id) {
        return Result.success(wfInstanceService.getDetail(id));
    }
    
    private Long getCurrentUserId(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Unauthorized");
        }
        String jwt = token.substring(7);
        String username = jwtUtil.getUsername(jwt);
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user.getId();
    }
}
