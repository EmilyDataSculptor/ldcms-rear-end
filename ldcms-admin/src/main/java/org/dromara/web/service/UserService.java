package org.dromara.web.service;

import org.dromara.web.domain.bo.UserBo;
import org.dromara.web.domain.vo.ContactUsVo;
import org.dromara.web.domain.vo.UserVo;

import java.util.List;

/**
 * 管理员登录注册
 * @author zhang
 */
public interface UserService {

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
     * 更改管理员密码
     * @param userBo 管理员密码
     * @return 返回更改条数
     */
    int updateByPassword(UserBo userBo);

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
