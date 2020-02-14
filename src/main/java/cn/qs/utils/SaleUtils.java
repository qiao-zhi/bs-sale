package cn.qs.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.ui.ModelMap;

import cn.qs.bean.sale.ActuallySale;
import cn.qs.bean.sale.PlanSale;
import cn.qs.bean.user.User;
import cn.qs.service.sale.ActuallySaleService;
import cn.qs.service.sale.PlanSaleService;
import cn.qs.service.user.UserService;
import cn.qs.utils.system.MySystemUtils;
import cn.qs.utils.system.SpringBootUtils;

/**
 * 专用工具类
 * 
 * @author Administrator
 *
 */
public class SaleUtils {

	public static void addYearsAndMonths(ModelMap map) {
		map.put("years", SaleUtils.listYears());
		map.put("months", SaleUtils.listMonths());
		map.put("currentYear", DateFormatUtils.format(new Date(), "yyyy"));
		map.put("currentMonth", DateFormatUtils.format(new Date(), "MM"));
	}

	public static List<String> listYears() {
		List<String> result = new ArrayList<>();
		result.add("");

		String currentYear = DateFormatUtils.format(new Date(), "yyyy");
		Integer currentYearInt = Integer.valueOf(currentYear);
		for (int i = 5; i > 0; i--) {
			result.add((currentYearInt - i) + "");
		}

		for (int i = 0; i <= 5; i++) {
			result.add((currentYearInt + i) + "");
		}

		return result;
	}

	public static Map<String, String> listMonths() {
		Map<String, String> result = new LinkedHashMap<>();
		result.put("", "---不限制---");
		result.put("01", "1月");
		result.put("02", "2月");
		result.put("03", "3月");
		result.put("04", "4月");
		result.put("05", "5月");
		result.put("06", "6月");
		result.put("07", "7月");
		result.put("08", "8月");
		result.put("09", "9月");
		result.put("10", "10月");
		result.put("11", "11月");
		result.put("12", "12月");

		return result;
	}

