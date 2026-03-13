# AGENTS.md

Coding guidelines for agentic coding agents operating in this repository.

## Project Overview

HR Outsourcing Management System (HR外包人力管理系统):
- **hr-web/**: Vue 3 frontend with Vite
- **hr-admin/**: Spring Boot backend with MyBatis-Plus

---

## Build/Lint/Test Commands

### Frontend (hr-web/)

```bash
cd hr-web && npm install        # Install dependencies
npm run dev                      # Dev server (http://localhost:3000)
npm run build                    # Production build
```

### Backend (hr-admin/)

```bash
cd hr-admin && mvn clean install           # Build
mvn spring-boot:run                        # Run (http://localhost:8080)
mvn test                                    # Run all tests
mvn test -Dtest=HrPersonnelServiceTest     # Single test class
mvn test -Dtest=HrPersonnelServiceTest#testSavePersonnel  # Single test method
mvn clean install -DskipTests              # Skip tests
```

---

## Code Style Guidelines

### General
- UTF-8 encoding, max 120 char line length
- Indentation: 2 spaces (Vue/JS), 4 spaces (Java)
- No trailing whitespace

### Java Backend (hr-admin/)

#### Imports
- Order: java.*, javax.*, org.*, com.*, others
- Specific imports; avoid wildcards except static imports

#### Naming
- **Classes**: PascalCase with `Hr` prefix (e.g., `HrPersonnel`)
- **Methods**: camelCase (e.g., `savePersonnel`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `PENDING_ENTRY`)
- **Tables**: snake_case with `hr_` prefix (e.g., `hr_personnel`)

#### Annotations
- `@RequiredArgsConstructor` with `private final` for DI
- `@Data` from Lombok for entities/DTOs
- `@Transactional` for modifying service methods
- `@RestController`, `@RequestMapping("/api/resource")` for controllers

#### Entity Classes
- Services: extend `IService<Entity>`, impl: `ServiceImpl<Mapper, Entity>`
- Use `@TableName`, `@TableId`, `@TableField`, `@TableLogic` for MyBatis-Plus

#### Error Handling
- Use `Result<T>` wrapper for all API responses
- Success: `Result.success(data)`, Error: `Result.error(message)`

#### Controller Pattern
```java
@RestController
@RequestMapping("/api/personnel")
@RequiredArgsConstructor
public class HrPersonnelController {
    private final HrPersonnelService hrPersonnelService;
    
    @GetMapping
    public Result<Page<HrPersonnel>> list(...) { ... }
    @GetMapping("/{id}")
    public Result<HrPersonnel> getById(@PathVariable Long id) { ... }
    @PostMapping
    public Result<Void> save(@RequestBody HrPersonnel entity) { ... }
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody HrPersonnel entity) { ... }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { ... }
}
```

### Vue Frontend (hr-web/)

#### Imports
- Order: Vue core, third-party, local components, local utilities
- Path alias: `@/` for src (e.g., `@/views/Dashboard.vue`)
- Auto-import for Vue APIs via unplugin-auto-import

#### Naming
- **Components**: PascalCase files (e.g., `Personnel.vue`)
- **Variables**: camelCase (e.g., `personnelList`)

#### Component Structure
```vue
<template>
  <!-- Template content -->
</template>

<script setup>
// Imports, reactive state, computed, methods, lifecycle hooks
</script>

<style scoped>
/* Component styles */
</style>
```

#### Vue Patterns
- Always use `<script setup>` syntax
- `ref()` for primitives, `reactive()` for objects
- Element Plus for UI components (el-table, el-form, el-dialog)

#### API Calls
- Use axios from `@/api/index.js`, base URL `/api` proxies to `http://localhost:8080`
- JWT token auto-included via interceptor

---

## Project Structure

### Backend (hr-admin/src/main/java/com/hr/admin/)
```
controller/     # REST API endpoints
service/        # Business logic interfaces
service/impl/   # Service implementations
mapper/         # MyBatis-Plus mappers
entity/         # Database entities
dto/            # Data transfer objects
constant/       # Constants and enums
```

