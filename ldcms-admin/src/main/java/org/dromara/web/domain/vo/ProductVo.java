package org.dromara.web.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.web.domain.NewsCenter;
import org.dromara.web.domain.Product;

/**
 * 产品中心表 product
 */

@Data
@TableName("product")
@AutoMapper(target = Product.class)
public class ProductVo {

    /**
     * 产品中心ID
     */
    private Long id;

    /**
     * 主标题
     */
    private String mainTitle;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * 产品介绍
     */
    private String content;

    /**
     * 首页展示图片
     */
    private String indexPicture;

    /**
     * 产品详情
     */
    private String productDetails;

    /**
     * 图片
     */
    private String picture;

    /**
     * 颜色
     */
    private String colour;

    /**
     * 类型
     */
    private String type;

    /**
     * 状态
     */
    private char state;

    /**
     * 系列
     */
    private String series;

    /**
     * 当前页的页码
     */
    private int currentPage;

    /**
     * 每页显示的条数
     */
    private int pageSize;




}
