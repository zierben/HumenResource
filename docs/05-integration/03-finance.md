# 03 - 业财预留

## 配置项

```yaml
finance:
  enabled: false
  base-url: https://your-finance-system.com/api
  app-key: your-app-key
  app-secret: your-app-secret
```

## 组件

| 组件 | 说明 |
|------|------|
| FinanceConfig | 配置类 |
| FinanceClient | API 客户端（预留接口） |
| FinanceSyncService | 同步服务（预留接口） |

## 预留功能

- pushProject：推送项目信息
- pushExpense：推送费用信息
- pushBudget：推送预算信息

## 待对接

1. 确认业财系统 API 文档
2. 实现认证和数据格式转换
3. 实现推送逻辑
