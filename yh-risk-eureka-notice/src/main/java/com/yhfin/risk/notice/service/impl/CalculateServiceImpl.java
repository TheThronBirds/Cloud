package com.yhfin.risk.notice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yhfin.risk.common.consts.Const;
import com.yhfin.risk.common.pojos.notice.StaticSingleFundCalculateResult;
import com.yhfin.risk.common.requests.calculate.StaticSingleFundCalculateRequest;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.common.utils.StringUtil;
import com.yhfin.risk.notice.service.ICalculateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
/**
 * 发送基金计算服务接口{@link ICalculateService}实现
 * @author youlangta
 * @see ICalculateService
 * @since 2018-04-10
 */
public class CalculateServiceImpl implements ICalculateService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 单个基金静态计算请求
     *
     * @param calculateRequest
     * @return
     */
    @Override
    @HystrixCommand(fallbackMethod = "sendSingleStaticCalculateFallback")
    public ServerResponse<StaticSingleFundCalculateResult> sendSingleStaticCalculate(StaticSingleFundCalculateRequest calculateRequest) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "发起单个基金，基金序号{},分析计算请求", calculateRequest.getSerialNumber(), calculateRequest.getRequestId(), calculateRequest.getFundId());
        }
        String result = restTemplate.postForObject("http://RISK-ANALY/yhfin/analy/staticSingleCalculate", calculateRequest, String.class);
        return StringUtil.parseJsonObject(result, StaticSingleFundCalculateResult.class);
    }


    public ServerResponse<StaticSingleFundCalculateResult> sendSingleStaticCalculateFallback(StaticSingleFundCalculateRequest calculateRequest, Throwable e) {
        String message = "流水号:" + calculateRequest.getSerialNumber() + ",请求号:" + calculateRequest.getRequestId() + ",发送基金:" + calculateRequest.getFundId() + ",静态分析计算失败,";
        if (logger.isErrorEnabled()) {
            logger.error(message + e, e);
        }
        StaticSingleFundCalculateResult singleFundCalculateResult = new StaticSingleFundCalculateResult(calculateRequest.getRequestId(), calculateRequest.getSerialNumber(), calculateRequest.getFundId());
        return ServerResponse.createByError(calculateRequest.getRequestId(), calculateRequest.getSerialNumber(), Const.exceptionErrorCode.NOTICE_ERROR_CODE, message + e.getMessage(), singleFundCalculateResult);
    }


}
