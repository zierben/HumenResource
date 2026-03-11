package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.entity.HrPersonnelSalary;
import com.hr.admin.mapper.HrPersonnelSalaryMapper;
import com.hr.admin.service.HrPersonnelSalaryService;
import org.springframework.stereotype.Service;

@Service
public class HrPersonnelSalaryServiceImpl extends ServiceImpl<HrPersonnelSalaryMapper, HrPersonnelSalary> implements HrPersonnelSalaryService {}
