package com.yhfin.risk.calculate.service.impl;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.yhfin.risk.calculate.message.IMessageService;
import com.yhfin.risk.calculate.service.IConsiseCalculateResultService;
import com.yhfin.risk.common.consts.Const;
import com.yhfin.risk.common.pojos.calculate.EntryCalculateResult;
import com.yhfin.risk.common.requests.message.CalculateMessageSynchronizate;
import com.yhfin.risk.common.responses.ServerResponse;
import com.netflix.hystrix.HystrixCollapser.Scope;
import com.yhfin.risk.common.responses.result.CalculateResult;
import com.yhfin.risk.common.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class ConsiseCalculateResultServiceImpl implements IConsiseCalculateResultService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IMessageService messageService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ExecutorService executorService = new ThreadPoolExecutor(3, 500, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<Runnable>(512), new ThreadPoolExecutor.AbortPolicy());

    @Override
    @HystrixCollapser(batchMethod = "consiseCalculateResults", scope = Scope.GLOBAL, collapserProperties = {
            @HystrixProperty(name = "timerDelayInMilliseconds", value = "500"),
            @HystrixProperty(name = "maxRequestsInBatch", value = "5000")
    })
    public ServerResponse<String> consiseCalculateResult(EntryCalculateResult conciseCalculateResult) {

        return null;
    }

    @HystrixCommand(fallbackMethod = "consiseCalculateResultsFallBack")
    public List<ServerResponse<String>> consiseCalculateResults(List<EntryCalculateResult> conciseCalculateResults) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "合并发起条目计算结果信息,共{}条", conciseCalculateResults.get(0).getSerialNumber(), conciseCalculateResults.get(0).getRequestId(), conciseCalculateResults.size());
        }
        CompletableFuture.runAsync(() -> {
            Map<String, List<EntryCalculateResult>> messageOrigins = conciseCalculateResults.stream().parallel().collect(Collectors.groupingBy(EntryCalculateResult::getFundId));
            for (Map.Entry<String, List<EntryCalculateResult>> entry : messageOrigins.entrySet()) {
                CalculateMessageSynchronizate message = new CalculateMessageSynchronizate();
                List<EntryCalculateResult> calculateResults = entry.getValue();
                message.setRequestId(calculateResults.get(0).getRequestId());
                message.setSerialNumber(calculateResults.get(0).getSerialNumber());
                CalculateResult result = new CalculateResult();
                message.setCalculateResult(result);
                for (EntryCalculateResult calculateResult : calculateResults) {
                    if (calculateResult.isCalculateValid()) {
                        result.getSuccessCalculate().incrementAndGet();
                    } else {
                        result.getErrorCalculate().incrementAndGet();
                    }
                }
                messageService.calculateMessageSynchronizate(message);
            }
        }, executorService);

        return restTemplate.postForObject("http://RISK-RESULT/yhfin/result/consiseCalculateResultInfos", conciseCalculateResults, List.class);
    }


    public List<ServerResponse<String>> consiseCalculateResultsFallBack(List<EntryCalculateResult> conciseCalculateResults, Throwable e) {
        if (logger.isErrorEnabled()) {
            logger.error(StringUtil.commonLogStart() + "合并发起条目计算结果信息,处理发生错误", conciseCalculateResults.get(0).getSerialNumber(), conciseCalculateResults.get(0).getRequestId());
            logger.error("" + e, e);
        }
        //TODO 发送计算结果消息给通知服务器 ??
        return conciseCalculateResults.parallelStream().map((item) -> ServerResponse.createByError(item.getRequestId(), item.getSerialNumber(), Const.exceptionErrorCode.CALCULATE_ERROR_CODE, e.getMessage(), "")).collect(Collectors.toList());
    }


}
