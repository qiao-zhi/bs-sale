package cn.qs.controller.sale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.qs.bean.sale.PlanSale;
import cn.qs.bean.user.User;
import cn.qs.controller.AbstractSequenceController;
import cn.qs.service.BaseService;
import cn.qs.service.sale.PlanSaleService;
import cn.qs.service.user.UserService;
import cn.qs.utils.DefaultValue;
import cn.qs.utils.SaleUtils;
import cn.qs.utils.system.MySystemUtils;

@Controller
@RequestMapping("planSale")
public class PlanSaleController extends AbstractSequenceController<PlanSale> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PlanSaleController.class);

	@Autowired
	private PlanSaleService planSaleService;

	@Autowired
	private UserService userService;

	@RequestMapping("addCustom")
	public String addCustom(ModelMap map) {
		String remark1 = MySystemUtils.getLoginUser().getRemark1();
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("remark1", remark1);
		List<User> sameAreaUsers = userService.listByCondition(condition);
		if (CollectionUtils.isNotEmpty(sameAreaUsers)) {
			map.put("sameAreaUsers", sameAreaUsers);
		}

		SaleUtils.addYearsAndMonths(map);

		return getViewPath("add");
	}

	/**
	 * mybatis分页(交由有需要的子类复写)
	 * 
	 * @param condition
	 * @param request
	 * @return
	 */
	@RequestMapping("page2")
	@ResponseBody
	public PageInfo<PlanSale> page2(@RequestParam Map condition, HttpServletRequest request) {
		int pageNum = 1;
		if (StringUtils.isNotBlank(MapUtils.getString(condition, "pageNum"))) { // 如果不为空的话改变当前页号
			pageNum = MapUtils.getInteger(condition, "pageNum");
		}
		int pageSize = DefaultValue.PAGE_SIZE;
		if (StringUtils.isNotBlank(MapUtils.getString(condition, "pageSize"))) { // 如果不为空的话改变当前页大小
			pageSize = MapUtils.getInteger(condition, "pageSize");
		}

		// 开始分页
		PageHelper.startPage(pageNum, pageSize, " createtime desc");
		List<PlanSale> beans = new ArrayList<PlanSale>();
		try {
			beans = planSaleService.listByCondition(condition);
		} catch (Exception e) {
			LOGGER.error("getUsers error！", e);
		}
		PageInfo<PlanSale> pageInfo = new PageInfo<PlanSale>(beans);
		pageInfo.setPageSize(pageSize);
		
		return pageInfo;
	}

	@Override
	public String getViewBasePath() {
		return "planSale";
	}

	@Override
	public BaseService<PlanSale, Integer> getBaseService() {
		return planSaleService;
	}

}
