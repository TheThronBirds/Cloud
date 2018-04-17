package com.yhfin.risk.analy.aop;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.analy.message.IMessageService;
import com.yhfin.risk.common.consts.Const;
import com.yhfin.risk.common.requests.message.AnalyMessageSynchronizate;
import com.yhfin.risk.common.responses.result.FundAnalyResult;
import com.yhfin.risk.common.utils.StringUtil;
import com.yhfin.risk.core.analy.optimize.IFundAnalyResultService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Aspect
@Component
/**
 * 基金条目分析
 * @author youlangta
 * @since 2018-04-17
 */
public class FundAnalyResultAop {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IFundAnalyResultService fundAnalyResultService;

    @Autowired
    private IMessageService messageService;

    @After("execution(* com.yhfin.risk.core.analy.optimize.IFundAnalyResultService.updateAnalyState(..))")
    public void fundAnalyResultAop(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String fundId = (String) args[0];
        String analyState = (String) args[1];
        if (StringUtils.equals(analyState, Const.analyState.ANALY_ERROR) || StringUtils.equals(analyState, Const.analyState.ANALY_FINISH)) {
            FundAnalyResult fundAnalyResult = fundAnalyResultService.getFundAnalyResult(fundId);
            AnalyMessageSynchronizate messageSynchronizate = new AnalyMessageSynchronizate();
            messageSynchronizate.setRequestId(fundAnalyResult.getRequestId());
            messageSynchronizate.setSerialNumber(fundAnalyResult.getSerialNumber());
            messageSynchronizate.setFundAnalyResult(fundAnalyResult);
            if(logger.isInfoEnabled()){
                logger.info(StringUtil.commonLogStart()+"基金分析完毕，开始发送分析基金结果信息,{}",messageSynchronizate.getSerialNumber(),messageSynchronizate.getRequestId(), JSON.toJSONString(messageSynchronizate));
            }
            messageService.analyMessageSynchronizate(messageSynchronizate);
        }
    }


}

