# 02 - 测试数据

## 说明

本文件包含演示用测试数据，以 JSON 格式提供，可直接导入系统使用。

---

## 数据清单

| 数据类型 | 文件 | 说明 |
|---------|------|------|
| 项目数据 | hr_project.json | 5个项目 |
| 供应商数据 | hr_supplier.json | 3个供应商 |
| 需求数据 | hr_requirement.json | 5个需求 |
| 人员数据 | hr_personnel.json | 15个人员 |
| 工时数据 | hr_work_hours.json | 1个月工时 |
| 结算数据 | hr_settlement.json | 3条结算 |
| 用户数据 | sys_user.json | 2个用户 |

---

## 测试数据 JSON

### hr_project.json

```json
[
  {"projectCode": "PRJ2024001", "projectName": "核心交易系统升级", "department": "研发部", "manager": "李明", "budget": 500000, "status": 1},
  {"projectCode": "PRJ2024002", "projectName": "移动端APP重构", "department": "研发部", "manager": "王强", "budget": 300000, "status": 1},
  {"projectCode": "PRJ2024003", "projectName": "数据分析平台", "department": "数据部", "manager": "张伟", "budget": 400000, "status": 1},
  {"projectCode": "PRJ2024004", "projectName": "客服系统优化", "department": "产品部", "manager": "刘洋", "budget": 200000, "status": 1},
  {"projectCode": "PRJ2024005", "projectName": "运维监控平台", "department": "运维部", "manager": "陈刚", "budget": 250000, "status": 0}
]
```

### hr_supplier.json

```json
[
  {"supplierName": "华软科技有限公司", "contactPerson": "张经理", "contactPhone": "13800138001", "contactEmail": "zhang@huaruan.com", "level": "A", "deliveryRate": 95, "currentPersonCount": 45, "status": 1},
  {"supplierName": "智联外包服务有限公司", "contactPerson": "李经理", "contactPhone": "13800138002", "contactEmail": "li@zhilian.com", "level": "A", "deliveryRate": 92, "currentPersonCount": 38, "status": 1},
  {"supplierName": "博达信息技术有限公司", "contactPerson": "王经理", "contactPhone": "13800138003", "contactEmail": "wang@boda.com", "level": "B", "deliveryRate": 85, "currentPersonCount": 28, "status": 1}
]
```

### hr_requirement.json

```json
[
  {"requirementCode": "RQ202401001", "projectName": "核心交易系统升级", "positionName": "Java高级工程师", "positionLevel": "P7", "skills": "Java,Spring Boot,MySQL,分布式", "expectedEntryDate": "2024-02-01", "serviceMonths": 12, "dailyRate": 1200, "monthlyBudget": 26400, "totalBudget": 316800, "demandCount": 2, "demandReason": "系统架构升级，需要资深Java开发", "workContent": "负责核心交易模块的设计与开发", "departmentId": 1, "projectId": 1, "status": 4},
  {"requirementCode": "RQ202401002", "projectName": "移动端APP重构", "positionName": "前端开发工程师", "positionLevel": "P6", "skills": "Vue3,TypeScript,移动端开发", "expectedEntryDate": "2024-02-15", "serviceMonths": 6, "dailyRate": 900, "monthlyBudget": 19800, "totalBudget": 118800, "demandCount": 3, "demandReason": "APP重构项目启动", "workContent": "负责移动端页面开发", "departmentId": 1, "projectId": 2, "status": 4},
  {"requirementCode": "RQ202401003", "projectName": "数据分析平台", "positionName": "数据分析师", "positionLevel": "P6", "skills": "Python,SQL,数据分析", "expectedEntryDate": "2024-03-01", "serviceMonths": 8, "dailyRate": 1000, "monthlyBudget": 22000, "totalBudget": 176000, "demandCount": 2, "demandReason": "数据平台建设需要专业人员", "workContent": "数据建模与分析报告", "departmentId": 1, "projectId": 3, "status": 2},
  {"requirementCode": "RQ202401004", "projectName": "客服系统优化", "positionName": "测试工程师", "positionLevel": "P5", "skills": "自动化测试,接口测试", "expectedEntryDate": "2024-02-20", "serviceMonths": 4, "dailyRate": 700, "monthlyBudget": 15400, "totalBudget": 61600, "demandCount": 2, "demandReason": "系统优化需要测试支持", "workContent": "功能测试与自动化测试", "departmentId": 1, "projectId": 4, "status": 1},
  {"requirementCode": "RQ202401005", "projectName": "核心交易系统升级", "positionName": "产品经理", "positionLevel": "P7", "skills": "产品规划,需求分析,金融行业", "expectedEntryDate": "2024-01-15", "serviceMonths": 12, "dailyRate": 1500, "monthlyBudget": 33000, "totalBudget": 396000, "demandCount": 1, "demandReason": "需要资深产品经理把控方向", "workContent": "产品规划与需求管理", "departmentId": 1, "projectId": 1, "status": 5}
]
```

