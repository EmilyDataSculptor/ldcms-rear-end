package org.dromara.web.service;

import org.dromara.web.domain.Product;
import org.dromara.web.domain.bo.ProductBo;
import org.dromara.web.domain.vo.DownloadCenterVo;
import org.dromara.web.domain.vo.PageableVo;
import org.dromara.web.domain.vo.ProductVo;

import java.util.List;

/**
 * 产品中心
 * @author zhang
 */
public interface ProductService {

    /**
     * 产品中心添加操作
     * @param productVo 联系我们实体类
     * @return 添加条数
     */
    int addProduct(ProductVo productVo);

    /**
     * 分页查询所有产品
     * @param productVo 商品实体类
     * @return 返回分页查询所有产品
     */
    List<ProductVo> selectListByProduct(ProductVo productVo);

    /**
     * 查询产品条数
     * @return 返回产品条数
     */
    int selectCountByProduct();

    /**
     * 查询所有产品
     * @return 返回所有产品
     */
    List<ProductVo> selectAllByProduct();

    /**
     * 根据id物理删除产品
     * @param id 产品ID
     * @return 删除条数
     */
    int deleteProductById(Long id);

    /**
     * 修改商品信息
     * @param productVo 商品实体类
     * @return 修改条数
     */
    int updateByProduct(ProductVo productVo);

    /**
     * 根据id逻辑删除产品
     * @param id 产品ID
     * @return 删除条数
     */
    int deleteProductByIdOnLogic(Long id);

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
