package org.dromara.web.service;

import org.dromara.web.domain.ContactUs;
import org.dromara.web.domain.vo.ContactUsVo;
import org.dromara.web.domain.vo.IndustryNewsVo;

import java.util.List;

/**
 * 联系我们
 * @author zhang
 */
public interface ContactUsService {

    /**
     * 联系我们添加操作
     * @param contactUsVo 联系我们实体类
     * @return 添加条数
     */
    int addContactUs(ContactUsVo contactUsVo);

    /**
     * 答复客户消息
     * @param contactUsVo 联系我们信息实体类
     * @return 答复条数
     */
    int replyContent(ContactUsVo contactUsVo);

    /**
     * 逻辑删除联系我们信息
     * @param contactUsVo 联系我们信息实体类
     * @return 删除条数
     */
    int deleteContactUs(ContactUsVo contactUsVo);

    /**
     * 查询所有联系我们信息
     * @return 返回所有联系我们信息
     */
    List<ContactUsVo> selectListByContactUs(ContactUsVo contactUsVo);

    /**
     * 根据ID查询未处理联系
     * @param id 联系我们ID
     * @return 返回根据ID查询未处理联系
     */
    List<ContactUsVo> selectListById(Long id);

    /**
     * 未处理联系条数
     * @return 返回查未处理联系
     */
    int selectCountByContactUs();

    /**
     * 模糊查询名字和公司
     * @return 返回模糊名字和公司
     */
    List<ContactUsVo> selectVagueByMainTitleAndCompany(ContactUsVo contactUsVo);

    /**
     * 查询模糊查询名字和公司数量
     * @return 返回模糊查询名字和公司数量
     */
    int selectVagueCountByMainTitleAndCompany(ContactUsVo contactUsVo);

    /**
     * 查询所有已提交联系我们信息
     * @return 返回所有已提交联系我们信息
     */
    List<ContactUsVo> selectListOnSubmittedByContactUs(ContactUsVo contactUsVo);

    /**
     * 根据ID查询已提交联系
     * @param id 已提交联系我们ID
     * @return 返回根据ID查询已提交联系
     */
    List<ContactUsVo> selectListByIdOnSubmitted(Long id);

    /**
     * 驳回已提交信息
     * @param contactUsVo 联系我们实体类
     * @return 驳回条数
     */
    int rejectContactUsOnSubmitted(ContactUsVo contactUsVo);

    /**
     * 已提交联系我们总条数
     * @return 返回已提交联系我们总条数
     */
    int selectCountOnSubmitted();

    /**
     * 模糊查询已提交名字和公司
     * @return 返回已提交模糊查询名字和公司
     */
    List<ContactUsVo> selectVagueByMainTitleAndCompanyOnSubmitted(ContactUsVo contactUsVo);

    /**
     * 查询模糊查询已提交名字和公司数量
     * @return 返回已提交模糊查询名字和公司数量
     */
    int selectVagueCountByMainTitleAndCompanyOnSubmitted(ContactUsVo contactUsVo);

    /**
     * 查询所有已提交联系我们信息（只给redis使用）
     * @return 返回所有已提交联系我们信息
     */
    List<ContactUsVo> selectAllStorageToRedisOnSubmitted();

    /**
     * 查询所有联系我们信息（只给redis使用）
     * @return 返回所有联系我们信息
     */
    List<ContactUsVo> selectAllStorageToRedis();

    /**
     * 联系我们总条数 （只给redis使用）
     * @return 返回联系我们总条数
     */
    int selectCountToRedis();

}
