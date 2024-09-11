package org.dromara.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 产品中心表 product
 */

@Data
@TableName("product")
public class Product implements Serializable {

    /**
     * 产品中心ID
     */
    @TableId(value = "id",type = IdType.AUTO)
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
     * 系列
     */
    private String series;

    /**
     * 状态
     */
    private char state;


}
