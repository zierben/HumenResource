# 操作日志模块 (Logs)

> 最后更新: 2026-03-13

## 基本信息

| 属性 | 值 |
|------|-----|
| 路由 | /logs |
| 前端文件 | hr-web/src/views/Logs.vue |
| 后端Controller | HrOperationLogController.java |
| 完成度 | **90%** |

## 功能清单

| 功能 | 前端 | 后端 | 测试 | 状态 |
|------|------|------|------|------|
| 日志列表(分页) | ✅ | ✅ | ❌ | 完成 |
| 搜索过滤(模块) | ✅ | ✅ | ❌ | 完成 |
| 搜索过滤(操作) | ✅ | ✅ | ❌ | 完成 |
| 搜索过滤(时间) | ✅ | ✅ | ❌ | 完成 |
| 日志详情查看 | ✅ | ✅ | ❌ | 完成 |
| 自动记录操作 | ✅ | ✅ | ❌ | 完成 |

## 测试覆盖

| 测试文件 | 测试数 | 状态 |
|----------|--------|------|
| - | 0 | ❌ 缺失 |

## 注解使用

```java
@OperationLog(module = "人员管理", action = "新增人员", targetType = "人员")
public Result<Void> save(@RequestBody HrPersonnel personnel) { ... }
```

## 日志字段

| 字段 | 说明 |
|------|------|
| module | 模块名称 |
| action | 操作动作 |
| targetType | 目标类型 |
| targetId | 目标ID |
| operator | 操作人 |
| operateTime | 操作时间 |
| detail | 操作详情 |
