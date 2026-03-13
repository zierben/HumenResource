-- 组织架构数据迁移脚本
-- 将现有用户数据迁移到新的部门架构

USE hr_system;

-- 清空部门表
DELETE FROM hr_department;

-- 创建默认的组织架构
-- L1: 总经理
INSERT INTO hr_department (id, dept_name, parent_id, manager_id, level, sort_order, status, create_time, update_time, deleted)
VALUES (1, '总经理', NULL, NULL, 1, 1, 1, NOW(), NOW(), 0);

-- L2: 副总
INSERT INTO hr_department (id, dept_name, parent_id, manager_id, level, sort_order, status, create_time, update_time, deleted)
VALUES 
(2, '技术副总', 1, NULL, 2, 1, 1, NOW(), NOW(), 0),
(3, '人力副总', 1, NULL, 2, 2, 1, NOW(), NOW(), 0),
(4, '财务副总', 1, NULL, 2, 3, 1, NOW(), NOW(), 0);

-- L3: 部门
INSERT INTO hr_department (id, dept_name, parent_id, manager_id, level, sort_order, status, create_time, update_time, deleted)
VALUES 
(5, '研发部', 2, NULL, 3, 1, 1, NOW(), NOW(), 0),
(6, '测试部', 2, NULL, 3, 2, 1, NOW(), NOW(), 0),
(7, '人力资源部', 3, NULL, 3, 1, 1, NOW(), NOW(), 0),
(8, '财务部', 4, NULL, 3, 1, 1, NOW(), NOW(), 0);

-- 更新用户的部门归属
-- 总经理
UPDATE sys_user SET dept_id = 1 WHERE username = 'ceo';

-- 副总
UPDATE sys_user SET dept_id = 2 WHERE username = 'vp_tech';
UPDATE sys_user SET dept_id = 3 WHERE username = 'vp_hr';
UPDATE sys_user SET dept_id = 4 WHERE username = 'vp_finance';

-- 研发部
UPDATE sys_user SET dept_id = 5 WHERE username IN ('dept_dev', 'pm_erp', 'pm_app', 'pm_data', 'dev_zhang', 'dev_li', 'dev_wang', 'dev_zhao');

-- 测试部
UPDATE sys_user SET dept_id = 6 WHERE username IN ('dept_test', 'pm_test', 'tester_chen');

-- 人力资源部
UPDATE sys_user SET dept_id = 7 WHERE username IN ('hr001', 'hr_zhou', 'hr_wu');

-- 财务部
UPDATE sys_user SET dept_id = 8 WHERE username = 'dept_finance';
UPDATE sys_user SET dept_id = 8 WHERE username = 'finance_liu';

-- 设置部门长
-- 研发部: dept_dev (陈志远)
UPDATE sys_user SET is_manager = 1 WHERE username = 'dept_dev';
UPDATE hr_department SET manager_id = (SELECT id FROM sys_user WHERE username = 'dept_dev') WHERE id = 5;

-- 测试部: dept_test (刘芳)
UPDATE sys_user SET is_manager = 1 WHERE username = 'dept_test';
UPDATE hr_department SET manager_id = (SELECT id FROM sys_user WHERE username = 'dept_test') WHERE id = 6;

-- 人力资源部: hr001 (孙丽娜)
UPDATE sys_user SET is_manager = 1 WHERE username = 'hr001';
UPDATE hr_department SET manager_id = (SELECT id FROM sys_user WHERE username = 'hr001') WHERE id = 7;

-- 财务部: dept_finance (周敏)
UPDATE sys_user SET is_manager = 1 WHERE username = 'dept_finance';
UPDATE hr_department SET manager_id = (SELECT id FROM sys_user WHERE username = 'dept_finance') WHERE id = 8;

-- 技术副总
UPDATE sys_user SET is_manager = 1 WHERE username = 'vp_tech';
UPDATE hr_department SET manager_id = (SELECT id FROM sys_user WHERE username = 'vp_tech') WHERE id = 2;

-- 人力副总
UPDATE sys_user SET is_manager = 1 WHERE username = 'vp_hr';
UPDATE hr_department SET manager_id = (SELECT id FROM sys_user WHERE username = 'vp_hr') WHERE id = 3;

-- 财务副总
UPDATE sys_user SET is_manager = 1 WHERE username = 'vp_finance';
UPDATE hr_department SET manager_id = (SELECT id FROM sys_user WHERE username = 'vp_finance') WHERE id = 4;

-- 总经理
UPDATE sys_user SET is_manager = 1 WHERE username = 'ceo';
UPDATE hr_department SET manager_id = (SELECT id FROM sys_user WHERE username = 'ceo') WHERE id = 1;

-- 验证迁移结果
SELECT '=== 部门结构 ===' AS info;
SELECT d.id, d.dept_name, d.level, d.parent_id, u.real_name AS manager_name
FROM hr_department d
LEFT JOIN sys_user u ON d.manager_id = u.id
WHERE d.deleted = 0
ORDER BY d.level, d.id;

SELECT '=== 用户部门归属 ===' AS info;
SELECT u.id, u.username, u.real_name, u.dept_id, d.dept_name, u.is_manager
FROM sys_user u
LEFT JOIN hr_department d ON u.dept_id = d.id
WHERE u.deleted = 0
ORDER BY u.dept_id, u.is_manager DESC;
