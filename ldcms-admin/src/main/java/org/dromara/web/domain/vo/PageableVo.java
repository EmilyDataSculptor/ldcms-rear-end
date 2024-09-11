package org.dromara.web.domain.vo;

import lombok.Data;

@Data
public class PageableVo {

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
