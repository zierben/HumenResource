package com.hr.admin.integration.common;

import lombok.Data;
import java.util.List;

@Data
public class IntegrationResult<T> {
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    
    public static <T> IntegrationResult<T> success(T data) {
        IntegrationResult<T> result = new IntegrationResult<>();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }
    
    public static <T> IntegrationResult<T> error(String message) {
        IntegrationResult<T> result = new IntegrationResult<>();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
    
    public static <T> IntegrationResult<T> error(List<String> errors) {
        IntegrationResult<T> result = new IntegrationResult<>();
        result.setSuccess(false);
        result.setErrors(errors);
        return result;
    }
}
