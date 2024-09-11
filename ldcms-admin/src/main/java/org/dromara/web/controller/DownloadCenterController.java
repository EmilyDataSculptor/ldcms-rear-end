package org.dromara.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.web.constants.MainConstants;
import org.dromara.web.domain.vo.DownloadCenterVo;
import org.dromara.web.domain.vo.IndustryNewsVo;
import org.dromara.web.domain.vo.SocialResponsibilityVo;
import org.dromara.web.service.DownloadCenterService;
import org.dromara.web.utils.RedisUtil;
import org.springframework.data.web.PageableDefault;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 下载中心
 * @author zhang
 */
@SaIgnore
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/downloadCenter")
public class DownloadCenterController {

    private final DownloadCenterService downloadCenterService;

    private final RedisUtil redisUtil;

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX="ldcms:downloadCenter";

    /**
     * 下载中心添加操作
     * @param downloadCenterVo 下载中心实体类
     * @return 新增条数
     */
    @PostMapping("/addDownloadCenter")
    public R<Integer> addDownloadCenter(@Validated @RequestBody DownloadCenterVo downloadCenterVo) {
        int flag = downloadCenterService.addDownloadCenter(downloadCenterVo);
        if (flag == MainConstants.MAIN_TITLE_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_MAIN_TITLE_IS_NOT_NULL,MainConstants.MSG_MAIN_TITLE_IS_NOT_NULL);
        }
        if (flag == MainConstants.TYPE_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_TYPE_IS_NOT_NULL,MainConstants.MSG_TYPE_IS_NOT_NULL);
        }

        updateRedisByDownloadCenter();
        return R.ok(flag);
    }

    /**
     * 修改下载中心内容
     * @param downloadCenterVo 下载中心实体类
     * @return 修改条数
     */
    @PostMapping("/updateDownloadCenter")
    public R<Integer> updateDownloadCenter(@Validated @RequestBody DownloadCenterVo downloadCenterVo) {
        int flag = downloadCenterService.updateDownloadCenter(downloadCenterVo);
        if (flag == MainConstants.ID_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        updateRedisByDownloadCenter();
        return R.ok(flag);
    }

    /**
     * 查询所有下载中心内容
     * @param downloadCenterVo 下载中心实体类
     * @return 返回所有下载中心内容
     */
    @GetMapping("/selectListByDownloadCenter")
    public R<List<DownloadCenterVo>> selectListByDownloadCenter(@PageableDefault DownloadCenterVo downloadCenterVo) {
        int total = downloadCenterService.selectCountToRedis();
        if (downloadCenterVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (downloadCenterVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<DownloadCenterVo> list = downloadCenterService.selectListByDownloadCenter(downloadCenterVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL + returnTotal,list);
    }

    /**
     * 根据类型查询下载中心内容
     * @param downloadCenterVo 下载中心实体类
     * @return 返回根据类型查询下载中心内容
     */
    @PostMapping("/selectTypeByDownloadCenter")
    public R<List<DownloadCenterVo>> selectTypeByDownloadCenter(@Validated @RequestBody DownloadCenterVo downloadCenterVo) {
        return R.ok(downloadCenterService.selectTypeByDownloadCenter(downloadCenterVo));
    }

    /**
     * 逻辑删除下载中心内容
     * @param downloadCenterVo 下载中心实体类
     * @return 删除条数
     */
    @PostMapping("/deleteDownloadCenter")
    public R<Integer> deleteDownloadCenter(@Validated @RequestBody DownloadCenterVo downloadCenterVo) {
        int flag = downloadCenterService.deleteDownloadCenter(downloadCenterVo);
        if (flag == MainConstants.ID_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        updateRedisByDownloadCenter();
        return R.ok(flag);
    }

    /**
     * 查询类型并去重
     * @return 返回类型并去重
     */
    @GetMapping("/selectByDistinctType")
    public R<List<DownloadCenterVo>> selectByDistinctType() {
        return R.ok(downloadCenterService.selectByDistinctType());
    }

    /**
     * 查询所有下载中心内容
     * @return 返回所有下载中心内容
     */
    @GetMapping("/selectLAllByDownloadCenter")
    public R<List<DownloadCenterVo>> selectLAllByDownloadCenter() {
        return R.ok(downloadCenterService.selectLAllByDownloadCenter());
    }

    /**
     * 根据ID查询下载中心内容
     * @param id 下载中心ID
     * @return 返回根据ID查询下载中心内容
     */
    @GetMapping("/selectDownloadFileById")
    public R<List<DownloadCenterVo>> selectDownloadFileById(@RequestParam("id") Long id) {
        if (id==null){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        return R.ok(downloadCenterService.selectDownloadFileById(id));
    }

    /**
     * 查询下载中心总条数
     * @return 返回下载中心总条数
     */
    @GetMapping("/selectCountByDownloadCenter")
    public R<Integer> selectCountByDownloadCenter() {
        return R.ok(downloadCenterService.selectCountByDownloadCenter());
    }

    /**
     * 查询所有下载中心内容（只给redis使用）
     * @return 返回所有下载中心内容
     */
    @GetMapping("/selectAllStorageToRedis")
    public R<List<DownloadCenterVo>> selectAllStorageToRedis() {
        return R.ok(downloadCenterService.selectAllStorageToRedis());
    }

    /**
     * 模糊查询主标题和类型
     * @return 返回模糊查询主标题和类型
     */
    @GetMapping("/selectVagueByMainTitleAndType")
    public R<List<DownloadCenterVo>> selectVagueByMainTitleAndType(@PageableDefault DownloadCenterVo downloadCenterVo) {
        int total = downloadCenterService.selectVagueCountByMainTitleAndType(downloadCenterVo);
        if (downloadCenterVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (downloadCenterVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<DownloadCenterVo> list = downloadCenterService.selectVagueByMainTitleAndType(downloadCenterVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL + returnTotal, list);
    }

    /**
     * 查询模糊查询主标题和类型数量
     * @return 返回模糊查询主标题和类型数量
     */
    @GetMapping("/selectVagueCountByMainTitleAndType")
    public R<Integer> selectVagueCountByMainTitleAndType(@PageableDefault DownloadCenterVo downloadCenterVo) {
        return R.ok(downloadCenterService.selectVagueCountByMainTitleAndType(downloadCenterVo));
    }

    /**
     * 下载中心总条数 （只给redis使用）
     * @return 返回下载中心总条数
     */
    @GetMapping("/selectCountToRedis")
    public R<Integer> selectCountToRedis() {
        return  R.ok(downloadCenterService.selectCountToRedis());
    }


    // 每天凌晨2点执行
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateRedisByDownloadCenter() {
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX);
        }
        List<DownloadCenterVo> downloadCenterVos = downloadCenterService.selectAllStorageToRedis();
        for (DownloadCenterVo downloadCenterVo : downloadCenterVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX,downloadCenterVo);
        }
        log.info("updateRedisByDownloadCenter方法被调用：" + REDIS_KEY_PREFIX + "更新成功！！！");
    }
}
