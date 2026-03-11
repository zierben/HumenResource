package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.entity.HrCandidate;
import com.hr.admin.mapper.HrCandidateMapper;
import com.hr.admin.service.HrCandidateService;
import org.springframework.stereotype.Service;

@Service
public class HrCandidateServiceImpl extends ServiceImpl<HrCandidateMapper, HrCandidate> implements HrCandidateService {}
