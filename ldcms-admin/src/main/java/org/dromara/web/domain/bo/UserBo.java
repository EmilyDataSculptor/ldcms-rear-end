package org.dromara.web.domain.bo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.web.domain.User;

import java.io.Serializable;

/**
 * 管理员表 user
 */

@Data
public class UserBo implements Serializable {

    /**
     * 管理员ID
     */
    private Long id;

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;


}
