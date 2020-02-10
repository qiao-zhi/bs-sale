package cn.qs.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.ui.ModelMap;

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

	public static Map<String, Object> listPersonalCount(Map condition) {
		return null;
	}
}
