package org.dromara.web.mapper;

import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.web.domain.IndustryNews;
import org.dromara.web.domain.SocialResponsibility;
import org.dromara.web.domain.vo.IndustryNewsVo;
import org.dromara.web.domain.vo.NewsCenterVo;
import org.dromara.web.domain.vo.SocialResponsibilityVo;

import java.util.List;

/**
 * 联系我们
 * @author zhang
 */
public interface IndustryNewsMapper extends BaseMapperPlus<IndustryNews, IndustryNewsVo> {

    /**
     * 查询所有行业新闻
     * @param industryNewsVo 行业新闻实体类
     * @return 返回所有行业新闻
     */
    List<IndustryNewsVo> selectListByIndustryNews(IndustryNewsVo industryNewsVo);

    /**
     * 查询所有行业新闻
     * @return 返回所有行业新闻
     */
    List<IndustryNewsVo> selectAllByIndustryNews();

    /**
     * 根据ID查询行业新闻
     * @param id 行业新闻ID
     * @return 返回根据ID查询行业新闻
     */
    List<IndustryNewsVo> selectListById(Long id);

    /**
     * 行业新闻中心条数
     * @return 返回查询行业新闻
     */
    int selectCountByIndustryNews();

    /**
     * 模糊查询主标题
     * @return 返回模糊查询主标题
     */
    List<IndustryNewsVo> selectVagueByMainTitle(IndustryNewsVo industryNewsVo);

    /**
     * 查询模糊查询主标题数量
     * @return 返回模糊查询主标题数量
     */
    int selectVagueCountByMainTitle(IndustryNewsVo industryNewsVo);

    /**
     * 查询所有行业新闻（只给redis使用）
     * @return 返回所有行业新闻
     */
    List<IndustryNewsVo> selectAllStorageToRedis();

    /**
     * 行业新闻总条数 （只给redis使用）
     * @return 返回行业新闻总条数
     */
    int selectCountToRedis();



}
