package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("hr_payment")
public class HrPayment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String paymentCode;
    @TableField("supplier_id")
    private Long supplierId;
    @TableField("settlement_id")
    private Long settlementId;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String voucherPath;
    private Integer status;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
