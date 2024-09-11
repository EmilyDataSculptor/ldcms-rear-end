package org.dromara.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.web.constants.MainConstants;
import org.dromara.web.domain.vo.DownloadCenterVo;
import org.dromara.web.domain.vo.IndustryNewsVo;
import org.dromara.web.domain.vo.NewsCenterVo;
import org.dromara.web.service.IndustryNewsService;
import org.dromara.web.utils.PageUtil;
import org.dromara.web.utils.RedisUtil;
import org.springframework.data.web.PageableDefault;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 行业新闻
 * @author zhang
 */
@SaIgnore
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/industryNews")
public class IndustryNewsController {

    private final IndustryNewsService industryNewsService;
    private final RedisUtil redisUtil;

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX="ldcms:industryNews";


    /**
     * 添加行业新闻
     * @param industryNewsVo 行业新闻实体类
     * @return 添加条数
     */
    @PostMapping("/addIndustryNews")
    public R<Integer> addIndustryNews(@Validated @RequestBody IndustryNewsVo industryNewsVo) {
        int flag = industryNewsService.addIndustryNews(industryNewsVo);

        if (flag == MainConstants.MAIN_TITLE_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_MAIN_TITLE_IS_NOT_NULL,MainConstants.MSG_MAIN_TITLE_IS_NOT_NULL);
        }
        if (flag == MainConstants.CONTENT_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_CONTENT_IS_NOT_NULL,MainConstants.MSG_CONTENT_IS_NOT_NULL);
        }
        updateRedisByIndustryNews();
        return R.ok(flag);
    }

    /**
     * 逻辑删除行业新闻
     * @param industryNewsVo 行业新闻实体类
     * @return 删除条数
     */
    @PostMapping("/deleteIndustryNews")
    public R<Integer> deleteIndustryNews(@Validated @RequestBody IndustryNewsVo industryNewsVo){
        int flag = industryNewsService.deleteIndustryNews(industryNewsVo);
        if (flag == MainConstants.ID_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        updateRedisByIndustryNews();
        return R.ok(flag);
    }

    /**
     * 修改行业新闻
     * @param industryNewsVo 行业新闻实体类
     * @return 修改条数
     */
    @PostMapping("/updateIndustryNews")
    public R<Integer> updateIndustryNews(@Validated @RequestBody IndustryNewsVo industryNewsVo){
        int flag = industryNewsService.updateIndustryNews(industryNewsVo);
        if (flag == MainConstants.ID_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        updateRedisByIndustryNews();
        return R.ok(flag);
    }

    /**
     * 查询所有行业新闻
     * @param industryNewsVo 行业新闻实体类
     * @return 返回所有行业新闻
     */
    @GetMapping("/selectListByIndustryNews")
    public R<List<IndustryNewsVo>> selectListByIndustryNews(@PageableDefault IndustryNewsVo industryNewsVo) {
        int total = industryNewsService.selectCountToRedis();
        if (industryNewsVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (industryNewsVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<IndustryNewsVo> list = industryNewsService.selectListByIndustryNews(industryNewsVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL+returnTotal,list);

    }

    /**
     * 查询所有行业新闻
     * @return 返回所有行业新闻
     */
    @GetMapping("/selectAllByIndustryNews")
    public R<List<IndustryNewsVo>> selectAllByIndustryNews() {
        int total = industryNewsService.selectCountToRedis();
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL+returnTotal,industryNewsService.selectAllByIndustryNews());
    }

    /**
     * 根据ID查询行业新闻
     * @param id 行业新闻ID
     * @return 返回根据ID查询行业新闻
     */
    @GetMapping("/selectListById")
    public R<List<IndustryNewsVo>> selectListById(@RequestParam("id") Long id) {
        if (id==null){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        return R.ok(industryNewsService.selectListById(id));
    }

    /**
     * 行业新闻中心条数
     * @return 返回查询行业新闻
     */
    @GetMapping("/selectCountByIndustryNews")
    public R<Integer> selectCountByIndustryNews() {
        return R.ok(industryNewsService.selectCountByIndustryNews());
    }

    /**
     * 模糊查询主标题
     * @return 返回模糊查询主标题
     */
    @GetMapping("/selectVagueByMainTitle")
    public R<List<IndustryNewsVo>> selectVagueByMainTitle(@PageableDefault IndustryNewsVo industryNewsVo) {
        int total = industryNewsService.selectVagueCountByMainTitle(industryNewsVo);
        if (industryNewsVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (industryNewsVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<IndustryNewsVo> list = industryNewsService.selectVagueByMainTitle(industryNewsVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL + returnTotal, list);
    }

    /**
     * 查询模糊查询主标题数量
     * @return 返回模糊查询主标题数量
     */
    @GetMapping("/selectVagueCountByMainTitle")
    public R<Integer> selectVagueCountByMainTitle(@PageableDefault IndustryNewsVo industryNewsVo) {
        return R.ok(industryNewsService.selectVagueCountByMainTitle(industryNewsVo));
    }

    /**
     * 查询所有行业新闻（只给redis使用）
     * @return 返回所有行业新闻
     */
    @GetMapping("/selectAllStorageToRedis")
    public R<List<IndustryNewsVo>> selectAllStorageToRedis() {
        return R.ok(industryNewsService.selectAllStorageToRedis());
    }

    /**
     * 行业新闻总条数 （只给redis使用）
     * @return 返回行业新闻总条数
     */
    @GetMapping("/selectCountToRedis")
    public R<Integer> selectCountToRedis() {
        return R.ok(industryNewsService.selectCountToRedis());
    }

    // 每天凌晨2点执行
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateRedisByIndustryNews() {
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX);
        }
        List<IndustryNewsVo> industryNewsVos = industryNewsService.selectAllStorageToRedis();
        for (IndustryNewsVo industryNewsVo : industryNewsVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX,industryNewsVo);
        }
        log.info("updateRedisByIndustryNews方法被调用：" + REDIS_KEY_PREFIX + "更新成功！！！");
    }
}
