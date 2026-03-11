package com.hr.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hr.admin.entity.HrSettlement;

public interface HrSettlementService extends IService<HrSettlement> {
    void generateSettlement(Integer year, Integer month);
    void hrConfirm(Long id);
    void pmConfirm(Long id);
    void financeConfirm(Long id);
    void supplierConfirm(Long id, String remark);
    void pay(Long id);
}
