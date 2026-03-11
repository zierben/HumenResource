package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("hr_project")
public class HrProject {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String projectCode;
    private String projectName;
    private String department;
    private String manager;
    private String partyA;
    private String partyB;
    private BigDecimal budget;
    
    @TableField("zentao_project_id")
    private Long zentaoProjectId;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
