package com.hr.admin.integration.zentao.dto;

import lombok.Data;

@Data
public class ZentaoUserDTO {
    private Long id;
    private String account;
    private String realname;
    private String email;
    private String phone;
}
