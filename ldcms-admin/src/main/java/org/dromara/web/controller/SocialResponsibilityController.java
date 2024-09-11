package org.dromara.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.dromara.common.core.domain.R;
import org.dromara.web.constants.MainConstants;
import org.dromara.web.domain.bo.SocialResponsibilityBo;
import org.dromara.web.domain.vo.ProductVo;
import org.dromara.web.domain.vo.SocialResponsibilityVo;
import org.dromara.web.service.SocialResponsibilityService;
import org.dromara.web.utils.PageUtil;
import org.dromara.web.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.web.PageableDefault;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 社会责任
 * @author zhang
 */
@SaIgnore
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/socialResponsibility")
public class SocialResponsibilityController {

    private final SocialResponsibilityService socialResponsibilityService;

    private final RedisUtil redisUtil;

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX="ldcms:socialResponsibility";

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX_BY_ID="ldcms:socialResponsibility:id:";

    /**
     * 添加社会责任新闻
     * @param socialResponsibilityVo 社会责任实体类
     * @return 添加条数
     */
    @PostMapping("/addSocialResponsibility")
    public R<Object> addSocialResponsibility(@Validated @RequestBody SocialResponsibilityVo socialResponsibilityVo) {
        int flag = socialResponsibilityService.addSocialResponsibility(socialResponsibilityVo);

        if (flag == MainConstants.MAIN_TITLE_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_MAIN_TITLE_IS_NOT_NULL,MainConstants.MSG_MAIN_TITLE_IS_NOT_NULL);
        }
        if (flag == MainConstants.CONTENT_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_CONTENT_IS_NOT_NULL,MainConstants.MSG_CONTENT_IS_NOT_NULL);
        }

        updateRedisBySocialResponsibility();
        return R.ok(flag);
    }

    /**
     * 逻辑删除社会责任新闻
     * @param socialResponsibilityVo 社会责任实体类
     * @return 删除条数
     */
    @PostMapping("/deleteSocialResponsibility")
    public R<Integer> deleteSocialResponsibility(@Validated @RequestBody SocialResponsibilityVo socialResponsibilityVo){
        int flag = socialResponsibilityService.deleteSocialResponsibility(socialResponsibilityVo);
        if (flag == MainConstants.ID_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }

        updateRedisBySocialResponsibility();
        return R.ok(flag);
    }

    /**
     * 修改社会责任新闻
     * @param socialResponsibilityVo 社会责任实体类
     * @return 修改条数
     */
    @PostMapping("/updateSocialResponsibility")
    public R<Integer> updateSocialResponsibility(@Validated @RequestBody SocialResponsibilityVo socialResponsibilityVo){
        int flag = socialResponsibilityService.updateSocialResponsibility(socialResponsibilityVo);
        if (flag == MainConstants.ID_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        updateRedisBySocialResponsibility();
        return R.ok(flag);
    }

    /**
     * 查询所有社会责任新闻
     * @param socialResponsibilityVo 社会责任实体类
     * @return 返回所有社会责任新闻
     */
    @GetMapping("/selectListBySocialResponsibility")
    public R<List<SocialResponsibilityVo>> selectListBySocialResponsibility(@PageableDefault SocialResponsibilityVo socialResponsibilityVo) {
        int total = socialResponsibilityService.selectCountToRedis();
        if (socialResponsibilityVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (socialResponsibilityVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<SocialResponsibilityVo> list = socialResponsibilityService.selectListBySocialResponsibility(socialResponsibilityVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL + returnTotal,list);
    }

    /**
     * 查询社会责任数量
     * @return 返回社会责任数量
     */
    @GetMapping("/selectCountBySocialResponsibility")
    public R<Integer> selectCountBySocialResponsibility(){
        return R.ok(socialResponsibilityService.selectCountBySocialResponsibility());
    }


    /**
     * 根据产品ID查询社会责任
     * @return 返回根据产品ID查询社会责任
     */
    @GetMapping("/selectSocialResponsibilityById")
    public R<List<SocialResponsibilityVo>> selectSocialResponsibilityById(@RequestParam("id")Long id){
        if (id==null){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        return R.ok(socialResponsibilityService.selectSocialResponsibilityById(id));
    }

    /**
     * 查询所有社会责任
     * @return 查询所有社会责任
     */
    @GetMapping("/selectAllBySocialResponsibility")
    public R<List<SocialResponsibilityVo>> selectAllBySocialResponsibility() {
        int total = socialResponsibilityService.selectCountToRedis();
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL+returnTotal,socialResponsibilityService.selectAllBySocialResponsibility());


    }

    // 每天凌晨2点执行
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateRedisBySocialResponsibility() {
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX);
        }
        List<SocialResponsibilityVo> socialResponsibilityVos = socialResponsibilityService.selectAllStorageToRedis();
        for (SocialResponsibilityVo socialResponsibilityVo : socialResponsibilityVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX,socialResponsibilityVo);
        }
        log.info("updateRedisBySocialResponsibility方法被调用：" + REDIS_KEY_PREFIX + "更新成功！！！");
    }


    /**
     * 根据产品ID查询社会责任Redis测试
     * @return 返回根据产品ID查询社会责任Redis测试
     */
    @GetMapping("/selectSocialResponsibilityByIdAndRedisTest")
    public R<List<SocialResponsibilityVo>> selectSocialResponsibilityByIdAndRedisTest(@RequestParam("id")Long id){
        if (id==null){
            return R.ok(MainConstants.MSG_ID_IS_NOT_NULL);
        }
        String redisKey = REDIS_KEY_PREFIX_BY_ID + id;
        if (redisUtil.hasKey(redisKey)){
            List<SocialResponsibilityVo> resultList = new ArrayList<>();
            List<Object> range = redisUtil.range(redisKey, 0, -1);
            Object o = range.get(0);
            resultList.add((SocialResponsibilityVo) o);
            return R.ok(resultList);
        }


        List<SocialResponsibilityVo> list = socialResponsibilityService.selectSocialResponsibilityById(id);
        for (SocialResponsibilityVo socialResponsibilityVo : list) {
            redisUtil.rightPushAllByAllTypes(redisKey,socialResponsibilityVo);
        }
        return R.ok(socialResponsibilityService.selectSocialResponsibilityById(id));
    }

    /**
     * 模糊查询主标题
     * @return 返回模糊查询主标题
     */
    @GetMapping("/selectVagueByMainTitle")
    public R<List<SocialResponsibilityVo>> selectVagueByMainTitle(@PageableDefault SocialResponsibilityVo socialResponsibilityVo) {
        int total = socialResponsibilityService.selectVagueCountByMainTitle(socialResponsibilityVo);
        if (socialResponsibilityVo.getCurrentPage() < 0) {
            return R.ok(MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (socialResponsibilityVo.getPageSize() < 0) {
            return R.ok(MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<SocialResponsibilityVo> list = socialResponsibilityService.selectVagueByMainTitle(socialResponsibilityVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL + returnTotal, list);
    }

    /**
     * 查询模糊查询主标题数量
     * @return 返回模糊查询主标题数量
     */
    @GetMapping("/selectVagueCountByMainTitle")
    public R<Integer> selectVagueCountByMainTitle(@PageableDefault SocialResponsibilityVo socialResponsibilityVo) {
        return R.ok(socialResponsibilityService.selectVagueCountByMainTitle(socialResponsibilityVo));
    }

    /**
     * 查询所有社会责任（只给redis使用）
     * @return 返回查询所有社会责任
     */
    @GetMapping("/selectAllStorageToRedis")
    public R<List<SocialResponsibilityVo>> selectAllStorageToRedis() {
        return R.ok(socialResponsibilityService.selectAllStorageToRedis());
    }

    /**
     * 社会责任总条数 （只给redis使用）
     * @return 返回社会责任总条数
     */
    @GetMapping("/selectCountToRedis")
    public R<Integer> selectCountToRedis() {
        return R.ok(socialResponsibilityService.selectCountToRedis());
    }
}
