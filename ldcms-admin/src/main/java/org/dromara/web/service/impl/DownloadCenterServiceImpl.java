package org.dromara.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.web.constants.MainConstants;
import org.dromara.web.domain.DownloadCenter;
import org.dromara.web.domain.IndustryNews;
import org.dromara.web.domain.Product;
import org.dromara.web.domain.vo.DownloadCenterVo;
import org.dromara.web.domain.vo.IndustryNewsVo;
import org.dromara.web.domain.vo.SocialResponsibilityVo;
import org.dromara.web.mapper.DownloadCenterMapper;
import org.dromara.web.service.DownloadCenterService;
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
 * 下载中心
 * @author zhang
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class DownloadCenterServiceImpl implements DownloadCenterService {

    private final DownloadCenterMapper downloadCenterMapper;

    private final RedisUtil redisUtil;

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX="ldcms:downloadCenter";
    private final MyselfPageUtils myselfPageUtils;
    int flag = 0;
    /**
     * 下载中心添加操作
     * @param downloadCenterVo 下载中心实体类
     * @return 新增条数
     */
    @Override
    public int addDownloadCenter(DownloadCenterVo downloadCenterVo) {
        DownloadCenter downloadCenter = MapstructUtils.convert(downloadCenterVo, DownloadCenter.class);
        if (StringUtils.isBlank(downloadCenterVo.getMainTitle())) {
            flag = MainConstants.MAIN_TITLE_IS_NOT_NULL;
            return flag;
        }
        if (StringUtils.isBlank(downloadCenterVo.getType())) {
            flag = MainConstants.TYPE_IS_NOT_NULL;
            return flag;
        }
        if (StringUtils.isBlank(downloadCenterVo.getDownloadFile())) {
            downloadCenter.setDownloadFile(MainConstants.HOMEFILE_IMAGE_ADDRESS);
        }
        downloadCenter.setState('1');
        flag = downloadCenterMapper.insert(downloadCenter);
        return flag;
    }

    /**
     * 修改下载中心内容
     * @param downloadCenterVo 下载中心实体类
     * @return 修改条数
     */
    @Override
    public int updateDownloadCenter(DownloadCenterVo downloadCenterVo) {
        if (downloadCenterVo.getId() == null) {
            flag = MainConstants.ID_IS_NOT_NULL;
            return flag;
        }
        LambdaUpdateWrapper<DownloadCenter> wrapper = new UpdateWrapper<DownloadCenter>().lambda();
        wrapper.eq(DownloadCenter::getId, downloadCenterVo.getId());
        if (StringUtils.isNotBlank(downloadCenterVo.getMainTitle())) {
            wrapper.set(DownloadCenter::getMainTitle, downloadCenterVo.getMainTitle());
        }
        if (StringUtils.isNotBlank(downloadCenterVo.getDownloadFile())) {
            wrapper.set(DownloadCenter::getDownloadFile, downloadCenterVo.getDownloadFile());
        }
        if (StringUtils.isNotBlank(downloadCenterVo.getType())) {
            wrapper.set(DownloadCenter::getType, downloadCenterVo.getType());
        }
        flag = downloadCenterMapper.update(null, wrapper);
        return flag;
    }

    /**
     * 查询所有下载中心内容
     * @param downloadCenterVo 下载中心实体类
     * @return 返回所有下载中心内容
     */
    @Override
    public List<DownloadCenterVo> selectListByDownloadCenter(DownloadCenterVo downloadCenterVo) {

        if (downloadCenterVo.getCurrentPage() == 0 && downloadCenterVo.getPageSize() == 0) {
            downloadCenterVo.setCurrentPage(1);
            downloadCenterVo.setPageSize(5);
        }

        if (downloadCenterVo.getCurrentPage() == 0 && downloadCenterVo.getPageSize() > 0) {
            downloadCenterVo.setCurrentPage(1);
        }

        if (downloadCenterVo.getCurrentPage() > 0 && downloadCenterVo.getPageSize() == 0) {
            downloadCenterVo.setPageSize(1);
        }

        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX,  0, -1);
            List<DownloadCenterVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((DownloadCenterVo) o);
            }
            List<DownloadCenterVo> resultList = PageUtil.startPage(list, downloadCenterVo.getCurrentPage(), downloadCenterVo.getPageSize());
            log.info("selectListByDownloadCenter方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            return resultList;
        }

        List<DownloadCenterVo> downloadCenterVos = downloadCenterMapper.selectListByDownloadCenter(downloadCenterVo);
        List<DownloadCenterVo> list = PageUtil.startPage(downloadCenterVos, downloadCenterVo.getCurrentPage(), downloadCenterVo.getPageSize());
        List<DownloadCenterVo> theList = new ArrayList<>();
        for (DownloadCenterVo o : list) {
            o.setCurrentPage(downloadCenterVo.getCurrentPage());
            o.setPageSize(downloadCenterVo.getPageSize());
            theList.add(o);
        }
        updateRedisByDownloadCenter();
        return theList;

    }

    /**
     * 根据类型查询下载中心内容
     * @param downloadCenterVo 下载中心实体类
     * @return 返回根据类型查询下载中心内容
     */
    @Override
    public List<DownloadCenterVo> selectTypeByDownloadCenter(DownloadCenterVo downloadCenterVo) {
        if (StringUtils.isBlank(downloadCenterVo.getType())) {
            return null;
        }

        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<DownloadCenterVo> resultList = new ArrayList<>();
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<DownloadCenterVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((DownloadCenterVo) o);
            }
            for (DownloadCenterVo vo : list) {
                if (vo.getType().equals(downloadCenterVo.getType())) {
                    resultList.add(vo);
                }
            }

            return resultList;
        }
        updateRedisByDownloadCenter();
        return downloadCenterMapper.selectTypeByDownloadCenter(downloadCenterVo);
    }

    /**
     * 逻辑删除下载中心内容
     * @param downloadCenterVo 下载中心实体类
     * @return 删除条数
     */
    @Override
    public int deleteDownloadCenter(DownloadCenterVo downloadCenterVo) {
        if (downloadCenterVo.getId() == null) {
            flag = MainConstants.ID_IS_NOT_NULL;
            return flag;
        }
        LambdaUpdateWrapper<DownloadCenter> wrapper = new UpdateWrapper<DownloadCenter>().lambda();
        wrapper.eq(DownloadCenter::getId, downloadCenterVo.getId())
            .set(DownloadCenter::getState, "0");
        flag = downloadCenterMapper.update(wrapper);
        return flag;
    }

    /**
     * 查询类型并去重
     * @return 返回类型并去重
     */
    @Override
    public List<DownloadCenterVo> selectByDistinctType() {
        return downloadCenterMapper.selectByDistinctType();
    }

    /**
     * 查询所有下载中心内容
     * @return 返回所有下载中心内容
     */
    @Override
    public List<DownloadCenterVo> selectLAllByDownloadCenter() {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<DownloadCenterVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((DownloadCenterVo) o);
            }
            log.info("selectLAllByDownloadCenter方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            return list;
        }

        updateRedisByDownloadCenter();
        return downloadCenterMapper.selectLAllByDownloadCenter();
    }

    /**
     * 根据ID查询下载中心内容
     * @param id 下载中心ID
     * @return 返回根据ID查询下载中心内容
     */
    @Override
    public List<DownloadCenterVo> selectDownloadFileById(Long id) {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            DownloadCenterVo found = null;
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<DownloadCenterVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((DownloadCenterVo) o);
            }
            for (DownloadCenterVo vo : list) {
                if (vo.getId().equals(id)) {
                    found = vo;
                    break;
                }
            }
            List<DownloadCenterVo> resultList = new ArrayList<>();
            resultList.add(found);
            return resultList;
        }
        updateRedisByDownloadCenter();
        return downloadCenterMapper.selectDownloadFileById(id);
    }

    /**
     * 查询下载中心总条数
     * @return 返回下载中心总条数
     */
    @Override
    public int selectCountByDownloadCenter() {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            return (int) redisUtil.getLengthByList(REDIS_KEY_PREFIX);
        }
        updateRedisByDownloadCenter();
        return downloadCenterMapper.selectCountByDownloadCenter();
    }

    /**
     * 模糊查询主标题和类型
     * @return 返回模糊查询主标题和类型
     */
    @Override
    public List<DownloadCenterVo> selectVagueByMainTitleAndType(DownloadCenterVo downloadCenterVo) {
        myselfPageUtils.determineToPageByDownloadCenter(downloadCenterVo);
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)) {
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<DownloadCenterVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((DownloadCenterVo) o);
            }
            Predicate<DownloadCenterVo> filterByMainTitle = p -> p.getMainTitle().toLowerCase().contains(downloadCenterVo.getMainTitle().toLowerCase());
            List<DownloadCenterVo> filteredByMainTitle = list.stream()
                .filter(filterByMainTitle)
                .collect(Collectors.toList());
            Predicate<DownloadCenterVo> filterByType = p -> p.getType().toLowerCase().contains(downloadCenterVo.getType().toLowerCase());
            List<DownloadCenterVo> filteredByType = filteredByMainTitle.stream()
                .filter(filterByType)
                .collect(Collectors.toList());
            log.info("selectVagueByProduct方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            List<DownloadCenterVo> resultList = PageUtil.startPage(filteredByType, downloadCenterVo.getCurrentPage(), downloadCenterVo.getPageSize());
            return resultList;
        }
        List<DownloadCenterVo> newsCenterVos = downloadCenterMapper.selectVagueByMainTitleAndType(downloadCenterVo);
        List<DownloadCenterVo> resultList = myselfPageUtils.queryToMysqlByDownloadCenter(newsCenterVos, downloadCenterVo);
        updateRedisByDownloadCenter();
        return resultList;
    }

    /**
     * 查询模糊查询主标题和类型数量
     * @return 返回模糊查询主标题和类型数量
     */
    @Override
    public int selectVagueCountByMainTitleAndType(DownloadCenterVo downloadCenterVo) {
        return downloadCenterMapper.selectVagueCountByMainTitleAndType(downloadCenterVo);
    }

    /**
     * 查询所有下载中心内容（只给redis使用）
     * @return 返回所有下载中心内容
     */
    @Override
    public List<DownloadCenterVo> selectAllStorageToRedis() {
        return downloadCenterMapper.selectAllStorageToRedis();
    }

    /**
     * 下载中心总条数 （只给redis使用）
     * @return 返回下载中心总条数
     */
    @Override
    public int selectCountToRedis() {
        return downloadCenterMapper.selectCountToRedis();
    }

    public void updateRedisByDownloadCenter() {
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX);
        }
        List<DownloadCenterVo> downloadCenterVos = downloadCenterMapper.selectAllStorageToRedis();
        for (DownloadCenterVo downloadCenterVo : downloadCenterVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX,downloadCenterVo);
        }
        log.info("updateRedisByDownloadCenter方法被调用：" + REDIS_KEY_PREFIX + "更新成功！！！");
    }


}
