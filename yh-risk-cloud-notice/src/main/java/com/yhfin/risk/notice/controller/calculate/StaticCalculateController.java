package com.yhfin.risk.notice.controller.calculate;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yhfin.risk.common.consts.Const;
import com.yhfin.risk.common.pojos.notice.StaticCalculateResult;
import com.yhfin.risk.common.requests.calculate.StaticCalculateRequest;
import com.yhfin.risk.common.requests.calculate.StaticSingleFundCalculateRequest;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.common.utils.StringUtil;
import com.yhfin.risk.notice.service.ICalculateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 静态计算请求
 *
 * @author youlangta
 * @since 2018-04-10
 */
@RestController
@RequestMapping("/yhfin/notice")
public class StaticCalculateController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ICalculateService calculateService;


    @RequestMapping(value = "/staticCalculate", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "staticCalculateFallBack")
    public ServerResponse<StaticCalculateResult> staticCalculate(@RequestBody StaticCalculateRequest calculateRequest) {
        if (logger.isErrorEnabled()) {
            logger.info("收到静态计算请求,{}", JSON.toJSONString(calculateRequest));
        }
        List<StaticSingleFundCalculateRequest> staticSingleFundCalculateRequests =
                calculateRequest.singleFundCalculateRequests();
        StaticCalculateResult calculateResult = staticSingleFundCalculateRequests.stream().parallel().map(calculateService::sendSingleStaticCalculate)
                .collect(
                        () -> new StaticCalculateResult(calculateRequest.getRequestId(), calculateRequest.getSerialNumber()),
                        (a, t) -> a.append(t),
                        (a, c) -> a.append(c)
                );
        return ServerResponse.createBySuccess(calculateRequest.getRequestId(), calculateRequest.getSerialNumber(), calculateResult);
    }


    public ServerResponse<StaticCalculateResult> staticCalculateFallBack(StaticCalculateRequest calculateRequest, Throwable e) {
        if (logger.isErrorEnabled()) {
            logger.error(StringUtil.commonLogStart() + "静态风控请求发生错误," + e.getMessage(), calculateRequest.getSerialNumber(), calculateRequest.getRequestId());
            logger.error("" + e, e);
        }
        return ServerResponse.createByError(calculateRequest.getRequestId(), calculateRequest.getSerialNumber(), Const.exceptionErrorCode.NOTICE_ERROR_CODE, e.getMessage());
    }

}
