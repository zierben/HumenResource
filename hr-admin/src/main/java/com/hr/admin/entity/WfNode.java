package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("wf_node")
public class WfNode {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long workflowId;
    private String nodeCode;
    private String nodeName;
    private String nodeType;
    private String approverRole;
    private Long nextNodeId;
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
