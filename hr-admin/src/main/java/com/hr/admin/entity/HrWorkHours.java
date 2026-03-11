package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("hr_work_hours")
public class HrWorkHours {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("personnel_id")
    private Long personnelId;
    
    @TableField("project_id")
    private Long projectId;
    
    private LocalDate workDate;
    private BigDecimal hours;
    private String status;
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
