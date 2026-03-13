package com.hr.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.admin.entity.HrDepartment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface HrDepartmentMapper extends BaseMapper<HrDepartment> {
    
    @Select("SELECT COUNT(*) FROM sys_user WHERE dept_id = #{deptId} AND deleted = 0")
    int countMembers(Long deptId);
}
