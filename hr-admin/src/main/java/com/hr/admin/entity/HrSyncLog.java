package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hr_sync_log")
public class HrSyncLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String syncType;
    private String syncDirection;
    private String status;
    private Integer totalCount;
    private Integer successCount;
    private String errorMessage;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
