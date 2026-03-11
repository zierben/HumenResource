package com.hr.admin.integration.zentao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.admin.integration.zentao.entity.ZentaoProject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
@DS("zentao")
public interface ZentaoProjectMapper extends BaseMapper<ZentaoProject> {
    
    @Select("SELECT id, code, name, status, pm, `desc`, deleted, begin, end, budget, estimate, consumed FROM zt_project WHERE deleted = '0'")
    List<ZentaoProject> selectActiveProjects();
}
