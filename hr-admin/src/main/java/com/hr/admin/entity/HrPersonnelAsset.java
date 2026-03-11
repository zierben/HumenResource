package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("hr_personnel_asset")
public class HrPersonnelAsset {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("personnel_id")
    private Long personnelId;
    
    private String assetName;
    private String assetCode;
    private LocalDate receiveDate;
    private LocalDate returnDate;
    private Integer status;
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
