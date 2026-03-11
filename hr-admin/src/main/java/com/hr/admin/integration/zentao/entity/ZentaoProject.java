package com.hr.admin.integration.zentao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@TableName("zt_project")
public class ZentaoProject {
    private Long id;
    private String code;
    private String name;
    private String status;
    private String pm;
    private String desc;
    private Integer deleted;
    private LocalDateTime begin;
    private LocalDateTime end;
    private BigDecimal budget;
    private BigDecimal estimate;
    private BigDecimal consumed;
}
