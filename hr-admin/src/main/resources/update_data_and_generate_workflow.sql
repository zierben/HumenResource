-- 更新历史数据日期并生成测试数据
-- 1. 将2024年数据改为2026年
-- 2. 生成流程待办和历史审批记录

USE hr_system;

-- =============== 第一部分：更新日期 ===============

-- 更新人员入场日期
UPDATE hr_personnel 
SET entry_date = DATE_ADD(entry_date, INTERVAL 2 YEAR),
    contract_start_date = DATE_ADD(contract_start_date, INTERVAL 2 YEAR),
    contract_end_date = DATE_ADD(contract_end_date, INTERVAL 2 YEAR),
    create_time = DATE_ADD(create_time, INTERVAL 2 YEAR)
WHERE entry_date < '2025-01-01' AND deleted = 0;

-- 更新需求日期
UPDATE hr_requirement 
SET expected_entry_date = DATE_ADD(expected_entry_date, INTERVAL 2 YEAR),
    create_time = DATE_ADD(create_time, INTERVAL 2 YEAR)
WHERE expected_entry_date < '2025-01-01' AND deleted = 0;

-- 更新结算年月
UPDATE hr_settlement 
SET settlement_year = settlement_year + 2,
    create_time = DATE_ADD(create_time, INTERVAL 2 YEAR)
WHERE settlement_year < 2025 AND deleted = 0;

-- 更新项目里程碑日期
UPDATE hr_project_milestone 
SET plan_date = DATE_ADD(plan_date, INTERVAL 2 YEAR),
    actual_date = IF(actual_date IS NOT NULL, DATE_ADD(actual_date, INTERVAL 2 YEAR), NULL)
WHERE plan_date < '2025-01-01';

-- 更新合同日期
UPDATE hr_contract 
SET start_date = DATE_ADD(start_date, INTERVAL 2 YEAR),
    end_date = DATE_ADD(end_date, INTERVAL 2 YEAR),
    create_time = DATE_ADD(create_time, INTERVAL 2 YEAR)
WHERE start_date < '2025-01-01';

-- 更新付款日期
UPDATE hr_payment 
SET payment_date = DATE_ADD(payment_date, INTERVAL 2 YEAR),
    create_time = DATE_ADD(create_time, INTERVAL 2 YEAR)
WHERE payment_date < '2025-01-01';

-- =============== 第二部分：创建工作流实例（待办任务）==============

-- 清理旧的工作流数据
DELETE FROM wf_record;
DELETE FROM wf_instance;

-- 获取用户ID变量
SET @admin_id = (SELECT id FROM sys_user WHERE username = 'admin');
SET @ceo_id = (SELECT id FROM sys_user WHERE username = 'ceo');
SET @vp_tech_id = (SELECT id FROM sys_user WHERE username = 'vp_tech');
SET @vp_hr_id = (SELECT id FROM sys_user WHERE username = 'vp_hr');
SET @dept_dev_id = (SELECT id FROM sys_user WHERE username = 'dept_dev');
SET @pm_erp_id = (SELECT id FROM sys_user WHERE username = 'pm_erp');
SET @hr001_id = (SELECT id FROM sys_user WHERE username = 'hr001');

-- 创建待审批的需求申请（状态PENDING）
INSERT INTO wf_instance (workflow_id, business_type, business_id, title, initiator_id, current_node_id, status, create_time)
VALUES 
(1, 'REQUIREMENT', 1, 'ERP系统开发人员需求', @pm_erp_id, 2, 'PENDING', NOW()),
(1, 'REQUIREMENT', 2, 'APP前端开发需求', @pm_erp_id, 2, 'PENDING', NOW()),
(1, 'REQUIREMENT', 3, '数据分析平台需求', @pm_erp_id, 2, 'PENDING', NOW());

-- 创建待审批的入场申请
INSERT INTO wf_instance (workflow_id, business_type, business_id, title, initiator_id, current_node_id, status, create_time)
VALUES 
(2, 'ENTRY', 1, '张明入场审批', @dept_dev_id, 2, 'PENDING', NOW()),
(2, 'ENTRY', 2, '李华入场审批', @dept_dev_id, 2, 'PENDING', NOW()),
(2, 'ENTRY', 3, '王强入场审批', @dept_dev_id, 2, 'PENDING', NOW());

