package org.dromara.web.domain;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 联系我们表 contact_us
 */

@Data
@TableName("contact_us")
public class ContactUs implements Serializable {


    /**
     * 联系我们ID
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 公司
     */
    private String company;

    /**
     * 留言内容
     */
    private String content;

    /**
     * 留言日期
     */
    private Date creationDate;

    /**
     * 答复内容
     */
    private String reply;

    /**
     * 答复日期
     */
    private Date replyDate;

    /**
     * 驳回理由
     */
    private String rejectReason;

    /**
     * 状态
     */
    private char state;
}
