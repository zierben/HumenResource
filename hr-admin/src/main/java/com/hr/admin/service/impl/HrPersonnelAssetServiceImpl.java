package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.entity.HrPersonnelAsset;
import com.hr.admin.mapper.HrPersonnelAssetMapper;
import com.hr.admin.service.HrPersonnelAssetService;
import org.springframework.stereotype.Service;

@Service
public class HrPersonnelAssetServiceImpl extends ServiceImpl<HrPersonnelAssetMapper, HrPersonnelAsset> implements HrPersonnelAssetService {}
