package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    private String password;
    private String realName;
    private String role;
    private Integer status;
    private String department;
    private String position;
    private Integer level;
    @TableField("manager_id")
    private Long managerId;
    
    @TableField("dept_id")
    private Long deptId;
    
    @TableField("is_manager")
    private Integer isManager;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
