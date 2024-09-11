package org.dromara.web.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.web.constants.MainConstants;
import org.dromara.web.domain.SocialResponsibility;
import org.dromara.web.domain.vo.SocialResponsibilityVo;
import org.dromara.web.mapper.SocialResponsibilityMapper;
import org.dromara.web.service.SocialResponsibilityService;
import org.dromara.web.utils.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 社会责任
 * @author zhang
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SocialResponsibilityServiceImpl implements SocialResponsibilityService {

    private final SocialResponsibilityMapper socialResponsibilityMapper;


    private final RedisUtil redisUtil;

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX="ldcms:socialResponsibility";

    private final MyselfPageUtils myselfPageUtils;

    /**
     * 添加社会责任新闻
     * @param socialResponsibilityVo 社会责任实体类
     * @return 添加条数
     */
    @Override
    public int addSocialResponsibility(SocialResponsibilityVo socialResponsibilityVo) {
        SocialResponsibility socialResponsibility = MapstructUtils.convert(socialResponsibilityVo, SocialResponsibility.class);
        int flag = 0;
        if (StringUtils.isBlank(socialResponsibilityVo.getMainTitle())) {
            flag = MainConstants.MAIN_TITLE_IS_NOT_NULL;
            return flag;
        }
        if (StringUtils.isBlank(socialResponsibilityVo.getContent())) {
            flag = MainConstants.CONTENT_IS_NOT_NULL;
            return flag;
        }
        if (StringUtils.isBlank(socialResponsibilityVo.getPicture())) {
            socialResponsibility.setPicture(MainConstants.HOMEPAGE_IMAGE_ADDRESS);
        }
        socialResponsibility.setCreationDate(new DateTime());
        socialResponsibility.setState('1');
        flag = socialResponsibilityMapper.insert(socialResponsibility);
        return flag;
    }

    /**
     * 逻辑删除社会责任新闻
     * @param socialResponsibilityVo 社会责任实体类
     * @return 删除条数
     */
    @Override
    public int deleteSocialResponsibility(SocialResponsibilityVo socialResponsibilityVo) {
        int flag = 0;
        if (socialResponsibilityVo.getId() == null) {
            flag = MainConstants.ID_IS_NOT_NULL;
            return flag;
        }
        LambdaUpdateWrapper<SocialResponsibility> wrapper = new UpdateWrapper<SocialResponsibility>().lambda();
        wrapper.eq(SocialResponsibility::getId, socialResponsibilityVo.getId())
            .set(SocialResponsibility::getState, "0");
        flag = socialResponsibilityMapper.update(wrapper);
        return flag;
    }

    /**
     * 修改社会责任新闻
     * @param socialResponsibilityVo 社会责任实体类
     * @return 修改条数
     */
    @Override
    public int updateSocialResponsibility(SocialResponsibilityVo socialResponsibilityVo) {
        int flag = 0;
        if (socialResponsibilityVo.getId() == null) {
            flag = MainConstants.ID_IS_NOT_NULL;
            return flag;
        }
        LambdaUpdateWrapper<SocialResponsibility> wrapper = new UpdateWrapper<SocialResponsibility>().lambda();
        wrapper.eq(SocialResponsibility::getId, socialResponsibilityVo.getId());
        if (StringUtils.isNotBlank(socialResponsibilityVo.getMainTitle())) {
            wrapper.set(SocialResponsibility::getMainTitle, socialResponsibilityVo.getMainTitle());
        }
        if (StringUtils.isNotBlank(socialResponsibilityVo.getContent())) {
            wrapper.set(SocialResponsibility::getContent, socialResponsibilityVo.getContent());
        }
        if (StringUtils.isNotBlank(socialResponsibilityVo.getPicture())) {
            wrapper.set(SocialResponsibility::getPicture, socialResponsibilityVo.getPicture());
        }
        if (socialResponsibilityVo.getState() != '\0') {
            wrapper.set(SocialResponsibility::getState, socialResponsibilityVo.getState());
        }
        flag = socialResponsibilityMapper.update(null, wrapper);
        return flag;
    }

    /**
     * 查询所有社会责任新闻
     * @param socialResponsibilityVo 社会责任实体类
     * @return 返回所有社会责任新闻
     */
    @Override
    public List<SocialResponsibilityVo> selectListBySocialResponsibility(SocialResponsibilityVo socialResponsibilityVo) {

        if (socialResponsibilityVo.getCurrentPage() == 0 && socialResponsibilityVo.getPageSize() == 0) {
            socialResponsibilityVo.setCurrentPage(1);
            socialResponsibilityVo.setPageSize(5);
        }

        if (socialResponsibilityVo.getCurrentPage() == 0 && socialResponsibilityVo.getPageSize() > 0) {
            socialResponsibilityVo.setCurrentPage(1);
        }

        if (socialResponsibilityVo.getCurrentPage() > 0 && socialResponsibilityVo.getPageSize() == 0) {
            socialResponsibilityVo.setPageSize(1);
        }

        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX,  0, -1);
            List<SocialResponsibilityVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((SocialResponsibilityVo) o);
            }
            List<SocialResponsibilityVo> resultList = PageUtil.startPage(list, socialResponsibilityVo.getCurrentPage(), socialResponsibilityVo.getPageSize());
            log.info("selectListBySocialResponsibility方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            return resultList;
        }

        List<SocialResponsibilityVo> socialResponsibilityVos = socialResponsibilityMapper.selectListBySocialResponsibility(socialResponsibilityVo);
        List<SocialResponsibilityVo> list = PageUtil.startPage(socialResponsibilityVos, socialResponsibilityVo.getCurrentPage(), socialResponsibilityVo.getPageSize());
        List<SocialResponsibilityVo> theList = new ArrayList<>();
        for (SocialResponsibilityVo o : list) {
            o.setCurrentPage(socialResponsibilityVo.getCurrentPage());
            o.setPageSize(socialResponsibilityVo.getPageSize());
            theList.add(o);
        }
        updateRedisBySocialResponsibility();
        return theList;
    }

    /**
     * 查询社会责任数量
     * @return 返回社会责任数量
     */
    @Override
    public int selectCountBySocialResponsibility(){
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            return (int) redisUtil.getLengthByList(REDIS_KEY_PREFIX);
        }
        updateRedisBySocialResponsibility();
        return socialResponsibilityMapper.selectCountBySocialResponsibility();
    }

    /**
     * 根据产品ID查询社会责任
     * @return 返回根据产品ID查询社会责任
     */
    @Override
    public List<SocialResponsibilityVo> selectSocialResponsibilityById(Long id){

        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            SocialResponsibilityVo found = null;
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<SocialResponsibilityVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((SocialResponsibilityVo) o);
            }
            for (SocialResponsibilityVo vo : list) {
                if (vo.getId().equals(id)) {
                    found = vo;
                    break;
                }
            }
            List<SocialResponsibilityVo> resultList = new ArrayList<>();
            resultList.add(found);
            return resultList;
        }
        updateRedisBySocialResponsibility();
        return socialResponsibilityMapper.selectSocialResponsibilityById(id);
    }

    /**
     * 查询所有社会责任
     * @return 查询所有社会责任
     */
    @Override
    public List<SocialResponsibilityVo> selectAllBySocialResponsibility() {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<SocialResponsibilityVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((SocialResponsibilityVo) o);
            }
            log.info("selectAllBySocialResponsibility方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            return list;
        }

        updateRedisBySocialResponsibility();
        return socialResponsibilityMapper.selectAllBySocialResponsibility();
    }

    /**
     * 模糊查询主标题
     * @return 返回模糊查询主标题
     */
    @Override
    public List<SocialResponsibilityVo> selectVagueByMainTitle(SocialResponsibilityVo socialResponsibilityVo) {
        myselfPageUtils.determineToPageBySocialResponsibility(socialResponsibilityVo);
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)) {
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<SocialResponsibilityVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((SocialResponsibilityVo) o);
            }
            Predicate<SocialResponsibilityVo> filterByMainTitle = p -> p.getMainTitle().toLowerCase().contains(socialResponsibilityVo.getMainTitle().toLowerCase());
            List<SocialResponsibilityVo> filteredProductsByMainTitle = list.stream()
                .filter(filterByMainTitle)
                .collect(Collectors.toList());

            log.info("selectVagueByProduct方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            List<SocialResponsibilityVo> resultList = PageUtil.startPage(filteredProductsByMainTitle, socialResponsibilityVo.getCurrentPage(), socialResponsibilityVo.getPageSize());
            return resultList;
        }
        List<SocialResponsibilityVo> socialResponsibilityVos = socialResponsibilityMapper.selectVagueByMainTitle(socialResponsibilityVo);
        List<SocialResponsibilityVo> resultList = myselfPageUtils.queryToMysqlBySocialResponsibility(socialResponsibilityVos, socialResponsibilityVo);
        updateRedisBySocialResponsibility();
        return resultList;
    }

    /**
     * 查询模糊查询主标题数量
     * @return 返回模糊查询主标题数量
     */
    @Override
    public int selectVagueCountByMainTitle(SocialResponsibilityVo socialResponsibilityVo) {
        return socialResponsibilityMapper.selectVagueCountByMainTitle(socialResponsibilityVo);
    }

    public void updateRedisBySocialResponsibility() {
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX);
        }
        List<SocialResponsibilityVo> socialResponsibilityVos = socialResponsibilityMapper.selectAllStorageToRedis();
        for (SocialResponsibilityVo socialResponsibilityVo : socialResponsibilityVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX,socialResponsibilityVo);
        }
        log.info("updateRedisBySocialResponsibility方法被调用：" + REDIS_KEY_PREFIX + "更新成功！！！");
    }

    /**
     * 查询所有社会责任（只给redis使用）
     * @return 返回查询所有社会责任
     */
    @Override
    public List<SocialResponsibilityVo> selectAllStorageToRedis() {
        return socialResponsibilityMapper.selectAllStorageToRedis();
    }

    /**
     * 社会责任总条数 （只给redis使用）
     * @return 返回社会责任总条数
     */
    @Override
    public int selectCountToRedis() {
        return socialResponsibilityMapper.selectCountToRedis();
    }
}
