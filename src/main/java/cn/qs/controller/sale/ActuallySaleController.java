package cn.qs.controller.sale;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.qs.bean.sale.ActuallySale;
import cn.qs.controller.AbstractSequenceController;
import cn.qs.service.BaseService;
import cn.qs.service.sale.ActuallySaleService;
import cn.qs.utils.DefaultValue;

@Controller
@RequestMapping("actuallySale")
public class ActuallySaleController extends AbstractSequenceController<ActuallySale> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActuallySaleController.class);

	@Autowired
	private ActuallySaleService actuallySaleService;

	/**
	 * mybatis分页(交由有需要的子类复写)
	 * 
	 * @param condition
	 * @param request
	 * @return
	 */
	@RequestMapping("page2")
	@ResponseBody
	public PageInfo<ActuallySale> page2(@RequestParam Map condition, HttpServletRequest request) {
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
		List<ActuallySale> beans = new ArrayList<ActuallySale>();
		try {
			beans = actuallySaleService.listByCondition(condition);
		} catch (Exception e) {
			LOGGER.error("getUsers error！", e);
		}
		PageInfo<ActuallySale> pageInfo = new PageInfo<ActuallySale>(beans);
		pageInfo.setPageSize(pageSize);
		return pageInfo;
	}

	@Override
	public String getViewBasePath() {
		return "actuallySale";
	}

	@Override
	public BaseService<ActuallySale, Integer> getBaseService() {
		return actuallySaleService;
	}

}
