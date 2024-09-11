package com.example.productadmin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.dromara.LdcmsAdminApplication;
import org.dromara.web.domain.User;
import org.dromara.web.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest(classes = LdcmsAdminApplication.class) // 确保类名正确
@Transactional // 可选，用于测试方法的事务管理
public class UserTest {

    @Resource
    private UserMapper userMapper;

    private final QueryWrapper<User> wrapper = new QueryWrapper<>();

    // 查
    @Test
    public void testGet() {
        wrapper.eq("id", 1);
        List<User> users = userMapper.selectList(wrapper);
        users.forEach(System.out::println);
        // 如果需要，可以在这里添加断言来验证结果
    }
}
