package org.dromara.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 行业新闻表 industry_news
 */

@Data
@TableName("industry_news")
public class IndustryNews implements Serializable {

    /**
     * 行业新闻ID
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 主标题
     */
    private String mainTitle;

    /**
     * 创建日期
     */
    private Date creationDate;

    /**
     * 内容
     */
    private String content;

    /**
     * 图片
     */
    private String picture;

    /**
     * 状态
     */
    private char state;




}
