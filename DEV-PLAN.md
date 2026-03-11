# HR外包人力管理系统 - 开发计划

## 项目概述

HR外包人力管理系统，支持人力外包的全流程管理，包括需求、供应商、人员、工时、结算等模块，并与禅道、业财系统对接。

---

## 技术栈

- **前端**: Vue 3 + Vite + Element Plus + Pinia
- **后端**: Spring Boot 3.2 + MyBatis-Plus + MySQL
- **认证**: JWT

---

## 模块清单

| 模块 | 说明文档 | 状态 |
|------|---------|------|
| 01-数据库设计 | [docs/01-database/README.md](docs/01-database/README.md) | ✅ 已完成 |
| 02-用户认证 | [docs/02-auth/README.md](docs/02-auth/README.md) | ✅ 已完成 |
| 03-权限管理 | [docs/03-permission/README.md](docs/03-permission/README.md) | ✅ 已完成 |
| 04-管理后台 | [docs/04-admin/README.md](docs/04-admin/README.md) | ✅ 已完成 |
| 05-集成层框架 | [docs/05-integration/README.md](docs/05-integration/README.md) | ✅ 已完成 |
| 06-同步日志 | [docs/06-sync-log/README.md](docs/06-sync-log/README.md) | ✅ 已完成 |
| 07-禅道对接 | [docs/07-zentao/README.md](docs/07-zentao/README.md) | ✅ 已完成 |
| 08-业财预留 | [docs/08-finance/README.md](docs/08-finance/README.md) | ⏸️ 接口预留 |

---

## 开发规范

1. 每个模块必须在 `docs/模块名/` 下有 README.md 说明文档
2. 开发前必须阅读对应模块的 README.md
3. 模块过大时拆分子模块，每个子模块单独 README.md
4. 遵循 AGENTS.md 中的代码规范

---

## 目录结构

```
HR/
├── hr-admin/           # 后端
├── hr-web/             # 前端
├── docs/               # 开发文档
│   ├── 01-database/
│   ├── 02-auth/
│   ├── 03-permission/
│   ├── 04-admin/
│   ├── 05-integration/
│   ├── 06-sync-log/
│   ├── 07-zentao/
│   └── 08-finance/
├── AGENTS.md           # 代码规范
└── DEV-PLAN.md         # 本文件
```
