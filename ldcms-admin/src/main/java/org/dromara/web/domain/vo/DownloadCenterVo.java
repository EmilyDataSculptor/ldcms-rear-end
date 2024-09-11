package org.dromara.web.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.web.domain.ContactUs;
import org.dromara.web.domain.DownloadCenter;

/**
 * 下载中心表 download_center
 */

@Data
@TableName("download_center")
@AutoMapper(target = DownloadCenter.class)
public class DownloadCenterVo {

    /**
     * 下载中心ID
     */
    private Long id;

    /**
     * 标题
     */
    private String mainTitle;

    /**
     * 下载文件
     */
    private String downloadFile;

    /**
     * 类型
     */
    private String type;

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
