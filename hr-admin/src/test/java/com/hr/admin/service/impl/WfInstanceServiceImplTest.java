package com.hr.admin.service.impl;

import com.hr.admin.constant.WfConstants;
import com.hr.admin.dto.InstanceDetailDTO;
import com.hr.admin.entity.WfInstance;
import com.hr.admin.entity.WfNode;
import com.hr.admin.entity.WfRecord;
import com.hr.admin.mapper.WfInstanceMapper;
import com.hr.admin.service.WfNodeService;
import com.hr.admin.service.WfRecordService;
import com.hr.admin.service.SysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WfInstanceServiceImplTest {

    @Mock
    private WfInstanceMapper wfInstanceMapper;

    @Mock
    private WfNodeService wfNodeService;

    @Mock
    private WfRecordService wfRecordService;

    @Mock
    private SysUserService sysUserService;

    private WfInstanceServiceImpl wfInstanceService;

    private WfNode startNode;
    private WfNode approvalNode;
    private WfNode endNode;
    private WfInstance testInstance;

    @BeforeEach
    void setUp() {
        wfInstanceService = new WfInstanceServiceImpl(wfNodeService, wfRecordService, sysUserService);
        ReflectionTestUtils.setField(wfInstanceService, "baseMapper", wfInstanceMapper);

        startNode = new WfNode();
        startNode.setId(1L);
        startNode.setWorkflowId(1L);
        startNode.setNodeName("开始");
        startNode.setNodeType(WfConstants.NodeType.START);
        startNode.setNextNodeId(2L);

        approvalNode = new WfNode();
        approvalNode.setId(2L);
        approvalNode.setWorkflowId(1L);
        approvalNode.setNodeName("审批");
        approvalNode.setNodeType(WfConstants.NodeType.APPROVAL);
        approvalNode.setNextNodeId(3L);

        endNode = new WfNode();
        endNode.setId(3L);
        endNode.setWorkflowId(1L);
        endNode.setNodeName("结束");
        endNode.setNodeType(WfConstants.NodeType.END);
        endNode.setNextNodeId(null);

        testInstance = new WfInstance();
        testInstance.setId(1L);
        testInstance.setWorkflowId(1L);
        testInstance.setBusinessType("REQUIREMENT");
        testInstance.setBusinessId(100L);
        testInstance.setTitle("测试需求审批");
        testInstance.setInitiatorId(1L);
        testInstance.setCurrentNodeId(2L);
        testInstance.setStatus(WfConstants.InstanceStatus.PENDING);
    }

    @Test
    @DisplayName("启动工作流")
    void testStartWorkflow() {
        when(wfNodeService.getStartNode(1L)).thenReturn(startNode);
        when(wfInstanceMapper.insert(any(WfInstance.class))).thenReturn(1);
        when(wfRecordService.save(any(WfRecord.class))).thenReturn(true);

        WfInstance result = wfInstanceService.startWorkflow(1L, "REQUIREMENT", 100L, "测试需求", 1L);

        assertNotNull(result);
        assertEquals(2L, result.getCurrentNodeId());
        assertEquals(WfConstants.InstanceStatus.PENDING, result.getStatus());

        verify(wfNodeService).getStartNode(1L);
        verify(wfInstanceMapper).insert(any(WfInstance.class));
        verify(wfRecordService).save(any(WfRecord.class));
    }

    @Test
    @DisplayName("启动工作流-无开始节点")
    void testStartWorkflow_NoStartNode() {
        when(wfNodeService.getStartNode(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            wfInstanceService.startWorkflow(1L, "REQUIREMENT", 100L, "测试需求", 1L);
        });

        assertEquals("Workflow has no start node", exception.getMessage());
    }

    @Test
    @DisplayName("审批通过-流转到下一节点")
    void testApprove_MoveToNextNode() {
        WfNode middleNode = new WfNode();
        middleNode.setId(3L);
        middleNode.setWorkflowId(1L);
        middleNode.setNodeName("中间审批");
        middleNode.setNodeType(WfConstants.NodeType.APPROVAL);
        middleNode.setNextNodeId(4L);
        
        testInstance.setCurrentNodeId(2L);
        when(wfInstanceMapper.selectById(1L)).thenReturn(testInstance);
        when(wfNodeService.getById(2L)).thenReturn(approvalNode);
        when(wfNodeService.getById(3L)).thenReturn(middleNode);
        when(wfRecordService.save(any(WfRecord.class))).thenReturn(true);
        when(wfInstanceMapper.updateById(any(WfInstance.class))).thenReturn(1);

        wfInstanceService.approve(1L, 2L, "审批人", "同意");

        ArgumentCaptor<WfInstance> captor = ArgumentCaptor.forClass(WfInstance.class);
        verify(wfInstanceMapper).updateById(captor.capture());
        
        WfInstance updatedInstance = captor.getValue();
        assertEquals(3L, updatedInstance.getCurrentNodeId());
        assertEquals(WfConstants.InstanceStatus.PENDING, updatedInstance.getStatus());
    }

    @Test
    @DisplayName("审批拒绝")
    void testReject() {
        when(wfInstanceMapper.selectById(1L)).thenReturn(testInstance);
        when(wfNodeService.getById(2L)).thenReturn(approvalNode);
        when(wfRecordService.save(any(WfRecord.class))).thenReturn(true);
        when(wfInstanceMapper.updateById(any(WfInstance.class))).thenReturn(1);

        wfInstanceService.reject(1L, 2L, "审批人", "拒绝原因");

        ArgumentCaptor<WfInstance> captor = ArgumentCaptor.forClass(WfInstance.class);
        verify(wfInstanceMapper).updateById(captor.capture());
        
        WfInstance updatedInstance = captor.getValue();
        assertEquals(WfConstants.InstanceStatus.REJECTED, updatedInstance.getStatus());
        assertNull(updatedInstance.getCurrentNodeId());
    }

    @Test
    @DisplayName("审批-实例不存在")
    void testApprove_InstanceNotFound() {
        when(wfInstanceMapper.selectById(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            wfInstanceService.approve(1L, 2L, "审批人", "同意");
        });

        assertEquals("Instance not found", exception.getMessage());
    }

    @Test
    @DisplayName("审批-实例非待审批状态")
    void testApprove_InstanceNotPending() {
        testInstance.setStatus(WfConstants.InstanceStatus.APPROVED);
        when(wfInstanceMapper.selectById(1L)).thenReturn(testInstance);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            wfInstanceService.approve(1L, 2L, "审批人", "同意");
        });

        assertEquals("Instance is not pending", exception.getMessage());
    }

    @Test
    @DisplayName("获取实例详情")
    void testGetDetail() {
        WfRecord record = new WfRecord();
        record.setId(1L);
        record.setInstanceId(1L);
        
        when(wfInstanceMapper.selectById(1L)).thenReturn(testInstance);
        when(wfRecordService.getByInstanceId(1L)).thenReturn(Arrays.asList(record));

        InstanceDetailDTO result = wfInstanceService.getDetail(1L);

        assertNotNull(result);
        assertEquals(testInstance, result.getInstance());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    @DisplayName("获取实例详情-不存在")
    void testGetDetail_NotFound() {
        when(wfInstanceMapper.selectById(1L)).thenReturn(null);

        InstanceDetailDTO result = wfInstanceService.getDetail(1L);

        assertNull(result);
    }
}
