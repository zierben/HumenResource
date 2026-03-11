package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.entity.HrPayment;
import com.hr.admin.mapper.HrPaymentMapper;
import com.hr.admin.service.HrPaymentService;
import org.springframework.stereotype.Service;

@Service
public class HrPaymentServiceImpl extends ServiceImpl<HrPaymentMapper, HrPayment> implements HrPaymentService {}
