# 02 - 用户认证模块

## 模块目标

实现系统登录认证功能，包括 JWT Token 生成、验证、刷新机制。

---

## 功能清单

| 功能 | 说明 |
|------|------|
| 用户登录 | 用户名密码登录，返回 JWT Token |
| 用户登出 | 清除 Token（前端处理） |
| Token 验证 | 请求拦截，验证 Token 有效性 |
| 获取当前用户 | 根据 Token 获取用户信息 |

---

## 技术方案

### JWT 配置
- Token 有效期：24小时（可配置）
- 签名算法：HS256
- 存储方式：前端 localStorage，请求头 Authorization: Bearer {token}

### 密码加密
- 算法：BCrypt
- 强度：10

---

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/auth/login | POST | 登录 |
| /api/auth/logout | POST | 登出 |
| /api/auth/current | GET | 获取当前用户信息 |

---

## 开发清单

### 后端
- [ ] 创建 LoginDTO、LoginVO
- [ ] 创建 JwtUtil 工具类
- [ ] 创建 AuthController
- [ ] 创建 AuthService
- [ ] 配置 JWT 拦截器
- [ ] 配置密码编码器

### 前端
- [ ] 创建登录页面
- [ ] 创建用户 Store (Pinia)
- [ ] 配置路由守卫
- [ ] 修改 API 拦截器

---

## 完成状态

✅ 已完成

**已完成内容：**
- 后端：LoginDTO、LoginVO、JwtUtil、AuthController、AuthService、JwtInterceptor、WebConfig、PasswordConfig
- 前端：Login.vue、user.js (Pinia Store)、路由守卫、App.vue 权限菜单
