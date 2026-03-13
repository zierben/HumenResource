package com.hr.admin.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class SupplierDetailVO {
    private Long id;
    private String supplierName;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private String level;
    private Integer deliveryRate;
    private Integer currentPersonCount;
    private Integer status;
    
    private BigDecimal totalContractAmount;
    private BigDecimal totalSettledAmount;
    private BigDecimal pendingSettlement;
    
    private Integer totalPersonnel;
    private Integer activePersonnel;
    private Integer exitPersonnel;
    private BigDecimal turnoverRate;
    
    private Map<String, BigDecimal> avgScores;
    private List<Map<String, Object>> scoreHistory;
    private List<Map<String, Object>> monthlyScores;
    
    private List<Map<String, Object>> projects;
    private List<Map<String, Object>> contracts;
    private List<Map<String, Object>> personnelList;
    private List<Map<String, Object>> settlements;
}
