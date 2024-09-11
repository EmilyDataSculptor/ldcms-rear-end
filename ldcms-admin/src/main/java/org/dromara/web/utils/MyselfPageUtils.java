package org.dromara.web.utils;

import lombok.extern.slf4j.Slf4j;
import org.dromara.web.domain.vo.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MyselfPageUtils {

    public static void determineToPageByProduct(ProductVo productVo) {
        if (productVo.getCurrentPage() == 0 && productVo.getPageSize() == 0) {
            productVo.setCurrentPage(1);
            productVo.setPageSize(5);
        }

        if (productVo.getCurrentPage() == 0 && productVo.getPageSize() > 0) {
            productVo.setCurrentPage(1);
        }

        if (productVo.getCurrentPage() > 0 && productVo.getPageSize() == 0) {
            productVo.setPageSize(1);
        }
    }

    public static List<ProductVo> queryToMysqlByProduct(List<ProductVo> productVos,ProductVo productVo) {
        List<ProductVo> list = PageUtil.startPage(productVos, productVo.getCurrentPage(), productVo.getPageSize());
        List<ProductVo> theList = new ArrayList<>();
        for (ProductVo o : list) {
            o.setCurrentPage(productVo.getCurrentPage());
            o.setPageSize(productVo.getPageSize());
            theList.add(o);
        }
        return theList;
    }

    public static void determineToPageBySocialResponsibility(SocialResponsibilityVo socialResponsibilityVo) {
        if (socialResponsibilityVo.getCurrentPage() == 0 && socialResponsibilityVo.getPageSize() == 0) {
            socialResponsibilityVo.setCurrentPage(1);
            socialResponsibilityVo.setPageSize(5);
        }

        if (socialResponsibilityVo.getCurrentPage() == 0 && socialResponsibilityVo.getPageSize() > 0) {
            socialResponsibilityVo.setCurrentPage(1);
        }

        if (socialResponsibilityVo.getCurrentPage() > 0 && socialResponsibilityVo.getPageSize() == 0) {
            socialResponsibilityVo.setPageSize(1);
        }
    }

    public static List<SocialResponsibilityVo> queryToMysqlBySocialResponsibility(List<SocialResponsibilityVo> socialResponsibilityVos,SocialResponsibilityVo socialResponsibilityVo) {
        List<SocialResponsibilityVo> list = PageUtil.startPage(socialResponsibilityVos, socialResponsibilityVo.getCurrentPage(), socialResponsibilityVo.getPageSize());
        List<SocialResponsibilityVo> theList = new ArrayList<>();
        for (SocialResponsibilityVo o : list) {
            o.setCurrentPage(socialResponsibilityVo.getCurrentPage());
            o.setPageSize(socialResponsibilityVo.getPageSize());
            theList.add(o);
        }
        return theList;
    }

    public static void determineToPageByServiceAndSupport(ServiceAndSupportVo serviceAndSupportVo) {
        if (serviceAndSupportVo.getCurrentPage() == 0 && serviceAndSupportVo.getPageSize() == 0) {
            serviceAndSupportVo.setCurrentPage(1);
            serviceAndSupportVo.setPageSize(5);
        }

        if (serviceAndSupportVo.getCurrentPage() == 0 && serviceAndSupportVo.getPageSize() > 0) {
            serviceAndSupportVo.setCurrentPage(1);
        }

        if (serviceAndSupportVo.getCurrentPage() > 0 && serviceAndSupportVo.getPageSize() == 0) {
            serviceAndSupportVo.setPageSize(1);
        }
    }

    public static List<ServiceAndSupportVo> queryToMysqlByServiceAndSupport(List<ServiceAndSupportVo> serviceAndSupportVos,ServiceAndSupportVo serviceAndSupportVo) {
        List<ServiceAndSupportVo> list = PageUtil.startPage(serviceAndSupportVos, serviceAndSupportVo.getCurrentPage(), serviceAndSupportVo.getPageSize());
        List<ServiceAndSupportVo> theList = new ArrayList<>();
        for (ServiceAndSupportVo o : list) {
            o.setCurrentPage(serviceAndSupportVo.getCurrentPage());
            o.setPageSize(serviceAndSupportVo.getPageSize());
            theList.add(o);
        }
        return theList;
    }

    public static void determineToPageByNewsCenter(NewsCenterVo newsCenterVo) {
        if (newsCenterVo.getCurrentPage() == 0 && newsCenterVo.getPageSize() == 0) {
            newsCenterVo.setCurrentPage(1);
            newsCenterVo.setPageSize(5);
        }

        if (newsCenterVo.getCurrentPage() == 0 && newsCenterVo.getPageSize() > 0) {
            newsCenterVo.setCurrentPage(1);
        }

        if (newsCenterVo.getCurrentPage() > 0 && newsCenterVo.getPageSize() == 0) {
            newsCenterVo.setPageSize(1);
        }
    }

    public static List<NewsCenterVo> queryToMysqlByNewsCenter(List<NewsCenterVo> newsCenterVos,NewsCenterVo newsCenterVo) {
        List<NewsCenterVo> list = PageUtil.startPage(newsCenterVos, newsCenterVo.getCurrentPage(), newsCenterVo.getPageSize());
        List<NewsCenterVo> theList = new ArrayList<>();
        for (NewsCenterVo o : list) {
            o.setCurrentPage(newsCenterVo.getCurrentPage());
            o.setPageSize(newsCenterVo.getPageSize());
            theList.add(o);
        }
        return theList;
    }

    public static void determineToPageByIndustryNews(IndustryNewsVo industryNewsVo) {
        if (industryNewsVo.getCurrentPage() == 0 && industryNewsVo.getPageSize() == 0) {
            industryNewsVo.setCurrentPage(1);
            industryNewsVo.setPageSize(5);
        }

        if (industryNewsVo.getCurrentPage() == 0 && industryNewsVo.getPageSize() > 0) {
            industryNewsVo.setCurrentPage(1);
        }

        if (industryNewsVo.getCurrentPage() > 0 && industryNewsVo.getPageSize() == 0) {
            industryNewsVo.setPageSize(1);
        }
    }

    public static List<IndustryNewsVo> queryToMysqlByIndustryNews(List<IndustryNewsVo> industryNewsVos,IndustryNewsVo industryNewsVo) {
        List<IndustryNewsVo> list = PageUtil.startPage(industryNewsVos, industryNewsVo.getCurrentPage(), industryNewsVo.getPageSize());
        List<IndustryNewsVo> theList = new ArrayList<>();
        for (IndustryNewsVo o : list) {
            o.setCurrentPage(industryNewsVo.getCurrentPage());
            o.setPageSize(industryNewsVo.getPageSize());
            theList.add(o);
        }
        return theList;
    }

    public static void determineToPageByContactUs(ContactUsVo contactUsVo) {
        if (contactUsVo.getCurrentPage() == 0 && contactUsVo.getPageSize() == 0) {
            contactUsVo.setCurrentPage(1);
            contactUsVo.setPageSize(5);
        }

        if (contactUsVo.getCurrentPage() == 0 && contactUsVo.getPageSize() > 0) {
            contactUsVo.setCurrentPage(1);
        }

        if (contactUsVo.getCurrentPage() > 0 && contactUsVo.getPageSize() == 0) {
            contactUsVo.setPageSize(1);
        }
    }

    public static List<ContactUsVo> queryToMysqlByContactUs(List<ContactUsVo> contactUsVos,ContactUsVo contactUsVo) {
        List<ContactUsVo> list = PageUtil.startPage(contactUsVos, contactUsVo.getCurrentPage(), contactUsVo.getPageSize());
        List<ContactUsVo> theList = new ArrayList<>();
        for (ContactUsVo o : list) {
            o.setCurrentPage(contactUsVo.getCurrentPage());
            o.setPageSize(contactUsVo.getPageSize());
            theList.add(o);
        }
        return theList;
    }

    public static void determineToPageByDownloadCenter(DownloadCenterVo downloadCenterVo) {
        if (downloadCenterVo.getCurrentPage() == 0 && downloadCenterVo.getPageSize() == 0) {
            downloadCenterVo.setCurrentPage(1);
            downloadCenterVo.setPageSize(5);
        }

        if (downloadCenterVo.getCurrentPage() == 0 && downloadCenterVo.getPageSize() > 0) {
            downloadCenterVo.setCurrentPage(1);
        }

        if (downloadCenterVo.getCurrentPage() > 0 && downloadCenterVo.getPageSize() == 0) {
            downloadCenterVo.setPageSize(1);
        }
    }

    public static List<DownloadCenterVo> queryToMysqlByDownloadCenter(List<DownloadCenterVo> contactUsVos,DownloadCenterVo downloadCenterVo) {
        List<DownloadCenterVo> list = PageUtil.startPage(contactUsVos, downloadCenterVo.getCurrentPage(), downloadCenterVo.getPageSize());
        List<DownloadCenterVo> theList = new ArrayList<>();
        for (DownloadCenterVo o : list) {
            o.setCurrentPage(downloadCenterVo.getCurrentPage());
            o.setPageSize(downloadCenterVo.getPageSize());
            theList.add(o);
        }
        return theList;
    }

}
