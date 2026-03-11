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
