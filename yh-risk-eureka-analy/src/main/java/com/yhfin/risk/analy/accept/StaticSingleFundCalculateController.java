package com.yhfin.risk.analy.accept;


import com.yhfin.risk.common.pojos.notice.StaticSingleFundCalculateResult;
import com.yhfin.risk.common.requests.calculate.StaticSingleFundCalculateRequest;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.core.analy.optimize.IEntryAnalyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * 单个基金计算请求
 * @author youlangta
 * @since 2018-04-11
 */
@RestController
@RequestMapping("/yhfin/analy")
public class StaticSingleFundCalculateController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IEntryAnalyService analyService;

    @RequestMapping(value = "/staticSingleCalculate",method = RequestMethod.POST)
    public ServerResponse<StaticSingleFundCalculateResult> staticSingleFundCalculate(@RequestBody StaticSingleFundCalculateRequest calculateRequest) {
        StaticSingleFundCalculateResult calculateResult = new StaticSingleFundCalculateResult(calculateRequest.getRequestId(), calculateRequest.getSerialNumber(), calculateRequest.getFundId());
        CompletableFuture.runAsync(() -> analyService.stockInstructionCalculateRequestSingleFund(calculateRequest.getFundId(), calculateRequest.getRiskIds(), calculateRequest.getRequestId(), calculateRequest.getSerialNumber()));
        return ServerResponse.createBySuccess(calculateRequest.getRequestId(), calculateRequest.getSerialNumber(), calculateResult);
    }


}
