package org.dromara.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.web.constants.MainConstants;
import org.dromara.web.domain.Product;
import org.dromara.web.domain.bo.ProductBo;
import org.dromara.web.domain.vo.NewsCenterVo;
import org.dromara.web.domain.vo.PageableVo;
import org.dromara.web.domain.vo.ProductVo;
import org.dromara.web.service.ProductService;
import org.dromara.web.utils.MinioUtils;
import org.dromara.web.utils.PageUtil;
import org.dromara.web.utils.RedisUtil;
import org.springframework.data.web.PageableDefault;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 产品中心
 *
 * @author zhang
 */
@SaIgnore
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    private final RedisUtil redisUtil;

    // redis Key的前缀
    public static final String REDIS_KEY_PREFIX = "ldcms:product";

    /**
     * 产品中心添加操作
     *
     * @param productVo 联系我们实体类
     * @return 添加条数
     */
    @PostMapping("/addProduct")
    public R<Object> addProduct(@Validated @RequestBody ProductVo productVo) {
        int flag = productService.addProduct(productVo);

        if (flag == MainConstants.MAIN_TITLE_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_MAIN_TITLE_IS_NOT_NULL,MainConstants.MSG_MAIN_TITLE_IS_NOT_NULL);
        }
        if (flag == MainConstants.COLOUR_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_COLOUR_IS_NOT_NULL,MainConstants.MSG_COLOUR_IS_NOT_NULL);
        }
        if (flag == MainConstants.TYPE_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_TYPE_IS_NOT_NULL,MainConstants.MSG_TYPE_IS_NOT_NULL);
        }
        if (flag == MainConstants.SERIES_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_SERIES_IS_NOT_NULL,MainConstants.MSG_SERIES_IS_NOT_NULL);
        }
        if (flag == MainConstants.PRODUCT_DETAILS_IS_NOT_NULL) {
            return R.fail(MainConstants.CODE_PRODUCT_DETAILS_IS_NOT_NULL,MainConstants.MSG_PRODUCT_DETAILS_IS_NOT_NULL);
        }
        updateRedisByProduct();
        return R.ok(flag);
    }

    /**
     * 分页查询所有产品
     *
     * @param productVo 商品实体类
     * @return 返回分页查询所有产品
     */
    @GetMapping("/selectListByProduct")
    public R<List<ProductVo>> selectListByProduct(@PageableDefault ProductVo productVo) {
        int total = productService.selectCountToRedis();
        if (productVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (productVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<ProductVo> list = productService.selectListByProduct(productVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL + returnTotal, list);
    }

    /**
     * 查询所有产品
     *
     * @return 返回所有产品
     */
    @GetMapping("/selectAllByProduct")
    public R<List<ProductVo>> selectAllByProduct() {
        int total = productService.selectCountToRedis();
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL + returnTotal, productService.selectAllByProduct());
    }

    /**
     * 根据id物理删除产品
     *
     * @param id 产品ID
     * @return 删除条数
     */
    @GetMapping("/deleteProductById")
    public R<Integer> deleteProductById(@RequestParam("id") Long id) {
        int i = productService.deleteProductById(id);
        updateRedisByProduct();
        return R.ok(i);
    }

    /**
     * 修改商品信息
     *
     * @param productVo 商品实体类
     * @return 修改条数
     */
    @PostMapping("/updateByProduct")
    public R<Integer> updateByProduct(@Validated @RequestBody ProductVo productVo) {
        int i = productService.updateByProduct(productVo);
        updateRedisByProduct();
        return R.ok(i);
    }

    /**
     * 根据id逻辑删除产品
     *
     * @param id 产品ID
     * @return 删除条数
     */
    @PostMapping("/deleteProductByIdOnLogic")
    public R<Integer> deleteProductByIdOnLogic(@RequestParam("id") Long id) {
        int i = productService.deleteProductByIdOnLogic(id);
        updateRedisByProduct();
        return R.ok(i);
    }

    /**
     * 根据颜色查询所有商品
     *
     * @param productVo 颜色
     * @return 相关颜色所有商品
     */
    @GetMapping("/selectListByColour")
    public R<List<ProductVo>> selectListByColour(@PageableDefault ProductVo productVo) {
        int total = productService.selectCountByColour(productVo);
        if (productVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (productVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<ProductVo> list = productService.selectListByColour(productVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL + returnTotal, list);
    }

    /**
     * 根据类型查询所有商品
     *
     * @param productVo 类型
     * @return 相关类型所有商品
     */
    @GetMapping("/selectListByType")
    public R<List<ProductVo>> selectListByType(@PageableDefault ProductVo productVo) {
        int total = productService.selectCountByType(productVo);
        if (productVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (productVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }

        List<ProductVo> list = productService.selectListByType(productVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL + returnTotal, list);
    }

    /**
     * 根据颜色和类型查询所有商品
     *
     * @param productVo 颜色和类型
     * @return 相关颜色和类型所有商品
     */
    @GetMapping("/selectListByColourAndType")
    public R<List<ProductVo>> selectListByColourAndType(@PageableDefault ProductVo productVo) {
        int total = productService.selectCountToRedis();
        if (productVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (productVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<ProductVo> list = productService.selectListByColourAndType(productVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL + returnTotal, list);
    }

    /**
     * 根据产品系列查询所有商品
     *
     * @param productVo 产品系列
     * @return 相关产品系列所有商品
     */
    @GetMapping("/selectListBySeries")
    public R<List<ProductVo>> selectListBySeries(@PageableDefault ProductVo productVo) {
        int total = productService.selectCountToRedis();
        if (productVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (productVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }

        List<ProductVo> list = productService.selectListBySeries(productVo);
        String returnTotal = String.valueOf(total);
        int size = list.size();
        return R.ok(MainConstants.MSG_TOTAL + size, list);
    }

    /**
     * 根据产品ID查询商品
     *
     * @param productVo 产品系列
     * @return 返回根据产品ID查询商品
     */
    @PostMapping("/selectListById")
    public R<List<ProductVo>> selectListById(@Validated @RequestBody ProductVo productVo) {
        return R.ok(productService.selectListById(productVo));
    }

    /**
     * 查询商品颜色并去重
     *
     * @return 返回商品颜色并去重
     */
    @GetMapping("/selectByDistinctColour")
    public R<List<ProductVo>> selectByDistinctColour() {
        return R.ok(productService.selectByDistinctColour());
    }

    /**
     * 查询商品颜色数量
     *
     * @return 返回商品颜色数量
     */
    @GetMapping("/selectCountByColour")
    public R<Integer> selectCountByColour(@Validated @RequestBody ProductVo productVo) {
        return R.ok(productService.selectCountByColour(productVo));
    }

    /**
     * 查询商品类型并去重
     *
     * @return 返回商品类型并去重
     */
    @GetMapping("/selectByDistinctType")
    public R<List<ProductVo>> selectByDistinctType() {
        return R.ok(productService.selectByDistinctType());
    }

    /**
     * 查询商品类型数量
     *
     * @return 返回商品类型数量
     */
    @GetMapping("/selectCountByType")
    public R<Integer> selectCountByType(@Validated @RequestBody ProductVo productVo) {
        return R.ok(productService.selectCountByType(productVo));
    }

    /**
     * 模糊查询产品主标题
     *
     * @return 返回产品主标题相关的产品
     */
    @GetMapping("/selectVagueByMainTitle")
    public R<List<ProductVo>> selectVagueByMainTitle(@PageableDefault ProductVo productVo) {
        return R.ok(productService.selectVagueByMainTitle(productVo));
    }

    /**
     * 查询模糊查询产品主标题数量
     *
     * @return 返回模糊查询产品主标题数量
     */
    @GetMapping("/selectVagueCountByMainTitle")
    public R<Integer> selectVagueCountByMainTitle(@PageableDefault ProductVo productVo) {
        return R.ok(productService.selectVagueCountByMainTitle(productVo));
    }

    /**
     * 模糊查询产品
     *
     * @return 返回相关的产品
     */
    @GetMapping("/selectVagueByProduct")
    public R<List<ProductVo>> selectVagueByProduct(@PageableDefault ProductVo productVo) {
        int total = productService.selectVagueCountByProduct(productVo);
        if (productVo.getCurrentPage() < 0) {
            return R.fail(MainConstants.CODE_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_CURRENTPAGE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        if (productVo.getPageSize() < 0) {
            return R.fail(MainConstants.CODE_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO,MainConstants.MSG_PAGESIZE_CAN_NOT_BE_LESS_THAN_ZERO);
        }
        List<ProductVo> list = productService.selectVagueByProduct(productVo);
        String returnTotal = String.valueOf(total);
        return R.ok(MainConstants.MSG_TOTAL + returnTotal, list);
    }

    /**
     * 查询模糊查询产品数量
     *
     * @return 返回模糊查询产品数量
     */
    @GetMapping("/selectVagueCountByProduct")
    public R<Integer> selectVagueCountByProduct(@PageableDefault ProductVo productVo) {
        return R.ok(productService.selectVagueCountByProduct(productVo));
    }


    /**
     * 查询查询所有产品（只给redis使用）
     *
     * @return 返回查询所有产品
     */
    @GetMapping("/selectAllStorageToRedis")
    public R<List<ProductVo>> selectAllStorageToRedis() {
        return R.ok(productService.selectAllStorageToRedis());
    }

    /**
     * 产品总条数 （只给redis使用）
     *
     * @return 返回产品总条数
     */
    @GetMapping("/selectCountToRedis")
    public R<Integer> selectCountToRedis() {
        return R.ok(productService.selectCountToRedis());
    }

    // 每天凌晨2点执行
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateRedisByProduct() {
        boolean returnKey = redisUtil.hasKey(REDIS_KEY_PREFIX);
        if (returnKey) {
            redisUtil.deleteAllByKey(REDIS_KEY_PREFIX);
        }
        List<ProductVo> productVos = productService.selectAllStorageToRedis();
        for (ProductVo productVo : productVos) {
            redisUtil.rightPushAllByAllTypes(REDIS_KEY_PREFIX, productVo);
        }
        log.info("updateRedisByProduct方法被调用：" + REDIS_KEY_PREFIX + "更新成功！！！");
    }


    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        String resultFileUrl = MinioUtils.uploadFile(file);
        return resultFileUrl;
    }

    @PostMapping("/deleteFile")
    public String deleteFile(@RequestParam("fileName") String fileName) {
        System.out.println(fileName);
        String fileNameBySplit = fileName.split("/test/")[1];
        String resultMessage = MinioUtils.deleteFile(fileNameBySplit);
        return resultMessage;
    }
}
