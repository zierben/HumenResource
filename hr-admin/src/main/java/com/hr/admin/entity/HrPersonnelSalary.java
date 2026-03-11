package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("hr_personnel_salary")
public class HrPersonnelSalary {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("personnel_id")
    private Long personnelId;
    
    private String salaryType;
    private BigDecimal amount;
    private LocalDate effectiveDate;
    private String reason;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
