package org.dromara.web.domain.bo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.web.domain.Product;

/**
 * 产品中心表 product
 */

@Data
@TableName("product")
@AutoMapper(target = Product.class)
public class ProductBo {

    /**
     * 当前页的页码
     */
    private int currentPage;

    /**
     * 每页显示的条数
     */
    private int pageSize;

    /**
     * 每页显示的条数
     */
    private int total;

    /**
     * 颜色
     */
    private String colour;

    /**
     * 类型
     */
    private String type;

    /**
     * 系列
     */
    private String series;

}