### Frontend (hr-web/src/)
```
api/            # Axios configuration
views/          # Page components
router/         # Vue Router config
App.vue         # Root component
main.js         # Entry point
```

---

## Database Conventions

- Table names: `hr_` prefix, snake_case
- Primary key: `id` (auto-increment)
- Audit fields: `create_time`, `update_time`
- Soft delete: `deleted` (0=active, 1=deleted)
- Foreign keys: `{table}_id` pattern

---

## API Conventions

- Base path: `/api/{resource}`
- List: `GET /api/personnel?pageNum=1&pageSize=10`
- Get: `GET /api/personnel/{id}`
- Create: `POST /api/personnel`
- Update: `PUT /api/personnel/{id}`
- Delete: `DELETE /api/personnel/{id}`
- Response: `{ code: 200, message: "success", data: {...}, total: 100 }`

---

## Important Notes

- Frontend proxies `/api` to backend `http://localhost:8080`
- Backend: port 8080, Frontend: port 3000
- DB config in `hr-admin/src/main/resources/application.yml`
- No ESLint/Prettier - follow existing patterns

---

## Known Issues & Solutions

### 1. admin 用户登录问题

**问题**: 后端启动后 admin 用户无法登录，提示"用户名或密码错误"

**根本原因**: 
1. `DataCleanupRunner` 启动时会执行 `init_org_structure.sql`，该脚本使用 `DELETE FROM sys_user WHERE id > 1`，但如果数据库中 id=1 不是 admin 用户，就会导致 admin 用户丢失
2. **密码哈希不匹配**: SQL脚本中的密码哈希可能与后端 PasswordEncoder 生成的哈希不兼容

**解决方案**: 
1. `init_org_structure.sql` 已修改为使用 `INSERT ... ON DUPLICATE KEY UPDATE` 确保 admin 用户始终存在
2. 如果登录失败，调用 `POST /api/auth/reset-all` 重置所有用户密码为 admin123
3. 更新 `init_org_structure.sql` 中的密码哈希值（使用后端生成的正确哈希）

**验证**: 
```sql
SELECT id, username, real_name, role FROM sys_user WHERE username='admin';
```

**重置密码接口**:
```
POST /api/auth/reset-all  -- 重置所有用户密码为 admin123
POST /api/auth/init-admin -- 初始化 admin 用户
```

**默认账号**:
- admin / admin123 (超级管理员)
- hr001 / admin123 (HR专员)
- 其他用户密码均为 admin123

### 2. 登录页测试账号快捷登录

**位置**: `hr-web/src/views/Login.vue`

**说明**: 测试账号快捷登录按钮只在开发环境显示（`showTestAccounts = import.meta.env.DEV`），生产环境不显示

**如需生产环境显示**: 修改 `showTestAccounts` 为 `true`

### 3. 组织架构管理模块

**功能说明**: 独立的部门管理，支持3层架构：总经理 -> 副总 -> 部门

**数据库表**:
- `hr_department`: 部门表
- `sys_user` 新增字段: `dept_id`(所属部门), `is_manager`(是否部门长)

**业务规则**:
1. 每个部门只能有一个部门长
2. 部门长通过用户的 `is_manager=1` + `dept_id` 自动计算
3. 管理员角色(role=ADMIN)不需要设置岗位
4. 管理员权限不可被修改（除了admin用户）

**API接口**:
```
GET    /api/departments/tree        # 获取部门树
POST   /api/departments             # 创建部门
PUT    /api/dedepartments/{id}      # 更新部门
DELETE /api/departments/{id}        # 删除部门
PUT    /api/departments/{id}/manager # 设置部门长
GET    /api/departments/{id}/members # 获取部门成员
POST   /api/departments/{id}/members # 添加部门成员
DELETE /api/departments/{id}/members/{userId} # 移除部门成员
```

**前端页面**: `/org-chart` (OrgChart.vue)

**数据迁移**: 执行 `migrate_to_department.sql` 迁移现有用户数据
