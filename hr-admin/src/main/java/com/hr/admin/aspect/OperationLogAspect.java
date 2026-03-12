package com.hr.admin.aspect;

import com.hr.admin.annotation.OperationLog;
import com.hr.admin.entity.HrOperationLog;
import com.hr.admin.entity.SysUser;
import com.hr.admin.service.HrOperationLogService;
import com.hr.admin.service.SysUserService;
import com.hr.admin.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class OperationLogAspect {

    private final HrOperationLogService hrOperationLogService;
    private final SysUserService sysUserService;
    private final JwtUtil jwtUtil;

    @Around("@annotation(com.hr.admin.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = point.proceed();
        long time = System.currentTimeMillis() - beginTime;
        
        try {
            saveLog(point, time, null);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
        
        return result;
    }

    private void saveLog(ProceedingJoinPoint joinPoint, long time, Exception e) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        
        if (operationLog == null) {
            return;
        }

        HrOperationLog log = new HrOperationLog();
        log.setModule(operationLog.module());
        log.setAction(operationLog.action());
        log.setTargetType(operationLog.targetType());
        log.setCreateTime(LocalDateTime.now());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.setIp(getIp(request));
            
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                try {
                    String jwt = token.substring(7);
                    String username = jwtUtil.getUsername(jwt);
                    SysUser user = sysUserService.getByUsername(username);
                    if (user != null) {
                        log.setUserId(user.getId());
                        log.setUsername(user.getRealName() != null ? user.getRealName() : username);
                    }
                } catch (Exception ex) {
                    // ignore
                }
            }
            
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                StringBuilder detail = new StringBuilder();
                for (int i = 0; i < args.length && i < 3; i++) {
                    Object arg = args[i];
                    if (arg != null && !arg.getClass().getName().startsWith("jakarta.")) {
                        detail.append(arg.toString()).append("; ");
                    }
                }
                if (detail.length() > 500) {
                    log.setDetail(detail.substring(0, 500));
                } else {
                    log.setDetail(detail.toString());
                }
            }
        }

        hrOperationLogService.save(log);
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
