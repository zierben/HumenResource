-- 更新角色系统和添加唯一约束
-- 角色说明: GM=总经理, VP=副总, DEPT_HEAD=部门长, PM=项目经理, HR=人事

-- 1. 清理没有关联人员的供应商和项目
-- 先查看哪些供应商没有关联人员
DELETE FROM hr_supplier WHERE id NOT IN (
    SELECT DISTINCT supplier_id FROM hr_personnel WHERE supplier_id IS NOT NULL
) AND id NOT IN (
    SELECT DISTINCT supplier_id FROM hr_contract WHERE supplier_id IS NOT NULL
);

-- 删除没有关联人员的项目
DELETE FROM hr_project WHERE id NOT IN (
    SELECT DISTINCT project_id FROM hr_personnel WHERE project_id IS NOT NULL
) AND id NOT IN (
    SELECT DISTINCT project_id FROM hr_work_hours WHERE project_id IS NOT NULL
) AND id NOT IN (
    SELECT DISTINCT project_id FROM hr_contract WHERE project_id IS NOT NULL
);

-- 2. 添加唯一约束（先删除重复数据）
-- 处理供应商名称重复
DELETE s1 FROM hr_supplier s1
INNER JOIN hr_supplier s2 
WHERE s1.id > s2.id AND s1.supplier_name = s2.supplier_name;

-- 处理项目名称重复
DELETE p1 FROM hr_project p1
INNER JOIN hr_project p2 
WHERE p1.id > p2.id AND p1.project_name = p2.project_name;

-- 添加唯一索引
ALTER TABLE hr_supplier ADD UNIQUE INDEX uk_supplier_name (supplier_name);
ALTER TABLE hr_project ADD UNIQUE INDEX uk_project_name (project_name);

-- 3. 更新角色系统
-- 更新菜单权限表 - 清除旧数据
DELETE FROM sys_role_menu;

-- 重新插入菜单权限
-- GM(总经理) 拥有所有菜单
INSERT INTO sys_role_menu (role, menu_id, create_time)
SELECT 'GM', id, NOW() FROM sys_menu;

-- VP(副总) 拥有除系统设置外的所有菜单
INSERT INTO sys_role_menu (role, menu_id, create_time)
SELECT 'VP', id, NOW() FROM sys_menu WHERE id < 8;

-- DEPT_HEAD(部门长) 拥有业务菜单
INSERT INTO sys_role_menu (role, menu_id, create_time) VALUES
('DEPT_HEAD', 1, NOW()),
('DEPT_HEAD', 2, NOW()),
('DEPT_HEAD', 4, NOW()),
('DEPT_HEAD', 5, NOW()),
('DEPT_HEAD', 6, NOW()),
('DEPT_HEAD', 7, NOW());

-- PM(项目经理) 拥有基础业务菜单
INSERT INTO sys_role_menu (role, menu_id, create_time) VALUES
('PM', 1, NOW()),
('PM', 4, NOW()),
('PM', 5, NOW()),
('PM', 7, NOW());

-- HR(人事) 拥有人事相关菜单
INSERT INTO sys_role_menu (role, menu_id, create_time) VALUES
('HR', 1, NOW()),
('HR', 2, NOW()),
('HR', 3, NOW()),
('HR', 4, NOW()),
('HR', 5, NOW()),
('HR', 6, NOW()),
('HR', 7, NOW());

-- 4. 更新用户数据 - 使用新角色
-- 清空旧用户
TRUNCATE TABLE sys_user;

-- 插入新用户 (密码: 123456)
INSERT INTO sys_user (username, password, real_name, role, status, department, position, level, manager_id, create_time, update_time) VALUES
-- L1 总经理
('gm001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张总', 'GM', 1, '总经办', '总经理', 1, NULL, NOW(), NOW()),

-- L2 副总经理
('vp001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李副总', 'VP', 1, '总经办', '副总经理', 2, 1, NOW(), NOW()),
('vp002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王副总', 'VP', 1, '总经办', '副总经理', 2, 1, NOW(), NOW()),

-- L3 部门长
('dept001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李明', 'DEPT_HEAD', 1, '研发部', '研发总监', 3, 2, NOW(), NOW()),
('dept002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张伟', 'DEPT_HEAD', 1, '数据部', '数据总监', 3, 2, NOW(), NOW()),
('dept003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '刘洋', 'DEPT_HEAD', 1, '产品部', '产品总监', 3, 3, NOW(), NOW()),
('dept004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '陈刚', 'DEPT_HEAD', 1, '运维部', '运维总监', 3, 3, NOW(), NOW()),
('dept005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵芳', 'DEPT_HEAD', 1, '人事部', '人事总监', 3, 2, NOW(), NOW()),

-- L4 项目经理/专员
('pm001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王强', 'PM', 1, '研发部', '项目经理', 4, 4, NOW(), NOW()),
('pm002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '周杰', 'PM', 1, '研发部', '项目经理', 4, 4, NOW(), NOW()),
('pm003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '钱进', 'PM', 1, '数据部', '项目经理', 4, 5, NOW(), NOW()),
('pm004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '孙华', 'PM', 1, '产品部', '项目经理', 4, 6, NOW(), NOW()),

-- HR 人事专员
('hr001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张HR', 'HR', 1, '人事部', '人事专员', 4, 8, NOW(), NOW()),
('hr002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李HR', 'HR', 1, '人事部', '人事专员', 4, 8, NOW(), NOW());

-- 5. 更新项目表的manager字段为用户ID
UPDATE hr_project p SET manager = (SELECT real_name FROM sys_user WHERE username = 'pm001' LIMIT 1) WHERE project_code = 'PRJ2024002';
UPDATE hr_project p SET manager = (SELECT real_name FROM sys_user WHERE username = 'pm003' LIMIT 1) WHERE project_code = 'PRJ2024003';
UPDATE hr_project p SET manager = (SELECT real_name FROM sys_user WHERE username = 'dept001' LIMIT 1) WHERE project_code = 'PRJ2024001';
UPDATE hr_project p SET manager = (SELECT real_name FROM sys_user WHERE username = 'pm004' LIMIT 1) WHERE project_code = 'PRJ2024004';
UPDATE hr_project p SET manager = (SELECT real_name FROM sys_user WHERE username = 'dept004' LIMIT 1) WHERE project_code = 'PRJ2024005';
