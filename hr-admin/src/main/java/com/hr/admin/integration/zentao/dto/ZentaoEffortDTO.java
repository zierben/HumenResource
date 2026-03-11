package com.hr.admin.integration.zentao.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ZentaoEffortDTO {
    private Long id;
    private Long account;
    private Long project;
    private String date;
    private BigDecimal hours;
    private String work;
}
