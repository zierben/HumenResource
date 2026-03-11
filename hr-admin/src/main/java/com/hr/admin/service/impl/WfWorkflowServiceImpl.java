package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.entity.WfWorkflow;
import com.hr.admin.mapper.WfWorkflowMapper;
import com.hr.admin.service.WfWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WfWorkflowServiceImpl extends ServiceImpl<WfWorkflowMapper, WfWorkflow> implements WfWorkflowService {
}
