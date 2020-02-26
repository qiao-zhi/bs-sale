package cn.qs.controller.sale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
	public JSONResultUtil<List<Map<String, Object>>> listPersonalCount(@RequestParam Map condition,
			HttpServletRequest request) {
		List<Map<String, Object>> result = SaleUtils.listPersonalCount(condition);
		return new JSONResultUtil<List<Map<String, Object>>>(true, "ok", result);
	}

	@RequestMapping("groupCount")
	public String groupCount(ModelMap map) {
		SaleUtils.addYearsAndMonths(map);
		return getViewPath("groupCount");
	}

	/**
	 * mybatis分页(交由有需要的子类复写)
	 * 
	 * @param condition
	 * @param request
	 * @return
	 */
	@RequestMapping("listGroupCount")
	@ResponseBody
	public JSONResultUtil<List<Map<String, Object>>> listGroupCount(@RequestParam Map condition,
			HttpServletRequest request) {

		List<Map<String, Object>> result = SaleUtils.listPersonalCount(condition);

		// 1.对区域进行分区统计
		Map<String, List<Map<String, Object>>> areaDatas = handleAreaDatas(result);
		// 2. 每个区域单独汇总
		if (MapUtils.isNotEmpty(areaDatas)) {
			result.clear();

			Set<String> keySet = areaDatas.keySet();
			for (String key : keySet) {
				List<Map<String, Object>> list = areaDatas.get(key);
				Map<String, Object> countMap = getCountInfoMap(list);
				if (MapUtils.isNotEmpty(countMap)) {
					list.add(countMap);
				}

				result.addAll(list);
			}
		}

		// 汇总信息
		return new JSONResultUtil<List<Map<String, Object>>>(true, "ok", result);
	}

	private Map<String, Object> getCountInfoMap(List<Map<String, Object>> list) {
		Map<String, Object> countMap = new HashMap<>();

		if (CollectionUtils.isNotEmpty(list)) {
			// 记录汇总信息
			Float planSaleAmountTotal = 0F, actuallySaleAmountTotal = 0F;
			Float planAwayAmountTotal = 0F, actuallyAwayAmountTotal = 0F;
			Float planSocialAmountTotal = 0F, actuallySocialAmountTotal = 0F;
			Float selfPerformanceTotal = 0F;
			Float thisMonthRemainAwayAmountTotal = 0F, lastMonthRemainAwayAmountTotal = 0F;
			Float thisMonthRemainSocialAmountTotal = 0F, lastMonthRemainSocialAmountTotal = 0F;

			int index = 0;
			for (Map<String, Object> tMap : list) {
				planSaleAmountTotal += MapUtils.getFloat(tMap, "planSaleAmount", 0F);
				actuallySaleAmountTotal += MapUtils.getFloat(tMap, "actuallySaleAmount", 0F);
				planAwayAmountTotal += MapUtils.getFloat(tMap, "planAwayAmount", 0F);
				actuallyAwayAmountTotal += MapUtils.getFloat(tMap, "actuallyAwayAmount", 0F);
				planSocialAmountTotal += MapUtils.getFloat(tMap, "planSocialAmount", 0F);
				actuallySocialAmountTotal += MapUtils.getFloat(tMap, "actuallySocialAmount", 0F);
				selfPerformanceTotal += MapUtils.getFloat(tMap, "selfPerformance", 0F);
				thisMonthRemainAwayAmountTotal += MapUtils.getFloat(tMap, "thisMonthRemainAwayAmount", 0F);
				lastMonthRemainAwayAmountTotal += MapUtils.getFloat(tMap, "lastMonthRemainAwayAmount", 0F);
				thisMonthRemainSocialAmountTotal += MapUtils.getFloat(tMap, "thisMonthRemainSocialAmount", 0F);
				lastMonthRemainSocialAmountTotal += MapUtils.getFloat(tMap, "lastMonthRemainSocialAmount", 0F);

				if (index == 0) {
					countMap.put("yearNum", MapUtils.getString(tMap, "yearNum"));
					countMap.put("monthNum", MapUtils.getString(tMap, "monthNum"));
					countMap.put("area", MapUtils.getString(tMap, "area"));
					countMap.put("planGroupSaleAmount", MapUtils.getString(tMap, "planGroupSaleAmount"));
					countMap.put("actuallyGroupSaleAmount", MapUtils.getString(tMap, "actuallyGroupSaleAmount"));
					countMap.put("groupSaleRatio", MapUtils.getString(tMap, "groupSaleRatio"));
					countMap.put("groupPerformance", MapUtils.getString(tMap, "groupPerformance"));
					countMap.put("groupBuildCosts", MapUtils.getString(tMap, "groupBuildCosts"));
					index++;
				}
			}

			countMap.put("index", "汇总");
			countMap.put("planSaleAmount", planSaleAmountTotal);
			countMap.put("actuallySaleAmount", actuallySaleAmountTotal);
			countMap.put("planAwayAmount", planAwayAmountTotal);
			countMap.put("actuallyAwayAmount", actuallyAwayAmountTotal);
			countMap.put("planSocialAmount", planSocialAmountTotal);
			countMap.put("actuallySocialAmount", actuallySocialAmountTotal);
			countMap.put("selfPerformance", selfPerformanceTotal);
			countMap.put("thisMonthRemainAwayAmount", thisMonthRemainAwayAmountTotal);
			countMap.put("lastMonthRemainAwayAmount", lastMonthRemainAwayAmountTotal);
			countMap.put("thisMonthRemainSocialAmount", thisMonthRemainSocialAmountTotal);
			countMap.put("lastMonthRemainSocialAmount", lastMonthRemainSocialAmountTotal);
		}

		return countMap;
	}

	/**
	 * 将数据分区
	 * 
	 * @param allDatas
	 *            数据集合
	 * @return 区域-数据集合
	 */
	private Map<String, List<Map<String, Object>>> handleAreaDatas(List<Map<String, Object>> allDatas) {
		Map<String, List<Map<String, Object>>> result = new HashMap<>();
		if (CollectionUtils.isEmpty(allDatas)) {
			return result;
		}

		for (Map<String, Object> tMap : allDatas) {
			String area = MapUtils.getString(tMap, "area");
			List<Map<String, Object>> list = result.get(area);
			if (CollectionUtils.isEmpty(list)) {
				list = new ArrayList<>();
				result.put(area, list);
			}

			list.add(tMap);
		}
		return result;
	}

	public String getViewPath(String path) {
		return "saleCount/" + path;
	}

}
