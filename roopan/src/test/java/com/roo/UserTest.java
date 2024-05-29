package com.roo;

import com.roo.entity.dao.UserInfo;
import com.roo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class UserTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testCreateUser() {
        UserInfo user = new UserInfo(
                1L,
                "123",
                "zs",
                "baidu.com",
                "5432221",
                "1006@163.com",
                new Date(),
                new Date(),
                new Date(),
                (short) 0,
                100L,
                100L);
        userMapper.insert(user);
    }
}
