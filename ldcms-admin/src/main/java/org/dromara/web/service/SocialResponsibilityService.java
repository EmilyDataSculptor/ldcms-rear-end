package org.dromara.web.service;

import org.dromara.web.domain.Product;
import org.dromara.web.domain.bo.SocialResponsibilityBo;
import org.dromara.web.domain.vo.ProductVo;
import org.dromara.web.domain.vo.ServiceAndSupportVo;
import org.dromara.web.domain.vo.SocialResponsibilityVo;

import java.util.List;

/**
 * 社会责任
 * @author zhang
 */
public interface SocialResponsibilityService {

    /**
     * 添加社会责任新闻
     * @param socialResponsibilityVo 社会责任实体类
     * @return 添加条数
     */
    int addSocialResponsibility(SocialResponsibilityVo socialResponsibilityVo);

    /**
     * 逻辑删除社会责任新闻
     * @param socialResponsibilityVo 社会责任实体类
     * @return 删除条数
     */
    int deleteSocialResponsibility(SocialResponsibilityVo socialResponsibilityVo);

    /**
     * 修改社会责任新闻
     * @param socialResponsibilityVo 社会责任实体类
     * @return 修改条数
     */
    int updateSocialResponsibility(SocialResponsibilityVo socialResponsibilityVo);

    /**
     * 查询所有社会责任新闻
     * @param socialResponsibilityVo 社会责任实体类
     * @return 返回所有社会责任新闻
     */
    List<SocialResponsibilityVo> selectListBySocialResponsibility(SocialResponsibilityVo socialResponsibilityVo);

    /**
     * 查询社会责任数量
     * @return 返回社会责任数量
     */
    int selectCountBySocialResponsibility();

    /**
     * 根据产品ID查询社会责任
     * @return 返回根据产品ID查询社会责任
     */
    List<SocialResponsibilityVo> selectSocialResponsibilityById(Long id);

    /**
     * 查询所有社会责任
     * @return 查询所有社会责任
     */
    List<SocialResponsibilityVo> selectAllBySocialResponsibility();

    /**
     * 模糊查询主标题
     * @return 返回模糊查询主标题
     */
    List<SocialResponsibilityVo> selectVagueByMainTitle(SocialResponsibilityVo socialResponsibilityVo);

    /**
     * 查询模糊查询主标题数量
     * @return 返回模糊查询主标题数量
     */
    int selectVagueCountByMainTitle(SocialResponsibilityVo socialResponsibilityVo);

    /**
     * 查询所有社会责任（只给redis使用）
     * @return 返回查询所有社会责任
     */
    List<SocialResponsibilityVo> selectAllStorageToRedis();

    /**
     * 社会责任总条数 （只给redis使用）
     * @return 返回社会责任总条数
     */
    int selectCountToRedis();


}
