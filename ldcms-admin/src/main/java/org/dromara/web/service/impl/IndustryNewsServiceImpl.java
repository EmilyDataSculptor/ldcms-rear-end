package org.dromara.web.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.web.constants.MainConstants;
import org.dromara.web.domain.IndustryNews;
import org.dromara.web.domain.SocialResponsibility;
import org.dromara.web.domain.vo.DownloadCenterVo;
import org.dromara.web.domain.vo.IndustryNewsVo;
import org.dromara.web.domain.vo.NewsCenterVo;
import org.dromara.web.domain.vo.SocialResponsibilityVo;
import org.dromara.web.mapper.IndustryNewsMapper;
import org.dromara.web.mapper.SocialResponsibilityMapper;
import org.dromara.web.service.IndustryNewsService;
import org.dromara.web.service.SocialResponsibilityService;
import org.dromara.web.utils.MinioUtils;
import org.dromara.web.utils.MyselfPageUtils;
import org.dromara.web.utils.PageUtil;
import org.dromara.web.utils.RedisUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 行业新闻
 * @author zhang
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class IndustryNewsServiceImpl implements IndustryNewsService {

    private final IndustryNewsMapper industryNewsMapper;
    private final RedisUtil redisUtil;

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX="ldcms:industryNews";

    private final MyselfPageUtils myselfPageUtils;

    int flag = 0;

    /**
     * 添加行业新闻
     * @param industryNewsVo 行业新闻实体类
     * @return 添加条数
     */
    @Override
    public int addIndustryNews(IndustryNewsVo industryNewsVo) {
        IndustryNews industryNews = MapstructUtils.convert(industryNewsVo, IndustryNews.class);
        if (StringUtils.isBlank(industryNewsVo.getMainTitle())) {
            flag = MainConstants.MAIN_TITLE_IS_NOT_NULL;
            return flag;
        }
        if (StringUtils.isBlank(industryNewsVo.getContent())) {
            flag = MainConstants.CONTENT_IS_NOT_NULL;
            return flag;
        }
        if (StringUtils.isBlank(industryNewsVo.getPicture())) {
            industryNews.setPicture(MainConstants.HOMEPAGE_IMAGE_ADDRESS);
        }
        industryNews.setCreationDate(new DateTime());
        industryNews.setState('1');
        flag = industryNewsMapper.insert(industryNews);
        return flag;
    }

    /**
     * 逻辑删除行业新闻
     * @param industryNewsVo 行业新闻实体类
     * @return 删除条数
     */
    @Override
    public int deleteIndustryNews(IndustryNewsVo industryNewsVo) {
        if (industryNewsVo.getId() == null) {
            flag = MainConstants.ID_IS_NOT_NULL;
            return flag;
        }
        LambdaUpdateWrapper<IndustryNews> wrapper = new UpdateWrapper<IndustryNews>().lambda();
        wrapper.eq(IndustryNews::getId, industryNewsVo.getId())
            .set(IndustryNews::getState, "0");
        flag = industryNewsMapper.update(wrapper);
        return flag;
    }

    /**
     * 修改行业新闻
     * @param industryNewsVo 行业新闻实体类
     * @return 修改条数
     */
    @Override
    public int updateIndustryNews(IndustryNewsVo industryNewsVo) {
        if (industryNewsVo.getId() == null) {
            flag = MainConstants.ID_IS_NOT_NULL;
            return flag;
        }
        LambdaUpdateWrapper<IndustryNews> wrapper = new UpdateWrapper<IndustryNews>().lambda();
        wrapper.eq(IndustryNews::getId, industryNewsVo.getId());
        if (StringUtils.isNotBlank(industryNewsVo.getMainTitle())) {
            wrapper.set(IndustryNews::getMainTitle, industryNewsVo.getMainTitle());
        }
        if (StringUtils.isNotBlank(industryNewsVo.getContent())) {
            wrapper.set(IndustryNews::getContent, industryNewsVo.getContent());
        }
        if (StringUtils.isNotBlank(industryNewsVo.getPicture())) {
            wrapper.set(IndustryNews::getPicture, industryNewsVo.getPicture());
        }
        if (industryNewsVo.getState() != '\0') {
            wrapper.set(IndustryNews::getState, industryNewsVo.getState());
        }
        flag = industryNewsMapper.update(null, wrapper);
        return flag;
    }

    /**
     * 查询所有行业新闻
     * @param industryNewsVo 行业新闻实体类
     * @return 返回所有行业新闻
     */
    @Override
    public List<IndustryNewsVo> selectListByIndustryNews(IndustryNewsVo industryNewsVo) {

        if (industryNewsVo.getCurrentPage() == 0 && industryNewsVo.getPageSize() == 0) {
            industryNewsVo.setCurrentPage(1);
            industryNewsVo.setPageSize(5);
        }

        if (industryNewsVo.getCurrentPage() == 0 && industryNewsVo.getPageSize() > 0) {
            industryNewsVo.setCurrentPage(1);
        }

        if (industryNewsVo.getCurrentPage() > 0 && industryNewsVo.getPageSize() == 0) {
            industryNewsVo.setPageSize(1);
        }

        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX,  0, -1);
            List<IndustryNewsVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((IndustryNewsVo) o);
            }
            log.info("selectListByIndustryNews方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            List<IndustryNewsVo> resultList = PageUtil.startPage(list, industryNewsVo.getCurrentPage(), industryNewsVo.getPageSize());
            return resultList;
        }

        List<IndustryNewsVo> industryNewsVos = industryNewsMapper.selectListByIndustryNews(industryNewsVo);
        List<IndustryNewsVo> list = PageUtil.startPage(industryNewsVos, industryNewsVo.getCurrentPage(), industryNewsVo.getPageSize());
        List<IndustryNewsVo> theList = new ArrayList<>();
        for (IndustryNewsVo o : list) {
            o.setCurrentPage(industryNewsVo.getCurrentPage());
            o.setPageSize(industryNewsVo.getPageSize());
            theList.add(o);
        }
        updateRedisByIndustryNews();
        return theList;
    }


    /**
     * 查询所有行业新闻
     * @return 返回所有行业新闻
     */
    @Override
    public List<IndustryNewsVo> selectAllByIndustryNews() {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<IndustryNewsVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((IndustryNewsVo) o);
            }
            log.info("selectLAllByDownloadCenter方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            return list;
        }

        updateRedisByIndustryNews();
        return industryNewsMapper.selectAllByIndustryNews();
    }

    /**
     * 根据ID查询行业新闻
     * @param id 行业新闻ID
     * @return 返回根据ID查询行业新闻
     */
    @Override
    public List<IndustryNewsVo> selectListById(Long id) {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            IndustryNewsVo found = null;
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<IndustryNewsVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((IndustryNewsVo) o);
            }
            for (IndustryNewsVo vo : list) {
                if (vo.getId().equals(id)) {
                    found = vo;
                    break;
                }
            }
            List<IndustryNewsVo> resultList = new ArrayList<>();
            resultList.add(found);
            return resultList;
        }
        updateRedisByIndustryNews();
        return industryNewsMapper.selectListById(id);
    }

    /**
     * 行业新闻中心条数
     * @return 返回查询行业新闻
     */
    @Override
    public int selectCountByIndustryNews() {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            return (int) redisUtil.getLengthByList(REDIS_KEY_PREFIX);
        }
        updateRedisByIndustryNews();
        return industryNewsMapper.selectCountByIndustryNews();
    }

    /**
     * 模糊查询主标题
     * @return 返回模糊查询主标题
     */
    @Override
    public List<IndustryNewsVo> selectVagueByMainTitle(IndustryNewsVo industryNewsVo) {
        myselfPageUtils.determineToPageByIndustryNews(industryNewsVo);
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)) {
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<IndustryNewsVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((IndustryNewsVo) o);
            }
            Predicate<IndustryNewsVo> filterByMainTitle = p -> p.getMainTitle().toLowerCase().contains(industryNewsVo.getMainTitle().toLowerCase());
            List<IndustryNewsVo> filteredByMainTitle = list.stream()
                .filter(filterByMainTitle)
                .collect(Collectors.toList());

            log.info("selectVagueByProduct方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            List<IndustryNewsVo> resultList = PageUtil.startPage(filteredByMainTitle, industryNewsVo.getCurrentPage(), industryNewsVo.getPageSize());
            return resultList;
        }
        List<IndustryNewsVo> newsCenterVos = industryNewsMapper.selectVagueByMainTitle(industryNewsVo);
        List<IndustryNewsVo> resultList = myselfPageUtils.queryToMysqlByIndustryNews(newsCenterVos, industryNewsVo);
        updateRedisByIndustryNews();
        return resultList;
    }

    /**
     * 查询模糊查询主标题数量
     * @return 返回模糊查询主标题数量
     */
    @Override
    public int selectVagueCountByMainTitle(IndustryNewsVo industryNewsVo) {
        return industryNewsMapper.selectVagueCountByMainTitle(industryNewsVo);
    }

    /**
     * 行业新闻总条数 （只给redis使用）
     * @return 返回行业新闻总条数
     */
    @Override
    public int selectCountToRedis() {
        return industryNewsMapper.selectCountToRedis();
    }

    /**
     * 查询所有行业新闻（只给redis使用）
     * @return 返回所有行业新闻
     */
    @Override
    public List<IndustryNewsVo> selectAllStorageToRedis() {
        return industryNewsMapper.selectAllStorageToRedis();
    }

    public void updateRedisByIndustryNews() {
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX);
        }
        List<IndustryNewsVo> industryNewsVos = industryNewsMapper.selectAllStorageToRedis();
        for (IndustryNewsVo industryNewsVo : industryNewsVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX,industryNewsVo);
        }
        log.info("updateRedisByIndustryNews方法被调用：" + REDIS_KEY_PREFIX + "更新成功！！！");
    }
}
