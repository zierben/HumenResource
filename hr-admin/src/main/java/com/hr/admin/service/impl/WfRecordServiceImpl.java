package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.entity.WfRecord;
import com.hr.admin.mapper.WfRecordMapper;
import com.hr.admin.service.WfRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WfRecordServiceImpl extends ServiceImpl<WfRecordMapper, WfRecord> implements WfRecordService {
    
    @Override
    public List<WfRecord> getByInstanceId(Long instanceId) {
        LambdaQueryWrapper<WfRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WfRecord::getInstanceId, instanceId)
               .orderByAsc(WfRecord::getCreateTime);
        return list(wrapper);
    }
}
