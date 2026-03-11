package com.hr.admin.integration.zentao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("zt_effort")
public class ZentaoEffort {
    private Long id;
    private Integer objectType;
    private Long objectID;
    private String work;
    private String date;
    private BigDecimal consumed;
    private BigDecimal left;
    private String account;
    private LocalDateTime editedDate;
    private Long project;
    private Long execution;
    private Long task;
}
