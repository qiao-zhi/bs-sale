package cn.qs.mapper.sale.custom;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import cn.qs.bean.sale.PlanSale;

@Mapper
public interface PlanSaleCustomMapper {

	List<PlanSale> listByCondition(Map condition);

	List<Map<String, Object>> listPlanGroupSaleAmount(List<String> userSameAreaUsernames);

	Map<String, Object> getLastMonthRemain(Map<String, Object> tmpCondition);
}
