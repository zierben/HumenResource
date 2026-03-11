package com.hr.admin.integration.finance;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "finance")
public class FinanceConfig {
    private boolean enabled = false;
    private String baseUrl;
    private String appKey;
    private String appSecret;
}
