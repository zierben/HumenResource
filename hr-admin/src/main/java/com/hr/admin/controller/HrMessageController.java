package com.hr.admin.controller;

import com.hr.admin.dto.Result;
import com.hr.admin.entity.HrMessage;
import com.hr.admin.service.HrMessageService;
import com.hr.admin.websocket.WebSocketService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class HrMessageController {
    
    private final HrMessageService hrMessageService;
    private final WebSocketService webSocketService;
    
    @GetMapping
    public Result<Page<HrMessage>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String type) {
        Page<HrMessage> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HrMessage> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(HrMessage::getUserId, userId);
        }
        if (type != null) {
            wrapper.eq(HrMessage::getType, type);
        }
        wrapper.orderByDesc(HrMessage::getCreateTime);
        return Result.success(hrMessageService.page(page, wrapper));
    }
    
    @GetMapping("/{id}")
    public Result<HrMessage> getById(@PathVariable Long id) {
        return Result.success(hrMessageService.getById(id));
    }
    
    @PostMapping
    public Result<HrMessage> save(@RequestBody HrMessage hrMessage) {
        hrMessageService.save(hrMessage);
        if (hrMessage.getUserId() != null) {
            webSocketService.pushNotification(
                hrMessage.getUserId(),
                "新消息",
                hrMessage.getTitle()
            );
        }
        return Result.success(hrMessage);
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody HrMessage hrMessage) {
        hrMessage.setId(id);
        hrMessageService.updateById(hrMessage);
        return Result.success();
    }
    
    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        HrMessage message = hrMessageService.getById(id);
        if (message != null) {
            message.setIsRead(1);
            hrMessageService.updateById(message);
        }
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hrMessageService.removeById(id);
        return Result.success();
    }
}
