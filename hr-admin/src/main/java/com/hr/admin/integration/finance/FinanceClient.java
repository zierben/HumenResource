package com.hr.admin.integration.finance;

import com.hr.admin.integration.common.IntegrationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinanceClient {
    
    private final FinanceConfig financeConfig;
    
    public IntegrationResult<Map<String, Object>> pushProject(Object project) {
        if (!financeConfig.isEnabled()) {
            return IntegrationResult.error("业财系统对接未启用");
        }
        
        log.info("推送项目到业财系统: {}", financeConfig.getBaseUrl());
        
        return IntegrationResult.error("业财系统对接功能待开发");
    }
    
    public IntegrationResult<Map<String, Object>> pushExpense(Object expense) {
        if (!financeConfig.isEnabled()) {
            return IntegrationResult.error("业财系统对接未启用");
        }
        
        log.info("推送费用到业财系统: {}", financeConfig.getBaseUrl());
        
        return IntegrationResult.error("业财系统对接功能待开发");
    }
    
    public IntegrationResult<Map<String, Object>> pushBudget(Object budget) {
        if (!financeConfig.isEnabled()) {
            return IntegrationResult.error("业财系统对接未启用");
        }
        
        log.info("推送预算到业财系统: {}", financeConfig.getBaseUrl());
        
        return IntegrationResult.error("业财系统对接功能待开发");
    }
}
