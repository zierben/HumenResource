package com.hr.admin.integration.common;

public enum SyncType {
    ZENTAO_USER("ZENTAO_USER", "禅道-人员同步"),
    ZENTAO_PROJECT("ZENTAO_PROJECT", "禅道-项目同步"),
    ZENTAO_WORKHOURS("ZENTAO_WORKHOURS", "禅道-工时同步");
    
    private final String code;
    private final String name;
    
    SyncType(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public static SyncType fromCode(String code) {
        for (SyncType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
