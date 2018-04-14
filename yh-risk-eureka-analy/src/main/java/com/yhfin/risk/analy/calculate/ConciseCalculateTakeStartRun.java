package com.yhfin.risk.analy.calculate;

import com.alibaba.fastjson.JSON;
import com.netflix.discovery.converters.Auto;
import com.yhfin.risk.common.pojos.calculate.EntryConciseCalculateInfo;
import com.yhfin.risk.common.utils.StringUtil;
import com.yhfin.risk.core.analy.optimize.IConciseCalculateRequesttService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import java.util.concurrent.*;

@Component
/**
 * 从计算队列中拿出,需要计算的信息，发送到计算服务器
 * @author youlangta
 * @since 2018-04-13
 */
public class ConciseCalculateTakeStartRun implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IConciseCalculateRequesttService conciseCalculateRequesttService;

    @Autowired
    private IConsiseCalculateService consiseCalculateService;

    private ExecutorService executorService = new ThreadPoolExecutor(3, 500, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<Runnable>(512), new ThreadPoolExecutor.AbortPolicy());

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        takeConciseCalculate();
    }

    public void takeConciseCalculate() {
        Thread thread = new Thread(() -> {
            while (true) {
                EntryConciseCalculateInfo conciseCalculateInfo = conciseCalculateRequesttService.take();
                if (logger.isInfoEnabled()) {
                    logger.info(StringUtil.commonLogStart() + "发送计算请求,{}", conciseCalculateInfo.getSerialNumber(), conciseCalculateInfo.getRequestId(), JSON.toJSONString(conciseCalculateInfo));
                }
                CompletableFuture.runAsync(() -> {
                    consiseCalculateService.sendConsiseCalculate(conciseCalculateInfo);
                }, executorService);
            }
        });
        thread.start();

    }
}
