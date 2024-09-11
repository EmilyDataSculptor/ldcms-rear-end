package org.dromara.web.domain.vo;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.web.domain.IndustryNews;
import org.dromara.web.domain.NewsCenter;

import java.util.Date;

/**
 * 新闻中心表 news_center
 */

@Data
@TableName("news_center")
@AutoMapper(target = NewsCenter.class)
public class NewsCenterVo {

    /**
     * 新闻中心ID
     */
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

    /**
     * 当前页的页码
     */
    private int currentPage;

    /**
     * 每页显示的条数
     */
    private int pageSize;





}
