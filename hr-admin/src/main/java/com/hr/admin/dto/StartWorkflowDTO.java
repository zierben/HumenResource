package com.hr.admin.dto;

import lombok.Data;

@Data
public class StartWorkflowDTO {
    private Long workflowId;
    private String businessType;
    private Long businessId;
    private String title;
}
