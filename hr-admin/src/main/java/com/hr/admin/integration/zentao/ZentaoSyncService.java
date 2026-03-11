package com.hr.admin.integration.zentao;

import cn.hutool.core.util.StrUtil;
import com.hr.admin.entity.HrPersonnel;
import com.hr.admin.entity.HrProject;
import com.hr.admin.entity.HrWorkHours;
import com.hr.admin.integration.zentao.entity.ZentaoUser;
import com.hr.admin.integration.zentao.entity.ZentaoProject;
import com.hr.admin.integration.zentao.entity.ZentaoEffort;
import com.hr.admin.integration.zentao.mapper.ZentaoUserMapper;
import com.hr.admin.integration.zentao.mapper.ZentaoProjectMapper;
import com.hr.admin.integration.zentao.mapper.ZentaoEffortMapper;
import com.hr.admin.service.HrProjectService;
import com.hr.admin.service.HrPersonnelService;
import com.hr.admin.service.HrWorkHoursService;
import com.hr.admin.service.HrSyncLogService;
import com.hr.admin.constant.HrConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "zentao.enabled", havingValue = "true")
public class ZentaoSyncService {
    
    private final ZentaoUserMapper zentaoUserMapper;
    private final ZentaoProjectMapper zentaoProjectMapper;
    private final ZentaoEffortMapper zentaoEffortMapper;
    private final HrProjectService hrProjectService;
    private final HrPersonnelService hrPersonnelService;
    private final HrWorkHoursService hrWorkHoursService;
    private final HrSyncLogService hrSyncLogService;
    
    @Transactional
    public void syncUsers(Long logId) {
        log.info("开始同步禅道用户数据");
        
        try {
            List<ZentaoUser> zentaoUsers = zentaoUserMapper.selectActiveUsers();
            int successCount = 0;
            StringBuilder errors = new StringBuilder();
            
            for (ZentaoUser zu : zentaoUsers) {
                try {
                    HrPersonnel existing = hrPersonnelService.lambdaQuery()
                            .eq(HrPersonnel::getZentaoUserId, zu.getId())
                            .one();
                    
                    if (existing == null) {
                        HrPersonnel personnel = new HrPersonnel();
                        personnel.setPersonnelCode("ZT" + System.currentTimeMillis() + zu.getId());
                        personnel.setName(StrUtil.isNotEmpty(zu.getRealname()) ? zu.getRealname() : zu.getAccount());
                        personnel.setZentaoUserId(zu.getId());
                        personnel.setZentaoAccount(zu.getAccount());
                        personnel.setEmail(zu.getEmail());
                        personnel.setPhone(zu.getMobile());
                        personnel.setPositionName("待分配");
                        personnel.setDailyRate(BigDecimal.ZERO);
                        personnel.setSupplierId(1L);
                        personnel.setStatus(HrConstants.PersonnelStatus.PENDING_ENTRY);
                        personnel.setCreateTime(LocalDateTime.now());
                        personnel.setUpdateTime(LocalDateTime.now());
                        hrPersonnelService.save(personnel);
                    } else {
                        existing.setName(StrUtil.isNotEmpty(zu.getRealname()) ? zu.getRealname() : zu.getAccount());
                        existing.setZentaoAccount(zu.getAccount());
                        existing.setEmail(zu.getEmail());
                        existing.setPhone(zu.getMobile());
                        existing.setUpdateTime(LocalDateTime.now());
                        hrPersonnelService.updateById(existing);
                    }
                    successCount++;
                } catch (Exception e) {
                    errors.append(zu.getAccount()).append(":").append(e.getMessage()).append("; ");
                }
            }
            
            String errorMsg = errors.length() > 0 ? errors.toString() : null;
            hrSyncLogService.finishLog(logId, zentaoUsers.size(), successCount, errorMsg);
            log.info("禅道用户同步完成: 总数={}, 成功={}", zentaoUsers.size(), successCount);
            
        } catch (Exception e) {
            log.error("禅道用户同步失败", e);
            hrSyncLogService.finishLog(logId, 0, 0, "数据库连接失败: " + e.getMessage());
        }
    }
    
