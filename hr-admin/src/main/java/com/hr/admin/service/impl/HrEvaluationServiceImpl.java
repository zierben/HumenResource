package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.entity.HrEvaluation;
import com.hr.admin.mapper.HrEvaluationMapper;
import com.hr.admin.service.HrEvaluationService;
import org.springframework.stereotype.Service;

@Service
public class HrEvaluationServiceImpl extends ServiceImpl<HrEvaluationMapper, HrEvaluation> implements HrEvaluationService {}
