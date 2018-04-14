package com.yhfin.risk.analy.calculate.impl;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.yhfin.risk.analy.calculate.IConsiseCalculateService;
import com.yhfin.risk.common.consts.Const;
import com.yhfin.risk.common.pojos.calculate.EntryConciseCalculateInfo;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.common.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
import com.netflix.hystrix.HystrixCollapser.Scope;
@Service
public class ConsiseCalculateServiceImpl implements IConsiseCalculateService {

    @Autowired
    private RestTemplate restTemplate;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 发送计算请求
     *
     * @param conciseCalculateInfo
     * @return
     */
    @Override
    @HystrixCollapser(batchMethod = "sendConsiseCalculates",scope = Scope.GLOBAL,collapserProperties = {
            @HystrixProperty(name = "timerDelayInMilliseconds", value = "100"),
            @HystrixProperty(name = "maxRequestsInBatch", value = "200")
    })
    public ServerResponse<String> sendConsiseCalculate(EntryConciseCalculateInfo conciseCalculateInfo) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "发起单个基金，请求唯一标识{},计算请求", conciseCalculateInfo.getSerialNumber(), conciseCalculateInfo.getRequestId(), conciseCalculateInfo.getResultKey());
        }
        return restTemplate.postForObject("http://RISK-CALCULATE/yhfin/calculate/consiseCalculate", conciseCalculateInfo, ServerResponse.class);
    }

    /**
     * 合并发送计算请求
     *
     * @param conciseCalculateInfos
     * @return
     */
    @Override
    @HystrixCommand(fallbackMethod = "sendConsiseCalculatesFallBack")
    public List<ServerResponse<String>> sendConsiseCalculates(List<EntryConciseCalculateInfo> conciseCalculateInfos) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "发起单个基金，计算请求,共{}条", conciseCalculateInfos.get(0).getSerialNumber(), conciseCalculateInfos.get(0).getRequestId(), conciseCalculateInfos.size());
        }
        return restTemplate.postForObject("http://RISK-CALCULATE/yhfin/calculate/consiseCalculates", conciseCalculateInfos, List.class);
    }


    public List<ServerResponse<String>> sendConsiseCalculatesFallBack(List<EntryConciseCalculateInfo> conciseCalculateInfos, Throwable e) {
        if (logger.isErrorEnabled()) {
            logger.error(StringUtil.commonLogStart() + "发起单个基金，计算请求,计算请求发生错误", conciseCalculateInfos.get(0).getSerialNumber(), conciseCalculateInfos.get(0).getRequestId());
            logger.error("" + e, e);
        }
        return conciseCalculateInfos.parallelStream().map((item) -> ServerResponse.createByError(item.getRequestId(), item.getSerialNumber(), Const.exceptionErrorCode.ANALY_ERROR_CODE, e.getMessage(), "")).collect(Collectors.toList());
    }

}
