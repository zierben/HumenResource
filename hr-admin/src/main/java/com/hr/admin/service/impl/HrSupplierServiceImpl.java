package com.hr.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hr.admin.entity.HrSupplier;
import com.hr.admin.mapper.HrSupplierMapper;
import com.hr.admin.service.HrSupplierService;
import org.springframework.stereotype.Service;

@Service
public class HrSupplierServiceImpl extends ServiceImpl<HrSupplierMapper, HrSupplier> implements HrSupplierService {
}
