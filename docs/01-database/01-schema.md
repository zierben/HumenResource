# 01 - 表结构设计

## 新增表 SQL

### 1. 项目表 hr_project

```sql
CREATE TABLE hr_project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_code VARCHAR(50) COMMENT '项目编号',
    project_name VARCHAR(100) NOT NULL COMMENT '项目名称',
    department VARCHAR(100) COMMENT '所属部门',
    manager VARCHAR(50) COMMENT '项目经理',
    budget DECIMAL(12,2) DEFAULT 0 COMMENT '项目预算',
    zentao_project_id BIGINT COMMENT '禅道项目ID映射',
    status INT DEFAULT 1 COMMENT '状态 1-进行中 0-已结束',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';
```

### 2. 同步日志表 hr_sync_log

```sql
CREATE TABLE hr_sync_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sync_type VARCHAR(50) NOT NULL COMMENT '同步类型: ZENTAO_USER/ZENTAO_PROJECT/ZENTAO_WORKHOURS',
    sync_direction VARCHAR(20) NOT NULL COMMENT '同步方向: IMPORT/EXPORT',
    status VARCHAR(20) NOT NULL COMMENT '状态: SUCCESS/FAILED/RUNNING',
    total_count INT DEFAULT 0 COMMENT '总记录数',
    success_count INT DEFAULT 0 COMMENT '成功数',
    error_message TEXT COMMENT '错误信息',
    started_at DATETIME NOT NULL COMMENT '开始时间',
    finished_at DATETIME COMMENT '结束时间',
    create_time DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='同步日志表';
```

### 3. 系统用户表 sys_user

```sql
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL COMMENT '角色: ADMIN/HR',
    status INT DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';
```

### 4. 菜单表 sys_menu

```sql
CREATE TABLE sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    menu_code VARCHAR(50) NOT NULL COMMENT '菜单编码',
    path VARCHAR(100) COMMENT '路由路径',
    icon VARCHAR(50) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    menu_type INT DEFAULT 1 COMMENT '类型 1-菜单 2-按钮',
    visible INT DEFAULT 1 COMMENT '是否可见 1-是 0-否',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';
```

### 5. 角色菜单关联表 sys_role_menu

```sql
CREATE TABLE sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(20) NOT NULL COMMENT '角色: ADMIN/HR',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';
```

---

## 菜单初始数据

```sql
INSERT INTO sys_menu (id, parent_id, menu_name, menu_code, path, icon, sort_order, menu_type, visible, create_time, update_time) VALUES
(1, 0, '仪表盘', 'dashboard', '/dashboard', 'DataAnalysis', 1, 1, 1, NOW(), NOW()),
(2, 0, '需求管理', 'requirements', '/requirements', 'Document', 2, 1, 1, NOW(), NOW()),
(3, 0, '供应商管理', 'suppliers', '/suppliers', 'OfficeBuilding', 3, 1, 1, NOW(), NOW()),
(4, 0, '人员管理', 'personnel', '/personnel', 'User', 4, 1, 1, NOW(), NOW()),
(5, 0, '工时管理', 'workhours', '/workhours', 'Clock', 5, 1, 1, NOW(), NOW()),
(6, 0, '结算管理', 'settlements', '/settlements', 'Money', 6, 1, 1, NOW(), NOW()),
(7, 0, '报表中心', 'reports', '/reports', 'PieChart', 7, 1, 1, NOW(), NOW()),
(8, 0, '系统设置', 'settings', '/settings', 'Setting', 8, 1, 1, NOW(), NOW()),
(9, 8, '用户管理', 'users', '/settings/users', 'User', 1, 1, 1, NOW(), NOW()),
(10, 8, '数据同步', 'sync', '/settings/sync', 'Refresh', 2, 1, 1, NOW(), NOW());
```

---

## 角色菜单权限

```sql
-- ADMIN 拥有所有菜单
INSERT INTO sys_role_menu (role, menu_id, create_time)
SELECT 'ADMIN', id, NOW() FROM sys_menu;

-- HR 拥有业务菜单(不含系统设置)
INSERT INTO sys_role_menu (role, menu_id, create_time) VALUES
('HR', 1, NOW()),
('HR', 2, NOW()),
('HR', 3, NOW()),
('HR', 4, NOW()),
('HR', 5, NOW()),
('HR', 6, NOW()),
('HR', 7, NOW());
```

---

## 默认用户

```sql
-- 密码为 123456 的 BCrypt 加密值
INSERT INTO sys_user (username, password, real_name, role, status, create_time, update_time) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'ADMIN', 1, NOW(), NOW()),
('hr001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张HR', 'HR', 1, NOW(), NOW());
```
