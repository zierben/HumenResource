package com.hr.admin.integration.zentao.dto;

import lombok.Data;

@Data
public class ZentaoProjectDTO {
    private Long id;
    private String code;
    private String name;
    private String status;
    private String pm;
}