    @Transactional
    public void syncProjects(Long logId) {
        log.info("开始同步禅道项目数据");
        
        try {
            List<ZentaoProject> zentaoProjects = zentaoProjectMapper.selectActiveProjects();
            int successCount = 0;
            StringBuilder errors = new StringBuilder();
            
            for (ZentaoProject zp : zentaoProjects) {
                try {
                    HrProject existing = hrProjectService.lambdaQuery()
                            .eq(HrProject::getZentaoProjectId, zp.getId())
                            .one();
                    
                    if (existing == null) {
                        HrProject project = new HrProject();
                        project.setProjectCode(zp.getCode());
                        project.setProjectName(zp.getName());
                        project.setZentaoProjectId(zp.getId());
                        project.setManager(zp.getPm());
                        project.setBudget(zp.getBudget() != null ? zp.getBudget() : BigDecimal.ZERO);
                        project.setStatus("doing".equals(zp.getStatus()) ? 1 : 0);
                        project.setCreateTime(LocalDateTime.now());
                        project.setUpdateTime(LocalDateTime.now());
                        hrProjectService.save(project);
                    } else {
                        existing.setProjectName(zp.getName());
                        existing.setManager(zp.getPm());
                        existing.setBudget(zp.getBudget() != null ? zp.getBudget() : BigDecimal.ZERO);
                        existing.setStatus("doing".equals(zp.getStatus()) ? 1 : 0);
                        existing.setUpdateTime(LocalDateTime.now());
                        hrProjectService.updateById(existing);
                    }
                    successCount++;
                } catch (Exception e) {
                    errors.append(zp.getName()).append(":").append(e.getMessage()).append("; ");
                }
            }
            
            String errorMsg = errors.length() > 0 ? errors.toString() : null;
            hrSyncLogService.finishLog(logId, zentaoProjects.size(), successCount, errorMsg);
            log.info("禅道项目同步完成: 总数={}, 成功={}", zentaoProjects.size(), successCount);
            
        } catch (Exception e) {
            log.error("禅道项目同步失败", e);
            hrSyncLogService.finishLog(logId, 0, 0, "数据库连接失败: " + e.getMessage());
        }
    }
    
    @Transactional
    public void syncWorkHours(Long logId, String date) {
        log.info("开始同步禅道工时数据: date={}", date);
        
        try {
            List<ZentaoEffort> efforts = zentaoEffortMapper.selectByDate(date);
            
            Map<Long, HrPersonnel> personnelByIdMap = hrPersonnelService.lambdaQuery()
                    .isNotNull(HrPersonnel::getZentaoUserId)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(HrPersonnel::getZentaoUserId, p -> p, (a, b) -> a));
            
            Map<String, HrPersonnel> personnelByAccountMap = hrPersonnelService.lambdaQuery()
                    .isNotNull(HrPersonnel::getZentaoAccount)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(HrPersonnel::getZentaoAccount, p -> p, (a, b) -> a));
            
            Map<Long, HrProject> projectMap = hrProjectService.lambdaQuery()
                    .isNotNull(HrProject::getZentaoProjectId)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(HrProject::getZentaoProjectId, p -> p, (a, b) -> a));
            
            int successCount = 0;
            StringBuilder errors = new StringBuilder();
            
            for (ZentaoEffort ze : efforts) {
                try {
                    HrPersonnel personnel = personnelByAccountMap.get(ze.getAccount());
                    
                    if (personnel == null) {
                        errors.append(ze.getAccount()).append(":人员未同步,请先同步人员; ");
                        continue;
                    }
                    
                    HrProject project = projectMap.get(ze.getProject());
                    Long projectId = project != null ? project.getId() : null;
                    
                    HrWorkHours existing = hrWorkHoursService.lambdaQuery()
                            .eq(HrWorkHours::getPersonnelId, personnel.getId())
                            .eq(HrWorkHours::getWorkDate, LocalDate.parse(ze.getDate()))
                            .one();
                    
                    if (existing == null) {
                        HrWorkHours workHours = new HrWorkHours();
                        workHours.setPersonnelId(personnel.getId());
                        workHours.setProjectId(projectId);
                        workHours.setWorkDate(LocalDate.parse(ze.getDate()));
                        workHours.setHours(ze.getConsumed() != null ? ze.getConsumed() : BigDecimal.ZERO);
                        workHours.setStatus("PENDING");
                        workHours.setRemark(ze.getWork());
                        workHours.setCreateTime(LocalDateTime.now());
                        workHours.setUpdateTime(LocalDateTime.now());
                        hrWorkHoursService.save(workHours);
                    } else {
                        existing.setHours(ze.getConsumed() != null ? ze.getConsumed() : BigDecimal.ZERO);
                        existing.setProjectId(projectId);
                        existing.setRemark(ze.getWork());
                        existing.setUpdateTime(LocalDateTime.now());
                        hrWorkHoursService.updateById(existing);
                    }
                    successCount++;
                } catch (Exception e) {
                    errors.append(ze.getId()).append(":").append(e.getMessage()).append("; ");
                }
            }
            
            String errorMsg = errors.length() > 0 ? errors.toString() : null;
            hrSyncLogService.finishLog(logId, efforts.size(), successCount, errorMsg);
            log.info("禅道工时同步完成: 总数={}, 成功={}", efforts.size(), successCount);
            
        } catch (Exception e) {
            log.error("禅道工时同步失败", e);
            hrSyncLogService.finishLog(logId, 0, 0, "数据库连接失败: " + e.getMessage());
        }
    }
}
