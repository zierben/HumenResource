-- 更新所有用户密码为 123456
-- BCrypt hash for "123456"
UPDATE sys_user SET password='$2a$10$EIWMWRPwkSr7l/4JfrEDnOOW2mjP0sE.kB7FfGVGxmkJfqJnDnFqC';
