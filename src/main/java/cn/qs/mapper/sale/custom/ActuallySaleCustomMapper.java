package cn.qs.mapper.sale.custom;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import cn.qs.bean.sale.ActuallySale;
@Mapper
public interface ActuallySaleCustomMapper {

	List<ActuallySale> listByCondition(Map condition);
}
