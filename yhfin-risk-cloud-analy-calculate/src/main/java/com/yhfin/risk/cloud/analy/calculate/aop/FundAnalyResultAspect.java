/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/26/13:18
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: FundAnalyResultAspect.java
 * 文件描述: @Description 基金分析状态更新发送消息
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.analy.calculate.aop;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.cloud.analy.calculate.service.feign.ISendMessageService;
import com.yhfin.risk.core.analy.manage.IEntryStaticAnalyManageService;
import com.yhfin.risk.core.common.pojos.dtos.analy.SingleFundAnalyResultDTO;
import com.yhfin.risk.core.common.types.AnalyStateEnum;
import com.yhfin.risk.core.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基金分析状态更新发送消息
 * 包名称：com.yhfin.risk.cloud.analy.calculate.aop
 * 类名称：FundAnalyResultAspect
 * 类描述：基金分析状态更新发送消息
 * 创建人：@author caohui
 * 创建时间：2018/5/26/13:18
 */
@Aspect
@Component
@Slf4j
public class FundAnalyResultAspect {

    @Autowired
    private IEntryStaticAnalyManageService entryStaticAnalyManageService;

    @Autowired
    private ISendMessageService messageService;

    /**
     *
     * 对基金分析状态更新进行切面,当基金分析状态为成功或者失败,则发送消息给通知中心
     *
     *
     * @Title fundAnalyResultAop
     * @Description: 对基金分析状态更新进行切面,当基金分析状态为成功或者失败,则发送消息给通知中心
     * @param joinPoint
     *            切面参数对象
     * @author: caohui
     * @Date: 2018年5月14日/下午3:55:49
     */
    @After("execution(* com.yhfin.risk.core.analy.manage.IEntryStaticAnalyManageService.updateStaticEntryFundState(..))")
    public void fundAnalyResultAop(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String fundId = (String) args[0];
        AnalyStateEnum analyState = (AnalyStateEnum) args[3];
        if (analyState == AnalyStateEnum.FINISH || analyState == AnalyStateEnum.ERROR) {
            String requestId = (String) args[2];
            String serialNumber = (String) args[1];
            Map<String, SingleFundAnalyResultDTO> singleFundAnalyResultDTOs = entryStaticAnalyManageService
                    .getfundAnalyResults();
            SingleFundAnalyResultDTO fundAnalyResult = singleFundAnalyResultDTOs.get(fundId);
            if (fundAnalyResult != null) {
                if (log.isInfoEnabled()) {
                    log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",单个基金{}分析完毕,{}", fundId,
                            JSON.toJSONString(fundAnalyResult));
                    log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",发送单个基金{}分析结果到通知服务器", fundId);
                }
                messageService.analyMessage(fundAnalyResult);
                return;
            }

            if (fundAnalyResult == null && analyState == AnalyStateEnum.ERROR) {
                if (log.isInfoEnabled()) {
                    log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",单个基金{}分析完毕,{}", fundId,
                            JSON.toJSONString(fundAnalyResult));
                    log.info(StringUtil.commonLogStart(serialNumber, requestId) + ",发送单个基金{}分析结果到通知服务器", fundId);
                }
                SingleFundAnalyResultDTO fundAnalyResultError = new SingleFundAnalyResultDTO(requestId, serialNumber);
                fundAnalyResultError.setAnalyState(AnalyStateEnum.ERROR);
                fundAnalyResultError.setSuccessCalculateAnaly(new AtomicInteger(0));
                fundAnalyResultError.setErrorCalculateAnaly(new AtomicInteger(0));
                fundAnalyResultError.setFundId(fundId);
                messageService.analyMessage(fundAnalyResultError);
                return;
            }

        }
    }
}
