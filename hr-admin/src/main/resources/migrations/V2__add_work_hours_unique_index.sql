-- 添加工时表唯一索引：同一人员、同一项目、同一日期不能重复
ALTER TABLE hr_work_hours ADD UNIQUE INDEX uk_work_hours_unique (personnel_id, project_id, work_date);

-- 如果已有重复数据，删除保留ID最大的那条
-- 先查看重复数据
-- SELECT personnel_id, project_id, work_date, COUNT(*) as cnt 
-- FROM hr_work_hours 
-- WHERE deleted = 0 
-- GROUP BY personnel_id, project_id, work_date 
-- HAVING cnt > 1;
