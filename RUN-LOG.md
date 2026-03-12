# HR外包人力管理系统 - 运行记录

## 项目状态

- Git仓库已初始化，共2个提交
- 后端编译成功（157个Java文件）
- 前端构建成功（2374个模块）

## 数据库配置

- 地址: localhost:3306/hr_system
- 用户: root
- 密码: 123456
- 状态: 已就绪

## 服务端口

- 后端: http://localhost:8080
- 前端: http://localhost:3000

## 启动命令

### 后端 (hr-admin)
```bash
cd hr-admin
mvn spring-boot:run
```

### 前端 (hr-web)
```bash
cd hr-web
npm run dev
```

## 测试账号

- admin / admin123 (系统管理员，不参与组织架构)
- hr001 / admin123 (人力资源部HR总监)

## 组织架构（4层）

| 层级 | 名称 | 职位示例 |
|-----|------|---------|
| L1 | 总经理 | 张伟华 |
| L2 | 副总经理 | 技术副总、人力副总、财务副总 |
| L3 | 部门长 | 研发总监、HR总监、财务总监 |
| L4 | 项目经理/专员 | ERP项目经理、HR专员、会计 |

## API接口

- 登录: POST /api/auth/login
- 用户列表: GET /api/users
- 人员管理: GET/POST/PUT/DELETE /api/personnel
- 供应商管理: /api/suppliers
- 项目管理: /api/projects
- 合同管理: /api/contracts
- 工时管理: /api/workhours
- 结算管理: /api/settlements
- 组织架构: /api/users (按managerId构建树)

## 已完成模块

1. 数据库设计
2. 用户认证 (JWT)
3. 权限管理
4. 核心业务模块
5. 工作流引擎
6. 禅道对接框架
7. 业财预留接口
8. 组织架构树（4层级）

## 注意事项

- Windows发布时vite.config.js已设置 `base: './'` 使用相对路径
- admin用户level=0不显示在组织架构树上
- 服务启动时会自动初始化组织架构数据
