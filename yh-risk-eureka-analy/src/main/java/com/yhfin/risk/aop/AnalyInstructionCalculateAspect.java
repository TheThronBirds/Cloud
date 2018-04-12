package com.yhfin.risk.aop;

import com.yhfin.risk.common.consts.Const;
import com.yhfin.risk.common.requests.message.EntryMessageSynchronizate;
import com.yhfin.risk.common.requests.message.MemoryMessageSynchronizate;
import com.yhfin.risk.common.utils.SerializeUtil;
import com.yhfin.risk.core.dao.IJedisClusterDao;
import com.yhfin.risk.core.synchronizate.entry.IEntrySynchronizateService;
import com.yhfin.risk.core.synchronizate.memory.IMemorySynchronizateService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;


/**
 * 基金静态计算请求
 *
 * @author youlangta
 * @since 2018-04-11
 */
@Aspect
@Component
public class AnalyInstructionCalculateAspect {

    /**
     * 当前流水号
     */
    private String currentSerialNumber;

    /**
     * 内存同步在redis中存储的消息，hashkey值
     */
    private final byte[] MEMORY = "MEMORY".getBytes();
    /**
     * 条目同步在redis中存储的消息，hashkey值
     */
    private final byte[] ENTRY = "ENTRY".getBytes();

    @Autowired
    private IJedisClusterDao jedisCluster;

    @Autowired
    private IEntrySynchronizateService entrySynchronizateService;

    @Autowired
    private IMemorySynchronizateService memorySynchronizateService;

    /**
     * 对计算基金持仓进行切面，保证每次流水号进行的基金静态请求，条目版本号和内存版本号是最新的
     */
    @Pointcut("execution(* com.yhfin.risk.core.analy.optimize.IEntryAnalyService.stockInstructionCalculateRequestSingleFund(..))")
    public void calculateAop() {

    }

    @Before("calculateAop()")
    public synchronized void beforeCalculate(JoinPoint joinPoint) {
        String serialNumber = (String) joinPoint.getArgs()[joinPoint.getArgs().length - 1];

        if (StringUtils.equals(currentSerialNumber, serialNumber)) {
            return;
        }
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> checkEntrySynchronizate()),
                CompletableFuture.runAsync(() -> checkMemorySynchronizate())
        ).join();
        this.currentSerialNumber = serialNumber;
    }

    /**
     * 确认条目版本号
     */
    private void checkEntrySynchronizate() {
        Integer entryVersionNumber = Integer.valueOf(new String(jedisCluster.hget(Const.cacheKey.CACHE_SYNCHRONIZATE_VERSION, ENTRY)));
        Integer ownEntryVersionNumber = entrySynchronizateService.getEntryVersionNumber();
        if (entryVersionNumber.compareTo(ownEntryVersionNumber) == 0) {
            return;
        }
        //TODO  版本管理     条目版本号
        if (entryVersionNumber > ownEntryVersionNumber) {
            while (entryVersionNumber > ownEntryVersionNumber) {
                ownEntryVersionNumber++;
                EntryMessageSynchronizate message = (EntryMessageSynchronizate) SerializeUtil.unserialize(
                        jedisCluster.hget(Const.cacheKey.CACHE_MESSAGE_SYNCHRONIZATE_ENTRY, String.valueOf(ownEntryVersionNumber).getBytes()));
                entrySynchronizateService.entrySynchronizateByMessage(message);
            }
            entrySynchronizateService.setEntryVersionNumber(entryVersionNumber);
        }


    }

    /**
     * 确认内存版本号
     */
    private void checkMemorySynchronizate() {
        Integer memoryVersionNumber = Integer.valueOf(new String(jedisCluster.hget(Const.cacheKey.CACHE_SYNCHRONIZATE_VERSION, MEMORY)));
        Integer ownMemoryVersionNumber = memorySynchronizateService.getMemoryVersionNumber();
        if (memoryVersionNumber.compareTo(ownMemoryVersionNumber) == 0) {
            return;
        }
        //TODO  版本管理   内存版本号
        if (memoryVersionNumber > ownMemoryVersionNumber) {
            while (memoryVersionNumber > ownMemoryVersionNumber) {
                ownMemoryVersionNumber++;
                MemoryMessageSynchronizate message = (MemoryMessageSynchronizate) SerializeUtil.unserialize(
                        jedisCluster.hget(Const.cacheKey.CACHE_MESSAGE_SYNCHRONIZATE_MEMORY, String.valueOf(ownMemoryVersionNumber).getBytes()));
                memorySynchronizateService.memorySynchronizateByMessage(message);
            }
            memorySynchronizateService.setMemoryVersionNumber(memoryVersionNumber);
        }

    }

}
