package com.yhfin.risk.calculate.accept;

import com.yhfin.risk.calculate.service.IEntryConsiseCalculateService;
import com.yhfin.risk.common.pojos.calculate.EntryConciseCalculateInfo;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.common.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 风控服务计算
 *
 * @author youlangta
 * @since 2018-04-13
 */
@RestController
@RequestMapping("/yhfin/calculate")
public class CalculateController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IEntryConsiseCalculateService entryConsiseCalculateService;


    private ExecutorService executorService = new ThreadPoolExecutor(3, 500, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<Runnable>(512), new ThreadPoolExecutor.AbortPolicy());

    @RequestMapping(value = "/consiseCalculate", method = RequestMethod.POST)
    public ServerResponse<String> consiseCalculate(@RequestBody EntryConciseCalculateInfo conciseCalculateInfo) {
        return entryConsiseCalculateService.consiseCalculate(conciseCalculateInfo);
    }


    @RequestMapping(value = "/consiseCalculates", method = RequestMethod.POST)
    public List<ServerResponse<String>> consiseCalculates(@RequestBody List<EntryConciseCalculateInfo> conciseCalculateInfos) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "接收到{}条计算请求" + conciseCalculateInfos.get(0).getSerialNumber(), conciseCalculateInfos.get(0).getRequestId(), conciseCalculateInfos.size());
        }
        CompletableFuture.runAsync(() -> {
            conciseCalculateInfos.stream().parallel().forEach((item) -> {
                CompletableFuture.runAsync(() -> {
                    entryConsiseCalculateService.consiseCalculate(item);
                }, executorService);
            });
        }, executorService);
        return conciseCalculateInfos.parallelStream().map((item) -> ServerResponse.createBySuccess(item.getRequestId(), item.getSerialNumber(), "")).collect(Collectors.toList());
    }

}
