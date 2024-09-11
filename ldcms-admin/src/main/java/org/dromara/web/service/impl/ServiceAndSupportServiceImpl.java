package org.dromara.web.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.web.constants.MainConstants;
import org.dromara.web.domain.ServiceAndSupport;
import org.dromara.web.domain.bo.ServiceAndSupportBo;
import org.dromara.web.domain.vo.IndustryNewsVo;
import org.dromara.web.domain.vo.ProductVo;
import org.dromara.web.domain.vo.ServiceAndSupportVo;
import org.dromara.web.domain.vo.SocialResponsibilityVo;
import org.dromara.web.mapper.ServiceAndSupportMapper;
import org.dromara.web.service.ServiceAndSupportService;
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
 * 服务与支持
 * @author zhang
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ServiceAndSupportServiceImpl implements ServiceAndSupportService {

    private final ServiceAndSupportMapper serviceAndSupportMapper;
    private final RedisUtil redisUtil;

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX="ldcms:serviceAndSupport";

    private final MyselfPageUtils myselfPageUtils;

    /**
     * 添加服务与支持
     * @param serviceAndSupportVo 服务与支持实体类
     * @return 添加条数
     */
    @Override
    public int addServiceAndSupport(ServiceAndSupportVo serviceAndSupportVo) {
        ServiceAndSupport serviceAndSupport = MapstructUtils.convert(serviceAndSupportVo, ServiceAndSupport.class);
        int flag = 0;
        if (StringUtils.isBlank(serviceAndSupportVo.getMainTitle())) {
            flag = MainConstants.MAIN_TITLE_IS_NOT_NULL;
            return flag;
        }
        if (StringUtils.isBlank(serviceAndSupportVo.getContent())) {
            flag = MainConstants.CONTENT_IS_NOT_NULL;
            return flag;
        }

        if (StringUtils.isBlank(serviceAndSupportVo.getCity())) {
            flag = MainConstants.CITY_IS_NOT_NULL;
            return flag;
        }
        if (StringUtils.isBlank(serviceAndSupportVo.getPicture())) {
            serviceAndSupport.setPicture(MainConstants.HOMEPAGE_IMAGE_ADDRESS);
        }
        serviceAndSupport.setCreationDate(new DateTime());
        serviceAndSupport.setState('1');
        flag = serviceAndSupportMapper.insert(serviceAndSupport);
        return flag;
    }

    /**
     * 逻辑删除服务与支持
     * @param serviceAndSupportVo 服务与支持实体类
     * @return 删除条数
     */
    @Override
    public int deleteServiceAndSupport(ServiceAndSupportVo serviceAndSupportVo) {
        int flag = 0;
        if (serviceAndSupportVo.getId() == null) {
            flag = MainConstants.ID_IS_NOT_NULL;
            return flag;
        }
        LambdaUpdateWrapper<ServiceAndSupport> wrapper = new UpdateWrapper<ServiceAndSupport>().lambda();
        wrapper.eq(ServiceAndSupport::getId, serviceAndSupportVo.getId())
            .set(ServiceAndSupport::getState, "0");
        flag = serviceAndSupportMapper.update(wrapper);
        return flag;
    }

    /**
     * 修改社会责任新闻
     * @param serviceAndSupportVo 服务与支持实体类
     * @return 修改条数
     */
    @Override
    public int updateServiceAndSupport(ServiceAndSupportVo serviceAndSupportVo) {

        int flag = 0;
        if (serviceAndSupportVo.getId() == null) {
            flag = MainConstants.ID_IS_NOT_NULL;
            return flag;
        }
        LambdaUpdateWrapper<ServiceAndSupport> wrapper = new UpdateWrapper<ServiceAndSupport>().lambda();
        wrapper.eq(ServiceAndSupport::getId, serviceAndSupportVo.getId());
        if (StringUtils.isNotBlank(serviceAndSupportVo.getMainTitle())) {
            wrapper.set(ServiceAndSupport::getMainTitle, serviceAndSupportVo.getMainTitle());
        }
        if (StringUtils.isNotBlank(serviceAndSupportVo.getContent())) {
            wrapper.set(ServiceAndSupport::getContent, serviceAndSupportVo.getContent());
        }
        if (StringUtils.isNotBlank(serviceAndSupportVo.getPicture())) {
            wrapper.set(ServiceAndSupport::getPicture, serviceAndSupportVo.getPicture());
        }
        if (StringUtils.isNotBlank(serviceAndSupportVo.getCity())) {
            wrapper.set(ServiceAndSupport::getCity, serviceAndSupportVo.getCity());
        }
        if (serviceAndSupportVo.getState() != '\0') {
            wrapper.set(ServiceAndSupport::getState, serviceAndSupportVo.getState());
        }
        flag = serviceAndSupportMapper.update(null, wrapper);
        return flag;
    }

    /**
     * 查询所有服务与支持
     * @param serviceAndSupportVo 服务与支持实体类
     * @return 返回所有服务与支持
     */
    @Override
    public List<ServiceAndSupportVo> selectListByServiceAndSupport(ServiceAndSupportVo serviceAndSupportVo) {
        if (serviceAndSupportVo.getCurrentPage() == 0 && serviceAndSupportVo.getPageSize() == 0) {
            serviceAndSupportVo.setCurrentPage(1);
            serviceAndSupportVo.setPageSize(5);
        }

        if (serviceAndSupportVo.getCurrentPage() == 0 && serviceAndSupportVo.getPageSize() > 0) {
            serviceAndSupportVo.setCurrentPage(1);
        }

        if (serviceAndSupportVo.getCurrentPage() > 0 && serviceAndSupportVo.getPageSize() == 0) {
            serviceAndSupportVo.setPageSize(1);
        }

        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX,  0, -1);
            List<ServiceAndSupportVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ServiceAndSupportVo) o);
            }
            List<ServiceAndSupportVo> resultList = PageUtil.startPage(list, serviceAndSupportVo.getCurrentPage(), serviceAndSupportVo.getPageSize());
            log.info("selectListByServiceAndSupport方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            return resultList;
        }

        List<ServiceAndSupportVo> productVos = serviceAndSupportMapper.selectListByServiceAndSupport(serviceAndSupportVo);
        List<ServiceAndSupportVo> list = PageUtil.startPage(productVos, serviceAndSupportVo.getCurrentPage(), serviceAndSupportVo.getPageSize());
        List<ServiceAndSupportVo> theList = new ArrayList<>();
        for (ServiceAndSupportVo o : list) {
            o.setCurrentPage(serviceAndSupportVo.getCurrentPage());
            o.setPageSize(serviceAndSupportVo.getPageSize());
            theList.add(o);
        }
        updateRedisByServiceAndSupport();
        return theList;
    }

    /**
     * 根据城市查询服务与支持
     * @param serviceAndSupportVo 服务与支持实体类
     * @return 返回根据城市查询服务与支持
     */
    @Override
    public List<ServiceAndSupportVo> selectCityByServiceAndSupport(ServiceAndSupportVo serviceAndSupportVo) {
        if (serviceAndSupportVo.getCurrentPage() == 0 && serviceAndSupportVo.getPageSize() == 0) {
            serviceAndSupportVo.setCurrentPage(1);
            serviceAndSupportVo.setPageSize(5);
        }

        if (serviceAndSupportVo.getCurrentPage() == 0 && serviceAndSupportVo.getPageSize() > 0) {
            serviceAndSupportVo.setCurrentPage(1);
        }

        if (serviceAndSupportVo.getCurrentPage() > 0 && serviceAndSupportVo.getPageSize() == 0) {
            serviceAndSupportVo.setPageSize(1);
        }

        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<ServiceAndSupportVo> colourList = new ArrayList<>();
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<ServiceAndSupportVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ServiceAndSupportVo) o);
            }
            for (ServiceAndSupportVo vo : list) {
                if (vo.getCity().equals(serviceAndSupportVo.getCity())) {
                    colourList.add(vo);
                }
            }
            log.info("selectCityByServiceAndSupport方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            List<ServiceAndSupportVo> resultList = PageUtil.startPage(colourList, serviceAndSupportVo.getCurrentPage(), serviceAndSupportVo.getPageSize());
            return resultList;
        }

        List<ServiceAndSupportVo> productVos = serviceAndSupportMapper.selectCityByServiceAndSupport(serviceAndSupportVo);
        List<ServiceAndSupportVo> list = PageUtil.startPage(productVos, serviceAndSupportVo.getCurrentPage(), serviceAndSupportVo.getPageSize());
        List<ServiceAndSupportVo> theList = new ArrayList<>();
        for (ServiceAndSupportVo o : list) {
            o.setCurrentPage(serviceAndSupportVo.getCurrentPage());
            o.setPageSize(serviceAndSupportVo.getPageSize());
            theList.add(o);
        }
        updateRedisByServiceAndSupport();
        return theList;
    }

    /**
     * 查询城市并去重
     * @return 返回城市并去重
     */
    @Override
    public List<ServiceAndSupportVo> selectByDistinctCity() {
        return serviceAndSupportMapper.selectByDistinctCity();
    }

    /**
     * 查询城市数量
     * @return 返回城市数量
     */
    @Override
    public int selectCountByCity(ServiceAndSupportVo serviceAndSupportVo) {
        return serviceAndSupportMapper.selectCountByCity(serviceAndSupportVo);
    }

    /**
     * 查询所有服务与支持数量
     * @return 查询所有服务与支持数量
     */
    @Override
    public int selectCountByServiceAndSupport(ServiceAndSupportVo serviceAndSupportVo) {
        return serviceAndSupportMapper.selectCountByServiceAndSupport(serviceAndSupportVo);
    }

    /**
     * 根据产品ID查询服务与支持
     * @return 返回根据产品ID查询服务与支持
     */
    @Override
    public List<ServiceAndSupportVo> selectServiceAndSupportById(Long id) {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            ServiceAndSupportVo found = null;
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<ServiceAndSupportVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ServiceAndSupportVo) o);
            }
            for (ServiceAndSupportVo vo : list) {
                if (vo.getId().equals(id)) {
                    found = vo;
                    break;
                }
            }
            List<ServiceAndSupportVo> resultList = new ArrayList<>();
            resultList.add(found);
            return resultList;
        }
        updateRedisByServiceAndSupport();
        return serviceAndSupportMapper.selectServiceAndSupportById(id);
    }

    /**
     * 查询所有服务与支持
     * @return 返回查询所有服务与支持
     */
    @Override
    public List<ServiceAndSupportVo> selectAllByServiceAndSupport() {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<ServiceAndSupportVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ServiceAndSupportVo) o);
            }
            log.info("selectAllByServiceAndSupport方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            return list;
        }

        updateRedisByServiceAndSupport();
        return serviceAndSupportMapper.selectAllByServiceAndSupport();
    }

    /**
     * 模糊查询主标题和城市
     * @return 返回模糊查询主标题和城市
     */
    @Override
    public List<ServiceAndSupportVo> selectVagueByMainTitleAndCity(ServiceAndSupportVo serviceAndSupportVo) {
        myselfPageUtils.determineToPageByServiceAndSupport(serviceAndSupportVo);
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)) {
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<ServiceAndSupportVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ServiceAndSupportVo) o);
            }
            Predicate<ServiceAndSupportVo> filterByMainTitle = p -> p.getMainTitle().toLowerCase().contains(serviceAndSupportVo.getMainTitle().toLowerCase());
            List<ServiceAndSupportVo> filteredProductsByMainTitle = list.stream()
                .filter(filterByMainTitle)
                .collect(Collectors.toList());

            Predicate<ServiceAndSupportVo> filterByCity = p -> p.getCity().toLowerCase().contains(serviceAndSupportVo.getCity().toLowerCase());
            List<ServiceAndSupportVo> filteredProductsByCity = filteredProductsByMainTitle.stream()
                .filter(filterByCity)
                .collect(Collectors.toList());

            log.info("selectVagueByProduct方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            List<ServiceAndSupportVo> resultList = PageUtil.startPage(filteredProductsByCity, serviceAndSupportVo.getCurrentPage(), serviceAndSupportVo.getPageSize());
            return resultList;
        }
        List<ServiceAndSupportVo> socialResponsibilityVos = serviceAndSupportMapper.selectVagueByMainTitleAndCity(serviceAndSupportVo);
        List<ServiceAndSupportVo> resultList = myselfPageUtils.queryToMysqlByServiceAndSupport(socialResponsibilityVos, serviceAndSupportVo);
        updateRedisByServiceAndSupport();
        return resultList;
    }

    /**
     * 查询模糊查询主标题和城市数量
     * @return 返回模糊查询主标题和城市数量
     */
    @Override
    public int selectVagueCountByMainTitleAndCity(ServiceAndSupportVo serviceAndSupportVo) {
        return serviceAndSupportMapper.selectVagueCountByMainTitleAndCity(serviceAndSupportVo);
    }

    /**
     * 查询所有服务与支持（只给redis使用）
     * @return 返回查询所有服务与支持
     */
    @Override
    public List<ServiceAndSupportVo> selectAllStorageToRedis() {
        return serviceAndSupportMapper.selectAllStorageToRedis();
    }

    /**
     * 所有服务与支持总条数 （只给redis使用）
     * @return 返回所有服务与支持总条数
     */
    @Override
    public int selectCountToRedis() {
        return serviceAndSupportMapper.selectCountToRedis();
    }

    public void updateRedisByServiceAndSupport() {
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX);
        }
        List<ServiceAndSupportVo> serviceAndSupportVos = serviceAndSupportMapper.selectAllStorageToRedis();
        for (ServiceAndSupportVo serviceAndSupportVo : serviceAndSupportVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX,serviceAndSupportVo);
        }
        log.info("updateRedisByServiceAndSupport方法被调用：" + REDIS_KEY_PREFIX + "更新成功！！！");
    }
}
