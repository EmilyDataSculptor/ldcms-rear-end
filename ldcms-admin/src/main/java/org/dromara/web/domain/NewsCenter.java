package org.dromara.web.domain;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 新闻中心表 news_center
 */

@Data
@TableName("news_center")
public class NewsCenter implements Serializable {

    /**
     * 新闻中心ID
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