-- 创建待审批的结算申请
INSERT INTO wf_instance (workflow_id, business_type, business_id, title, initiator_id, current_node_id, status, create_time)
VALUES 
(3, 'SETTLEMENT', 1, '2026年1月结算审批', @hr001_id, 2, 'PENDING', NOW()),
(3, 'SETTLEMENT', 2, '2026年2月结算审批', @hr001_id, 2, 'PENDING', NOW());

-- 创建待审批的离场申请
INSERT INTO wf_instance (workflow_id, business_type, business_id, title, initiator_id, current_node_id, status, create_time)
VALUES 
(4, 'EXIT', 14, '测试人员离场审批', @dept_dev_id, 2, 'PENDING', NOW());

-- =============== 第三部分：创建历史审批记录 ===============

-- 已完成的审批（需求）
INSERT INTO wf_instance (workflow_id, business_type, business_id, title, initiator_id, current_node_id, status, create_time, update_time)
VALUES 
(1, 'REQUIREMENT', 4, 'CRM系统开发需求', @pm_erp_id, 4, 'APPROVED', DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
(1, 'REQUIREMENT', 5, '数据迁移需求', @pm_erp_id, 4, 'APPROVED', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
(1, 'REQUIREMENT', 6, '测试自动化需求', @pm_erp_id, 4, 'APPROVED', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 'REQUIREMENT', 7, '运维监控需求', @pm_erp_id, 3, 'REJECTED', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY));

-- 已完成的审批（入场）
INSERT INTO wf_instance (workflow_id, business_type, business_id, title, initiator_id, current_node_id, status, create_time, update_time)
VALUES 
(2, 'ENTRY', 4, '陈晓入场审批', @dept_dev_id, 4, 'APPROVED', DATE_SUB(NOW(), INTERVAL 45 DAY), DATE_SUB(NOW(), INTERVAL 40 DAY)),
(2, 'ENTRY', 5, '周婷入场审批', @dept_dev_id, 4, 'APPROVED', DATE_SUB(NOW(), INTERVAL 35 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY)),
(2, 'ENTRY', 6, '吴佳入场审批', @dept_dev_id, 4, 'APPROVED', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY));

-- 已完成的审批（结算）
INSERT INTO wf_instance (workflow_id, business_type, business_id, title, initiator_id, current_node_id, status, create_time, update_time)
VALUES 
(3, 'SETTEMENT', 3, '2025年11月结算审批', @hr001_id, 4, 'APPROVED', DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_SUB(NOW(), INTERVAL 55 DAY)),
(3, 'SETTLEMENT', 4, '2025年12月结算审批', @hr001_id, 4, 'APPROVED', DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY));

-- 创建审批记录（wf_record）
-- 需求审批记录
INSERT INTO wf_record (instance_id, node_id, operator_id, operator_name, action, comment, create_time)
VALUES 
((SELECT id FROM wf_instance WHERE title = 'CRM系统开发需求'), 2, @dept_dev_id, '陈志远', 'APPROVE', '同意，项目需要此岗位', DATE_SUB(NOW(), INTERVAL 28 DAY)),
((SELECT id FROM wf_instance WHERE title = 'CRM系统开发需求'), 3, @vp_tech_id, '李明强', 'APPROVE', '同意申请', DATE_SUB(NOW(), INTERVAL 26 DAY)),
((SELECT id FROM wf_instance WHERE title = 'CRM系统开发需求'), 4, @ceo_id, '张伟华', 'APPROVE', '批准', DATE_SUB(NOW(), INTERVAL 25 DAY)),

((SELECT id FROM wf_instance WHERE title = '数据迁移需求'), 2, @dept_dev_id, '陈志远', 'APPROVE', '需求合理', DATE_SUB(NOW(), INTERVAL 18 DAY)),
((SELECT id FROM wf_instance WHERE title = '数据迁移需求'), 3, @vp_tech_id, '李明强', 'APPROVE', '同意', DATE_SUB(NOW(), INTERVAL 16 DAY)),
((SELECT id FROM wf_instance WHERE title = '数据迁移需求'), 4, @ceo_id, '张伟华', 'APPROVE', '批准执行', DATE_SUB(NOW(), INTERVAL 15 DAY)),

