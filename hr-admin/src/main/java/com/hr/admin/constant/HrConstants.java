package com.hr.admin.constant;

public class HrConstants {
    
    public static final class RequirementStatus {
        public static final Integer DRAFT = 0;
        public static final Integer PENDING_DEPT = 1;
        public static final Integer PENDING_HR = 2;
        public static final Integer PENDING_FINANCE = 3;
        public static final Integer APPROVED = 4;
        public static final Integer REJECTED = 5;
    }
    
    public static final class PersonnelStatus {
        public static final String PENDING_ENTRY = "PENDING_ENTRY";
        public static final String ON_DUTY = "ON_DUTY";
        public static final String LEAVE = "LEAVE";
        public static final String TRANSFER = "TRANSFER";
        public static final String OFF_DUTY = "OFF_DUTY";
        
        public static String getText(String status) {
            switch (status) {
                case PENDING_ENTRY: return "待入场";
                case ON_DUTY: return "在岗";
                case LEAVE: return "请假";
                case TRANSFER: return "调配中";
                case OFF_DUTY: return "已离场";
                default: return "未知";
            }
        }
    }
    
    public static final class SettlementStatus {
        public static final Integer PENDING = 0;
        public static final Integer HR_CONFIRMED = 1;
        public static final Integer PM_CONFIRMED = 2;
        public static final Integer FINANCE_CONFIRMED = 3;
        public static final Integer SUPPLIER_CONFIRMED = 4;
        public static final Integer PAID = 5;
    }
    
    public static final class SupplierLevel {
        public static final String A = "A";
        public static final String B = "B";
        public static final String C = "C";
    }
    
    public static final class WorkHoursStatus {
        public static final String PENDING = "PENDING";
        public static final String APPROVED = "APPROVED";
        public static final String REJECTED = "REJECTED";
    }
}
