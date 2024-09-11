package org.dromara.web.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.web.domain.ContactUs;
import org.dromara.web.domain.Product;
import org.dromara.web.domain.bo.ProductBo;
import org.dromara.web.domain.vo.ContactUsVo;
import org.dromara.web.domain.vo.ProductVo;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 产品中心
 * @author zhang
 */
public interface ProductMapper extends BaseMapperPlus<Product, ProductVo> {

    /**
     * 查询所有产品
     * @return 返回所有产品
     */
    List<ProductVo> selectByProduct(ProductVo productVo);

    /**
     * 查询所有产品
     * @return 返回所有产品
     */
    List<ProductVo> selectAllByProduct();

    /**
     * 查询产品条数
     * @return 返回产品条数
     */
    int selectCountByProduct();

    /**
     * 根据颜色查询所有商品
     * @param productVo 颜色
     * @return 相关颜色所有商品
     */
    List<ProductVo> selectListByColour(ProductVo productVo);

    /**
     * 根据类型查询所有商品
     * @param productVo 类型
     * @return 相关类型所有商品
     */
    List<ProductVo> selectListByType(ProductVo productVo);

    /**
     * 根据颜色和类型查询所有商品
     * @param productVo 颜色和类型
     * @return 相关颜色和类型所有商品
     */
    List<ProductVo> selectListByColourAndType(ProductVo productVo);

    /**
     * 根据产品系列查询所有商品
     * @param productVo 产品系列
     * @return 相关产品系列所有商品
     */
    List<ProductVo> selectListBySeries(ProductVo productVo);

    /**
     * 根据产品ID查询所有商品
     * @param productVo 产品系列
     * @return 返回根据产品ID查询所有商品
     */
    List<ProductVo> selectListById(ProductVo productVo);

    /**
     * 查询商品颜色并去重
     * @return 返回商品颜色并去重
     */
    List<ProductVo> selectByDistinctColour();

    /**
     * 查询商品颜色数量
     * @return 返回商品颜色数量
     */
    int selectCountByColour(ProductVo productVo);

    /**
     * 查询商品类型并去重
     * @return 返回商品类型并去重
     */
    List<ProductVo> selectByDistinctType();

    /**
     * 查询商品类型数量
     * @return 返回商品类型数量
     */
    int selectCountByType(ProductVo productVo);

    /**
     * 模糊查询产品主标题
     * @return 返回产品主标题相关的产品
     */
    List<ProductVo> selectVagueByMainTitle(ProductVo productVo);

    /**
     * 查询模糊查询产品主标题数量
     * @return 返回模糊查询产品主标题数量
     */
    int selectVagueCountByMainTitle(ProductVo productVo);

    /**
     * 模糊查询产品
     * @return 返回相关的产品
     */
    List<ProductVo> selectVagueByProduct(ProductVo productVo);

    /**
     * 查询模糊查询产品数量
     * @return 返回模糊查询产品数量
     */
    int selectVagueCountByProduct(ProductVo productVo);


    /**
     * 查询查询所有产品（只给redis使用）
     * @return 返回查询所有产品
     */
    List<ProductVo> selectAllStorageToRedis();

    /**
     * 产品总条数 （只给redis使用）
     * @return 返回产品总条数
     */
    int selectCountToRedis();





}
