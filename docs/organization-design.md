# 组织架构管理模块设计

## 需求背景

目前组织架构通过人员的上下级关系堆砌，存在以下问题：
1. 人员更换部门时，需要移动很多上下级关系，操作不便
2. 没有独立的部门管理，部门结构不清晰
3. 部门长和组员关系混乱

## 解决方案

### 1. 新增部门表 (hr_department)

```sql
CREATE TABLE hr_department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dept_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    parent_id BIGINT DEFAULT NULL COMMENT '上级部门ID',
    manager_id BIGINT DEFAULT NULL COMMENT '部门长ID（冗余，方便查询）',
    level INT DEFAULT 3 COMMENT '层级：1=总经理 2=副总 3=部门',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态 1=启用 0=禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
);
```

### 2. 层级结构

```
L1: 总经理（公司级，parent_id=NULL, level=1）
├── L2: 技术副总（parent_id=总经理ID, level=2）
│   ├── L3: 研发部（parent_id=技术副总ID, level=3）
│   └── L3: 测试部（parent_id=技术副总ID, level=3）
├── L2: 人力副总（parent_id=总经理ID, level=2）
│   └── L3: 人力资源部（parent_id=人力副总ID, level=3）
└── L2: 财务副总（parent_id=总经理ID, level=2）
    └── L3: 财务部（parent_id=财务副总ID, level=3）
```

### 3. 人员表调整 (sys_user)

```sql
ALTER TABLE sys_user ADD COLUMN dept_id BIGINT COMMENT '所属部门ID';
ALTER TABLE sys_user ADD COLUMN is_manager INT DEFAULT 0 COMMENT '是否部门长 0=否 1=是';
```

### 4. 业务规则

#### 管理员规则
- 管理员角色(role=ADMIN)不需要设置岗位
- 管理员权限不可被修改（除了admin用户）
- 任何用户都可以被设置为管理员

#### 部门长规则
- 部门长通过人员的 `is_manager=1` + `dept_id` 自动计算
- 每个部门只能有一个部门长（唯一约束）
- 部门表中的 `manager_id` 为冗余字段，方便查询

### 5. 页面设计

#### 组织架构管理页面
- 树形结构展示（固定展开）
- 拖拽调整部门顺序
- 点击部门可编辑部门信息
- 可添加/删除部门
- 可设置部门长（从部门人员中选择）

#### 展示内容
- 部门名称
- 部门长姓名（单独一行显示）
- 组员数量（浮窗显示组员名称）
- 总人数

### 6. API设计

```
GET    /api/departments/tree        # 获取部门树
POST   /api/departments             # 创建部门
PUT    /api/departments/{id}        # 更新部门
DELETE /api/departments/{id}        # 删除部门
PUT    /api/departments/{id}/manager # 设置部门长
GET    /api/departments/{id}/members # 获取部门成员
POST   /api/departments/{id}/members # 添加部门成员
DELETE /api/departments/{id}/members/{userId} # 移除部门成员
```

### 7. 数据迁移

需要将现有数据迁移到新结构：
1. 创建部门表
2. 根据现有用户数据创建部门记录
3. 更新用户的 dept_id 和 is_manager
4. 更新部门的 manager_id

## 实现优先级

1. P0: 创建部门表、调整用户表
2. P0: 后端API实现
3. P0: 前端组织架构管理页面
4. P1: 数据迁移脚本
5. P2: 拖拽调整部门顺序