### hr_personnel.json

```json
[
  {"personnelCode": "PER202401001", "name": "张伟", "idCard": "310101199001011234", "phone": "13900139001", "email": "zhangwei@example.com", "positionName": "Java高级工程师", "positionLevel": "P7", "dailyRate": 1200, "supplierId": 1, "projectId": 1, "requirementId": 1, "status": "ON_DUTY", "entryDate": "2024-01-15", "contractStartDate": "2024-01-15", "contractEndDate": "2025-01-14", "zentaoAccount": "zhangwei"},
  {"personnelCode": "PER202401002", "name": "李娜", "idCard": "310101199102022345", "phone": "13900139002", "email": "lina@example.com", "positionName": "Java高级工程师", "positionLevel": "P7", "dailyRate": 1150, "supplierId": 1, "projectId": 1, "requirementId": 1, "status": "ON_DUTY", "entryDate": "2024-01-20", "contractStartDate": "2024-01-20", "contractEndDate": "2025-01-19", "zentaoAccount": "lina"},
  {"personnelCode": "PER202401003", "name": "王磊", "idCard": "310101199203033456", "phone": "13900139003", "email": "wanglei@example.com", "positionName": "前端开发工程师", "positionLevel": "P6", "dailyRate": 900, "supplierId": 1, "projectId": 2, "requirementId": 2, "status": "ON_DUTY", "entryDate": "2024-02-01", "contractStartDate": "2024-02-01", "contractEndDate": "2024-07-31", "zentaoAccount": "wanglei"},
  {"personnelCode": "PER202401004", "name": "赵敏", "idCard": "310101199304044567", "phone": "13900139004", "email": "zhaomin@example.com", "positionName": "前端开发工程师", "positionLevel": "P6", "dailyRate": 880, "supplierId": 2, "projectId": 2, "requirementId": 2, "status": "ON_DUTY", "entryDate": "2024-02-10", "contractStartDate": "2024-02-10", "contractEndDate": "2024-08-09", "zentaoAccount": "zhaomin"},
  {"personnelCode": "PER202401005", "name": "钱进", "idCard": "310101199405055678", "phone": "13900139005", "email": "qianjin@example.com", "positionName": "前端开发工程师", "positionLevel": "P5", "dailyRate": 750, "supplierId": 2, "projectId": 2, "requirementId": 2, "status": "ON_DUTY", "entryDate": "2024-02-15", "contractStartDate": "2024-02-15", "contractEndDate": "2024-08-14", "zentaoAccount": "qianjin"},
  {"personnelCode": "PER202401006", "name": "孙华", "idCard": "310101199506066789", "phone": "13900139006", "email": "sunhua@example.com", "positionName": "数据分析师", "positionLevel": "P6", "dailyRate": 1000, "supplierId": 2, "projectId": 3, "requirementId": 3, "status": "PENDING_ENTRY", "entryDate": null, "contractStartDate": "2024-03-01", "contractEndDate": "2024-10-31", "zentaoAccount": "sunhua"},
  {"personnelCode": "PER202401007", "name": "周杰", "idCard": "310101199607077890", "phone": "13900139007", "email": "zhoujie@example.com", "positionName": "数据分析师", "positionLevel": "P5", "dailyRate": 850, "supplierId": 3, "projectId": 3, "requirementId": 3, "status": "PENDING_ENTRY", "entryDate": null, "contractStartDate": "2024-03-01", "contractEndDate": "2024-10-31", "zentaoAccount": "zhoujie"},
  {"personnelCode": "PER202401008", "name": "吴芳", "idCard": "310101199708088901", "phone": "13900139008", "email": "wufang@example.com", "positionName": "测试工程师", "positionLevel": "P5", "dailyRate": 700, "supplierId": 3, "projectId": 4, "requirementId": 4, "status": "ON_DUTY", "entryDate": "2024-02-20", "contractStartDate": "2024-02-20", "contractEndDate": "2024-06-19", "zentaoAccount": "wufang"},
  {"personnelCode": "PER202401009", "name": "郑凯", "idCard": "310101199809099012", "phone": "13900139009", "email": "zhengkai@example.com", "positionName": "测试工程师", "positionLevel": "P5", "dailyRate": 680, "supplierId": 1, "projectId": 4, "requirementId": 4, "status": "ON_DUTY", "entryDate": "2024-02-22", "contractStartDate": "2024-02-22", "contractEndDate": "2024-06-21", "zentaoAccount": "zhengkai"},
  {"personnelCode": "PER202401010", "name": "陈晨", "idCard": "310101199910101123", "phone": "13900139010", "email": "chenchen@example.com", "positionName": "产品经理", "positionLevel": "P7", "dailyRate": 1500, "supplierId": 1, "projectId": 1, "requirementId": 5, "status": "OFF_DUTY", "entryDate": "2024-01-10", "contractStartDate": "2024-01-10", "contractEndDate": "2025-01-09", "zentaoAccount": "chenchen"},
  {"personnelCode": "PER202401011", "name": "林涛", "idCard": "310101200001011234", "phone": "13900139011", "email": "lintao@example.com", "positionName": "Java中级工程师", "positionLevel": "P6", "dailyRate": 950, "supplierId": 2, "projectId": 1, "requirementId": 1, "status": "ON_DUTY", "entryDate": "2024-02-01", "contractStartDate": "2024-02-01", "contractEndDate": "2025-01-31", "zentaoAccount": "lintao"},
  {"personnelCode": "PER202401012", "name": "黄丽", "idCard": "310101200102022345", "phone": "13900139012", "email": "huangli@example.com", "positionName": "UI设计师", "positionLevel": "P5", "dailyRate": 800, "supplierId": 3, "projectId": 2, "requirementId": 2, "status": "ON_DUTY", "entryDate": "2024-02-05", "contractStartDate": "2024-02-05", "contractEndDate": "2024-08-04", "zentaoAccount": "huangli"},
  {"personnelCode": "PER202401013", "name": "杨帆", "idCard": "310101200203033456", "phone": "13900139013", "email": "yangfan@example.com", "positionName": "运维工程师", "positionLevel": "P6", "dailyRate": 900, "supplierId": 2, "projectId": 5, "requirementId": null, "status": "LEAVE", "entryDate": "2024-01-05", "contractStartDate": "2024-01-05", "contractEndDate": "2024-07-04", "zentaoAccount": "yangfan"},
  {"personnelCode": "PER202401014", "name": "徐明", "idCard": "310101200304044567", "phone": "13900139014", "email": "xuming@example.com", "positionName": "Java高级工程师", "positionLevel": "P7", "dailyRate": 1100, "supplierId": 1, "projectId": 1, "requirementId": 1, "status": "TRANSFER", "entryDate": "2024-01-20", "contractStartDate": "2024-01-20", "contractEndDate": "2025-01-19", "zentaoAccount": "xuming"},
  {"personnelCode": "PER202401015", "name": "马超", "idCard": "310101200405055678", "phone": "13900139015", "email": "machao@example.com", "positionName": "前端开发工程师", "positionLevel": "P6", "dailyRate": 860, "supplierId": 3, "projectId": null, "requirementId": null, "status": "PENDING_ENTRY", "entryDate": null, "contractStartDate": "2024-03-01", "contractEndDate": "2024-08-31", "zentaoAccount": "machao"}
]
```

