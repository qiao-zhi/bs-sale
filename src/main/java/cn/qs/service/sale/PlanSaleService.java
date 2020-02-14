package cn.qs.service.sale;

import java.util.List;
import java.util.Map;

import cn.qs.bean.sale.PlanSale;
import cn.qs.service.BaseSequenceService;

public interface PlanSaleService extends BaseSequenceService<PlanSale> {

	List<Map<String, Object>> listPlanGroupSaleAmount(List<String> userSameAreaUsernames);

	Map<String, Object> getLastMonthRemain(Map<String, Object> tmpCondition);

}
