-- 清理无效数据脚本
-- 请在MySQL客户端中执行

USE hr_system;

-- 删除无效日期的用户记录
DELETE FROM sys_user WHERE create_time = '0000-00-00 00:00:00';

-- 删除重复用户（保留id<=3的初始用户）
DELETE FROM sys_user WHERE id > 3;

-- 确认清理结果
SELECT id, username, real_name, role, create_time FROM sys_user;
