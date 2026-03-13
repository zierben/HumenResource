package com.hr.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.admin.entity.HrSupplierScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface HrSupplierScoreMapper extends BaseMapper<HrSupplierScore> {
    
    @Select("SELECT AVG(tech_ability) as techAbility, AVG(delivery_quality) as deliveryQuality, " +
            "AVG(response_speed) as responseSpeed, AVG(communication) as communication, " +
            "AVG(stability) as stability, AVG(overall_score) as overallScore " +
            "FROM hr_supplier_score WHERE supplier_id = #{supplierId} AND deleted = 0")
    Map<String, BigDecimal> getAverageScores(Long supplierId);
    
    @Select("SELECT DATE_FORMAT(score_date, '%Y-%m') as month, AVG(overall_score) as score " +
            "FROM hr_supplier_score WHERE supplier_id = #{supplierId} AND deleted = 0 " +
            "GROUP BY DATE_FORMAT(score_date, '%Y-%m') ORDER BY month DESC LIMIT 12")
    List<Map<String, Object>> getMonthlyScores(Long supplierId);
}
