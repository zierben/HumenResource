package com.hr.admin.dto;

import lombok.Data;

@Data
public class LoginVO {
    private String token;
    private String username;
    private String realName;
    private String role;
}
