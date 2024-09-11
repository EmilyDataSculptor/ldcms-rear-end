package org.dromara.web.mapper;

import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.web.domain.DownloadCenter;
import org.dromara.web.domain.vo.DownloadCenterVo;
import org.dromara.web.domain.vo.IndustryNewsVo;
import org.dromara.web.domain.vo.NewsCenterVo;
import org.dromara.web.domain.vo.SocialResponsibilityVo;

import java.util.List;

/**
 * 下载中心
 * @author zhang
 */
public interface DownloadCenterMapper extends BaseMapperPlus<DownloadCenter, DownloadCenterVo> {

    /**
     * 查询所有下载中心内容
     * @param downloadCenterVo 下载中心实体类
     * @return 返回所有下载中心内容
     */
    List<DownloadCenterVo> selectListByDownloadCenter(DownloadCenterVo downloadCenterVo);

    /**
     * 根据类型查询下载中心内容
     * @param downloadCenterVo 下载中心实体类
     * @return 返回根据类型查询下载中心内容
     */
    List<DownloadCenterVo> selectTypeByDownloadCenter(DownloadCenterVo downloadCenterVo);

    /**
     * 查询类型并去重
     * @return 返回类型并去重
     */
    List<DownloadCenterVo> selectByDistinctType();

    /**
     * 查询所有下载中心内容
     * @return 返回所有下载中心内容
     */
    List<DownloadCenterVo> selectLAllByDownloadCenter();

    /**
     * 根据ID查询下载中心内容
     * @param id 下载中心ID
     * @return 返回根据ID查询下载中心内容
     */
    List<DownloadCenterVo> selectDownloadFileById(Long id);

    /**
     * 查询下载中心总条数
     * @return 返回下载中心总条数
     */
    int selectCountByDownloadCenter();

    /**
     * 模糊查询主标题和类型
     * @return 返回模糊查询主标题和类型
     */
    List<DownloadCenterVo> selectVagueByMainTitleAndType(DownloadCenterVo downloadCenterVo);

    /**
     * 查询模糊查询主标题和类型数量
     * @return 返回模糊查询主标题和类型数量
     */
    int selectVagueCountByMainTitleAndType(DownloadCenterVo downloadCenterVo);

    /**
     * 查询所有下载中心内容（只给redis使用）
     * @return 返回所有下载中心内容
     */
    List<DownloadCenterVo> selectAllStorageToRedis();

    /**
     * 下载中心总条数 （只给redis使用）
     * @return 返回下载中心总条数
     */
    int selectCountToRedis();
}
