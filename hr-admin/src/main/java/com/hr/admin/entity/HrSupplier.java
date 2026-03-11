package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hr_supplier")
public class HrSupplier {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String supplierName;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private String level;
    private Integer deliveryRate;
    private Integer currentPersonCount;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
