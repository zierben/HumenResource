package com.hr.admin.integration.zentao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("zt_user")
public class ZentaoUser {
    private Long id;
    private String account;
    private String password;
    private String realname;
    private String nickname;
    private String email;
    private String mobile;
    private String phone;
    private Integer gender;
    private Integer deleted;
    private String role;
    private LocalDateTime regTime;
    private Integer commits;
}
