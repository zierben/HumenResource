# 01 - 数据库设计

## 模块目标

设计并实现系统所需的全部数据库表结构，并提供完整的演示测试数据。

---

## 子模块

| 子模块 | 说明 | 文档 |
|--------|------|------|
| 01-表结构 | 新增表结构设计 | [01-schema.md](01-schema.md) |
| 02-测试数据 | 演示用 JSON 测试数据 | [02-test-data.md](02-test-data.md) |

---

## 现有表（已存在）

- `hr_requirement` - 需求表
- `hr_supplier` - 供应商表
- `hr_personnel` - 人员表
- `hr_work_hours` - 工时表
- `hr_settlement` - 结算表

---

## 新增表

| 表名 | 说明 |
|------|------|
| `hr_project` | 项目表 |
| `hr_sync_log` | 同步日志表 |
| `sys_user` | 系统用户表 |
| `sys_menu` | 菜单表 |
| `sys_role_menu` | 角色菜单关联表 |

---

## 开发顺序

1. 先阅读 [01-schema.md](01-schema.md) 理解表结构
2. 执行 SQL 创建新表
3. 阅读 [02-test-data.md](02-test-data.md) 准备测试数据
4. 提供测试数据 JSON 文件

---

## 完成状态

| 子模块 | 状态 |
|--------|------|
| 01-表结构 | ✅ 已完成 |
| 02-测试数据 | ✅ 已完成 |

**已完成内容：**
- 新增 5 张表：hr_project, hr_sync_log, sys_user, sys_menu, sys_role_menu
- 更新 schema.sql，包含完整初始化数据和测试数据
- 创建 Entity 类：HrProject, HrSyncLog, SysUser, SysMenu, SysRoleMenu
- 创建 Mapper 接口
- 创建 Service 接口和实现类
- 创建 HrProjectController
