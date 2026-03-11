package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.constant.WfConstants;
import com.hr.admin.dto.InstanceDetailDTO;
import com.hr.admin.entity.*;
import com.hr.admin.mapper.WfInstanceMapper;
import com.hr.admin.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WfInstanceServiceImpl extends ServiceImpl<WfInstanceMapper, WfInstance> implements WfInstanceService {
    
    private final WfNodeService wfNodeService;
    private final WfRecordService wfRecordService;
    private final SysUserService sysUserService;
    
    @Override
    @Transactional
    public WfInstance startWorkflow(Long workflowId, String businessType, Long businessId, String title, Long initiatorId) {
        WfNode startNode = wfNodeService.getStartNode(workflowId);
        if (startNode == null) {
            throw new RuntimeException("Workflow has no start node");
        }
        
        WfInstance instance = new WfInstance();
        instance.setWorkflowId(workflowId);
        instance.setBusinessType(businessType);
        instance.setBusinessId(businessId);
        instance.setTitle(title);
        instance.setInitiatorId(initiatorId);
        instance.setCurrentNodeId(startNode.getNextNodeId());
        instance.setStatus(WfConstants.InstanceStatus.PENDING);
        save(instance);
        
        SysUser initiator = sysUserService.getById(initiatorId);
        WfRecord record = new WfRecord();
        record.setInstanceId(instance.getId());
        record.setNodeId(startNode.getId());
        record.setOperatorId(initiatorId);
        record.setOperatorName(initiator != null ? initiator.getRealName() : "Unknown");
        record.setAction(WfConstants.Action.SUBMIT);
        record.setComment("Submit workflow");
        wfRecordService.save(record);
        
        return instance;
    }
    
    @Override
    @Transactional
    public void approve(Long instanceId, Long operatorId, String operatorName, String comment) {
        WfInstance instance = getById(instanceId);
        if (instance == null) {
            throw new RuntimeException("Instance not found");
        }
        if (!WfConstants.InstanceStatus.PENDING.equals(instance.getStatus())) {
            throw new RuntimeException("Instance is not pending");
        }
        
        WfNode currentNode = wfNodeService.getById(instance.getCurrentNodeId());
        if (currentNode == null) {
            throw new RuntimeException("Current node not found");
        }
        
        WfRecord record = new WfRecord();
        record.setInstanceId(instanceId);
        record.setNodeId(currentNode.getId());
        record.setOperatorId(operatorId);
        record.setOperatorName(operatorName);
        record.setAction(WfConstants.Action.APPROVE);
        record.setComment(comment);
        wfRecordService.save(record);
        
        if (WfConstants.NodeType.END.equals(currentNode.getNextNodeId() == null ? null : 
            wfNodeService.getById(currentNode.getNextNodeId()) != null ? 
            wfNodeService.getById(currentNode.getNextNodeId()).getNodeType() : null)) {
            instance.setStatus(WfConstants.InstanceStatus.APPROVED);
            instance.setCurrentNodeId(null);
        } else {
            instance.setCurrentNodeId(currentNode.getNextNodeId());
        }
        updateById(instance);
    }
    
    @Override
    @Transactional
    public void reject(Long instanceId, Long operatorId, String operatorName, String comment) {
        WfInstance instance = getById(instanceId);
        if (instance == null) {
            throw new RuntimeException("Instance not found");
        }
        if (!WfConstants.InstanceStatus.PENDING.equals(instance.getStatus())) {
            throw new RuntimeException("Instance is not pending");
        }
        
        WfNode currentNode = wfNodeService.getById(instance.getCurrentNodeId());
        
        WfRecord record = new WfRecord();
        record.setInstanceId(instanceId);
        record.setNodeId(currentNode != null ? currentNode.getId() : null);
        record.setOperatorId(operatorId);
        record.setOperatorName(operatorName);
        record.setAction(WfConstants.Action.REJECT);
        record.setComment(comment);
        wfRecordService.save(record);
        
        instance.setStatus(WfConstants.InstanceStatus.REJECTED);
        instance.setCurrentNodeId(null);
        updateById(instance);
    }
    
    @Override
    public Page<WfInstance> getMyPending(Long userId, Integer pageNum, Integer pageSize) {
        Page<WfInstance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WfInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WfInstance::getStatus, WfConstants.InstanceStatus.PENDING)
               .orderByDesc(WfInstance::getCreateTime);
        return page(page, wrapper);
    }
    
    @Override
    public Page<WfInstance> getMyDone(Long userId, Integer pageNum, Integer pageSize) {
        Page<WfInstance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WfInstance> instanceWrapper = new LambdaQueryWrapper<>();
        instanceWrapper.in(WfInstance::getStatus, WfConstants.InstanceStatus.APPROVED, WfConstants.InstanceStatus.REJECTED)
                       .orderByDesc(WfInstance::getUpdateTime);
        return page(page, instanceWrapper);
    }
    
    @Override
    public InstanceDetailDTO getDetail(Long instanceId) {
        WfInstance instance = getById(instanceId);
        if (instance == null) {
            return null;
        }
        List<WfRecord> records = wfRecordService.getByInstanceId(instanceId);
        
        InstanceDetailDTO dto = new InstanceDetailDTO();
        dto.setInstance(instance);
        dto.setRecords(records);
        return dto;
    }
}
