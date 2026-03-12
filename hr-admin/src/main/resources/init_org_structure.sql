-- 组织架构初始化脚本
-- 4层架构：L1总经理 L2副总经理 L3部门长 L4项目经理/专员

USE hr_system;

-- 清理旧用户数据（保留admin）
DELETE FROM sys_user WHERE id > 1;

-- 重置自增ID
ALTER TABLE sys_user AUTO_INCREMENT = 2;

-- 插入完整的组织架构
INSERT INTO sys_user (id, username, password, real_name, role, status, department, position, level, manager_id, create_time, update_time, deleted) VALUES
-- L1 总经理 (无上级)
(2, 'ceo', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '张伟华', 'ADMIN', 1, '公司', '总经理', 1, NULL, NOW(), NOW(), 0),

-- L2 副总经理 (上级：总经理)
(3, 'vp_tech', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '李明强', 'ADMIN', 1, '公司', '技术副总', 2, 2, NOW(), NOW(), 0),
(4, 'vp_hr', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '王晓红', 'ADMIN', 1, '公司', '人力副总', 2, 2, NOW(), NOW(), 0),
(5, 'vp_finance', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '赵建国', 'ADMIN', 1, '公司', '财务副总', 2, 2, NOW(), NOW(), 0),

-- L3 部门长 (上级：对应副总)
(6, 'dept_dev', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '陈志远', 'HR', 1, '研发部', '研发总监', 3, 3, NOW(), NOW(), 0),
(7, 'dept_test', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '刘芳', 'HR', 1, '测试部', '测试总监', 3, 3, NOW(), NOW(), 0),
(8, 'hr001', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '孙丽娜', 'HR', 1, '人力资源部', 'HR总监', 3, 4, NOW(), NOW(), 0),
(9, 'dept_finance', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '周敏', 'HR', 1, '财务部', '财务总监', 3, 5, NOW(), NOW(), 0),

-- L4 项目经理/专员 (上级：部门长)
(10, 'pm_erp', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '吴鹏', 'HR', 1, '研发部', 'ERP项目经理', 4, 6, NOW(), NOW(), 0),
(11, 'pm_app', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '郑涛', 'HR', 1, '研发部', 'APP项目经理', 4, 6, NOW(), NOW(), 0),
(12, 'pm_data', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '黄磊', 'HR', 1, '研发部', '数据平台经理', 4, 6, NOW(), NOW(), 0),
(13, 'pm_test', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '林静', 'HR', 1, '测试部', '测试经理', 4, 7, NOW(), NOW(), 0),
(14, 'dev_zhang', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '张明', 'HR', 1, '研发部', '高级开发工程师', 4, 10, NOW(), NOW(), 0),
(15, 'dev_li', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '李华', 'HR', 1, '研发部', '开发工程师', 4, 10, NOW(), NOW(), 0),
(16, 'dev_wang', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '王强', 'HR', 1, '研发部', '前端工程师', 4, 11, NOW(), NOW(), 0),
(17, 'dev_zhao', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '赵敏', 'HR', 1, '研发部', '后端工程师', 4, 11, NOW(), NOW(), 0),
(18, 'tester_chen', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '陈晓', 'HR', 1, '测试部', '测试工程师', 4, 13, NOW(), NOW(), 0),
(19, 'hr_zhou', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '周婷', 'HR', 1, '人力资源部', 'HR专员', 4, 8, NOW(), NOW(), 0),
(20, 'hr_wu', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '吴佳', 'HR', 1, '人力资源部', '招聘专员', 4, 8, NOW(), NOW(), 0),
(21, 'finance_liu', '$2a$10$iQguAEhmCHx7REjBYF5l4O2g0JlqRzQh5FL5UuvifZYyUQ96H6hAW', '刘芳', 'HR', 1, '财务部', '会计', 4, 9, NOW(), NOW(), 0);

-- 更新admin用户（不参与组织架构树，level设为0）
UPDATE sys_user SET department = NULL, position = '系统管理员', level = 0, manager_id = NULL WHERE id = 1;

-- 验证结果
SELECT id, username, real_name, department, position, level, manager_id FROM sys_user WHERE deleted = 0 ORDER BY level, id;
