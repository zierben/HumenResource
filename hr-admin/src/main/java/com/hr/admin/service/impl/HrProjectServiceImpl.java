package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.entity.HrProject;
import com.hr.admin.mapper.HrProjectMapper;
import com.hr.admin.service.HrProjectService;
import org.springframework.stereotype.Service;

@Service
public class HrProjectServiceImpl extends ServiceImpl<HrProjectMapper, HrProject> implements HrProjectService {
}
