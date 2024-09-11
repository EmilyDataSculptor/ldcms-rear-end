package org.dromara.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.web.constants.MainConstants;
import org.dromara.web.domain.User;
import org.dromara.web.domain.bo.UserBo;
import org.dromara.web.domain.vo.UserVo;
import org.dromara.web.mapper.UserMapper;
import org.dromara.web.service.UserService;
import org.dromara.web.utils.RedisUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 联系我们
 * @author zhang
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final RedisUtil redisUtil;

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX_ID="ldcms:user:id:";

    int flag = 0;

    /**
     * 登录
     * @param userVo 账号密码
     * @return  返回账号密码
     */
    @Override
    public UserVo selectByUserNameAndPassword(UserVo userVo) {
        UserVo resultUserVo = userMapper.selectByUserNameAndPassword(userVo);
        if (resultUserVo==null) {
            return null;
        }
        return userMapper.selectByUserNameAndPassword(userVo);
    }

    /**
     * 注册
     * @param userVo 账号
     * @return 返回 1-注册失败 0-注册成功
     */
    @Override
    public int register(UserVo userVo) {
        User user = MapstructUtils.convert(userVo, User.class);
        int register = userMapper.register(userVo);
        System.out.println(register);
        if (register==0){
            user.setState('1');
            int insert = userMapper.insert(user);
            return insert;
        }
        else {
            return MainConstants.THE_USER_ALREADY_EXISTS;
        }
    }

    /**
     * 根据ID查找昵称和头像
     * @param userVo 管理员表实体类
     * @return 返回根据ID查找昵称和头像
     */
    @Override
    public UserVo selectNickNameAndProfilePictureById(UserVo userVo) {
        return userMapper.selectNickNameAndProfilePictureById(userVo);
    }

    /**
     * 更改管理员信息（除账号和密码）
     * @param userVo 管理员表实体类
     * @return 返回更改管理员信息
     */
    @Override
    public int updateByUser(UserVo userVo) {
        if (userVo.getId() == null) {
            flag = MainConstants.ID_IS_NOT_NULL;
            return flag;
        }
        LambdaUpdateWrapper<User> wrapper = new UpdateWrapper<User>().lambda();
        wrapper.eq(User::getId, userVo.getId());
        if (StringUtils.isNotBlank(userVo.getNickName())) {
            wrapper.set(User::getNickName, userVo.getNickName());
        }
        if (StringUtils.isNotBlank(userVo.getProfilePicture())) {
            wrapper.set(User::getProfilePicture, userVo.getProfilePicture());
        }
        if (StringUtils.isNotBlank(userVo.getPersonalProfile())) {
            wrapper.set(User::getPersonalProfile, userVo.getPersonalProfile());
        }
        flag = userMapper.update(null, wrapper);
        return flag;
    }

    /**
     * 更改管理员密码
     * @param userBo 管理员密码
     * @return 返回更改条数
     */
    @Override
    public int updateByPassword(UserBo userBo) {
        if (userBo.getId() == null) {
            flag = MainConstants.ID_IS_NOT_NULL;
            return flag;
        }
        LambdaUpdateWrapper<User> wrapper = new UpdateWrapper<User>().lambda();
        wrapper.eq(User::getId, userBo.getId());
        User user = userMapper.selectOne(wrapper);

        if(!user.getPassword().equals(userBo.getOldPassword())){
            return MainConstants.ORIGINAL_PASSWORD_INCORRECT;
        }

        if (StringUtils.isNotBlank(userBo.getNewPassword())) {
            wrapper.set(User::getPassword, userBo.getNewPassword());
        } else {
            return MainConstants.NEW_PASSWORD_IS_NOT_NULL;
        }
        flag = userMapper.update(null, wrapper);
        return flag;
    }

    /**
     * 根据ID查询管理员信息
     * @param userVo ID
     * @return 返回根据ID查询管理员信息
     */
    @Override
    public List<UserVo> selectListById(UserVo userVo) {
        String REDIS_KEY_PREFIX = REDIS_KEY_PREFIX_ID+userVo.getId();
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            UserVo found = null;
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<UserVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((UserVo) o);
            }
            for (UserVo vo : list) {
                if (vo.getId().equals(userVo.getId())) {
                    found = vo;
                    break;
                }
            }
            List<UserVo> resultList = new ArrayList<>();
            resultList.add(found);
            return resultList;
        }
        updateRedisToUserById(userVo.getId());
        return userMapper.selectListById(userVo);
    }

    /**
     * 根据ID查询管理员是否激活
     * @param userVo ID
     * @return 返回根据ID查询管理员是否激活
     */
    @Override
    public UserVo selectStateById(UserVo userVo) {
        return userMapper.selectStateById(userVo);
    }

    /**
     * 根据ID查询管理员信息（只给redis使用）
     * @param id ID
     * @return 返回根据ID查询管理员信息
     */
    @Override
    public List<UserVo> selectListByIdToRedis(Long id) {
        return userMapper.selectListByIdToRedis(id);
    }

    public void updateRedisToUserById(Long id) {
        String REDIS_KEY_PREFIX = REDIS_KEY_PREFIX_ID+id;
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX);
        }
        List<UserVo> userVos = userMapper.selectListByIdToRedis(id);
        for (UserVo resUserVo : userVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX,resUserVo);
        }
        log.info("uupdateRedisToUserById方法被调用：" + REDIS_KEY_PREFIX + "更新成功！！！");
    }
}
