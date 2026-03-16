-- 造测试数据：特殊工时记录
-- 先查询可用的人员和项目ID
-- SELECT id, name FROM hr_personnel LIMIT 5;
-- SELECT id, project_name FROM hr_project LIMIT 5;

-- 请假工时（0小时，但有请假备注）
INSERT INTO hr_work_hours (personnel_id, project_id, work_date, hours, status, remark, create_time, update_time, deleted) VALUES
(1, 1, '2026-03-10', 0, 'PENDING', '请年假', NOW(), NOW(), 0),
(2, 2, '2026-03-10', 0, 'PENDING', '病假', NOW(), NOW(), 0),
(3, 1, '2026-03-11', 0, 'PENDING', '事假', NOW(), NOW(), 0);

-- 缺岗（0小时，无备注或正常填报0）
INSERT INTO hr_work_hours (personnel_id, project_id, work_date, hours, status, remark, create_time, update_time, deleted) VALUES
(4, 3, '2026-03-12', 0, 'PENDING', '', NOW(), NOW(), 0),
(5, 2, '2026-03-12', 0, 'PENDING', '缺勤', NOW(), NOW(), 0);

-- 工时不足8小时（需要确认）
INSERT INTO hr_work_hours (personnel_id, project_id, work_date, hours, status, remark, create_time, update_time, deleted) VALUES
(1, 1, '2026-03-13', 4, 'PENDING', '下午提前离开', NOW(), NOW(), 0),
(2, 2, '2026-03-13', 6, 'PENDING', '迟到早退', NOW(), NOW(), 0),
(3, 1, '2026-03-14', 7.5, 'PENDING', '', NOW(), NOW(), 0),
(4, 3, '2026-03-14', 3, 'PENDING', '中途离开', NOW(), NOW(), 0);

-- 已审核的正常工时（对比用）
INSERT INTO hr_work_hours (personnel_id, project_id, work_date, hours, status, remark, create_time, update_time, deleted) VALUES
(1, 1, '2026-03-09', 8, 'APPROVED', '正常出勤', NOW(), NOW(), 0),
(2, 2, '2026-03-09', 8, 'APPROVED', '正常出勤', NOW(), NOW(), 0);

-- 再造一些待审核的正常8小时工时
INSERT INTO hr_work_hours (personnel_id, project_id, work_date, hours, status, remark, create_time, update_time, deleted) VALUES
(5, 1, '2026-03-15', 8, 'PENDING', '', NOW(), NOW(), 0),
(6, 2, '2026-03-15', 8, 'PENDING', '', NOW(), NOW(), 0),
(7, 3, '2026-03-15', 8, 'PENDING', '', NOW(), NOW(), 0),
(8, 1, '2026-03-15', 8, 'PENDING', '', NOW(), NOW(), 0);
