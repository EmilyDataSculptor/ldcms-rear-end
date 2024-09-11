package org.dromara.web.domain.vo;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.web.domain.ContactUs;

import java.util.Date;

/**
 * 联系我们表 contact_us
 */

@Data
@TableName("contact_us")
@AutoMapper(target = ContactUs.class)
public class ContactUsVo {

    /**
     * 联系我们ID
     */
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

    /**
     * 当前页的页码
     */
    private int currentPage;

    /**
     * 每页显示的条数
     */
    private int pageSize;
}
