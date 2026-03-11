-- HR外包人力管理系统数据库初始化脚本
-- 版本: 2.0
-- 更新: 2024-01-01 新增项目表、同步日志表、用户权限相关表

CREATE DATABASE IF NOT EXISTS hr_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hr_system;

-- =====================================================
-- 业务表
-- =====================================================

-- 项目表
CREATE TABLE IF NOT EXISTS hr_project (
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

-- 需求表
CREATE TABLE IF NOT EXISTS hr_requirement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    requirement_code VARCHAR(50) NOT NULL,
    project_name VARCHAR(100) NOT NULL,
    position_name VARCHAR(50) NOT NULL,
    position_level VARCHAR(20),
    skills TEXT,
    expected_entry_date DATE,
    service_months INT,
    daily_rate DECIMAL(10,2) NOT NULL,
    monthly_budget DECIMAL(12,2) NOT NULL,
    total_budget DECIMAL(12,2) NOT NULL,
    demand_count INT NOT NULL,
    demand_reason TEXT,
    work_content TEXT,
    project_roi VARCHAR(200),
    department_id BIGINT,
    project_id BIGINT,
    status INT NOT NULL DEFAULT 0,
    reject_reason TEXT,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='需求表';

-- 供应商表
CREATE TABLE IF NOT EXISTS hr_supplier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(50) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    contact_email VARCHAR(100),
    level VARCHAR(10) NOT NULL,
    delivery_rate INT DEFAULT 0,
    current_person_count INT DEFAULT 0,
    status INT DEFAULT 1,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 人员表
CREATE TABLE IF NOT EXISTS hr_personnel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    personnel_code VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL,
    id_card VARCHAR(18),
    phone VARCHAR(20),
    email VARCHAR(100),
    position_name VARCHAR(50) NOT NULL,
    position_level VARCHAR(20),
    daily_rate DECIMAL(10,2) NOT NULL,
    supplier_id BIGINT NOT NULL,
    project_id BIGINT,
    requirement_id BIGINT,
    status VARCHAR(20) NOT NULL,
    entry_date DATE,
    contract_start_date DATE,
    contract_end_date DATE,
    zentao_user_id BIGINT COMMENT '禅道用户ID',
    zentao_account VARCHAR(50) COMMENT '禅道登录账号',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    deleted INT DEFAULT 0,
    UNIQUE KEY uk_zentao_user_id (zentao_user_id),
    UNIQUE KEY uk_zentao_account (zentao_account)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员表';

-- 工时表
CREATE TABLE IF NOT EXISTS hr_work_hours (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    personnel_id BIGINT NOT NULL,
    project_id BIGINT,
    work_date DATE NOT NULL,
    hours DECIMAL(5,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    remark TEXT,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工时表';

-- 结算表
CREATE TABLE IF NOT EXISTS hr_settlement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    settlement_code VARCHAR(50) NOT NULL,
    settlement_month INT NOT NULL,
    settlement_year INT NOT NULL,
    supplier_id BIGINT NOT NULL,
    project_id BIGINT,
    total_amount DECIMAL(12,2) NOT NULL,
    valid_days DECIMAL(5,2) NOT NULL,
    deduction_amount DECIMAL(12,2) DEFAULT 0.00,
    final_amount DECIMAL(12,2) NOT NULL,
    status INT NOT NULL DEFAULT 0,
    confirm_remark TEXT,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    deleted INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='结算表';

-- 同步日志表
CREATE TABLE IF NOT EXISTS hr_sync_log (
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

-- =====================================================
-- 系统表
-- =====================================================

-- 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
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

-- 菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
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

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(20) NOT NULL COMMENT '角色: ADMIN/HR',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 菜单数据
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
(10, 8, '数据同步', 'sync', '/settings/sync', 'Refresh', 2, 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- ADMIN 拥有所有菜单
INSERT INTO sys_role_menu (role, menu_id, create_time)
SELECT 'ADMIN', id, NOW() FROM sys_menu
ON DUPLICATE KEY UPDATE role = VALUES(role);

-- HR 拥有业务菜单(不含系统设置)
INSERT INTO sys_role_menu (role, menu_id, create_time) VALUES
('HR', 1, NOW()),
('HR', 2, NOW()),
('HR', 3, NOW()),
('HR', 4, NOW()),
('HR', 5, NOW()),
('HR', 6, NOW()),
('HR', 7, NOW())
ON DUPLICATE KEY UPDATE role = VALUES(role);

-- 默认用户 (密码: 123456)
INSERT INTO sys_user (username, password, real_name, role, status, create_time, update_time) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'ADMIN', 1, NOW(), NOW()),
('hr001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张HR', 'HR', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE real_name = VALUES(real_name);

-- 项目数据
INSERT INTO hr_project (project_code, project_name, department, manager, budget, status, create_time, update_time) VALUES
('PRJ2024001', '核心交易系统升级', '研发部', '李明', 500000, 1, NOW(), NOW()),
('PRJ2024002', '移动端APP重构', '研发部', '王强', 300000, 1, NOW(), NOW()),
('PRJ2024003', '数据分析平台', '数据部', '张伟', 400000, 1, NOW(), NOW()),
('PRJ2024004', '客服系统优化', '产品部', '刘洋', 200000, 1, NOW(), NOW()),
('PRJ2024005', '运维监控平台', '运维部', '陈刚', 250000, 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE project_name = VALUES(project_name);

-- 供应商数据
INSERT INTO hr_supplier (supplier_name, contact_person, contact_phone, contact_email, level, delivery_rate, current_person_count, status, create_time, update_time) VALUES
('华软科技有限公司', '张经理', '13800138001', 'zhang@huaruan.com', 'A', 95, 45, 1, NOW(), NOW()),
('智联外包服务有限公司', '李经理', '13800138002', 'li@zhilian.com', 'A', 92, 38, 1, NOW(), NOW()),
('博达信息技术有限公司', '王经理', '13800138003', 'wang@boda.com', 'B', 85, 28, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE supplier_name = VALUES(supplier_name);

-- 需求数据
INSERT INTO hr_requirement (requirement_code, project_name, position_name, position_level, skills, expected_entry_date, service_months, daily_rate, monthly_budget, total_budget, demand_count, demand_reason, work_content, department_id, project_id, status, create_time, update_time) VALUES
('RQ202401001', '核心交易系统升级', 'Java高级工程师', 'P7', 'Java,Spring Boot,MySQL,分布式', '2024-02-01', 12, 1200, 26400, 316800, 2, '系统架构升级，需要资深Java开发', '负责核心交易模块的设计与开发', 1, 1, 4, NOW(), NOW()),
('RQ202401002', '移动端APP重构', '前端开发工程师', 'P6', 'Vue3,TypeScript,移动端开发', '2024-02-15', 6, 900, 19800, 118800, 3, 'APP重构项目启动', '负责移动端页面开发', 1, 2, 4, NOW(), NOW()),
('RQ202401003', '数据分析平台', '数据分析师', 'P6', 'Python,SQL,数据分析', '2024-03-01', 8, 1000, 22000, 176000, 2, '数据平台建设需要专业人员', '数据建模与分析报告', 1, 3, 2, NOW(), NOW()),
('RQ202401004', '客服系统优化', '测试工程师', 'P5', '自动化测试,接口测试', '2024-02-20', 4, 700, 15400, 61600, 2, '系统优化需要测试支持', '功能测试与自动化测试', 1, 4, 1, NOW(), NOW()),
('RQ202401005', '核心交易系统升级', '产品经理', 'P7', '产品规划,需求分析,金融行业', '2024-01-15', 12, 1500, 33000, 396000, 1, '需要资深产品经理把控方向', '产品规划与需求管理', 1, 1, 5, NOW(), NOW())
ON DUPLICATE KEY UPDATE requirement_code = VALUES(requirement_code);

-- 人员数据
INSERT INTO hr_personnel (personnel_code, name, id_card, phone, email, position_name, position_level, daily_rate, supplier_id, project_id, requirement_id, status, entry_date, contract_start_date, contract_end_date, zentao_account, create_time, update_time) VALUES
('PER202401001', '张伟', '310101199001011234', '13900139001', 'zhangwei@example.com', 'Java高级工程师', 'P7', 1200, 1, 1, 1, 'ON_DUTY', '2024-01-15', '2024-01-15', '2025-01-14', 'zhangwei', NOW(), NOW()),
('PER202401002', '李娜', '310101199102022345', '13900139002', 'lina@example.com', 'Java高级工程师', 'P7', 1150, 1, 1, 1, 'ON_DUTY', '2024-01-20', '2024-01-20', '2025-01-19', 'lina', NOW(), NOW()),
('PER202401003', '王磊', '310101199203033456', '13900139003', 'wanglei@example.com', '前端开发工程师', 'P6', 900, 1, 2, 2, 'ON_DUTY', '2024-02-01', '2024-02-01', '2024-07-31', 'wanglei', NOW(), NOW()),
('PER202401004', '赵敏', '310101199304044567', '13900139004', 'zhaomin@example.com', '前端开发工程师', 'P6', 880, 2, 2, 2, 'ON_DUTY', '2024-02-10', '2024-02-10', '2024-08-09', 'zhaomin', NOW(), NOW()),
('PER202401005', '钱进', '310101199405055678', '13900139005', 'qianjin@example.com', '前端开发工程师', 'P5', 750, 2, 2, 2, 'ON_DUTY', '2024-02-15', '2024-02-15', '2024-08-14', 'qianjin', NOW(), NOW()),
('PER202401006', '孙华', '310101199506066789', '13900139006', 'sunhua@example.com', '数据分析师', 'P6', 1000, 2, 3, 3, 'PENDING_ENTRY', NULL, '2024-03-01', '2024-10-31', 'sunhua', NOW(), NOW()),
('PER202401007', '周杰', '310101199607077890', '13900139007', 'zhoujie@example.com', '数据分析师', 'P5', 850, 3, 3, 3, 'PENDING_ENTRY', NULL, '2024-03-01', '2024-10-31', 'zhoujie', NOW(), NOW()),
('PER202401008', '吴芳', '310101199708088901', '13900139008', 'wufang@example.com', '测试工程师', 'P5', 700, 3, 4, 4, 'ON_DUTY', '2024-02-20', '2024-02-20', '2024-06-19', 'wufang', NOW(), NOW()),
('PER202401009', '郑凯', '310101199809099012', '13900139009', 'zhengkai@example.com', '测试工程师', 'P5', 680, 1, 4, 4, 'ON_DUTY', '2024-02-22', '2024-02-22', '2024-06-21', 'zhengkai', NOW(), NOW()),
('PER202401010', '陈晨', '310101199910101123', '13900139010', 'chenchen@example.com', '产品经理', 'P7', 1500, 1, 1, 5, 'OFF_DUTY', '2024-01-10', '2024-01-10', '2025-01-09', 'chenchen', NOW(), NOW()),
('PER202401011', '林涛', '310101200001011234', '13900139011', 'lintao@example.com', 'Java中级工程师', 'P6', 950, 2, 1, 1, 'ON_DUTY', '2024-02-01', '2024-02-01', '2025-01-31', 'lintao', NOW(), NOW()),
('PER202401012', '黄丽', '310101200102022345', '13900139012', 'huangli@example.com', 'UI设计师', 'P5', 800, 3, 2, 2, 'ON_DUTY', '2024-02-05', '2024-02-05', '2024-08-04', 'huangli', NOW(), NOW()),
('PER202401013', '杨帆', '310101200203033456', '13900139013', 'yangfan@example.com', '运维工程师', 'P6', 900, 2, 5, NULL, 'LEAVE', '2024-01-05', '2024-01-05', '2024-07-04', 'yangfan', NOW(), NOW()),
('PER202401014', '徐明', '310101200304044567', '13900139014', 'xuming@example.com', 'Java高级工程师', 'P7', 1100, 1, 1, 1, 'TRANSFER', '2024-01-20', '2024-01-20', '2025-01-19', 'xuming', NOW(), NOW()),
('PER202401015', '马超', '310101200405055678', '13900139015', 'machao@example.com', '前端开发工程师', 'P6', 860, 3, NULL, NULL, 'PENDING_ENTRY', NULL, '2024-03-01', '2024-08-31', 'machao', NOW(), NOW())
ON DUPLICATE KEY UPDATE personnel_code = VALUES(personnel_code);

-- 工时数据
INSERT INTO hr_work_hours (personnel_id, project_id, work_date, hours, status, remark, create_time, update_time) VALUES
(1, 1, '2024-01-15', 8, 'APPROVED', NULL, NOW(), NOW()),
(1, 1, '2024-01-16', 8, 'APPROVED', NULL, NOW(), NOW()),
(1, 1, '2024-01-17', 8, 'APPROVED', NULL, NOW(), NOW()),
(1, 1, '2024-01-18', 8, 'APPROVED', NULL, NOW(), NOW()),
(1, 1, '2024-01-19', 8, 'APPROVED', NULL, NOW(), NOW()),
(2, 1, '2024-01-20', 8, 'APPROVED', NULL, NOW(), NOW()),
(2, 1, '2024-01-21', 8, 'APPROVED', NULL, NOW(), NOW()),
(2, 1, '2024-01-22', 8, 'APPROVED', NULL, NOW(), NOW()),
(2, 1, '2024-01-23', 8, 'PENDING', NULL, NOW(), NOW()),
(3, 2, '2024-01-15', 8, 'APPROVED', NULL, NOW(), NOW()),
(3, 2, '2024-01-16', 8, 'APPROVED', NULL, NOW(), NOW()),
(3, 2, '2024-01-17', 4, 'APPROVED', '下午请假', NOW(), NOW()),
(11, 1, '2024-01-15', 8, 'APPROVED', NULL, NOW(), NOW()),
(11, 1, '2024-01-16', 8, 'APPROVED', NULL, NOW(), NOW());

-- 结算数据
INSERT INTO hr_settlement (settlement_code, settlement_month, settlement_year, supplier_id, project_id, total_amount, valid_days, deduction_amount, final_amount, status, confirm_remark, create_time, update_time) VALUES
('SET202401001', 1, 2024, 1, 1, 158400, 22, 0, 158400, 5, '已支付', NOW(), NOW()),
('SET202401002', 1, 2024, 2, 2, 79200, 22, 2400, 76800, 3, NULL, NOW(), NOW()),
('SET202401003', 1, 2024, 3, 4, 30800, 22, 0, 30800, 1, NULL, NOW(), NOW());
