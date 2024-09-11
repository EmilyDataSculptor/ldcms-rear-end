package org.dromara.web.service;

import org.dromara.web.domain.bo.ProductBo;
import org.dromara.web.domain.bo.ServiceAndSupportBo;
import org.dromara.web.domain.vo.ProductVo;
import org.dromara.web.domain.vo.ServiceAndSupportVo;
import org.dromara.web.domain.vo.SocialResponsibilityVo;

import java.util.List;

/**
 * 服务与支持
 * @author zhang
 */
public interface ServiceAndSupportService {

    /**
     * 添加服务与支持
     * @param serviceAndSupportVo 服务与支持实体类
     * @return 添加条数
     */
    int addServiceAndSupport(ServiceAndSupportVo serviceAndSupportVo);

    /**
     * 逻辑删除服务与支持
     * @param serviceAndSupportVo 服务与支持实体类
     * @return 删除条数
     */
    int deleteServiceAndSupport(ServiceAndSupportVo serviceAndSupportVo);

    /**
     * 修改社会责任新闻
     * @param serviceAndSupportVo 服务与支持实体类
     * @return 修改条数
     */
    int updateServiceAndSupport(ServiceAndSupportVo serviceAndSupportVo);

    /**
     * 查询所有服务与支持
     * @param serviceAndSupportVo 服务与支持实体类
     * @return 返回所有服务与支持
     */
    List<ServiceAndSupportVo> selectListByServiceAndSupport(ServiceAndSupportVo serviceAndSupportVo);

    /**
     * 根据城市查询服务与支持
     * @param serviceAndSupportVo 服务与支持实体类
     * @return 返回根据城市查询服务与支持
     */
    List<ServiceAndSupportVo> selectCityByServiceAndSupport(ServiceAndSupportVo serviceAndSupportVo);

    /**
     * 查询城市并去重
     * @return 返回城市并去重
     */
    List<ServiceAndSupportVo> selectByDistinctCity();

    /**
     * 查询城市数量
     * @return 返回城市数量
     */
    int selectCountByCity(ServiceAndSupportVo serviceAndSupportVo);

    /**
     * 查询所有服务与支持数量
     * @return 查询所有服务与支持数量
     */
    int selectCountByServiceAndSupport(ServiceAndSupportVo serviceAndSupportVo);

    /**
     * 根据产品ID查询服务与支持
     * @return 返回根据产品ID查询服务与支持
     */
    List<ServiceAndSupportVo> selectServiceAndSupportById(Long id);

    /**
     * 查询所有服务与支持
     * @return 返回查询所有服务与支持
     */
    List<ServiceAndSupportVo> selectAllByServiceAndSupport();

    /**
     * 模糊查询主标题和城市
     * @return 返回模糊查询主标题和城市
     */
    List<ServiceAndSupportVo> selectVagueByMainTitleAndCity(ServiceAndSupportVo serviceAndSupportVo);

    /**
     * 查询模糊查询主标题和城市数量
     * @return 返回模糊查询主标题和城市数量
     */
    int selectVagueCountByMainTitleAndCity(ServiceAndSupportVo serviceAndSupportVo);

    /**
     * 查询所有服务与支持（只给redis使用）
     * @return 返回查询所有服务与支持
     */
    List<ServiceAndSupportVo> selectAllStorageToRedis();

    /**
     * 所有服务与支持总条数 （只给redis使用）
     * @return 返回所有服务与支持总条数
     */
    int selectCountToRedis();


}
