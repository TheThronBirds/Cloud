package com.yhfin.risk.result.accept;

import com.yhfin.risk.common.pojos.analy.EntryCalculateBaseInfo;
import com.yhfin.risk.common.pojos.calculate.EntryCalculateResult;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.common.utils.StringUtil;
import com.yhfin.risk.result.handle.ICalculateResultHandelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 接收计算结果信息
 *
 * @author youlangta
 * @since 2018-04-16
 */
public class CalculateResultController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ExecutorService executorService = new ThreadPoolExecutor(3, 500, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<Runnable>(512), new ThreadPoolExecutor.AbortPolicy());

    @Autowired
    private ICalculateResultHandelService resulteHandelService;

    public List<ServerResponse<String>> consiseCalculateBaseInfos(@RequestBody List<EntryCalculateBaseInfo> calculateBaseInfos) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "接收到{}条计算结果基本信息" + calculateBaseInfos.get(0).getSerialNumber(), calculateBaseInfos.get(0).getRequestId(), calculateBaseInfos.size());
        }
        CompletableFuture.runAsync(() -> {
            resulteHandelService.acceptCalculateResultBaseInfo(calculateBaseInfos);
        }, executorService);
        return calculateBaseInfos.parallelStream().map((item) -> ServerResponse.createBySuccess(item.getRequestId(), item.getSerialNumber(), "")).collect(Collectors.toList());
    }


    public List<ServerResponse<String>> consiseCalculateResultInfos(@RequestBody List<EntryCalculateResult> calculateResults) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "接收到{}条计算结果基本信息" + calculateResults.get(0).getSerialNumber(), calculateResults.get(0).getRequestId(), calculateResults.size());
        }
        CompletableFuture.runAsync(() -> {
            resulteHandelService.acceptCalculateResultInfo(calculateResults);
        }, executorService);
        return calculateResults.parallelStream().map((item) -> ServerResponse.createBySuccess(item.getRequestId(), item.getSerialNumber(), "")).collect(Collectors.toList());
    }

}
