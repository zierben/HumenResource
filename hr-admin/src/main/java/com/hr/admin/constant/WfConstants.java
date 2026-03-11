package com.hr.admin.constant;

public class WfConstants {
    
    public static final class WorkflowStatus {
        public static final Integer DRAFT = 0;
        public static final Integer ACTIVE = 1;
        public static final Integer INACTIVE = 2;
    }
    
    public static final class InstanceStatus {
        public static final Integer PENDING = 0;
        public static final Integer APPROVED = 1;
        public static final Integer REJECTED = 2;
        public static final Integer CANCELLED = 3;
    }
    
    public static final class NodeType {
        public static final String START = "START";
        public static final String APPROVAL = "APPROVAL";
        public static final String END = "END";
    }
    
    public static final class Action {
        public static final String SUBMIT = "SUBMIT";
        public static final String APPROVE = "APPROVE";
        public static final String REJECT = "REJECT";
        public static final String CANCEL = "CANCEL";
    }
}
