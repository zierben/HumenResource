package com.hr.admin.dto;

import com.hr.admin.entity.WfInstance;
import com.hr.admin.entity.WfRecord;
import lombok.Data;
import java.util.List;

@Data
public class InstanceDetailDTO {
    private WfInstance instance;
    private List<WfRecord> records;
}
