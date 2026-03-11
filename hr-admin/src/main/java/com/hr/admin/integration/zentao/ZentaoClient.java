package com.hr.admin.integration.zentao;

import com.hr.admin.integration.common.IntegrationResult;
import com.hr.admin.integration.zentao.dto.ZentaoUserDTO;
import com.hr.admin.integration.zentao.dto.ZentaoProjectDTO;
import com.hr.admin.integration.zentao.dto.ZentaoEffortDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZentaoClient {
    
    private final ZentaoConfig zentaoConfig;
    
    public IntegrationResult<List<ZentaoUserDTO>> getUsers() {
        if (!zentaoConfig.isEnabled()) {
            return IntegrationResult.error("禅道同步未启用");
        }
        
        log.info("调用禅道API获取用户列表: {}", zentaoConfig.getBaseUrl());
        
        return IntegrationResult.success(List.of());
    }
    
    public IntegrationResult<List<ZentaoProjectDTO>> getProjects() {
        if (!zentaoConfig.isEnabled()) {
            return IntegrationResult.error("禅道同步未启用");
        }
        
        log.info("调用禅道API获取项目列表: {}", zentaoConfig.getBaseUrl());
        
        return IntegrationResult.success(List.of());
    }
    
    public IntegrationResult<List<ZentaoEffortDTO>> getEfforts(String date) {
        if (!zentaoConfig.isEnabled()) {
            return IntegrationResult.error("禅道同步未启用");
        }
        
        log.info("调用禅道API获取工时数据: {}, date={}", zentaoConfig.getBaseUrl(), date);
        
        return IntegrationResult.success(List.of());
    }
}
