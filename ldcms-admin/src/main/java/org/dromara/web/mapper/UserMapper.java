package org.dromara.web.mapper;

import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.web.domain.ContactUs;
import org.dromara.web.domain.User;
import org.dromara.web.domain.vo.ContactUsVo;
import org.dromara.web.domain.vo.UserVo;

import java.util.List;

/**
 * 管理员登录注册
 * @author zhang
 */
public interface UserMapper extends BaseMapperPlus<User, UserVo> {

    /**
     * 登录
     * @param userVo 账号密码
     * @return  返回账号密码
     */
    UserVo selectByUserNameAndPassword(UserVo userVo);

    /**
     * 注册
     * @param userVo 账号
     * @return 返回 1-注册失败 0-注册成功
     */
    int register(UserVo userVo);

    /**
     * 根据ID查找昵称和头像
     * @param userVo 管理员表实体类
     * @return 返回根据ID查找昵称和头像
     */
    UserVo selectNickNameAndProfilePictureById(UserVo userVo);

    /**
     * 更改管理员信息（除账号和密码）
     * @param userVo 管理员表实体类
     * @return 返回更改管理员信息
     */
    int updateByUser(UserVo userVo);

    /**
     * 根据ID查询管理员信息
     * @param userVo ID
     * @return 返回根据ID查询管理员信息
     */
    List<UserVo> selectListById(UserVo userVo);

    /**
     * 根据ID查询管理员是否激活
     * @param userVo ID
     * @return 返回根据ID查询管理员是否激活
     */
    UserVo selectStateById(UserVo userVo);

    /**
     * 根据ID查询管理员信息（只给redis使用）
     * @param id ID
     * @return 返回根据ID查询管理员信息
     */
    List<UserVo> selectListByIdToRedis(Long id);


}
