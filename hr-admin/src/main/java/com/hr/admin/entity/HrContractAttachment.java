package com.hr.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hr_contract_attachment")
public class HrContractAttachment {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("contract_id")
    private Long contractId;
    private String fileName;
    private String filePath;
    private String fileType;
    private LocalDateTime uploadTime;
}
