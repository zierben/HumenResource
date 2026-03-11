package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_role_menu")
public class SysRoleMenu {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String role;
    private Long menuId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
