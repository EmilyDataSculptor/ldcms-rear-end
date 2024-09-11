package org.dromara.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.web.constants.MainConstants;
import org.dromara.web.constants.PageConstants;
import org.dromara.web.domain.bo.ServiceAndSupportBo;
import org.dromara.web.domain.vo.ProductVo;
import org.dromara.web.domain.vo.ServiceAndSupportVo;
import org.dromara.web.domain.vo.SocialResponsibilityVo;
import org.dromara.web.service.ServiceAndSupportService;
import org.dromara.web.utils.PageUtil;
import org.dromara.web.utils.RedisUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.web.PageableDefault;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务与支持
 * @author zhang
 */
@SaIgnore
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/serviceAndSupport")
public class ServiceAndSupportController {

    private final ServiceAndSupportService serviceAndSupportService;

    private final RedisUtil redisUtil;

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX="ldcms:serviceAndSupport";

    /**
     * 添加服务与支持
     * @param serviceAndSupportVo 服务与支持实体类
     * @return 添加条数
     */
    @PostMapping("/addServiceAndSupport")
    public R<Object> addServiceAndSupport(@Validated @RequestBody ServiceAndSupportVo serviceAndSupportVo) {
        int flag = serviceAndSupportService.addServiceAndSupport(serviceAndSupportVo);

        if (flag ==  MainConstants.MAIN_TITLE_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_MAIN_TITLE_IS_NOT_NULL,MainConstants.MSG_MAIN_TITLE_IS_NOT_NULL);
        }
        if (flag == MainConstants.CONTENT_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_CONTENT_IS_NOT_NULL,MainConstants.MSG_CONTENT_IS_NOT_NULL);
        }
        if (flag == MainConstants.CITY_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_CITY_IS_NOT_NULL,MainConstants.MSG_CITY_IS_NOT_NULL);
        }
        updateRedisByServiceAndSupport();
        return R.ok(flag);
    }

    /**
     * 逻辑删除服务与支持
     * @param serviceAndSupportVo 服务与支持实体类
     * @return 删除条数
     */
    @PostMapping("/deleteServiceAndSupport")
    public R<Integer> deleteServiceAndSupport(@Validated @RequestBody ServiceAndSupportVo serviceAndSupportVo){
        int flag = serviceAndSupportService.deleteServiceAndSupport(serviceAndSupportVo);
        if (flag == MainConstants.ID_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        updateRedisByServiceAndSupport();
        return R.ok(flag);
    }

    /**
     * 修改社会责任新闻
     * @param serviceAndSupportVo 服务与支持实体类
     * @return 修改条数
     */
    @PostMapping("/updateServiceAndSupport")
    public R<Integer> updateServiceAndSupport(@Validated @RequestBody ServiceAndSupportVo serviceAndSupportVo){
        int flag = serviceAndSupportService.updateServiceAndSupport(serviceAndSupportVo);
        if (flag == MainConstants.ID_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        updateRedisByServiceAndSupport();
        return R.ok(flag);
    }

    /**
     * 查询所有服务与支持
     * @param serviceAndSupportVo 服务与支持实体类
     * @return 返回所有服务与支持
     */
    @GetMapping("/selectListByServiceAndSupport")
    public R<List<ServiceAndSupportVo>> selectListByServiceAndSupport(@PageableDefault ServiceAndSupportVo serviceAndSupportVo) {
        int total = serviceAndSupportService.selectCountToRedis();
        if (serviceAndSupportVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (serviceAndSupportVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }

        List<ServiceAndSupportVo> list = serviceAndSupportService.selectListByServiceAndSupport(serviceAndSupportVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL+returnTotal,list);
    }


    /**
     * 根据城市查询服务与支持
     * @param serviceAndSupportVo 服务与支持实体类
     * @return 返回根据城市查询服务与支持
     */
    @GetMapping("/selectCityByServiceAndSupport")
    public R<List<ServiceAndSupportVo>> selectCityByServiceAndSupport(@PageableDefault ServiceAndSupportVo serviceAndSupportVo) {
        int total = serviceAndSupportService.selectCountByCity(serviceAndSupportVo);
        if (serviceAndSupportVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (serviceAndSupportVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<ServiceAndSupportVo> list = serviceAndSupportService.selectCityByServiceAndSupport(serviceAndSupportVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL+returnTotal,list);
    }


    /**
     * 查询城市并去重
     * @return 返回城市并去重
     */
    @GetMapping("/selectByDistinctCity")
    public R<List<ServiceAndSupportVo>> selectByDistinctCity() {
        return R.ok(serviceAndSupportService.selectByDistinctCity());
    }

    /**
     * 查询城市数量
     * @return 返回城市数量
     */
    @GetMapping("/selectCountByCity")
    public R<Integer> selectCountByCity(ServiceAndSupportVo serviceAndSupportVo) {
        return R.ok(serviceAndSupportService.selectCountByCity(serviceAndSupportVo));
    }

    /**
     * 查询所有服务与支持数量
     * @return 查询所有服务与支持数量
     */
    @GetMapping("/selectCountByServiceAndSupport")
    public R<Integer> selectCountByServiceAndSupport(ServiceAndSupportVo serviceAndSupportVo) {
        return R.ok(serviceAndSupportService.selectCountByServiceAndSupport(serviceAndSupportVo));
    }

    /**
     * 根据产品ID查询服务与支持
     * @return 返回根据产品ID查询服务与支持
     */
    @GetMapping("/selectServiceAndSupportById")
    public R<List<ServiceAndSupportVo>> selectServiceAndSupportById(@RequestParam("id") Long id) {
        if (id==null){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        return R.ok(serviceAndSupportService.selectServiceAndSupportById(id));
    }

    /**
     * 查询所有服务与支持
     * @return 返回查询所有服务与支持
     */
    @GetMapping("/selectAllByServiceAndSupport")
    public R<List<ServiceAndSupportVo>> selectAllByServiceAndSupport() {
        int total = serviceAndSupportService.selectCountToRedis();
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL+returnTotal,serviceAndSupportService.selectAllByServiceAndSupport());
    }

    /**
     * 模糊查询主标题和城市
     * @return 返回模糊查询主标题和城市
     */
    @GetMapping("/selectVagueByMainTitleAndCity")
    public R<List<ServiceAndSupportVo>> selectVagueByMainTitleAndCity(@PageableDefault ServiceAndSupportVo serviceAndSupportVo) {
        int total = serviceAndSupportService.selectVagueCountByMainTitleAndCity(serviceAndSupportVo);
        if (serviceAndSupportVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (serviceAndSupportVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<ServiceAndSupportVo> list = serviceAndSupportService.selectVagueByMainTitleAndCity(serviceAndSupportVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL + returnTotal, list);
    }

    /**
     * 查询模糊查询主标题和城市数量
     * @return 返回模糊查询主标题和城市数量
     */
    @GetMapping("/selectVagueCountByMainTitleAndCity")
    public R<Integer> selectVagueCountByMainTitleAndCity(@PageableDefault ServiceAndSupportVo serviceAndSupportVo) {
        return R.ok(serviceAndSupportService.selectVagueCountByMainTitleAndCity(serviceAndSupportVo));
    }


    /**
     * 查询所有服务与支持（只给redis使用）
     * @return 返回查询所有服务与支持
     */
    @GetMapping("/selectAllStorageToRedis")
    public R<List<ServiceAndSupportVo>> selectAllStorageToRedis() {
        return R.ok(serviceAndSupportService.selectAllStorageToRedis());
    }

    /**
     * 所有服务与支持总条数 （只给redis使用）
     * @return 返回所有服务与支持总条数
     */
    @GetMapping("/selectCountToRedis")
    public R<Integer> selectCountToRedis() {
        return R.ok(serviceAndSupportService.selectCountToRedis());
    }

    // 每天凌晨2点执行
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateRedisByServiceAndSupport() {
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX);
        }
        List<ServiceAndSupportVo> serviceAndSupportVos = serviceAndSupportService.selectAllStorageToRedis();
        for (ServiceAndSupportVo serviceAndSupportVo : serviceAndSupportVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX,serviceAndSupportVo);
        }
        log.info("updateRedisByServiceAndSupport方法被调用：" + REDIS_KEY_PREFIX + "更新成功！！！");
    }
}
