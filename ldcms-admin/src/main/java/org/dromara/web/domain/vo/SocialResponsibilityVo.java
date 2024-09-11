package org.dromara.web.domain.vo;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.web.domain.ServiceAndSupport;
import org.dromara.web.domain.SocialResponsibility;

import java.io.Serializable;
import java.util.Date;

/**
 * 社会责任表 social_responsibility
 */

@Data
@TableName("social_responsibility")
@AutoMapper(target = SocialResponsibility.class)
public class SocialResponsibilityVo implements Serializable {

    /**
     * 社会责任ID
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
