package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("hr_settlement")
public class HrSettlement {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String settlementCode;
    private Integer settlementMonth;
    private Integer settlementYear;
    
    @TableField("supplier_id")
    private Long supplierId;
    
    @TableField("project_id")
    private Long projectId;
    
    private BigDecimal totalAmount;
    private BigDecimal validDays;
    private BigDecimal deductionAmount;
    private BigDecimal finalAmount;
    private Integer status;
    private String confirmRemark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
