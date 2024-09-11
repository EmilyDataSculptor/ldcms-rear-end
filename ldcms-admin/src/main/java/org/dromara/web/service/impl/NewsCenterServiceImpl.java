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
import org.dromara.web.domain.NewsCenter;
import org.dromara.web.domain.vo.IndustryNewsVo;
import org.dromara.web.domain.vo.NewsCenterVo;
import org.dromara.web.domain.vo.SocialResponsibilityVo;
import org.dromara.web.mapper.IndustryNewsMapper;
import org.dromara.web.mapper.NewsCenterMapper;
import org.dromara.web.service.IndustryNewsService;
import org.dromara.web.service.NewsCenterService;
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
 * 新闻中心
 * @author zhang
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class NewsCenterServiceImpl implements NewsCenterService {

    private final NewsCenterMapper newsCenterMapper;
    private final RedisUtil redisUtil;

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX="ldcms:newsCenter";
    private final MyselfPageUtils myselfPageUtils;

    /**
     * 添加新闻中心
     * @param newsCenterVo 新闻中心实体类
     * @return 添加条数
     */
    @Override
    public int addNewsCenter(NewsCenterVo newsCenterVo) {
        NewsCenter newsCenter = MapstructUtils.convert(newsCenterVo, NewsCenter.class);
        int flag = 0;
        if (StringUtils.isBlank(newsCenterVo.getMainTitle())) {
            flag = MainConstants.MAIN_TITLE_IS_NOT_NULL;
            return flag;
        }
        if (StringUtils.isBlank(newsCenterVo.getContent())) {
            flag = MainConstants.CONTENT_IS_NOT_NULL;
            return flag;
        }
        if (StringUtils.isBlank(newsCenterVo.getPicture())) {
            newsCenter.setPicture(MainConstants.HOMEPAGE_IMAGE_ADDRESS);
        }
        newsCenter.setCreationDate(new DateTime());
        newsCenter.setState('1');
        flag = newsCenterMapper.insert(newsCenter);
        return flag;
    }

    /**
     * 逻辑删除新闻中心
     * @param newsCenterVo 新闻中心实体类
     * @return 删除条数
     */
    @Override
    public int deleteNewsCenter(NewsCenterVo newsCenterVo) {
        int flag = 0;
        if (newsCenterVo.getId() == null) {
            flag = MainConstants.ID_IS_NOT_NULL;
            return flag;
        }
        LambdaUpdateWrapper<NewsCenter> wrapper = new UpdateWrapper<NewsCenter>().lambda();
        wrapper.eq(NewsCenter::getId, newsCenterVo.getId())
            .set(NewsCenter::getState, "0");
        flag = newsCenterMapper.update(wrapper);
        return flag;
    }

    /**
     * 修改新闻中心
     * @param newsCenterVo 新闻中心实体类
     * @return 修改条数
     */
    @Override
    public int updateNewsCenter(NewsCenterVo newsCenterVo) {

        int flag = 0;
        if (newsCenterVo.getId() == null) {
            flag = MainConstants.ID_IS_NOT_NULL;
            return flag;
        }
        LambdaUpdateWrapper<NewsCenter> wrapper = new UpdateWrapper<NewsCenter>().lambda();
        wrapper.eq(NewsCenter::getId, newsCenterVo.getId());
        if (StringUtils.isNotBlank(newsCenterVo.getMainTitle())) {
            wrapper.set(NewsCenter::getMainTitle, newsCenterVo.getMainTitle());
        }
        if (StringUtils.isNotBlank(newsCenterVo.getContent())) {
            wrapper.set(NewsCenter::getContent, newsCenterVo.getContent());
        }
        if (StringUtils.isNotBlank(newsCenterVo.getPicture())) {
            wrapper.set(NewsCenter::getPicture, newsCenterVo.getPicture());
        }
        if (newsCenterVo.getState() != '\0') {
            wrapper.set(NewsCenter::getState, newsCenterVo.getState());
        }
        flag = newsCenterMapper.update(null, wrapper);
        return flag;
    }

    /**
     * 查询所有新闻中心
     * @param newsCenterVo 新闻中心实体类
     * @return 返回所有新闻中心
     */
    @Override
    public List<NewsCenterVo> selectListByNewsCenter(NewsCenterVo newsCenterVo) {
        if (newsCenterVo.getCurrentPage() == 0 && newsCenterVo.getPageSize() == 0) {
            newsCenterVo.setCurrentPage(1);
            newsCenterVo.setPageSize(5);
        }

        if (newsCenterVo.getCurrentPage() == 0 && newsCenterVo.getPageSize() > 0) {
            newsCenterVo.setCurrentPage(1);
        }

        if (newsCenterVo.getCurrentPage() > 0 && newsCenterVo.getPageSize() == 0) {
            newsCenterVo.setPageSize(1);
        }

        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX,  0, -1);
            List<NewsCenterVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((NewsCenterVo) o);
            }
            List<NewsCenterVo> resultList = PageUtil.startPage(list, newsCenterVo.getCurrentPage(), newsCenterVo.getPageSize());
            log.info("selectListByNewsCenter方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            return resultList;
        }

        List<NewsCenterVo> newsCenterVos = newsCenterMapper.selectListByNewsCenter(newsCenterVo);
        List<NewsCenterVo> list = PageUtil.startPage(newsCenterVos, newsCenterVo.getCurrentPage(), newsCenterVo.getPageSize());
        List<NewsCenterVo> theList = new ArrayList<>();
        for (NewsCenterVo o : list) {
            o.setCurrentPage(newsCenterVo.getCurrentPage());
            o.setPageSize(newsCenterVo.getPageSize());
            theList.add(o);
        }
        updateRedisByNewsCenter();
        return theList;
    }

    /**
     * 查询所有新闻中心
     * @return 返回所有新闻中心
     */
    @Override
    public List<NewsCenterVo> selectAllByNewsCenter() {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<NewsCenterVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((NewsCenterVo) o);
            }
            log.info("selectLAllByDownloadCenter方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            return list;
        }

        updateRedisByNewsCenter();
        return newsCenterMapper.selectAllByNewsCenter();
    }

    /**
     * 根据ID查询新闻中心
     * @param id 新闻中心ID
     * @return 返回根据ID查询新闻中心
     */
    @Override
    public List<NewsCenterVo> selectListById(Long id) {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            NewsCenterVo found = null;
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<NewsCenterVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((NewsCenterVo) o);
            }
            for (NewsCenterVo vo : list) {
                if (vo.getId().equals(id)) {
                    found = vo;
                    break;
                }
            }
            List<NewsCenterVo> resultList = new ArrayList<>();
            resultList.add(found);
            return resultList;
        }
        updateRedisByNewsCenter();
        return newsCenterMapper.selectListById(id);
    }

    /**
     * 查询新闻中心条数
     * @return 返回查询新闻中心条数
     */
    @Override
    public int selectCountByNewsCenter() {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            return (int) redisUtil.getLengthByList(REDIS_KEY_PREFIX);
        }
        updateRedisByNewsCenter();
        return newsCenterMapper.selectCountByNewsCenter();
    }

    /**
     * 模糊查询主标题
     * @return 返回模糊查询主标题
     */
    @Override
    public List<NewsCenterVo> selectVagueByMainTitle(NewsCenterVo newsCenterVo) {
        myselfPageUtils.determineToPageByNewsCenter(newsCenterVo);
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)) {
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<NewsCenterVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((NewsCenterVo) o);
            }
            Predicate<NewsCenterVo> filterByMainTitle = p -> p.getMainTitle().toLowerCase().contains(newsCenterVo.getMainTitle().toLowerCase());
            List<NewsCenterVo> filteredByMainTitle = list.stream()
                .filter(filterByMainTitle)
                .collect(Collectors.toList());

            log.info("selectVagueByProduct方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            List<NewsCenterVo> resultList = PageUtil.startPage(filteredByMainTitle, newsCenterVo.getCurrentPage(), newsCenterVo.getPageSize());
            return resultList;
        }
        List<NewsCenterVo> newsCenterVos = newsCenterMapper.selectVagueByMainTitle(newsCenterVo);
        List<NewsCenterVo> resultList = myselfPageUtils.queryToMysqlByNewsCenter(newsCenterVos, newsCenterVo);
        updateRedisByNewsCenter();
        return resultList;
    }

    /**
     * 查询模糊查询主标题数量
     * @return 返回模糊查询主标题数量
     */
    @Override
    public int selectVagueCountByMainTitle(NewsCenterVo newsCenterVo) {
        return newsCenterMapper.selectVagueCountByMainTitle(newsCenterVo);
    }

    /**
     * 新闻中心总条数 （只给redis使用）
     * @return 返回新闻中心总条数
     */
    @Override
    public int selectCountToRedis() {
        return newsCenterMapper.selectCountToRedis();
    }

    /**
     * 查询所有新闻中心（只给redis使用）
     * @return 返回所有新闻中心
     */
    @Override
    public List<NewsCenterVo> selectAllStorageToRedis() {
        return newsCenterMapper.selectAllStorageToRedis();
    }

    public void updateRedisByNewsCenter() {
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX);
        }
        List<NewsCenterVo> newsCenterVos = newsCenterMapper.selectAllStorageToRedis();
        for (NewsCenterVo newsCenterVo : newsCenterVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX,newsCenterVo);
        }
        log.info("updateRedisByNewsCenter方法被调用：" + REDIS_KEY_PREFIX + "更新成功！！！");
    }
}
