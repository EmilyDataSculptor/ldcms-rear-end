package org.dromara.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.web.constants.MainConstants;
import org.dromara.web.domain.vo.IndustryNewsVo;
import org.dromara.web.domain.vo.NewsCenterVo;
import org.dromara.web.domain.vo.SocialResponsibilityVo;
import org.dromara.web.service.IndustryNewsService;
import org.dromara.web.service.NewsCenterService;
import org.dromara.web.utils.PageUtil;
import org.dromara.web.utils.RedisUtil;
import org.springframework.data.web.PageableDefault;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻中心
 * @author zhang
 */
@SaIgnore
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/newsCenter")
public class NewsCenterController {

    private final NewsCenterService newsCenterService;

    private final RedisUtil redisUtil;

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX="ldcms:newsCenter";

    /**
     * 添加新闻中心
     * @param newsCenterVo 新闻中心实体类
     * @return 添加条数
     */
    @PostMapping("/addNewsCenter")
    public R<Integer> addNewsCenter(@Validated @RequestBody NewsCenterVo newsCenterVo) {
        int flag = newsCenterService.addNewsCenter(newsCenterVo);

        if (flag == MainConstants.MAIN_TITLE_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_MAIN_TITLE_IS_NOT_NULL,MainConstants.MSG_MAIN_TITLE_IS_NOT_NULL);
        }
        if (flag == MainConstants.CONTENT_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_CONTENT_IS_NOT_NULL,MainConstants.MSG_CONTENT_IS_NOT_NULL);
        }
        updateRedisByNewsCenter();
        return R.ok(flag);
    }

    /**
     * 逻辑删除新闻中心
     * @param newsCenterVo 新闻中心实体类
     * @return 删除条数
     */
    @PostMapping("/deleteNewsCenter")
    public R<Integer> deleteNewsCenter(@Validated @RequestBody NewsCenterVo newsCenterVo){
        int flag = newsCenterService.deleteNewsCenter(newsCenterVo);
        if (flag == MainConstants.ID_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        updateRedisByNewsCenter();
        return R.ok(flag);
    }

    /**
     * 修改新闻中心
     * @param newsCenterVo 新闻中心实体类
     * @return 修改条数
     */
    @PostMapping("/updateNewsCenter")
    public R<Integer> updateNewsCenter(@Validated @RequestBody NewsCenterVo newsCenterVo){
        int flag = newsCenterService.updateNewsCenter(newsCenterVo);
        if (flag == MainConstants.ID_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        updateRedisByNewsCenter();
        return R.ok(flag);
    }

    /**
     * 查询所有新闻中心
     * @param newsCenterVo 新闻中心实体类
     * @return 返回所有新闻中心
     */
    @GetMapping("/selectListByNewsCenter")
    public R<List<NewsCenterVo>> selectListByNewsCenter(@PageableDefault NewsCenterVo newsCenterVo) {
        int total = newsCenterService.selectCountToRedis();
        if (newsCenterVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (newsCenterVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<NewsCenterVo> list = newsCenterService.selectListByNewsCenter(newsCenterVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL+returnTotal,list);
    }

    /**
     * 查询所有新闻中心
     * @return 返回所有新闻中心
     */
    @GetMapping("/selectAllByNewsCenter")
    public R<List<NewsCenterVo>> selectAllByNewsCenter() {
        int total = newsCenterService.selectCountToRedis();
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL+returnTotal,newsCenterService.selectAllByNewsCenter());
    }

    /**
     * 根据ID查询新闻中心
     * @param id 新闻中心ID
     * @return 返回根据ID查询新闻中心
     */
    @GetMapping("/selectListById")
    public R<List<NewsCenterVo>> selectListById(@RequestParam("id") Long id) {
        if (id==null){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        return R.ok(newsCenterService.selectListById(id));
    }

    /**
     * 查询新闻中心条数
     * @return 返回查询新闻中心条数
     */
    @GetMapping("/selectCountByNewsCenter")
    public R<Integer> selectCountByNewsCenter() {
        return R.ok(newsCenterService.selectCountByNewsCenter());
    }

    /**
     * 模糊查询主标题
     * @return 返回模糊查询主标题
     */
    @GetMapping("/selectVagueByMainTitle")
    public R<List<NewsCenterVo>> selectVagueByMainTitle(@PageableDefault NewsCenterVo newsCenterVo) {
        int total = newsCenterService.selectVagueCountByMainTitle(newsCenterVo);
        if (newsCenterVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (newsCenterVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<NewsCenterVo> list = newsCenterService.selectVagueByMainTitle(newsCenterVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL + returnTotal, list);
    }

    /**
     * 查询模糊查询主标题数量
     * @return 返回模糊查询主标题数量
     */
    @GetMapping("/selectVagueCountByMainTitle")
    public R<Integer> selectVagueCountByMainTitle(@PageableDefault NewsCenterVo newsCenterVo) {
        return R.ok(newsCenterService.selectVagueCountByMainTitle(newsCenterVo));
    }

    /**
     * 查询所有新闻中心（只给redis使用）
     * @return 返回所有新闻中心
     */
    @GetMapping("/selectAllStorageToRedis")
    public R<List<NewsCenterVo>> selectAllStorageToRedis() {
        return R.ok(newsCenterService.selectAllStorageToRedis());
    }

    /**
     * 新闻中心总条数 （只给redis使用）
     * @return 返回新闻中心总条数
     */
    @GetMapping("/selectCountToRedis")
    public R<Integer> selectCountToRedis() {
        return R.ok(newsCenterService.selectCountToRedis());
    }


    // 每天凌晨2点执行
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateRedisByNewsCenter() {
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX);
        }
        List<NewsCenterVo> newsCenterVos = newsCenterService.selectAllStorageToRedis();
        for (NewsCenterVo newsCenterVo : newsCenterVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX,newsCenterVo);
        }
        log.info("updateRedisByNewsCenter方法被调用：" + REDIS_KEY_PREFIX + "更新成功！！！");
    }
}