### hr_work_hours.json (2024年1月部分数据)

```json
[
  {"personnelId": 1, "projectId": 1, "workDate": "2024-01-15", "hours": 8, "status": "APPROVED", "remark": null},
  {"personnelId": 1, "projectId": 1, "workDate": "2024-01-16", "hours": 8, "status": "APPROVED", "remark": null},
  {"personnelId": 1, "projectId": 1, "workDate": "2024-01-17", "hours": 8, "status": "APPROVED", "remark": null},
  {"personnelId": 1, "projectId": 1, "workDate": "2024-01-18", "hours": 8, "status": "APPROVED", "remark": null},
  {"personnelId": 1, "projectId": 1, "workDate": "2024-01-19", "hours": 8, "status": "APPROVED", "remark": null},
  {"personnelId": 2, "projectId": 1, "workDate": "2024-01-20", "hours": 8, "status": "APPROVED", "remark": null},
  {"personnelId": 2, "projectId": 1, "workDate": "2024-01-21", "hours": 8, "status": "APPROVED", "remark": null},
  {"personnelId": 2, "projectId": 1, "workDate": "2024-01-22", "hours": 8, "status": "APPROVED", "remark": null},
  {"personnelId": 2, "projectId": 1, "workDate": "2024-01-23", "hours": 8, "status": "PENDING", "remark": null},
  {"personnelId": 3, "projectId": 2, "workDate": "2024-01-15", "hours": 8, "status": "APPROVED", "remark": null},
  {"personnelId": 3, "projectId": 2, "workDate": "2024-01-16", "hours": 8, "status": "APPROVED", "remark": null},
  {"personnelId": 3, "projectId": 2, "workDate": "2024-01-17", "hours": 4, "status": "APPROVED", "remark": "下午请假"},
  {"personnelId": 11, "projectId": 1, "workDate": "2024-01-15", "hours": 8, "status": "APPROVED", "remark": null},
  {"personnelId": 11, "projectId": 1, "workDate": "2024-01-16", "hours": 8, "status": "APPROVED", "remark": null}
]
```

