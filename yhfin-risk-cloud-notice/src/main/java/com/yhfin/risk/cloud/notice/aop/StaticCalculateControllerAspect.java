/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/16:03
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: StaticCalculateControllerAspect.java
 * 文件描述: @Description 静态风控请求切面
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.aop;

import com.yhfin.risk.core.common.execeptions.YhRiskNoticeException;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticCalculateDTO;
import com.yhfin.risk.core.common.utils.StringUtil;
import com.yhfin.risk.core.dao.IRiskDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 静态风控请求切面
 * 包名称：com.yhfin.risk.cloud.notice.aop
 * 类名称：StaticCalculateControllerAspect
 * 类描述：静态风控请求切面
 * 创建人：@author caohui
 * 创建时间：2018/5/13/16:03
 */
@Aspect
@Component
@Slf4j
public class StaticCalculateControllerAspect {

    @Autowired
    private IRiskDao riskDao;

    @Before("execution(* com.yhfin.risk.cloud.notice.controller.feign.StaticCalculateController.staticCalculate(..))")
    public void staticCalculateAspect(JoinPoint joinPoint) {
        StaticCalculateDTO calculateRequest = (StaticCalculateDTO) joinPoint.getArgs()[0];
        if (calculateRequest == null) {
            if (log.isErrorEnabled()) {
                log.error("请求静态风控计算参数错误，参数为空");
            }
            throw new YhRiskNoticeException("请求静态风控计算参数错误，参数为空");
        }

        if (StringUtils.isBlank(calculateRequest.getRequestId()) || StringUtils.isBlank(calculateRequest.getSerialNumber())) {
            if (log.isErrorEnabled()) {
                log.error("请求静态风控计算参数错误，请求序号为空或者流水号为空");
            }
            throw new YhRiskNoticeException("请求静态风控计算参数错误，请求序号为空或者流水号为空");
        }
        final List<String> allFundIds = getAllFundIds();
        if (calculateRequest.isCalculateAll()) {
            calculateRequest.setFundIds(allFundIds);
        } else {
            List<String> fundIds = calculateRequest.getFundIds();
            if (fundIds == null || fundIds.isEmpty()) {
                if (log.isErrorEnabled()) {
                    log.error(StringUtil.commonLogStart(calculateRequest.getSerialNumber(), calculateRequest.getRequestId()) + "请求静态计算发生错误,没有计算的基金序号信息");
                }
                throw new YhRiskNoticeException("请求静态计算发生错误,没有计算的基金序号信息");
            }
            List<String> filterFundIds = fundIds.stream().filter((fundId) -> allFundIds.contains(fundId)).collect(Collectors.toList());
            calculateRequest.setFundIds(filterFundIds);
        }
        if (calculateRequest.getFundIds().isEmpty()) {
            if (log.isErrorEnabled()) {
                log.error(StringUtil.commonLogStart(calculateRequest.getSerialNumber(), calculateRequest.getRequestId()) + "请求静态计算发生错误,经过数据库过滤,没有有效计算的基金序号信息");
            }
            throw new YhRiskNoticeException("请求静态计算发生错误,经过数据库过滤,没有有效计算的基金序号信息");
        }
    }

    /**
     * 获取所有的基金序号
     * @Title getAllFundIds
     * @Description: 获取所有的基金序号
     * @return 所有的基金序号集合
     * @author: caohui
     * @Date:  2018/5/13/16:16
     */
    private List<String> getAllFundIds() {

        return riskDao.resultSingleList("SELECT I_FUND FROM FUNDINFOS", "I_FUND");
    }
}
