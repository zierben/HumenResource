# 遗留功能项清单

> 最后更新: 2026-03-12

---

## 一、需要用户提供信息的项

| 功能 | 模块 | 需要什么 | 状态 |
|-----|------|----------|------|
| 禅道工时同步 | WorkHours | 禅道API地址、账号密钥 | 等待用户提供 |
| NUC登录对接 | Login | NUC对接文档、API地址 | 等待用户提供 |

---

## 二、保留但不实现的功能

| 功能 | 模块 | 原因 |
|-----|------|------|
| 企业微信登录 | Login | 用户不需要 |
| 钉钉登录 | Login | 用户不需要 |
| LDAP登录 | Login | 用户不需要 |
| 双因子认证(MFA) | Login | 用户暂时不需要 |
| 忘记密码 | Login | 管理员可重置密码 |
| 业财对接 | Sync | 暂时空着 |

---

## 三、本次已完成的增强

### 前端增强
1. **Dashboard数据对接** - 统计卡片、图表、待办任务调用真实API
2. **人员管理增强** - 搜索、编辑、删除、合同到期高亮
3. **登录记住我** - localStorage存储用户名
4. **合同到期提醒** - 即将到期预警、列表高亮
5. **候选人转正式人员** - 一键转换功能
6. **数据导出Excel** - 人员列表导出

### 后端增强
1. **WebSocket消息推送** - 实时通知能力
2. **全局搜索API** - 跨模块搜索
3. **操作日志AOP** - `@OperationLog`注解自动记录
4. **合同到期API** - `/api/contracts/expiring`
5. **候选人转换API** - `/api/candidates/{id}/convert`
6. **Excel导出** - 人员列表导出

---

## 四、新增文件清单

### 后端
- `annotation/OperationLog.java` - 操作日志注解
- `aspect/OperationLogAspect.java` - AOP切面
- `websocket/WebSocketConfig.java` - WebSocket配置
- `websocket/WebSocketService.java` - 推送服务
- `controller/SearchController.java` - 全局搜索
- `util/ExcelUtil.java` - Excel导出工具

### 前端
- `utils/websocket.js` - WebSocket服务

### 文档
- `docs/BACKLOG.md` - 本文档

---

## 五、操作日志AOP使用说明

在Controller方法上添加 `@OperationLog` 注解：

```java
@PostMapping
@OperationLog(module = "人员管理", action = "新增人员", targetType = "人员")
public Result<Void> save(@RequestBody HrPersonnel hrPersonnel) {
    // ...
}
```

已添加注解的Controller：
- `AuthController` - 登录/登出
- `HrPersonnelController` - 人员增删改查/入场/离场/调配

---

## 六、WebSocket使用说明

前端使用：
```javascript
import { useWebSocket } from '@/utils/websocket'

const { connect, onMessage } = useWebSocket()

// 连接
connect(userId)

// 监听消息
onMessage((data) => {
  console.log('收到消息:', data)
})
```

后端推送：
```java
@Autowired
private WebSocketService webSocketService;

// 推送给特定用户
webSocketService.pushNotification(userId, "标题", "内容");
```

---

## 七、下一步建议

1. **等待用户提供** - 禅道配置、NUC对接文档
2. **完善操作日志** - 为其他Controller添加`@OperationLog`注解
3. **WebSocket集成** - 在审批流程中使用实时推送
4. **扩展导出功能** - 其他模块的Excel导出
