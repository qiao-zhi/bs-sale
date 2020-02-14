package cn.qs.mapper.user.custom;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserCustomMapper {

	Float getGroupSalarys(List<String> userSameAreaUsernames);

}