	/**
	 * 个人业绩统计
	 * 
	 * @param condition
	 * @return
	 */
	public static List<Map<String, Object>> listPersonalCount(Map condition) {
		// 根据年度和季度查到预算表，并且查到实际销售表
		PlanSaleService planSaleService = SpringBootUtils.getBean(PlanSaleService.class);
		List<PlanSale> planSales = planSaleService.listByCondition(condition);

		List<Map<String, Object>> result = new ArrayList<>();
		if (CollectionUtils.isEmpty(planSales)) {
			return result;
		}

		// 月份_区域 - (groupPerformance-团队绩效， selfPerformance - 个人绩效和)
		Map<String, Map<String, Object>> performanceMap = new LinkedHashMap<>();

		UserService userService = SpringBootUtils.getBean(UserService.class);
		// 双层遍历做处理
		int index = 0;
		for (PlanSale planSale : planSales) {
			// 获取到用户名
			String saleusername = planSale.getSaleusername();
			User saleUser = userService.findUserByUsername(saleusername);
			if (saleUser == null) {
				continue;
			}

			Map<String, Object> record = new HashMap<>();
			result.add(record);

			String area = saleUser.getRemark1();
			record.put("index", ++index);// 序号
			record.put("saleUsername", saleusername);
			record.put("saleFullname", saleUser.getFullname());
			record.put("area", area);
			record.put("yearNum", planSale.getYearnum());
			record.put("monthNum", planSale.getMonthnum());
			record.put("planSaleAmount", planSale.getSaleamount());
			record.put("planSocialAmount", planSale.getSocialamount());
			record.put("planAwayAmount", planSale.getAwayamount());
			// 团队预计销售
			Object planGroupSaleAmount = getPlanGroupSaleAmount(planSale, planSaleService);
			record.put("planGroupSaleAmount", planGroupSaleAmount);

			Map<String, Object> tmpCondition = new HashMap<>();
			tmpCondition.put("saleusername", planSale.getSaleusername());
			tmpCondition.put("yearnum", planSale.getYearnum());
			tmpCondition.put("monthnum", planSale.getMonthnum());

			ActuallySaleService actuallySaleService = SpringBootUtils.getBean(ActuallySaleService.class);

			// 上月结余差旅和应酬(实际-预计)
			Float lastMonthRemainSocialAmount = 0F;
			Float lastMonthRemainAwayAmount = 0F;
			if (!"01".equals(planSale.getMonthnum())) {
				Map<String, Object> planAmounts = planSaleService.getLastMonthRemain(tmpCondition);
				Float planSocialAmounts = MapUtils.getFloat(planAmounts, "socialamounts", 0F);
				Float planAwayAmounts = MapUtils.getFloat(planAmounts, "awayamounts", 0F);

				Map<String, Object> actuallyAmounts = actuallySaleService.getLastMonthRemain(tmpCondition);
				Float actuallySocialAmounts = MapUtils.getFloat(actuallyAmounts, "socialamounts", 0F);
				Float actuallyAwayAmounts = MapUtils.getFloat(actuallyAmounts, "awayamounts", 0F);

				lastMonthRemainSocialAmount = planSocialAmounts - actuallySocialAmounts;
				lastMonthRemainAwayAmount = planAwayAmounts - actuallyAwayAmounts;
			}
			record.put("lastMonthRemainSocialAmount", lastMonthRemainSocialAmount);
			record.put("lastMonthRemainAwayAmount", lastMonthRemainAwayAmount);

			// 获取实际销售信息
			List<ActuallySale> actuallySales = actuallySaleService.listByCondition(tmpCondition);
			if (CollectionUtils.isEmpty(actuallySales)) {
				continue;
			}
			ActuallySale actuallySale = actuallySales.get(0);
			record.put("actuallySaleAmount", actuallySale.getSaleamount());
			record.put("actuallySocialAmount", actuallySale.getSocialamount());
			record.put("actuallyAwayAmount", actuallySale.getAwayamount());
			// 团队实际销售
			Object actuallyGroupSaleAmount = getActuallyGroupSaleAmount(actuallySale, actuallySaleService);
			record.put("actuallyGroupSaleAmount", actuallyGroupSaleAmount);
			Float actuallyGroupSaleAmountFloat = NumberUtils.toFloat(actuallyGroupSaleAmount.toString());
			Float planGroupSaleAmountFloat = NumberUtils.toFloat(planGroupSaleAmount.toString(), 0.0F);
			Float groupSaleRatio = 0F;
			if (!planGroupSaleAmountFloat.equals(0.0)) {
				groupSaleRatio = actuallyGroupSaleAmountFloat / planGroupSaleAmountFloat;
			}
			record.put("groupSaleRatio", groupSaleRatio);

			// 计算团队成本：工资 * 1.15
			// Float awayAndSocialAmount =
			// getPlanGroupAwayAndSocialAmount(planSale, planSaleService);
			Float salarys = getGroupSalarys(planSale.getSaleusername());
			// Float groupCosts = awayAndSocialAmount + salarys * 1.15F;
			Float groupCosts = salarys * 1.15F;

			// 团队绩效系数
			Float groupPerformanceRatio = getGroupPerformanceRatio(groupSaleRatio);
			// 团队绩效金额(实际团队销售*团队绩效系数-团队成本)
			Float groupPerformance = actuallyGroupSaleAmountFloat * groupPerformanceRatio - groupCosts;
			record.put("groupPerformance", groupPerformance);

			// 个人绩效金额（实际员工销售/实际团队销售*团队绩效金额*员工销售系数）
			Float selaActuallySaleAmount = NumberUtils.toFloat(actuallySale.getSaleamount());
			Float selfPerformance = selaActuallySaleAmount / actuallyGroupSaleAmountFloat * groupPerformance
					* NumberUtils.toFloat(planSale.getRatio());
			record.put("selfPerformance", selfPerformance);

			Map<String, Object> map = performanceMap.get(planSale.getMonthnum() + "_" + area);
			if (map == null) {
				map = new HashMap<>();
				performanceMap.put(planSale.getMonthnum() + "_" + area, map);
			}
			map.put("groupPerformance", groupPerformance);
			map.put("selfPerformance", MapUtils.getFloat(map, "selfPerformance", 0F) + selfPerformance);

			// 本月结余差旅和应酬(实际-预计)
			record.put("thisMonthRemainSocialAmount", NumberUtils.toFloat(planSale.getSocialamount(), 0F)
					- NumberUtils.toFloat(actuallySale.getSocialamount(), 0F));
			record.put("thisMonthRemainAwayAmount", NumberUtils.toFloat(planSale.getAwayamount(), 0F)
					- NumberUtils.toFloat(actuallySale.getAwayamount(), 0F));
		}

		// 处理经理绩效(团队绩效-所有销售绩效)
		for (Map<String, Object> tMap : result) {
			String saleusername = tMap.get("saleUsername").toString();
			User findUserByUsername = SpringBootUtils.getBean(UserService.class).findUserByUsername(saleusername);
			if (findUserByUsername == null || !DefaultValue.ROLE_LEADER.equals(findUserByUsername.getRoles())) {
				continue;
			}

			String area = findUserByUsername.getRemark1();
			String monthNum = MapUtils.getString(tMap, "monthNum");
			Map<String, Object> map = performanceMap.get(monthNum + "_" + area);
			Float groupPerformance = MapUtils.getFloat(map, "groupPerformance", 0F);
			Float selfPerformance = MapUtils.getFloat(map, "selfPerformance", 0F);
			tMap.put("selfPerformance", groupPerformance - selfPerformance);
		}

		return result;
	}

