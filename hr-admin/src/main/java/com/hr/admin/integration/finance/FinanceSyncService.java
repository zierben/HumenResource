package com.hr.admin.integration.finance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceSyncService {
    
    private final FinanceClient financeClient;
    
    public void pushProject(Long projectId) {
        log.info("推送项目到业财系统: projectId={}", projectId);
    }
    
    public void pushExpense(Long expenseId) {
        log.info("推送费用到业财系统: expenseId={}", expenseId);
    }
    
    public void pushBudget(Long budgetId) {
        log.info("推送预算到业财系统: budgetId={}", budgetId);
    }
}