### hr_settlement.json

```json
[
  {"settlementCode": "SET202401001", "settlementMonth": 1, "settlementYear": 2024, "supplierId": 1, "projectId": 1, "totalAmount": 158400, "validDays": 22, "deductionAmount": 0, "finalAmount": 158400, "status": 5, "confirmRemark": "已支付"},
  {"settlementCode": "SET202401002", "settlementMonth": 1, "settlementYear": 2024, "supplierId": 2, "projectId": 2, "totalAmount": 79200, "validDays": 22, "deductionAmount": 2400, "finalAmount": 76800, "status": 3, "confirmRemark": null},
  {"settlementCode": "SET202401003", "settlementMonth": 1, "settlementYear": 2024, "supplierId": 3, "projectId": 4, "totalAmount": 30800, "validDays": 22, "deductionAmount": 0, "finalAmount": 30800, "status": 1, "confirmRemark": null}
]
```

### sys_user.json

```json
[
  {"username": "admin", "password": "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.iW8jZRGdjGj/n3.iW8", "realName": "系统管理员", "role": "ADMIN", "status": 1},
  {"username": "hr001", "password": "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.iW8jZRGdjGj/n3.iW8", "realName": "张HR", "role": "HR", "status": 1}
]
```

---

## 使用说明

1. 密码统一为 `123456`
2. 时间数据以 2024年1-2月 为基准
3. 人员状态分布：ON_DUTY(在岗) x8, PENDING_ENTRY(待入场) x3, OFF_DUTY(已离场) x1, LEAVE(请假) x1, TRANSFER(调配中) x2
