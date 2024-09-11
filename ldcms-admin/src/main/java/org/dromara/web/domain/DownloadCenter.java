package org.dromara.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 下载中心表 download_center
 */

@Data
@TableName("download_center")
public class DownloadCenter implements Serializable {

    /**
     * 下载中心ID
     */
    @TableId(value = "id",type = IdType.AUTO)
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


}
