package cn.qs.controller.sale;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.qs.utils.JSONResultUtil;
import cn.qs.utils.SaleUtils;

/**
 * 销售统计
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("saleCount")
public class SaleCountController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SaleCountController.class);

	@RequestMapping("personalCount")
	public String personalCount(ModelMap map) {
		SaleUtils.addYearsAndMonths(map);
		return getViewPath("personalCount");
	}

	/**
	 * mybatis分页(交由有需要的子类复写)
	 * 
	 * @param condition
	 * @param request
	 * @return
	 */
	@RequestMapping("listPersonalCount")
	@ResponseBody
	public JSONResultUtil<Map<String, Object>> listPersonalCount(@RequestParam Map condition,
			HttpServletRequest request) {
		Map<String, Object> result = SaleUtils.listPersonalCount(condition);
		return new JSONResultUtil<Map<String, Object>>(true, "ok", result);
	}

	@RequestMapping("groupCount")
	public String groupCount(ModelMap map) {
		SaleUtils.addYearsAndMonths(map);
		return getViewPath("groupCount");
	}

	@RequestMapping("companyCount")
	public String companyCount(ModelMap map) {
		SaleUtils.addYearsAndMonths(map);
		return getViewPath("companyCount");
	}

	public String getViewPath(String path) {
		return "saleCount/" + path;
	}

}
