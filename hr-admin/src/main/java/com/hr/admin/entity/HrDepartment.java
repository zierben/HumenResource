package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hr_department")
public class HrDepartment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String deptName;
    
    @TableField("parent_id")
    private Long parentId;
    
    @TableField("manager_id")
    private Long managerId;
    
    private Integer level;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(exist = false)
    private String managerName;
    
    @TableField(exist = false)
    private Integer memberCount;
    
    @TableField(exist = false)
    private java.util.List<HrDepartment> children;
}
