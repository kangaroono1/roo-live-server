package com.roo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roo.entity.dao.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper extends BaseMapper<UserInfo> {
}
