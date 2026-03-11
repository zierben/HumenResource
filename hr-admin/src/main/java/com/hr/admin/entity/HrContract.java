package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("hr_contract")
public class HrContract {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String contractCode;
    private String contractName;
    private String contractType;
    @TableField("supplier_id")
    private Long supplierId;
    @TableField("personnel_id")
    private Long personnelId;
    @TableField("project_id")
    private Long projectId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal amount;
    private Integer status;
    private LocalDate signDate;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