	private static Float getGroupPerformanceRatio(Float groupSaleRatio) {
		if (groupSaleRatio >= 1) {
			return 0.079F;
		} else if (groupSaleRatio >= 0.8) {
			return 0.077F;
		}
		return 0.075F;
	}

	private static Float getGroupSalarys(String saleusername) {
		List<String> userSameAreaUsernames = MySystemUtils.getUserSameAreaUsernames(saleusername);
		UserService userService = SpringBootUtils.getBean(UserService.class);
		Float salarys = userService.getGroupSalarys(userSameAreaUsernames);
		return salarys;
	}

	private static Float getPlanGroupAwayAndSocialAmount(PlanSale planSale, PlanSaleService planSaleService) {
		String saleusername = planSale.getSaleusername();
		List<String> userSameAreaUsernames = MySystemUtils.getUserSameAreaUsernames(saleusername);
		if (CollectionUtils.isEmpty(userSameAreaUsernames)) {
			return 0F;
		}

		List<Map<String, Object>> planGroupSaleAmounts = planSaleService.listPlanGroupSaleAmount(userSameAreaUsernames);
		for (Map<String, Object> tmpMap : planGroupSaleAmounts) {
			String yearnum = tmpMap.get("yearnum").toString();
			String monthnum = tmpMap.get("monthnum").toString();
			if (yearnum.equals(planSale.getYearnum()) && monthnum.equals(planSale.getMonthnum())) {
				Float awayamounts = NumberUtils.toFloat(tmpMap.get("awayamounts").toString());
				Float socialamounts = NumberUtils.toFloat(tmpMap.get("socialamounts").toString());
				return awayamounts + socialamounts;
			}
		}

		return 0F;
	}

	private static Object getActuallyGroupSaleAmount(ActuallySale actuallySale,
			ActuallySaleService actuallySaleService) {
		String saleusername = actuallySale.getSaleusername();
		List<String> userSameAreaUsernames = MySystemUtils.getUserSameAreaUsernames(saleusername);
		if (CollectionUtils.isEmpty(userSameAreaUsernames)) {
			return 0;
		}

		List<Map<String, Object>> planGroupSaleAmounts = actuallySaleService
				.listActuallyGroupSaleAmount(userSameAreaUsernames);
		for (Map<String, Object> tmpMap : planGroupSaleAmounts) {
			String yearnum = tmpMap.get("yearnum").toString();
			String monthnum = tmpMap.get("monthnum").toString();
			if (yearnum.equals(actuallySale.getYearnum()) && monthnum.equals(actuallySale.getMonthnum())) {
				return tmpMap.get("saleamounts");
			}
		}

		return 0;
	}

	private static Object getPlanGroupSaleAmount(PlanSale planSale, PlanSaleService planSaleService) {
		String saleusername = planSale.getSaleusername();
		List<String> userSameAreaUsernames = MySystemUtils.getUserSameAreaUsernames(saleusername);
		if (CollectionUtils.isEmpty(userSameAreaUsernames)) {
			return 0;
		}

		List<Map<String, Object>> planGroupSaleAmounts = planSaleService.listPlanGroupSaleAmount(userSameAreaUsernames);
		for (Map<String, Object> tmpMap : planGroupSaleAmounts) {
			String yearnum = tmpMap.get("yearnum").toString();
			String monthnum = tmpMap.get("monthnum").toString();
			if (yearnum.equals(planSale.getYearnum()) && monthnum.equals(planSale.getMonthnum())) {
				return tmpMap.get("saleamounts");
			}
		}

		return 0;
	}
}
