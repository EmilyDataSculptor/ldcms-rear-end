package org.dromara.web.service;

import org.dromara.web.domain.vo.IndustryNewsVo;
import org.dromara.web.domain.vo.NewsCenterVo;
import org.dromara.web.domain.vo.SocialResponsibilityVo;

import java.util.List;

/**
 * 行业新闻
 * @author zhang
 */
public interface IndustryNewsService {

    /**
     * 添加行业新闻
     * @param industryNewsVo 行业新闻实体类
     * @return 添加条数
     */
    int addIndustryNews(IndustryNewsVo industryNewsVo);

    /**
     * 逻辑删除行业新闻
     * @param industryNewsVo 行业新闻实体类
     * @return 删除条数
     */
    int deleteIndustryNews(IndustryNewsVo industryNewsVo);

    /**
     * 修改行业新闻
     * @param industryNewsVo 行业新闻实体类
     * @return 修改条数
     */
    int updateIndustryNews(IndustryNewsVo industryNewsVo);

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
