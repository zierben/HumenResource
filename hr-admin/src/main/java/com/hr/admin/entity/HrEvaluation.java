package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("hr_evaluation")
public class HrEvaluation {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("personnel_id")
    private Long personnelId;
    @TableField("project_id")
    private Long projectId;
    private String evaluator;
    private String evaluationMonth;
    private BigDecimal score;
    private BigDecimal workQuality;
    private BigDecimal communication;
    private BigDecimal punctuality;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