((SELECT id FROM wf_instance WHERE title = '运维监控需求'), 2, @dept_dev_id, '陈志远', 'REJECT', '当前预算不足，暂缓', DATE_SUB(NOW(), INTERVAL 12 DAY));

-- 入场审批记录
INSERT INTO wf_record (instance_id, node_id, operator_id, operator_name, action, comment, create_time)
VALUES 
((SELECT id FROM wf_instance WHERE title = '陈晓入场审批'), 2, @dept_dev_id, '陈志远', 'APPROVE', '面试通过', DATE_SUB(NOW(), INTERVAL 43 DAY)),
((SELECT id FROM wf_instance WHERE title = '陈晓入场审批'), 3, @vp_tech_id, '李明强', 'APPROVE', '同意入场', DATE_SUB(NOW(), INTERVAL 41 DAY)),
((SELECT id FROM wf_instance WHERE title = '陈晓入场审批'), 4, @hr001_id, '孙丽娜', 'APPROVE', '已完成入职手续', DATE_SUB(NOW(), INTERVAL 40 DAY)),

((SELECT id FROM wf_instance WHERE title = '周婷入场审批'), 2, @dept_dev_id, '陈志远', 'APPROVE', '符合岗位要求', DATE_SUB(NOW(), INTERVAL 33 DAY)),
((SELECT id FROM wf_instance WHERE title = '周婷入场审批'), 3, @vp_hr_id, '王晓红', 'APPROVE', '同意', DATE_SUB(NOW(), INTERVAL 31 DAY)),
((SELECT id FROM wf_instance WHERE title = '周婷入场审批'), 4, @hr001_id, '孙丽娜', 'APPROVE', '入职完成', DATE_SUB(NOW(), INTERVAL 30 DAY));

-- 结算审批记录
INSERT INTO wf_record (instance_id, node_id, operator_id, operator_name, action, comment, create_time)
VALUES 
((SELECT id FROM wf_instance WHERE title = '2025年11月结算审批'), 2, @hr001_id, '孙丽娜', 'APPROVE', '数据核对无误', DATE_SUB(NOW(), INTERVAL 58 DAY)),
((SELECT id FROM wf_instance WHERE title = '2025年11月结算审批'), 3, @vp_hr_id, '王晓红', 'APPROVE', '金额正确，同意', DATE_SUB(NOW(), INTERVAL 56 DAY)),
((SELECT id FROM wf_instance WHERE title = '2025年11月结算审批'), 4, @ceo_id, '张伟华', 'APPROVE', '批准付款', DATE_SUB(NOW(), INTERVAL 55 DAY)),

((SELECT id FROM wf_instance WHERE title = '2025年12月结算审批'), 2, @hr001_id, '孙丽娜', 'APPROVE', '已核对', DATE_SUB(NOW(), INTERVAL 28 DAY)),
((SELECT id FROM wf_instance WHERE title = '2025年12月结算审批'), 3, @vp_hr_id, '王晓红', 'APPROVE', '同意结算', DATE_SUB(NOW(), INTERVAL 26 DAY)),
((SELECT id FROM wf_instance WHERE title = '2025年12月结算审批'), 4, @ceo_id, '张伟华', 'APPROVE', '批准', DATE_SUB(NOW(), INTERVAL 25 DAY));

-- =============== 第四部分：更新业务表状态 ===============

-- 更新需求状态
UPDATE hr_requirement SET status = 1, workflow_status = 'APPROVED' WHERE id IN (4, 5, 6);
UPDATE hr_requirement SET status = 2, workflow_status = 'REJECTED' WHERE id = 7;

-- =============== 验证结果 ===============

SELECT '=== 待办任务 ===' AS info;
SELECT id, business_type, title, status, create_time FROM wf_instance WHERE status = 'PENDING';

SELECT '=== 已办任务 ===' AS info;
SELECT id, business_type, title, status, create_time FROM wf_instance WHERE status != 'PENDING';

SELECT '=== 审批记录统计 ===' AS info;
SELECT COUNT(*) as total_records FROM wf_record;

SELECT '=== 人员日期更新 ===' AS info;
SELECT MIN(entry_date) as min_date, MAX(entry_date) as max_date FROM hr_personnel WHERE deleted = 0;

SELECT '=== 结算年份更新 ===' AS info;
SELECT MIN(settlement_year) as min_year, MAX(settlement_year) as max_year FROM hr_settlement WHERE deleted = 0;
