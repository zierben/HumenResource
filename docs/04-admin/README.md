# 04 - 管理后台模块

## 模块目标

提供系统管理功能，包括用户管理和数据同步管理。

---

## 功能清单

| 功能 | 页面 | 说明 |
|------|------|------|
| 用户管理 | /settings/users | 用户CRUD、重置密码 |
| 数据同步 | /settings/sync | 禅道同步、业财预留、同步日志 |

---

## 用户管理

### 功能
- 用户列表查询
- 添加用户
- 编辑用户
- 删除用户
- 重置密码（默认 123456）

### API
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/users | GET | 用户列表 |
| /api/users/{id} | GET | 用户详情 |
| /api/users | POST | 添加用户 |
| /api/users/{id} | PUT | 更新用户 |
| /api/users/{id} | DELETE | 删除用户 |
| /api/users/{id}/reset-password | PUT | 重置密码 |

---

## 数据同步

### 功能
- 禅道同步（人员、项目、工时）
- 业财系统（预留接口）
- 同步日志查看
- 失败重试

### API
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/sync/logs | GET | 同步日志列表 |
| /api/sync/execute | POST | 执行同步 |
| /api/sync/retry/{id} | POST | 重试同步 |

---

## 完成状态

✅ 已完成

**已完成内容：**
- 后端：SysUserController、SyncController
- 前端：Users.vue、Sync.vue
- 用户CRUD、密码重置
- 同步日志列表、执行同步、重试功能
