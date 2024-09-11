package org.dromara.web.mapper;

import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.web.domain.IndustryNews;
import org.dromara.web.domain.NewsCenter;
import org.dromara.web.domain.vo.IndustryNewsVo;
import org.dromara.web.domain.vo.NewsCenterVo;
import org.dromara.web.domain.vo.SocialResponsibilityVo;

import java.util.List;

/**
 * 联系我们
 * @author zhang
 */
public interface NewsCenterMapper extends BaseMapperPlus<NewsCenter, NewsCenterVo> {

    /**
     * 查询所有新闻中心
     * @param newsCenterVo 新闻中心实体类
     * @return 返回所有新闻中心
     */
    List<NewsCenterVo> selectListByNewsCenter(NewsCenterVo newsCenterVo);

    /**
     * 查询所有新闻中心
     * @return 返回所有新闻中心
     */
    List<NewsCenterVo> selectAllByNewsCenter();

    /**
     * 根据ID查询新闻中心
     * @param id 新闻中心ID
     * @return 返回根据ID查询新闻中心
     */
    List<NewsCenterVo> selectListById(Long id);

    /**
     * 查询新闻中心条数
     * @return 返回查询新闻中心条数
     */
    int selectCountByNewsCenter();

    /**
     * 模糊查询主标题
     * @return 返回模糊查询主标题
     */
    List<NewsCenterVo> selectVagueByMainTitle(NewsCenterVo newsCenterVo);

    /**
     * 查询模糊查询主标题数量
     * @return 返回模糊查询主标题数量
     */
    int selectVagueCountByMainTitle(NewsCenterVo newsCenterVo);

    /**
     * 查询所有新闻中心（只给redis使用）
     * @return 返回所有新闻中心
     */
    List<NewsCenterVo> selectAllStorageToRedis();

    /**
     * 新闻中心总条数 （只给redis使用）
     * @return 返回新闻中心总条数
     */
    int selectCountToRedis();



}
