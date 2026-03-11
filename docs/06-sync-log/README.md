# 06 - 同步日志模块

## 模块目标

记录每次外部系统同步的执行情况，支持失败重试。

---

## 数据库表

```sql
CREATE TABLE hr_sync_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sync_type VARCHAR(50) NOT NULL,
    sync_direction VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_count INT DEFAULT 0,
    success_count INT DEFAULT 0,
    error_message TEXT,
    started_at DATETIME NOT NULL,
    finished_at DATETIME,
    create_time DATETIME NOT NULL
);
```

---

## 功能

| 功能 | 说明 |
|------|------|
| 记录同步开始 | startLog() |
| 记录同步结束 | finishLog() |
| 查看日志列表 | SyncController.logs() |
| 失败重试 | SyncController.retry() |

---

## 状态

| 状态 | 说明 |
|------|------|
| RUNNING | 执行中 |
| SUCCESS | 成功 |
| FAILED | 失败 |

---

## 完成状态

✅ 已完成

**已完成内容：**
- HrSyncLog 实体类
- HrSyncLogMapper
- HrSyncLogService（startLog、finishLog）
- SyncController 日志接口
- Sync.vue 日志展示页面
