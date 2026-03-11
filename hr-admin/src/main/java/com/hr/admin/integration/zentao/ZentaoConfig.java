package com.hr.admin.integration.zentao;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "zentao")
public class ZentaoConfig {
    private boolean enabled = false;
    private String baseUrl;
    private String token;
    private String username;
    private String password;
}
