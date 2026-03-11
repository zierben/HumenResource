package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("hr_requirement")
public class HrRequirement {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String requirementCode;
    private String projectName;
    private String positionName;
    private String positionLevel;
    private String skills;
    private LocalDate expectedEntryDate;
    private Integer serviceMonths;
    private BigDecimal dailyRate;
    private BigDecimal monthlyBudget;
    private BigDecimal totalBudget;
    private Integer demandCount;
    private String demandReason;
    private String workContent;
    private String projectRoi;
    
    @TableField("department_id")
    private Long departmentId;
    
    @TableField("project_id")
    private Long projectId;
    
    private Integer status;
    private String rejectReason;
    
    @TableField("workflow_instance_id")
    private Long workflowInstanceId;
    
    private String workflowStatus;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
