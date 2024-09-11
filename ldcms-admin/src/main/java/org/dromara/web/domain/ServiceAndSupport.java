package org.dromara.web.domain;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 服务与支持表 service_and_support
 */

@Data
@TableName("service_and_support")
public class ServiceAndSupport implements Serializable {

    /**
     * 服务与支持ID
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
     * 城市
     */
    private String city;

    /**
     * 状态
     */
    private char state;



}
