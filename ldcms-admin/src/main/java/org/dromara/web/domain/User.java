package org.dromara.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * 管理员表 user
 */

@Data
@TableName("user")
@AllArgsConstructor
public class User {
    /**
     * 管理员登录注册ID
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String profilePicture;

    /**
     * 个人简介
     */
    private String personalProfile;

    /**
     * 状态
     */
    private char state;


    public User() {

    }

}
