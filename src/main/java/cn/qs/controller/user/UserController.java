package cn.qs.controller.user;

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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.qs.bean.user.User;
import cn.qs.controller.AbstractSequenceController;
import cn.qs.service.BaseService;
import cn.qs.service.user.UserService;
import cn.qs.utils.DefaultValue;
import cn.qs.utils.JSONResultUtil;
import cn.qs.utils.securty.MD5Utils;
import cn.qs.utils.system.MySystemUtils;

@Controller
@RequestMapping("user")
public class UserController extends AbstractSequenceController<User> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	public String getViewBasePath() {
		return "user";
	}

	@RequestMapping("/addUser")
	public String add(String from, ModelMap map) {
		if (StringUtils.isNotBlank(from)) {
			map.addAttribute("from", from);
		}

		// 添加所属的区域
		String areas = MySystemUtils.getProperty("areas", "华北,华南,华中");
		String[] split = areas.split(",|，");
		map.addAttribute("areas", split);

		return getViewPath("add");
	}

	@RequestMapping("updateUser")
	public String updateUser(Integer id, String from, ModelMap map, HttpServletRequest request) {
		if ("personal".equals(from)) {
			User user = (User) request.getSession().getAttribute("user");
			id = user.getId();
		} else {
			map.addAttribute("from", "admin");
		}

		User user = userService.findById(id);
		map.addAttribute("user", user);

		// 添加所属的区域
		String areas = MySystemUtils.getProperty("areas", "华北,华南,华中");
		String[] split = areas.split(",|，");
		map.addAttribute("areas", split);

		return getViewPath("update");
	}

	@RequestMapping("doAddUser")
	@ResponseBody
	@Override
	public JSONResultUtil doAdd(User user, HttpServletRequest request) {
		if (user != null && "admin".equals(user.getUsername())) {
			return JSONResultUtil.error("您不能添加管理员用户");
		}

		User findUser = userService.findUserByUsername(user.getUsername());
		if (findUser != null) {
			return JSONResultUtil.error("用户已经存在");
		}

		// md5加密密码
		user.setPassword(MD5Utils.md5(user.getPassword()));
		if (StringUtils.isBlank(user.getRoles())) {
			user.setRoles("普通用户");
		}

		user.setProperty("isJson", false);

		userService.add(user);
		return JSONResultUtil.ok();
	}

	/**
	 * JSON形式的数据
	 * 
	 * @param user
	 * @param request
	 * @return
	 */
	@RequestMapping("doAddUserJSON")
	@ResponseBody
	public JSONResultUtil doAddUserJSON(@RequestBody User user, HttpServletRequest request) {
		if (user != null && "admin".equals(user.getUsername())) {
			return JSONResultUtil.error("您不能添加管理员用户");
		}

		User findUser = userService.findUserByUsername(user.getUsername());
		if (findUser != null) {
			return JSONResultUtil.error("用户已经存在");
		}

		user.setPassword(MD5Utils.md5(user.getPassword()));// md5加密密码
		if (StringUtils.isBlank(user.getRoles())) {
			user.setRoles("普通用户");
		}

		userService.add(user);
		return JSONResultUtil.ok();
	}

	/**
	 * Mybatis分页(重写方法)
	 * 
	 * @param condition
	 * @return
	 */
	@RequestMapping("page2")
	@ResponseBody
	@Override
	public PageInfo<User> page2(@RequestParam Map condition, HttpServletRequest request) {
		int pageNum = 1;
		if (StringUtils.isNotBlank(MapUtils.getString(condition, "pageNum"))) { // 如果不为空的话改变当前页号
			pageNum = MapUtils.getInteger(condition, "pageNum");
		}
		int pageSize = DefaultValue.PAGE_SIZE;
		if (StringUtils.isNotBlank(MapUtils.getString(condition, "pageSize"))) { // 如果不为空的话改变当前页大小
			pageSize = MapUtils.getInteger(condition, "pageSize");
		}

		// 开始分页
		PageHelper.startPage(pageNum, pageSize);
		List<User> users = new ArrayList<User>();
		try {
			users = userService.listByCondition(condition);
		} catch (Exception e) {
			LOGGER.error("getUsers error！", e);
		}
		PageInfo<User> pageInfo = new PageInfo<User>(users);

		return pageInfo;
	}

	@Override
	public BaseService<User, Integer> getBaseService() {
		return userService;
	}
}