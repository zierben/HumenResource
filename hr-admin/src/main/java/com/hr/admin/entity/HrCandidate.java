package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("hr_candidate")
public class HrCandidate {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("requirement_id")
    private Long requirementId;
    @TableField("supplier_id")
    private Long supplierId;
    private String name;
    private String phone;
    private String email;
    private String positionName;
    private BigDecimal expectedRate;
    private String resumePath;
    private LocalDate interviewDate;
    private String interviewResult;
    private String interviewRemark;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
