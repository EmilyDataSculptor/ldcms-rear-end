package org.dromara.web.service;

import org.dromara.web.domain.DownloadCenter;
import org.dromara.web.domain.vo.*;

import java.util.List;

/**
 * 下载中心
 * @author zhang
 */
public interface DownloadCenterService {

    /**
     * 下载中心添加操作
     * @param downloadCenterVo 下载中心实体类
     * @return 新增条数
     */
    int addDownloadCenter(DownloadCenterVo downloadCenterVo);

    /**
     * 修改下载中心内容
     * @param downloadCenterVo 下载中心实体类
     * @return 修改条数
     */
    int updateDownloadCenter(DownloadCenterVo downloadCenterVo);

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
     * 逻辑删除下载中心内容
     * @param downloadCenterVo 下载中心实体类
     * @return 删除条数
     */
    int deleteDownloadCenter(DownloadCenterVo downloadCenterVo);

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
