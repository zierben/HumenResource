package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.entity.HrProjectMilestone;
import com.hr.admin.mapper.HrProjectMilestoneMapper;
import com.hr.admin.service.HrProjectMilestoneService;
import org.springframework.stereotype.Service;

@Service
public class HrProjectMilestoneServiceImpl extends ServiceImpl<HrProjectMilestoneMapper, HrProjectMilestone> implements HrProjectMilestoneService {}
