package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.constant.WfConstants;
import com.hr.admin.entity.WfNode;
import com.hr.admin.mapper.WfNodeMapper;
import com.hr.admin.service.WfNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WfNodeServiceImpl extends ServiceImpl<WfNodeMapper, WfNode> implements WfNodeService {
    
    @Override
    public List<WfNode> getByWorkflowId(Long workflowId) {
        LambdaQueryWrapper<WfNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WfNode::getWorkflowId, workflowId)
               .orderByAsc(WfNode::getSortOrder);
        return list(wrapper);
    }
    
    @Override
    public WfNode getStartNode(Long workflowId) {
        LambdaQueryWrapper<WfNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WfNode::getWorkflowId, workflowId)
               .eq(WfNode::getNodeType, WfConstants.NodeType.START);
        return getOne(wrapper);
    }
}
