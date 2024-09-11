package org.dromara.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.web.constants.DataConstants;
import org.dromara.web.constants.MainConstants;
import org.dromara.web.domain.vo.ContactUsVo;
import org.dromara.web.domain.vo.IndustryNewsVo;
import org.dromara.web.domain.vo.SocialResponsibilityVo;
import org.dromara.web.service.ContactUsService;
import org.dromara.web.utils.RedisUtil;
import org.springframework.data.web.PageableDefault;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 联系我们
 *
 * @author zhang
 */
@SaIgnore
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/contactUs")
public class ContactUsController {

    private final ContactUsService contactUsService;

    private final RedisUtil redisUtil;

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX="ldcms:contactUs";

    public static final String REDIS_KEY_PREFIX_SUBMITTED="ldcms:contactUs:submitted";

    /**
     * 联系我们添加操作
     *
     * @param contactUsVo 联系我们实体类
     * @return 添加条数
     */
    @PostMapping("/addContactUs")
    public R<Object> addContactUs(@Validated @RequestBody ContactUsVo contactUsVo) {
        int flag = contactUsService.addContactUs(contactUsVo);
        if (flag == MainConstants.NAME_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_NAME_IS_NOT_NULL,MainConstants.MSG_NAME_IS_NOT_NULL);
        }
        if (flag == MainConstants.PHONE_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_PHONE_IS_NOT_NULL,MainConstants.MSG_PHONE_IS_NOT_NULL);
        }
        if (flag == MainConstants.PHONE_DIGIT_IS_WRONG) {
            return R.fail(MainConstants.CODE_PHONE_DIGIT_IS_WRONG,MainConstants.MSG_PHONE_DIGIT_IS_WRONG);
        }
        if (flag == MainConstants.CONTENT_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_CONTENT_IS_NOT_NULL,MainConstants.MSG_CONTENT_IS_NOT_NULL);
        }
        updateRedisByContactUs();
        return R.ok(flag);
    }

    /**
     * 答复客户消息
     * @param contactUsVo 联系我们信息实体类
     * @return 答复条数
     */
    @PostMapping("/replyContent")
    public R<Integer> replyContent(@Validated @RequestBody ContactUsVo contactUsVo) {
        int flag = contactUsService.replyContent(contactUsVo);
        if (flag == MainConstants.ID_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        if (flag == MainConstants.REPLY_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_REPLY_IS_NOT_NULL,MainConstants.MSG_REPLY_IS_NOT_NULL);
        }
        if (flag == MainConstants.CONTENT_IS_REPLIED){
            return R.fail(MainConstants.CODE_CONTENT_IS_REPLIED,MainConstants.MSG_CONTENT_IS_REPLIED);
        }
        updateRedisByContactUs();
        updateRedisByContactUsOnSubmitted();
        return R.ok(flag);
    }

    /**
     * 逻辑删除联系我们信息
     * @param contactUsVo 联系我们信息实体类
     * @return 删除条数
     */
    @PostMapping("/deleteContactUs")
    public R<Integer> deleteContactUs(@Validated @RequestBody ContactUsVo contactUsVo){
        int flag = contactUsService.deleteContactUs(contactUsVo);
        if (flag == MainConstants.ID_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        updateRedisByContactUs();
        return R.ok(flag);
    }

    /**
     * 查询所有联系我们信息
     * @return 返回所有联系我们信息
     */
    @GetMapping("/selectListByContactUs")
    public R<List<ContactUsVo>> selectListByContactUs(@PageableDefault ContactUsVo contactUsVo) {
        int total = contactUsService.selectCountToRedis();
        if (contactUsVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (contactUsVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<ContactUsVo> list = contactUsService.selectListByContactUs(contactUsVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL+returnTotal,list);
    }

    @GetMapping("/selectListById")
    public R<List<ContactUsVo>> selectListById(@RequestParam("id") Long id) {
        if (id==null){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        return R.ok(contactUsService.selectListById(id));
    }

    /**
     * 未处理联系条数
     * @return 返回查未处理联系
     */
    @GetMapping("/selectCountByContactUs")
    public R<Integer> selectCountByContactUs() {
        return R.ok(contactUsService.selectCountByContactUs());
    }

    /**
     * 模糊查询主标题和公司
     * @return 返回模糊主标题和公司
     */
    @GetMapping("/selectVagueByMainTitleAndCompany")
    public R<List<ContactUsVo>> selectVagueByMainTitleAndCompany(@PageableDefault ContactUsVo contactUsVo) {
        int total = contactUsService.selectVagueCountByMainTitleAndCompany(contactUsVo);
        if (contactUsVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (contactUsVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<ContactUsVo> list = contactUsService.selectVagueByMainTitleAndCompany(contactUsVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL + returnTotal, list);
    }

    /**
     * 查询模糊查询名字和公司数量
     * @return 返回模糊查询名字和公司数量
     */
    @GetMapping("/selectVagueCountByMainTitleAndCompany")
    public R<Integer> selectVagueCountByMainTitleAndCompany(@PageableDefault ContactUsVo contactUsVo) {
        return R.ok(contactUsService.selectVagueCountByMainTitleAndCompany(contactUsVo));
    }

    /**
     * 查询所有已提交联系我们信息
     * @return 返回所有已提交联系我们信息
     */
    @GetMapping("/selectListOnSubmittedByContactUs")
    public R<List<ContactUsVo>> selectListOnSubmittedByContactUs(@PageableDefault ContactUsVo contactUsVo) {
        int total = contactUsService.selectCountOnSubmitted();
        if (contactUsVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (contactUsVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<ContactUsVo> list = contactUsService.selectListOnSubmittedByContactUs(contactUsVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL+returnTotal,list);
    }

    /**
     * 根据ID查询已提交联系
     * @param id 已提交联系我们ID
     * @return 返回根据ID查询已提交联系
     */
    @GetMapping("/selectListByIdOnSubmitted")
    public R<List<ContactUsVo>> selectListByIdOnSubmitted(@RequestParam("id") Long id) {
        if (id==null){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        return R.ok(contactUsService.selectListByIdOnSubmitted(id));
    }

    /**
     * 驳回已提交信息
     * @param contactUsVo 联系我们实体类
     * @return 驳回条数
     */
    @PostMapping("/rejectContactUsOnSubmitted")
    public R<Integer> rejectContactUsOnSubmitted(@Validated @RequestBody ContactUsVo contactUsVo) {
        int flag = contactUsService.rejectContactUsOnSubmitted(contactUsVo);
        if (flag == MainConstants.ID_IS_NOT_NULL){
            return R.fail(MainConstants.CODE_ID_IS_NOT_NULL,MainConstants.MSG_ID_IS_NOT_NULL);
        }
        if (flag == MainConstants.REASON_FOR_REJECTION_CANNOT_BE_EMPTY){
            return R.fail(MainConstants.CODE_REASON_FOR_REJECTION_CANNOT_BE_EMPTY,MainConstants.MSG_REASON_FOR_REJECTION_CANNOT_BE_EMPTY);
        }
        updateRedisByContactUs();
        updateRedisByContactUsOnSubmitted();
        return R.ok(flag);
    }

    /**
     * 已提交联系我们总条数
     * @return 返回已提交联系我们总条数
     */
    @GetMapping("/selectCountOnSubmitted")
    public R<Integer> selectCountOnSubmitted() {
        return R.ok(contactUsService.selectCountOnSubmitted());
    }

    /**
     * 模糊查询已提交名字和公司
     * @return 返回已提交模糊查询名字和公司
     */
    @GetMapping("/selectVagueByMainTitleAndCompanyOnSubmitted")
    public R<List<ContactUsVo>> selectVagueByMainTitleAndCompanyOnSubmitted(@PageableDefault ContactUsVo contactUsVo) {
        int total = contactUsService.selectVagueCountByMainTitleAndCompanyOnSubmitted(contactUsVo);
        if (contactUsVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (contactUsVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<ContactUsVo> list = contactUsService.selectVagueByMainTitleAndCompanyOnSubmitted(contactUsVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL + returnTotal, list);

    }

    /**
     * 查询模糊查询已提交名字和公司数量
     * @return 返回已提交模糊查询名字和公司数量
     */
    @GetMapping("/selectVagueCountByMainTitleAndCompanyOnSubmitted")
    public R<Integer> selectVagueCountByMainTitleAndCompanyOnSubmitted(@PageableDefault ContactUsVo contactUsVo) {
        return R.ok(contactUsService.selectVagueCountByMainTitleAndCompanyOnSubmitted(contactUsVo));
    }

    /**
     * 查询所有已提交联系我们信息（只给redis使用）
     * @return 返回所有已提交联系我们信息
     */
    @GetMapping("/selectAllStorageToRedisOnSubmitted")
    public R<List<ContactUsVo>> selectAllStorageToRedisOnSubmitted() {
        return R.ok(contactUsService.selectAllStorageToRedisOnSubmitted());
    }

    /**
     * 查询所有联系我们信息（只给redis使用）
     * @return 返回所有联系我们信息
     */
    @GetMapping("/selectAllStorageToRedis")
    public R<List<ContactUsVo>> selectAllStorageToRedis() {
        return R.ok(contactUsService.selectAllStorageToRedis());
    }

    /**
     * 联系我们总条数 （只给redis使用）
     * @return 返回联系我们总条数
     */
    @GetMapping("/selectCountToRedis")
    public R<Integer> selectCountToRedis() {
        return R.ok(contactUsService.selectCountToRedis());
    }

    // 每天凌晨2点执行
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateRedisByContactUs() {
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX);
        }
        List<ContactUsVo> contactUsVos = contactUsService.selectAllStorageToRedis();
        for (ContactUsVo contactUsVo : contactUsVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX,contactUsVo);
        }
        log.info("updateRedisByContactUs方法被调用：" + REDIS_KEY_PREFIX + "更新成功！！！");
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void updateRedisByContactUsOnSubmitted() {
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX_SUBMITTED);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX_SUBMITTED);
        }
        List<ContactUsVo> contactUsVos = contactUsService.selectAllStorageToRedisOnSubmitted();
        for (ContactUsVo contactUsVo : contactUsVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX_SUBMITTED,contactUsVo);
        }
        log.info("updateRedisByContactUsOnSubmitted方法被调用：" + REDIS_KEY_PREFIX_SUBMITTED + "更新成功！！！");
    }

}
