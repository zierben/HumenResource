# 02 - 禅道对接

## 配置项

```yaml
zentao:
  enabled: false
  base-url: https://your-zentao.com/api.php/v1
  token: your-api-token
```

## 组件

| 组件 | 说明 |
|------|------|
| ZentaoConfig | 配置类 |
| ZentaoClient | API 客户端（目前返回 Mock 数据） |
| ZentaoSyncService | 同步服务 |

## DTO

| DTO | 说明 |
|-----|------|
| ZentaoUserDTO | 用户信息 |
| ZentaoProjectDTO | 项目信息 |
| ZentaoEffortDTO | 工时信息 |

## 待实现

1. 获取禅道 API Token
2. 实现 ZentaoClient 真实 API 调用
3. 实现数据转换和保存逻辑
