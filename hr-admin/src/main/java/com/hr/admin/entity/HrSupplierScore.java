package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("hr_supplier_score")
public class HrSupplierScore {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long supplierId;
    
    private LocalDate scoreDate;
    
    private Integer techAbility;
    
    private Integer deliveryQuality;
    
    private Integer responseSpeed;
    
    private Integer communication;
    
    private Integer stability;
    
    private BigDecimal overallScore;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
