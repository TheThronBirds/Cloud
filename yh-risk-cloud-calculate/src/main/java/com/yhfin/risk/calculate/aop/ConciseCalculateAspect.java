package com.yhfin.risk.calculate.aop;

import com.yhfin.risk.common.consts.Const;
import com.yhfin.risk.common.pojos.calculate.EntryConciseCalculateInfo;
import com.yhfin.risk.common.requests.message.EntryMessageSynchronizate;
import com.yhfin.risk.common.requests.message.MemoryMessageSynchronizate;
import com.yhfin.risk.common.utils.SerializeUtil;
import com.yhfin.risk.core.dao.IJedisClusterDao;
import com.yhfin.risk.core.synchronizate.entry.IEntrySynchronizateService;
import com.yhfin.risk.core.synchronizate.memory.IMemorySynchronizateService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Aspect
@Component
/**
 * 计算结果发送切面
 *  @author youlangta
 *  @since 2018-04-13
 */
public class ConciseCalculateAspect {


    private ExecutorService executorService = new ThreadPoolExecutor(3, 50, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<Runnable>(512), new ThreadPoolExecutor.AbortPolicy());

    /**
     * 内存同步在redis中存储的消息，hashkey值
     */
    private final byte[] MEMORY = "MEMORY".getBytes();

    /**
     * 当前流水号
     */
    private String currentSerialNumber;


    @Autowired
    private IJedisClusterDao jedisCluster;


    @Autowired
    private IMemorySynchronizateService memorySynchronizateService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Pointcut("execution(* com.yhfin.risk.core.calculate.reduce.IInstructionRequestCalculateService.calculateRequest(..))")
    public void calculateAop(){

    }

    @Before("calculateAop()")
    public synchronized void beforeCalculate(JoinPoint joinPoint){
        if(logger.isInfoEnabled()){
            logger.info("对静态请求，内存版本号、条目版本号进行确认");
        }
        EntryConciseCalculateInfo calculateInfo = (EntryConciseCalculateInfo) joinPoint.getArgs()[0];

        String serialNumber = calculateInfo.getSerialNumber();

        if (StringUtils.equals(currentSerialNumber, serialNumber)) {
            if(logger.isInfoEnabled()){
                logger.info("当前流水号，有确认记录，不必再次确认");
            }
            return;
        }
        checkMemorySynchronizate();
        this.currentSerialNumber = serialNumber;
        if(logger.isInfoEnabled()){
            logger.info("对静态请求，内存版本号、条目版本号确认完毕");
        }
    }

    @AfterReturning("calculateAop()")
    public void afterReturnResult(JoinPoint joinPoint){


    }

    /**
     * 确认内存版本号
     */
    private void checkMemorySynchronizate() {
        byte[] versionNumbers = jedisCluster.hget(Const.cacheKey.CACHE_SYNCHRONIZATE_VERSION, MEMORY);
        if (versionNumbers == null) {
            return;
        }
        Integer memoryVersionNumber = Integer.valueOf(new String(versionNumbers));
        Integer ownMemoryVersionNumber = memorySynchronizateService.getMemoryVersionNumber();
        if (memoryVersionNumber.compareTo(ownMemoryVersionNumber) == 0) {
            return;
        }
        if (memoryVersionNumber > ownMemoryVersionNumber) {
            while (memoryVersionNumber > ownMemoryVersionNumber) {
                ownMemoryVersionNumber++;
                byte[] messageBytes = jedisCluster.hget(Const.cacheKey.CACHE_MESSAGE_SYNCHRONIZATE_MEMORY, String.valueOf(ownMemoryVersionNumber).getBytes());
                if (messageBytes == null) {
                    continue;
                }
                MemoryMessageSynchronizate message = (MemoryMessageSynchronizate) SerializeUtil.unserialize(
                        messageBytes);
                memorySynchronizateService.memorySynchronizateByMessage(message);
                ownMemoryVersionNumber = memorySynchronizateService.getMemoryVersionNumber();
            }
        }
    }

}
