package com.hr.admin.dto;

import com.hr.admin.entity.HrPersonnel;
import com.hr.admin.entity.HrPersonnelAsset;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PersonnelDetailDTO {
    private HrPersonnel personnel;
    private String supplierName;
    private String projectName;
    private List<HrPersonnelAsset> assets;
    private List<SalaryRecord> salaryHistory;
    private List<ProjectHistory> projectHistory;
    private WorkStatistics statistics;

    @Data
    public static class SalaryRecord {
        private BigDecimal amount;
        private String salaryType;
        private LocalDate effectiveDate;
        private String reason;
    }

    @Data
    public static class ProjectHistory {
        private Long projectId;
        private String projectName;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer workDays;
        private String evaluation;
    }

    @Data
    public static class WorkStatistics {
        private Integer totalProjects;
        private BigDecimal totalWorkHours;
        private BigDecimal avgScore;
    }
}
