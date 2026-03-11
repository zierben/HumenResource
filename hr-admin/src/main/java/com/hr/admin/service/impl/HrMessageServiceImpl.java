package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.entity.HrMessage;
import com.hr.admin.mapper.HrMessageMapper;
import com.hr.admin.service.HrMessageService;
import org.springframework.stereotype.Service;

@Service
public class HrMessageServiceImpl extends ServiceImpl<HrMessageMapper, HrMessage> implements HrMessageService {}
