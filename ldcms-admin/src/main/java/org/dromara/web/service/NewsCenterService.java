package org.dromara.web.service;

import org.dromara.web.domain.vo.IndustryNewsVo;
import org.dromara.web.domain.vo.NewsCenterVo;

import java.util.List;

/**
 * 新闻中心
 * @author zhang
 */
public interface NewsCenterService {

    /**
     * 添加新闻中心
     * @param newsCenterVo 新闻中心实体类
     * @return 添加条数
     */
    int addNewsCenter(NewsCenterVo newsCenterVo);

    /**
     * 逻辑删除新闻中心
     * @param newsCenterVo 新闻中心实体类
     * @return 删除条数
     */
    int deleteNewsCenter(NewsCenterVo newsCenterVo);

    /**
     * 修改新闻中心
     * @param newsCenterVo 新闻中心实体类
     * @return 修改条数
     */
    int updateNewsCenter(NewsCenterVo newsCenterVo);

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
