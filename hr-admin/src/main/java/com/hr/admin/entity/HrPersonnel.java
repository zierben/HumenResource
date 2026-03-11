package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("hr_personnel")
public class HrPersonnel {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String personnelCode;
    private String name;
    private String idCard;
    private String phone;
    private String email;
    private String positionName;
    private String positionLevel;
    private BigDecimal dailyRate;
    
    @TableField("supplier_id")
    private Long supplierId;
    
    @TableField("project_id")
    private Long projectId;
    
    @TableField("requirement_id")
    private Long requirementId;
    
    private String status;
    private LocalDate entryDate;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    
    @TableField("zentao_user_id")
    private Long zentaoUserId;
    
    @TableField("zentao_account")
    private String zentaoAccount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
