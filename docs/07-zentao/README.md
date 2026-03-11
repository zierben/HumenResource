# 07 - 禅道对接模块

## 模块目标

通过数据库直连方式，从禅道数据库同步人员、项目、工时数据。

---

## 对接方式

**数据库直连**（禅道企业版 4.5 不支持 REST API）

---

## 配置

```yaml
spring:
  datasource:
    dynamic:
      primary: master
      datasource:
        master:
          # HR系统数据库
          url: jdbc:mysql://localhost:3306/hr_system
          username: root
          password: root
        zentao:
          # 禅道数据库
          url: jdbc:mysql://localhost:3306/zentao
          username: root
          password: root

zentao:
  enabled: true
  sync-days: 30
```

---

## 禅道表映射

| 禅道表 | HR表 | 映射字段 | 说明 |
|--------|------|---------|------|
| zt_user | hr_personnel | zt_user.id → hr_personnel.zentao_user_id | 用户ID映射（主键，唯一） |
| zt_user | hr_personnel | zt_user.account → hr_personnel.zentao_account | 登录账号（备用） |
| zt_project | hr_project | zt_project.id → hr_project.zentao_project_id | 项目ID映射（唯一） |
| zt_effort | hr_work_hours | zt_effort.account → hr_personnel.zentao_account | 工时关联人员 |

---

## 映射约束

**数据库唯一索引：**
```sql
UNIQUE KEY uk_zentao_user_id (zentao_user_id)
UNIQUE KEY uk_zentao_account (zentao_account)
```

**映射规则：**
1. **人员同步**：用 `zt_user.id`（禅道用户ID）作为主键映射，避免姓名重复问题
2. **工时同步**：通过 `zt_effort.account` 找到对应人员，再关联工时
3. **项目同步**：用 `zt_project.id` 作为主键映射

**同步顺序：**
1. 先同步人员 → 建立 zentao_user_id 映射
2. 再同步项目 → 建立 zentao_project_id 映射
3. 最后同步工时 → 关联人员和项目

---

## 同步逻辑

### 人员同步 (ZENTAO_USER)
1. 查询 `zt_user` 表未删除用户
2. 根据 `zt_user.id` 匹配 `hr_personnel.zentao_user_id`
3. 存在则更新，不存在则新增
4. 同时保存 `zt_user.account` 到 `hr_personnel.zentao_account`

### 项目同步 (ZENTAO_PROJECT)
1. 查询 `zt_project` 表未删除项目
2. 根据 `zt_project.id` 匹配 `hr_project.zentao_project_id`
3. 存在则更新，不存在则新增

### 工时同步 (ZENTAO_WORKHOURS)
1. 查询 `zt_effort` 表指定日期数据
2. 根据 `zt_effort.account` 匹配 `hr_personnel.zentao_account` 找到人员
3. 根据 `zt_effort.project` 匹配 `hr_project.zentao_project_id` 找到项目
4. 存在则更新，不存在则新增
5. **注意**：工时同步前必须先同步人员和项目

---

## 组件清单

| 组件 | 说明 |
|------|------|
| ZentaoConfig | 配置类 |
| ZentaoUser/Project/Effort | 禅道实体类 |
| ZentaoUserMapper | 用户 Mapper（@DS("zentao")） |
| ZentaoProjectMapper | 项目 Mapper（@DS("zentao")） |
| ZentaoEffortMapper | 工时 Mapper（@DS("zentao")） |
| ZentaoSyncService | 同步服务 |

---

## 完成状态

✅ 已完成

**已完成内容：**
- 多数据源配置（dynamic-datasource）
- 禅道实体类（ZentaoUser、ZentaoProject、ZentaoEffort）
- 禅道 Mapper（带 @DS 注解切换数据源）
- 同步服务（完整同步逻辑）
- 配置文档更新

**待配置：**
- 禅道数据库连接信息
