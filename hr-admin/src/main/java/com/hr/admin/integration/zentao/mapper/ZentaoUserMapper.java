package com.hr.admin.integration.zentao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.admin.integration.zentao.entity.ZentaoUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
@DS("zentao")
public interface ZentaoUserMapper extends BaseMapper<ZentaoUser> {
    
    @Select("SELECT id, account, realname, email, mobile, phone, gender, deleted FROM zt_user WHERE deleted = '0'")
    List<ZentaoUser> selectActiveUsers();
}
