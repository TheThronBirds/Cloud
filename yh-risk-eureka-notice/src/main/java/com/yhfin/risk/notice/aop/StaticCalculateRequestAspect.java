package com.yhfin.risk.notice.aop;

import com.yhfin.risk.common.consts.Const;
import com.yhfin.risk.common.exceptions.YhRiskNoticeException;
import com.yhfin.risk.common.pojos.sql.SqlProperties;
import com.yhfin.risk.common.requests.calculate.StaticCalculateRequest;
import com.yhfin.risk.common.utils.StringUtil;
import com.yhfin.risk.core.dao.IRiskDao;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 静态风控aop
 *
 * @author youlangta
 * @since 2018-04-10
 */
@Aspect
@Component
public class StaticCalculateRequestAspect {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IRiskDao riskDao;

    @Autowired
    private SqlProperties sqlProperties;

    @Before("execution(* com.yhfin.risk.notice.controller.calculate.StaticCalculateController.staticCalculate(..))")
    public void staticCalculateAspect(JoinPoint joinPoint) {
        StaticCalculateRequest calculateRequest = (StaticCalculateRequest) joinPoint.getArgs()[0];
        if (calculateRequest == null) {
            if (logger.isErrorEnabled()) {
                logger.error("请求静态风控计算参数错误，参数为空");
            }
            throw new YhRiskNoticeException("请求静态风控计算参数错误，参数为空");
        }

        if (StringUtils.isBlank(calculateRequest.getRequestId()) || StringUtils.isBlank(calculateRequest.getSerialNumber())) {
            if (logger.isErrorEnabled()) {
                logger.error("请求静态风控计算参数错误，请求序号为空或者流水号为空");
            }
            throw new YhRiskNoticeException("请求静态风控计算参数错误，请求序号为空或者流水号为空");
        }
        final List<String> allFundIds = getAllFundIds();
        if (calculateRequest.isCalculateAll()) {
            calculateRequest.setFundIds(allFundIds);
        } else {
            List<String> fundIds = calculateRequest.getFundIds();
            if (fundIds == null || fundIds.isEmpty()) {
                if (logger.isErrorEnabled()) {
                    logger.error(StringUtil.commonLogStart() + "请求静态计算发生错误,没有计算的基金序号信息", calculateRequest.getSerialNumber(), calculateRequest.getRequestId());
                }
                throw new YhRiskNoticeException("请求静态计算发生错误,没有计算的基金序号信息");
            }
            List<String> filterFundIds = fundIds.stream().filter((fundId) -> allFundIds.contains(fundId)).collect(Collectors.toList());
            calculateRequest.setFundIds(filterFundIds);
        }
        if (calculateRequest.getFundIds().isEmpty()) {
            if (logger.isErrorEnabled()) {
                logger.error(StringUtil.commonLogStart() + "请求静态计算发生错误,经过数据库过滤,没有有效计算的基金序号信息", calculateRequest.getSerialNumber(), calculateRequest.getRequestId());
            }
            throw new YhRiskNoticeException("请求静态计算发生错误,经过数据库过滤,没有有效计算的基金序号信息");
        }
    }


    private List<String> getAllFundIds() {
        return riskDao.resulteSingleList(sqlProperties.getValue("riskFundAllQuery"), "I_FUND");
    }
}
