package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("hr_project_milestone")
public class HrProjectMilestone {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("project_id")
    private Long projectId;
    
    private String milestoneName;
    private LocalDate planDate;
    private LocalDate actualDate;
    private BigDecimal amount;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
