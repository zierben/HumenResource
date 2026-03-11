package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.entity.HrOperationLog;
import com.hr.admin.mapper.HrOperationLogMapper;
import com.hr.admin.service.HrOperationLogService;
import org.springframework.stereotype.Service;

@Service
public class HrOperationLogServiceImpl extends ServiceImpl<HrOperationLogMapper, HrOperationLog> implements HrOperationLogService {}
