package com.hr.admin.dto;

import com.hr.admin.entity.HrProject;
import com.hr.admin.entity.HrProjectMilestone;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ProjectDetailDTO {
    private HrProject project;
    private List<SupplierPersonnel> supplierPersonnel;
    private List<HrProjectMilestone> milestones;
    private BudgetInfo budgetInfo;
    private List<RiskWarning> risks;

    @Data
    public static class SupplierPersonnel {
        private Long supplierId;
        private String supplierName;
        private Integer totalCount;
        private Integer activeCount;
        private List<PersonnelInfo> personnel;
    }

    @Data
    public static class PersonnelInfo {
        private Long id;
        private String name;
        private String positionName;
        private LocalDate entryDate;
        private String status;
    }

    @Data
    public static class BudgetInfo {
        private BigDecimal totalBudget;
        private BigDecimal usedBudget;
        private BigDecimal pendingBudget;
        private Integer usedPercent;
    }

    @Data
    public static class RiskWarning {
        private String type;
        private String level;
        private String message;
    }
}
