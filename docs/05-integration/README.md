# 05 - 集成层框架

## 模块目标

封装外部系统对接逻辑，提供统一的数据同步入口，支持禅道、业财系统的数据交换。

---

## 架构设计

```
integration/
├── common/
│   ├── IntegrationResult.java      # 统一返回结果
│   └── SyncType.java               # 同步类型枚举
├── zentao/
│   ├── ZentaoConfig.java           # 禅道配置
│   ├── ZentaoClient.java           # 禅道API客户端
│   ├── ZentaoSyncService.java      # 禅道同步服务
│   └── dto/
│       ├── ZentaoUserDTO.java
│       ├── ZentaoProjectDTO.java
│       └── ZentaoEffortDTO.java
└── finance/
    ├── FinanceConfig.java          # 业财配置（预留）
    ├── FinanceClient.java          # 业财客户端（预留）
    └── FinanceSyncService.java     # 业财同步服务（预留）
```

---

## 同步类型

| 类型 | 编码 | 说明 |
|------|------|------|
| 禅道人员 | ZENTAO_USER | 从禅道同步人员信息 |
| 禅道项目 | ZENTAO_PROJECT | 从禅道同步项目信息 |
| 禅道工时 | ZENTAO_WORKHOURS | 从禅道同步工时数据 |

---

## 调用流程

1. 前端调用 `/api/sync/execute` 触发同步
2. SyncController 调用对应的 SyncService
3. SyncService 调用 Client 获取外部数据
4. 转换数据并保存到本地数据库
5. 记录同步日志

---

## 子模块

| 子模块 | 说明 | 文档 |
|--------|------|------|
| 通用组件 | 结果封装、枚举 | [01-common.md](01-common.md) |
| 禅道对接 | API客户端、同步服务 | [02-zentao.md](02-zentao.md) |
| 业财预留 | 接口定义 | [03-finance.md](03-finance.md) |

---

## 完成状态

✅ 已完成

**已完成内容：**
- integration 模块目录结构
- IntegrationResult 统一返回结果
- ZentaoConfig、ZentaoClient、ZentaoSyncService（Mock 实现）
- FinanceConfig、FinanceClient、FinanceSyncService（预留接口）
- 配置项添加到 application.yml
- SyncController 集成调用
