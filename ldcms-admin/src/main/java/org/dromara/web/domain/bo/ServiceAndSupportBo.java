package org.dromara.web.domain.bo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.web.domain.ServiceAndSupport;

import java.util.Date;

/**
 * 服务与支持表 service_and_support
 */

@Data
@TableName("service_and_support")
@AutoMapper(target = ServiceAndSupport.class)
public class ServiceAndSupportBo {

    /**
     * 城市
     */
    private String city;

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
