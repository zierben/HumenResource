# 01 - 通用组件

## IntegrationResult

统一的外部系统调用返回结果封装。

```java
@Data
public class IntegrationResult<T> {
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
}
```

## SyncType

同步类型枚举。

| 枚举值 | 编码 | 说明 |
|--------|------|------|
| ZENTAO_USER | ZENTAO_USER | 禅道-人员同步 |
| ZENTAO_PROJECT | ZENTAO_PROJECT | 禅道-项目同步 |
| ZENTAO_WORKHOURS | ZENTAO_WORKHOURS | 禅道-工时同步 |
