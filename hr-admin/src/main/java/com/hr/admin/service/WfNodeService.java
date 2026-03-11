package com.hr.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hr.admin.entity.WfNode;
import java.util.List;

public interface WfNodeService extends IService<WfNode> {
    List<WfNode> getByWorkflowId(Long workflowId);
    WfNode getStartNode(Long workflowId);
}
