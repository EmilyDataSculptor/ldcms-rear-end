package org.dromara.web.domain.bo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.web.domain.SocialResponsibility;

/**
 * 社会责任表 social_responsibility
 */

@Data
@TableName("social_responsibility")
@AutoMapper(target = SocialResponsibility.class)
public class SocialResponsibilityBo {

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
