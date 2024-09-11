package org.dromara.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.web.constants.MainConstants;
import org.dromara.web.domain.Product;
import org.dromara.web.domain.bo.ProductBo;
import org.dromara.web.domain.vo.DownloadCenterVo;
import org.dromara.web.domain.vo.NewsCenterVo;
import org.dromara.web.domain.vo.PageableVo;
import org.dromara.web.domain.vo.ProductVo;
import org.dromara.web.mapper.ProductMapper;
import org.dromara.web.service.ProductService;
import org.dromara.web.utils.MinioUtils;

import org.dromara.web.utils.MyselfPageUtils;
import org.dromara.web.utils.PageUtil;
import org.dromara.web.utils.RedisUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 产品中心
 * @author zhang
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    private final RedisUtil redisUtil;

    private final MyselfPageUtils myselfPageUtils;

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX="ldcms:product";

    /**
     * 产品中心添加操作
     * @param productVo 产品中心实体类
     * @return 添加条数
     */
    @Override
    public int addProduct(ProductVo productVo) {
        Product product = MapstructUtils.convert(productVo, Product.class);
        int flag = 0;

        if (StringUtils.isBlank(productVo.getMainTitle())) {
            return MainConstants.MAIN_TITLE_IS_NOT_NULL;
        }
        if (StringUtils.isBlank(productVo.getColour())) {
            return MainConstants.COLOUR_IS_NOT_NULL;
        }
        if (StringUtils.isBlank(productVo.getType())) {
            return MainConstants.TYPE_IS_NOT_NULL;
        }

        if (StringUtils.isBlank(productVo.getSeries())) {
            return MainConstants.SERIES_IS_NOT_NULL;
        }
        if (StringUtils.isBlank(productVo.getProductDetails())) {
            return MainConstants.PRODUCT_DETAILS_IS_NOT_NULL;
        }

        if (StringUtils.isBlank(productVo.getIndexPicture())) {
            product.setIndexPicture(MainConstants.INDEX_HOMEPAGE_IMAGE_ADDRESS);
        }

        if (StringUtils.isBlank(productVo.getPicture())) {
            product.setPicture(MainConstants.HOMEPAGE_IMAGE_ADDRESS);
        }
        product.setState('1');
        flag = productMapper.insert(product);
        return flag;
    }

    /**
     * 分页查询所有产品
     * @param productVo 商品实体类
     * @return 返回分页查询所有产品
     */
    @Override
    public List<ProductVo> selectListByProduct(ProductVo productVo) {
        if (productVo.getCurrentPage() == 0 && productVo.getPageSize() == 0) {
            productVo.setCurrentPage(1);
            productVo.setPageSize(5);
        }

        if (productVo.getCurrentPage() == 0 && productVo.getPageSize() > 0) {
            productVo.setCurrentPage(1);
        }

        if (productVo.getCurrentPage() > 0 && productVo.getPageSize() == 0) {
            productVo.setPageSize(1);
        }

        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX,  0, -1);
            List<ProductVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ProductVo) o);
            }
            List<ProductVo> resultList = PageUtil.startPage(list, productVo.getCurrentPage(), productVo.getPageSize());
            log.info("selectListByProduct方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            return resultList;
        }

        List<ProductVo> productVos = productMapper.selectByProduct(productVo);
        List<ProductVo> list = PageUtil.startPage(productVos, productVo.getCurrentPage(), productVo.getPageSize());
        List<ProductVo> theList = new ArrayList<>();
        for (ProductVo o : list) {
            o.setCurrentPage(productVo.getCurrentPage());
            o.setPageSize(productVo.getPageSize());
            theList.add(o);
        }
        updateRedisByProduct();
        return theList;
    }

    /**
     * 查询产品条数
     * @return 返回产品条数
     */
    @Override
    public int selectCountByProduct() {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            return (int) redisUtil.getLengthByList(REDIS_KEY_PREFIX);
        }
        updateRedisByProduct();
        return productMapper.selectCountByProduct();
    }

    /**
     * 查询所有产品
     * @return 返回所有产品
     */
    @Override
    public List<ProductVo> selectAllByProduct() {
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<ProductVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ProductVo) o);
            }
            log.info("selectAllByProduct方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            return list;
        }

        updateRedisByProduct();
        return productMapper.selectAllByProduct();
    }

    /**
     * 根据id物理删除产品
     * @param id 产品ID
     * @return 删除条数
     */
    @Override
    public int deleteProductById(Long id) {
        return productMapper.deleteById(id);
    }

    /**
     * 修改商品信息
     * @param productVo 商品实体类
     * @return 修改条数
     */
    @Override
    public int updateByProduct(ProductVo productVo) {

        LambdaUpdateWrapper<Product> wrapper = new UpdateWrapper<Product>().lambda();
        wrapper.eq(Product::getId,productVo.getId());

        if (StringUtils.isNotBlank(productVo.getMainTitle())){
            wrapper.set(Product::getMainTitle,productVo.getMainTitle());
        }
        if (StringUtils.isNotBlank(productVo.getSubTitle())){
            wrapper.set(Product::getSubTitle,productVo.getSubTitle());
        }
        if (StringUtils.isNotBlank(productVo.getContent())){
            wrapper.set(Product::getContent,productVo.getContent());
        }
        if (StringUtils.isNotBlank(productVo.getIndexPicture())){
            wrapper.set(Product::getIndexPicture,productVo.getIndexPicture());
        }
        if (StringUtils.isNotBlank(productVo.getProductDetails())){
            wrapper.set(Product::getProductDetails,productVo.getProductDetails());
        }
        if (StringUtils.isNotBlank(productVo.getPicture())){
            wrapper.set(Product::getPicture,productVo.getPicture());
        }
        if (StringUtils.isNotBlank(productVo.getColour())){
            wrapper.set(Product::getColour,productVo.getColour());
        }
        if (StringUtils.isNotBlank(productVo.getType())){
            wrapper.set(Product::getType,productVo.getType());
        }
        if (StringUtils.isNotBlank(productVo.getSeries())){
            wrapper.set(Product::getSeries,productVo.getSeries());
        }
        if (productVo.getState()!='\0'){
            wrapper.set(Product::getState,productVo.getState());
        }
        return productMapper.update(null, wrapper);
    }

    /**
     * 根据id逻辑删除产品
     * @param id 产品ID
     * @return 删除条数
     */
    @Override
    public int deleteProductByIdOnLogic(Long id) {
        LambdaUpdateWrapper<Product> wrapper = new UpdateWrapper<Product>().lambda();
        wrapper.eq(Product::getId,id)
            .set(Product::getState,"0");
        return productMapper.update(null,wrapper);
    }

    /**
     * 根据颜色查询所有商品
     * @param productVo 颜色
     * @return 相关颜色所有商品
     */
    @Override
    public List<ProductVo> selectListByColour(ProductVo productVo) {
        if (productVo.getCurrentPage() == 0 && productVo.getPageSize() == 0) {
            productVo.setCurrentPage(1);
            productVo.setPageSize(5);
        }

        if (productVo.getCurrentPage() == 0 && productVo.getPageSize() > 0) {
            productVo.setCurrentPage(1);
        }

        if (productVo.getCurrentPage() > 0 && productVo.getPageSize() == 0) {
            productVo.setPageSize(1);
        }

        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<ProductVo> colourList = new ArrayList<>();
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<ProductVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ProductVo) o);
            }
            for (ProductVo vo : list) {
                if (vo.getColour().equals(productVo.getColour())) {
                    colourList.add(vo);
                }
            }
            log.info("selectListByColour方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            List<ProductVo> resultList = PageUtil.startPage(colourList, productVo.getCurrentPage(), productVo.getPageSize());
            return resultList;
        }

        List<ProductVo> productVos = productMapper.selectListByColour(productVo);
        List<ProductVo> list = PageUtil.startPage(productVos, productVo.getCurrentPage(), productVo.getPageSize());
        List<ProductVo> theList = new ArrayList<>();
        for (ProductVo o : list) {
            o.setCurrentPage(productVo.getCurrentPage());
            o.setPageSize(productVo.getPageSize());
            theList.add(o);
        }
        updateRedisByProduct();
        return theList;
    }

    /**
     * 根据类型查询所有商品
     * @param productVo 类型
     * @return 相关类型所有商品
     */
    @Override
    public List<ProductVo> selectListByType(ProductVo productVo) {
        if (productVo.getCurrentPage() == 0 && productVo.getPageSize() == 0) {
            productVo.setCurrentPage(1);
            productVo.setPageSize(5);
        }

        if (productVo.getCurrentPage() == 0 && productVo.getPageSize() > 0) {
            productVo.setCurrentPage(1);
        }

        if (productVo.getCurrentPage() > 0 && productVo.getPageSize() == 0) {
            productVo.setPageSize(1);
        }

        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<ProductVo> colourList = new ArrayList<>();
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<ProductVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ProductVo) o);
            }
            for (ProductVo vo : list) {
                if (vo.getType().equals(productVo.getType())) {
                    colourList.add(vo);
                }
            }
            log.info("selectListByColour方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            List<ProductVo> resultList = PageUtil.startPage(colourList, productVo.getCurrentPage(), productVo.getPageSize());
            return resultList;
        }

        List<ProductVo> productVos = productMapper.selectListByType(productVo);
        List<ProductVo> list = PageUtil.startPage(productVos, productVo.getCurrentPage(), productVo.getPageSize());
        List<ProductVo> theList = new ArrayList<>();
        for (ProductVo o : list) {
            o.setCurrentPage(productVo.getCurrentPage());
            o.setPageSize(productVo.getPageSize());
            theList.add(o);
        }
        updateRedisByProduct();
        return theList;
    }

    /**
     * 根据颜色和类型查询所有商品
     * @param productVo 颜色和类型
     * @return 相关颜色和类型所有商品
     */
    @Override
    public List<ProductVo> selectListByColourAndType(ProductVo productVo) {
        if (productVo.getCurrentPage() == 0 && productVo.getPageSize() == 0) {
            productVo.setCurrentPage(1);
            productVo.setPageSize(5);
        }

        if (productVo.getCurrentPage() == 0 && productVo.getPageSize() > 0) {
            productVo.setCurrentPage(1);
        }

        if (productVo.getCurrentPage() > 0 && productVo.getPageSize() == 0) {
            productVo.setPageSize(1);
        }

        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<ProductVo> colourList = new ArrayList<>();
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<ProductVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ProductVo) o);
            }
            for (ProductVo vo : list) {
                if (vo.getColour().equals(productVo.getColour())&&vo.getType().equals(productVo.getType())) {
                    colourList.add(vo);
                }
            }
            log.info("selectListByColour方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            List<ProductVo> resultList = PageUtil.startPage(colourList, productVo.getCurrentPage(), productVo.getPageSize());
            return resultList;
        }

        List<ProductVo> productVos = productMapper.selectListByColourAndType(productVo);
        List<ProductVo> list = PageUtil.startPage(productVos, productVo.getCurrentPage(), productVo.getPageSize());
        List<ProductVo> theList = new ArrayList<>();
        for (ProductVo o : list) {
            o.setCurrentPage(productVo.getCurrentPage());
            o.setPageSize(productVo.getPageSize());
            theList.add(o);
        }
        updateRedisByProduct();
        return theList;
    }

    /**
     * 根据产品系列查询所有商品
     * @param productVo 产品系列
     * @return 相关产品系列所有商品
     */
    @Override
    public List<ProductVo> selectListBySeries(ProductVo productVo) {
        if (productVo.getCurrentPage() == 0 && productVo.getPageSize() == 0) {
            productVo.setCurrentPage(1);
            productVo.setPageSize(5);
        }

        if (productVo.getCurrentPage() == 0 && productVo.getPageSize() > 0) {
            productVo.setCurrentPage(1);
        }

        if (productVo.getCurrentPage() > 0 && productVo.getPageSize() == 0) {
            productVo.setPageSize(1);
        }

        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            List<ProductVo> colourList = new ArrayList<>();
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<ProductVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ProductVo) o);
            }
            for (ProductVo vo : list) {
                if (vo.getSeries().equals(productVo.getSeries())) {
                    colourList.add(vo);
                }
            }
            log.info("selectListByColour方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            List<ProductVo> resultList = PageUtil.startPage(colourList, productVo.getCurrentPage(), productVo.getPageSize());
            return resultList;
        }

        List<ProductVo> productVos = productMapper.selectListBySeries(productVo);
        List<ProductVo> list = PageUtil.startPage(productVos, productVo.getCurrentPage(), productVo.getPageSize());
        List<ProductVo> theList = new ArrayList<>();
        for (ProductVo o : list) {
            o.setCurrentPage(productVo.getCurrentPage());
            o.setPageSize(productVo.getPageSize());
            theList.add(o);
        }
        updateRedisByProduct();
        return theList;
    }

    /**
     * 根据产品ID查询所有商品
     * @param productVo 产品系列
     * @return 返回根据产品ID查询所有商品
     */
    @Override
    public List<ProductVo> selectListById(ProductVo productVo){
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)){
            ProductVo found = null;
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<ProductVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ProductVo) o);
            }
            for (ProductVo vo : list) {
                if (vo.getId().equals(productVo.getId())) {
                    found = vo;
                    break;
                }
            }
            List<ProductVo> resultList = new ArrayList<>();
            resultList.add(found);
            return resultList;
        }
        updateRedisByProduct();
        return productMapper.selectListById(productVo);
    }


    /**
     * 查询商品颜色并去重
     * @return 返回商品颜色并去重
     */
    @Override
    public List<ProductVo> selectByDistinctColour() {
        return productMapper.selectByDistinctColour();
    }

    /**
     * 查询商品颜色数量
     * @return 返回商品颜色数量
     */
    @Override
    public int selectCountByColour(ProductVo productVo) {
        return productMapper.selectCountByColour(productVo);
    }

    /**
     * 查询商品类型并去重
     * @return 返回商品类型并去重
     */
    @Override
    public List<ProductVo> selectByDistinctType() {
        return productMapper.selectByDistinctType();
    }

    /**
     * 查询商品类型数量
     * @return 返回商品类型数量
     */
    @Override
    public int selectCountByType(ProductVo productVo) {
        return productMapper.selectCountByType(productVo);
    }

    /**
     * 模糊查询产品主标题
     * @return 返回产品主标题相关的产品
     */
    @Override
    public List<ProductVo> selectVagueByMainTitle(ProductVo productVo) {
        return productMapper.selectVagueByMainTitle(productVo);
    }

    /**
     * 查询模糊查询产品主标题数量
     * @return 返回模糊查询产品主标题数量
     */
    @Override
    public int selectVagueCountByMainTitle(ProductVo productVo) {
        return productMapper.selectVagueCountByMainTitle(productVo);
    }

    /**
     * 模糊查询产品
     * @return 返回相关的产品
     */
    @Override
    public List<ProductVo> selectVagueByProduct(ProductVo productVo) {
        myselfPageUtils.determineToPageByProduct(productVo);
        if (redisUtil.hasKey(REDIS_KEY_PREFIX)) {
            List<Object> range = redisUtil.range(REDIS_KEY_PREFIX, 0, -1);
            List<ProductVo> list = new ArrayList<>();
            for (Object o : range) {
                list.add((ProductVo) o);
            }
            Predicate<ProductVo> filterByMainTitle = p -> p.getMainTitle().toLowerCase().contains(productVo.getMainTitle().toLowerCase());
            List<ProductVo> filteredProductsByMainTitle = list.stream()
                .filter(filterByMainTitle)
                .collect(Collectors.toList());

            Predicate<ProductVo> filterByColour = p -> p.getColour().toLowerCase().contains(productVo.getColour().toLowerCase());
            List<ProductVo> filteredProductsByColour = filteredProductsByMainTitle.stream()
                .filter(filterByColour)
                .collect(Collectors.toList());

            Predicate<ProductVo> filterByType = p -> p.getType().toLowerCase().contains(productVo.getType().toLowerCase());
            List<ProductVo> filteredProductsByType = filteredProductsByColour.stream()
                .filter(filterByType)
                .collect(Collectors.toList());

            Predicate<ProductVo> filterBySeries = p -> p.getSeries().toLowerCase().contains(productVo.getSeries().toLowerCase());
            List<ProductVo> filteredProductsBySeries = filteredProductsByType.stream()
                .filter(filterBySeries)
                .collect(Collectors.toList());

            log.info("selectVagueByProduct方法被调用：" + REDIS_KEY_PREFIX + "已存在，直接获取redis缓存！！！");
            List<ProductVo> resultList = PageUtil.startPage(filteredProductsBySeries, productVo.getCurrentPage(), productVo.getPageSize());
            return resultList;
        }

        List<ProductVo> productVos = productMapper.selectVagueByProduct(productVo);
        List<ProductVo> resultList = myselfPageUtils.queryToMysqlByProduct(productVos, productVo);
        updateRedisByProduct();
        return resultList;
    }

    /**
     * 查询模糊查询产品数量
     * @return 返回模糊查询产品数量
     */
    @Override
    public int selectVagueCountByProduct(ProductVo productVo) {
        return productMapper.selectVagueCountByProduct(productVo);
    }

    /**
     * 查询查询所有产品（只给redis使用）
     * @return 返回查询所有产品
     */
    @Override
    public List<ProductVo> selectAllStorageToRedis() {
        return productMapper.selectAllStorageToRedis();
    }

    /**
     * 产品总条数 （只给redis使用）
     * @return 返回产品总条数
     */
    @Override
    public int selectCountToRedis() {
        return productMapper.selectCountToRedis();
    }

    public void updateRedisByProduct() {
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX);
        if (returnKey){
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX);
        }
        List<ProductVo> productVos = productMapper.selectAllStorageToRedis();
        for (ProductVo productVo : productVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX,productVo);
        }
        log.info("updateRedisByProduct方法被调用：" + REDIS_KEY_PREFIX + "更新成功！！！");
    }
}
