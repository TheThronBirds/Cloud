package com.yhfin.risk.notice.controller.calculate;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.common.pojos.notice.StaticCalculateResult;
import com.yhfin.risk.common.requests.calculate.StaticCalculateRequest;
import com.yhfin.risk.common.requests.calculate.StaticSingleFundCalculateRequest;
import com.yhfin.risk.common.responses.ServerResponse;
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


}
