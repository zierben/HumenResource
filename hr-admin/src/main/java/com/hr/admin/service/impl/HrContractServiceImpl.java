package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.entity.HrContract;
import com.hr.admin.mapper.HrContractMapper;
import com.hr.admin.service.HrContractService;
import org.springframework.stereotype.Service;

@Service
public class HrContractServiceImpl extends ServiceImpl<HrContractMapper, HrContract> implements HrContractService {}
