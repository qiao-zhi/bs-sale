package cn.qs.service.impl.sale;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.qs.bean.sale.ActuallySale;
import cn.qs.mapper.BaseMapper;
import cn.qs.mapper.sale.ActuallySaleMapper;
import cn.qs.mapper.sale.custom.ActuallySaleCustomMapper;
import cn.qs.service.impl.AbastractBaseSequenceServiceImpl;
import cn.qs.service.sale.ActuallySaleService;
import cn.qs.utils.system.MySystemUtils;

@Service
public class ActuallySaleServiceImpl extends AbastractBaseSequenceServiceImpl<ActuallySale>
		implements ActuallySaleService {

	@Autowired
	private ActuallySaleMapper actuallySaleMapper;

	@Autowired
	private ActuallySaleCustomMapper actuallySaleCustomMapper;

	@Override
	public BaseMapper<ActuallySale, Integer> getBaseMapper() {
		return actuallySaleMapper;
	}

	@Override
	public List<ActuallySale> listByCondition(Map condition) {
		// 放进去可以查看的用户名 信息
		condition.put("saleusernames", MySystemUtils.getLoginUserCanSeeUsernames());
		return actuallySaleCustomMapper.listByCondition(condition);
	}

	@Override
	public List<Map<String, Object>> listActuallyGroupSaleAmount(List<String> userSameAreaUsernames) {
		return actuallySaleCustomMapper.listActuallyGroupSaleAmount(userSameAreaUsernames);
	}

	@Override
	public Map<String, Object> getLastMonthRemain(Map<String, Object> tmpCondition) {
		return actuallySaleCustomMapper.getLastMonthRemain(tmpCondition);
	}
}
