package cn.qs.service.impl.sale;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.qs.bean.sale.PlanSale;
import cn.qs.mapper.BaseMapper;
import cn.qs.mapper.sale.PlanSaleMapper;
import cn.qs.mapper.sale.custom.PlanSaleCustomMapper;
import cn.qs.service.impl.AbastractBaseSequenceServiceImpl;
import cn.qs.service.sale.PlanSaleService;
import cn.qs.utils.system.MySystemUtils;

@Service
public class PlanSaleServiceImpl extends AbastractBaseSequenceServiceImpl<PlanSale> implements PlanSaleService {

	@Autowired
	private PlanSaleMapper planSaleMapper;
	
	@Autowired
	private PlanSaleCustomMapper planSaleCustomMapper;

	@Override
	public BaseMapper<PlanSale, Integer> getBaseMapper() {
		return planSaleMapper;
	}

	@Override
	public List<PlanSale> listByCondition(Map condition) {
		// 放进去可以查看的用户名 信息
		condition.put("saleusernames", MySystemUtils.getLoginUserCanSeeUsernames());
		return planSaleCustomMapper.listByCondition(condition);
	}

	@Override
	public List<Map<String, Object>> listPlanGroupSaleAmount(List<String> userSameAreaUsernames) {
		return planSaleCustomMapper.listPlanGroupSaleAmount(userSameAreaUsernames);
	}

	@Override
	public Map<String, Object> getLastMonthRemain(Map<String, Object> tmpCondition) {
		return planSaleCustomMapper.getLastMonthRemain(tmpCondition);
	}
}
