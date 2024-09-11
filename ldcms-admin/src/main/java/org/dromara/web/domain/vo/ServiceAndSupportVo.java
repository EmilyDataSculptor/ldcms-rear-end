package org.dromara.web.domain.vo;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.web.domain.Product;
import org.dromara.web.domain.ServiceAndSupport;

import java.util.Date;

/**
 * 服务与支持表 service_and_support
 */

@Data
@TableName("service_and_support")
@AutoMapper(target = ServiceAndSupport.class)
public class ServiceAndSupportVo {

    /**
     * 服务与支持ID
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
     * 城市
     */
    private String city;

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

    /**
     * 每页显示的条数
     */
    private int total;




}
