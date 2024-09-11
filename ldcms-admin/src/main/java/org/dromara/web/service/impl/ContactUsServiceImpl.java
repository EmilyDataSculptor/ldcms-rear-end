package org.dromara.web.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.web.constants.MainConstants;
import org.dromara.web.domain.ContactUs;
import org.dromara.web.domain.IndustryNews;
import org.dromara.web.domain.vo.ContactUsVo;
import org.dromara.web.domain.vo.IndustryNewsVo;
import org.dromara.web.domain.vo.SocialResponsibilityVo;
import org.dromara.web.mapper.ContactUsMapper;
import org.dromara.web.service.ContactUsService;
import org.dromara.web.utils.MyselfPageUtils;
import org.dromara.web.utils.PageUtil;
import org.dromara.web.utils.RedisUtil;
import org.dromara.web.utils.VerifyPhoneNumberUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * 联系我们
 * @author zhang
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ContactUsServiceImpl implements ContactUsService {

    private final ContactUsMapper contactUsMapper;

    private final RedisUtil redisUtil;

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX="ldcms:contactUs";
    // 已提交
    public static final String REDIS_KEY_PREFIX_SUBMITTED="ldcms:contactUs:submitted";

    private final MyselfPageUtils myselfPageUtils;

    int flag = 0;
    /**
     * 联系我们添加操作
     * @param contactUsVo 联系我们实体类
     * @return 添加条数
     */
    @Override
    public int addContactUs(ContactUsVo contactUsVo) {
        ContactUs contactUs = MapstructUtils.convert(contactUsVo, ContactUs.class);
        if (StringUtils.isBlank(contactUsVo.getName())){
            flag = MainConstants.NAME_IS_NOT_NULL;
            return flag;
        }
        if (StringUtils.isBlank(contactUsVo.getPhone())){
            flag = MainConstants.PHONE_IS_NOT_NULL;
            return flag;
        }
        if (!VerifyPhoneNumberUtils.isValidPhoneNumber(contactUsVo.getPhone())){
            flag = MainConstants.PHONE_DIGIT_IS_WRONG;
            return flag;
        }
        if (StringUtils.isBlank(contactUsVo.getContent())){
            flag = MainConstants.CONTENT_IS_NOT_NULL;
            return flag;
        }

        contactUs.setCreationDate(new Date());
        contactUs.setState('1');

        flag = contactUsMapper.insert(contactUs);
        return flag;
    }

    /**
     * 答复客户消息
     * @param contactUsVo 联系我们信息实体类
     * @return 答复条数
     */
    @Override
    public int replyContent(ContactUsVo contactUsVo) {
        if (contactUsVo.getId() == null) {
            flag = MainConstants.ID_IS_NOT_NULL;
            return flag;
        }
        LambdaUpdateWrapper<ContactUs> wrapper = new UpdateWrapper<ContactUs>().lambda();
        wrapper.eq(ContactUs::getId, contactUsVo.getId());
        LambdaQueryWrapper<ContactUs> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContactUs::getId, contactUsVo.getId())
                    .eq(ContactUs::getState, "2");
        Long state = contactUsMapper.selectCount(queryWrapper);
        if (StringUtils.isBlank(contactUsVo.getReply())) {
            flag = MainConstants.REPLY_IS_NOT_NULL;
            return flag;
        }
        if (state>=1) {
            flag = MainConstants.CONTENT_IS_REPLIED;
            return flag;
        }
        wrapper.set(ContactUs::getReply, contactUsVo.getReply())
            .set(ContactUs::getState, "2")
            .set(ContactUs::getReplyDate,new Date());
        flag = contactUsMapper.update(wrapper);
        return flag;
    }

    /**
     * 逻辑删除联系我们信息
     * @param contactUsVo 联系我们信息实体类
     * @return 删除条数
     */
    @Override
    public int deleteContactUs(ContactUsVo contactUsVo) {
        if (contactUsVo.getId() == null) {
            flag = MainConstants.ID_IS_NOT_NULL;
            return flag;
        }
        LambdaUpdateWrapper<ContactUs> wrapper = new UpdateWrapper<ContactUs>().lambda();
        wrapper.eq(ContactUs::getId, contactUsVo.getId())
            .set(ContactUs::getState, "0");
        flag = contactUsMapper.update(wrapper);
        return flag;
    }

    /**
     * 查询所有联系我们信息
     * @return 返回所有联系我们信息
     */
    @Override
    public List<ContactUsVo> selectListByContactUs(ContactUsVo contactUsVo) {
        myselfPageUtils.determineToPageByContactUs(contactUsVo);
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX,  0, -1);
            List<ContactUsVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ContactUsVo) o);
            }
            log.info("selectListByContactUs方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            List<ContactUsVo> resultList = PageUtil.startPage(list, contactUsVo.getCurrentPage(), contactUsVo.getPageSize());
            return resultList;
        }
        List<ContactUsVo> newsCenterVos = contactUsMapper.selectListByContactUs(contactUsVo);
        List<ContactUsVo> resultList = myselfPageUtils.queryToMysqlByContactUs(newsCenterVos, contactUsVo);
        updateRedisByContactUs();
        return resultList;
    }

    /**
     * 根据ID查询未处理联系
     * @param id 行业新闻ID
     * @return 返回根据ID查询未处理联系
     */
    @Override
    public List<ContactUsVo> selectListById(Long id) {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            ContactUsVo found = null;
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<ContactUsVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ContactUsVo) o);
            }
            for (ContactUsVo vo : list) {
                if (vo.getId().equals(id)) {
                    found = vo;
                    break;
                }
            }
            List<ContactUsVo> resultList = new ArrayList<>();
            resultList.add(found);
            return resultList;
        }
        updateRedisByContactUs();
        return contactUsMapper.selectListById(id);
    }

    /**
     * 未处理联系条数
     * @return 返回查未处理联系
     */
    @Override
    public int selectCountByContactUs() {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            return (int) redisUtil.getLengthByList(REDIS_KEY_PREFIX);
        }
        updateRedisByContactUs();
        return contactUsMapper.selectCountByContactUs();
    }

    /**
     * 模糊查询名字和公司
     * @return 返回模糊名字和公司
     */
    @Override
    public List<ContactUsVo> selectVagueByMainTitleAndCompany(ContactUsVo contactUsVo) {
        myselfPageUtils.determineToPageByContactUs(contactUsVo);
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)) {
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<ContactUsVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ContactUsVo) o);
            }
            Predicate<ContactUsVo> filterByName = p -> p.getName().toLowerCase().contains(contactUsVo.getName().toLowerCase());
            List<ContactUsVo> filteredByName = list.stream()
                .filter(filterByName)
                .collect(Collectors.toList());

            Predicate<ContactUsVo> filterByCompany = p -> p.getCompany().toLowerCase().contains(contactUsVo.getCompany().toLowerCase());
            List<ContactUsVo> filteredByCompany = filteredByName.stream()
                .filter(filterByCompany)
                .collect(Collectors.toList());

            log.info("selectVagueByProduct方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            List<ContactUsVo> resultList = PageUtil.startPage(filteredByCompany, contactUsVo.getCurrentPage(), contactUsVo.getPageSize());
            return resultList;
        }
        List<ContactUsVo> newsCenterVos = contactUsMapper.selectVagueByMainTitleAndCompany(contactUsVo);
        List<ContactUsVo> resultList = myselfPageUtils.queryToMysqlByContactUs(newsCenterVos, contactUsVo);
        updateRedisByContactUs();
        return resultList;
    }

    /**
     * 查询模糊查询名字和公司数量
     * @return 返回模糊查询名字和公司数量
     */
    @Override
    public int selectVagueCountByMainTitleAndCompany(ContactUsVo contactUsVo) {
        return contactUsMapper.selectVagueCountByMainTitleAndCompany(contactUsVo);
    }

    /**
     * 查询所有已提交联系我们信息
     * @return 返回所有已提交联系我们信息
     */
    @Override
    public List<ContactUsVo> selectListOnSubmittedByContactUs(ContactUsVo contactUsVo) {
        myselfPageUtils.determineToPageByContactUs(contactUsVo);
        if (redisUtil.hasKey(REDIS_KEY_PREFIX_SUBMITTED)){
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX_SUBMITTED,  0, -1);
            List<ContactUsVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ContactUsVo) o);
            }
            log.info("selectListByContactUs方法被调用：" + REDIS_KEY_PREFIX_SUBMITTED + "已存在，直接获取redis缓存！！！");
            List<ContactUsVo> resultList = PageUtil.startPage(list, contactUsVo.getCurrentPage(), contactUsVo.getPageSize());
            return resultList;
        }
        List<ContactUsVo> newsCenterVos = contactUsMapper.selectListByContactUs(contactUsVo);
        List<ContactUsVo> resultList = myselfPageUtils.queryToMysqlByContactUs(newsCenterVos, contactUsVo);
        updateRedisByContactUsOnSubmitted();
        return resultList;
    }

    /**
     * 根据ID查询已提交联系
     * @param id 已提交联系我们ID
     * @return 返回根据ID查询已提交联系
     */
    @Override
    public List<ContactUsVo> selectListByIdOnSubmitted(Long id) {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX_SUBMITTED)){
            ContactUsVo found = null;
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX_SUBMITTED, 0, -1);
            List<ContactUsVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ContactUsVo) o);
            }
            for (ContactUsVo vo : list) {
                if (vo.getId().equals(id)) {
                    found = vo;
                    break;
                }
            }
            List<ContactUsVo> resultList = new ArrayList<>();
            resultList.add(found);
            return resultList;
        }
        updateRedisByContactUsOnSubmitted();
        return contactUsMapper.selectListByIdOnSubmitted(id);
    }

    /**
     * 驳回已提交信息
     * @param contactUsVo 联系我们实体类
     * @return 驳回条数
     */
    @Override
    public int rejectContactUsOnSubmitted(ContactUsVo contactUsVo) {
        if (contactUsVo.getId() == null) {
            flag = MainConstants.ID_IS_NOT_NULL;
            return flag;
        }
        if (contactUsVo.getRejectReason() == null) {
            flag = MainConstants.REASON_FOR_REJECTION_CANNOT_BE_EMPTY;
            return flag;
        }
        LambdaUpdateWrapper<ContactUs> wrapper = new UpdateWrapper<ContactUs>().lambda();
        wrapper.eq(ContactUs::getId, contactUsVo.getId())
            .set(ContactUs::getState, "1")
            .set(ContactUs::getRejectReason,contactUsVo.getRejectReason());
        flag = contactUsMapper.update(wrapper);
        return flag;
    }

    /**
     * 已提交联系我们总条数
     * @return 返回已提交联系我们总条数
     */
    @Override
    public int selectCountOnSubmitted() {
        return contactUsMapper.selectCountOnSubmitted();
    }

    /**
     * 模糊查询已提交名字和公司
     * @return 返回已提交模糊查询名字和公司
     */
    @Override
    public List<ContactUsVo> selectVagueByMainTitleAndCompanyOnSubmitted(ContactUsVo contactUsVo) {
        myselfPageUtils.determineToPageByContactUs(contactUsVo);
        if (redisUtil.hasKey(REDIS_KEY_PREFIX_SUBMITTED)) {
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX_SUBMITTED, 0, -1);
            List<ContactUsVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ContactUsVo) o);
            }
            Predicate<ContactUsVo> filterByName = p -> p.getName().toLowerCase().contains(contactUsVo.getName().toLowerCase());
            List<ContactUsVo> filteredByName = list.stream()
                .filter(filterByName)
                .collect(Collectors.toList());

            Predicate<ContactUsVo> filterByCompany = p -> p.getCompany().toLowerCase().contains(contactUsVo.getCompany().toLowerCase());
            List<ContactUsVo> filteredByCompany = filteredByName.stream()
                .filter(filterByCompany)
                .collect(Collectors.toList());

            log.info("selectVagueByMainTitleAndCompanyOnSubmitted方法被调用：" + REDIS_KEY_PREFIX_SUBMITTED + "已存在，直接获取redis缓存！！！");
            List<ContactUsVo> resultList = PageUtil.startPage(filteredByCompany, contactUsVo.getCurrentPage(), contactUsVo.getPageSize());
            return resultList;
        }
        List<ContactUsVo> newsCenterVos = contactUsMapper.selectVagueByMainTitleAndCompanyOnSubmitted(contactUsVo);
        List<ContactUsVo> resultList = myselfPageUtils.queryToMysqlByContactUs(newsCenterVos, contactUsVo);
        updateRedisByContactUsOnSubmitted();
        return resultList;
    }

    /**
     * 查询模糊查询已提交名字和公司数量
     * @return 返回已提交模糊查询名字和公司数量
     */
    @Override
    public int selectVagueCountByMainTitleAndCompanyOnSubmitted(ContactUsVo contactUsVo) {
        return contactUsMapper.selectVagueCountByMainTitleAndCompanyOnSubmitted(contactUsVo);
    }

    /**
     * 查询所有已提交联系我们信息（只给redis使用）
     * @return 返回所有已提交联系我们信息
     */
    @Override
    public List<ContactUsVo> selectAllStorageToRedisOnSubmitted() {
        return contactUsMapper.selectAllStorageToRedisOnSubmitted();
    }

    /**
     * 联系我们总条数 （只给redis使用）
     * @return 返回联系我们总条数
     */
    @Override
    public int selectCountToRedis() {
        return contactUsMapper.selectCountToRedis();
    }

    /**
     * 查询所有联系我们信息（只给redis使用）
     * @return 返回所有联系我们信息
     */
    @Override
    public List<ContactUsVo> selectAllStorageToRedis() {
        return contactUsMapper.selectAllStorageToRedis();
    }

    public void updateRedisByContactUs() {
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX);
        }
        List<ContactUsVo> contactUsVos = contactUsMapper.selectAllStorageToRedis();
        for (ContactUsVo contactUsVo : contactUsVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX,contactUsVo);
        }
        log.info("updateRedisByContactUs方法被调用：" + REDIS_KEY_PREFIX + "更新成功！！！");
    }

    public void updateRedisByContactUsOnSubmitted() {
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX_SUBMITTED);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX_SUBMITTED);
        }
        List<ContactUsVo> contactUsVos = contactUsMapper.selectAllStorageToRedisOnSubmitted();
        for (ContactUsVo contactUsVo : contactUsVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX_SUBMITTED,contactUsVo);
        }
        log.info("updateRedisByContactUsOnSubmitted方法被调用：" + REDIS_KEY_PREFIX_SUBMITTED + "更新成功！！！");
    }


}
