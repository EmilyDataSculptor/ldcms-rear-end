package org.dromara.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.web.constants.MainConstants;
import org.dromara.web.domain.bo.UserBo;
import org.dromara.web.domain.vo.UserVo;
import org.dromara.web.service.UserService;
import org.dromara.web.utils.RedisUtil;
import org.dromara.web.utils.TokenUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 管理员登录注册
 * @author zhang
 */
@SaIgnore
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController{

    private final UserService userService;

    private final RedisUtil redisUtil;

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX_ID="ldcms:user:id:";


    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public R<UserVo> login(@Validated @RequestBody UserVo userVo) {

        UserVo resultUserVo = userService.selectByUserNameAndPassword(userVo);
        if (resultUserVo==null){
            return R.fail(MainConstants.CODE_USERNAME_OR_PASSWORD_ERROR,MainConstants.MSG_USERNAME_OR_PASSWORD_ERROR);
        }
        String token= TokenUtil.sign(resultUserVo);
        return R.ok(MainConstants.MSG_TOKEN + token,resultUserVo);
    }

    /**
     * 注册
     * @param userVo 账号
     * @return 返回 1-注册失败 0-注册成功
     */
    @PostMapping("/register")
    public R<Integer> register(@Validated @RequestBody UserVo userVo) {
        int register = userService.register(userVo);
        if (register==MainConstants.THE_USER_ALREADY_EXISTS){
            return R.fail(MainConstants.CODE_THE_USER_ALREADY_EXISTS,MainConstants.MSG_THE_USER_ALREADY_EXISTS);
        }
        return R.ok(MainConstants.MSG_THE_USER_ADDED_SUCCESSFULLY);
    }

    /**
     * 根据ID查找昵称和头像
     * @param userVo 管理员表实体类
     * @return 返回根据ID查找昵称和头像
     */
    @PostMapping("/selectNickNameAndProfilePictureById")
    public R<UserVo> selectNickNameAndProfilePictureById(@Validated @RequestBody UserVo userVo) {
        return R.ok(userService.selectNickNameAndProfilePictureById(userVo));
    }

    /**
     * 更改管理员信息（除账号和密码）
     * @param userVo 管理员表实体类
     * @return 返回更改管理员信息
     */
    @PostMapping("/updateByUser")
    public R<Integer> updateByUser(@Validated @RequestBody UserVo userVo) {
        int flag = userService.updateByUser(userVo);
        if (flag == MainConstants.ID_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        updateRedisToUserById(userVo.getId());
        return R.ok(flag);
    }

    /**
     * 更改管理员密码
     * @param userBo 管理员密码
     * @return 返回更改条数
     */
    @PostMapping("/updateByPassword")
    public R<Integer> updateByPassword(@Validated @RequestBody UserBo userBo) {
        int flag = userService.updateByPassword(userBo);
        if (flag == MainConstants.ID_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        if (flag == MainConstants.ORIGINAL_PASSWORD_INCORRECT){
            return R.fail(MainConstants.CODE_ORIGINAL_PASSWORD_INCORRECT,MainConstants.MSG_ORIGINAL_PASSWORD_INCORRECT);
        }
        if (flag == MainConstants.NEW_PASSWORD_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_NEW_PASSWORD_IS_NOT_NULL,MainConstants.MSG_NEW_PASSWORD_IS_NOT_NULL);
        }
        updateRedisToUserById(userBo.getId());
        return R.ok(flag);
    }

    /**
     * 根据ID查询管理员信息
     * @param userVo ID
     * @return 返回根据ID查询管理员信息
     */
    @PostMapping("/selectListById")
    public R<List<UserVo>> selectListById(@Validated @RequestBody UserVo userVo) {
        char state = userService.selectStateById(userVo).getState();
        if (state=='0'){
            return R.fail(MainConstants.CODE_ADMINISTRATOR_NOT_ACTIVATED,MainConstants.MSG_ADMINISTRATOR_NOT_ACTIVATED);
        }
        return R.ok(userService.selectListById(userVo));
    }

    /**
     * 根据ID查询管理员是否激活
     * @param userVo ID
     * @return 返回根据ID查询管理员是否激活
     */
    @PostMapping("/selectStateById")
    public R<UserVo> selectStateById(@Validated @RequestBody UserVo userVo) {
        return R.ok(userService.selectStateById(userVo));
    }

    /**
     * 根据ID查询管理员信息（只给redis使用）
     * @param id ID
     * @return 返回根据ID查询管理员信息
     */
    @GetMapping("/selectListByIdToRedis")
    public R<List<UserVo>> selectListByIdToRedis(@RequestParam("id") Long id) {
        return  R.ok(userService.selectListByIdToRedis(id));
    }

    public void updateRedisToUserById(Long id) {
        String REDIS_KEY_PREFIX = REDIS_KEY_PREFIX_ID+id;
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX);
        }
        List<UserVo> userVos = userService.selectListByIdToRedis(id);
        for (UserVo resUserVo : userVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX,resUserVo);
        }
        log.info("updateRedisToUserById方法被调用：" + REDIS_KEY_PREFIX + "更新成功！！！");
    }



}
