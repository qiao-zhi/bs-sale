package cn.qs.service.sale;

import java.util.List;
import java.util.Map;

import cn.qs.bean.sale.ActuallySale;
import cn.qs.service.BaseSequenceService;

public interface ActuallySaleService extends BaseSequenceService<ActuallySale> {

	List<Map<String, Object>> listActuallyGroupSaleAmount(List<String> userSameAreaUsernames);

	Map<String, Object> getLastMonthRemain(Map<String, Object> tmpCondition);

}
