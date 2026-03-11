package com.hr.admin.integration.zentao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.admin.integration.zentao.entity.ZentaoEffort;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
@DS("zentao")
public interface ZentaoEffortMapper extends BaseMapper<ZentaoEffort> {
    
    @Select("SELECT id, objectType, objectID, work, date, consumed, `left`, account, editedDate, project, execution, task " +
            "FROM zt_effort WHERE date = #{date}")
    List<ZentaoEffort> selectByDate(@Param("date") String date);
    
    @Select("SELECT id, objectType, objectID, work, date, consumed, `left`, account, editedDate, project, execution, task " +
            "FROM zt_effort WHERE date >= #{startDate} AND date <= #{endDate}")
    List<ZentaoEffort> selectByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
