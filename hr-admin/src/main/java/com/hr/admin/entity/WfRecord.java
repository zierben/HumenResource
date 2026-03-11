package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("wf_record")
public class WfRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long instanceId;
    private Long nodeId;
    private Long operatorId;
    private String operatorName;
    private String action;
    private String comment;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
