package com.yhfin.risk.analy.aop;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * 基金静态计算请求
 *
 * @author youlangta
 * @since 2018-04-11
 */
@Aspect
@Component
public class AnalyInstructionCalculateAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

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
        if(logger.isInfoEnabled()){
            logger.info("对静态请求，内存版本号、条目版本号进行确认");
        }

        String serialNumber = (String) joinPoint.getArgs()[joinPoint.getArgs().length - 1];

        if (StringUtils.equals(currentSerialNumber, serialNumber)) {
            if(logger.isInfoEnabled()){
                logger.info("当前流水号，有确认记录，不必再次确认");
            }
            return;
        }
        checkEntrySynchronizate();
        checkMemorySynchronizate();
        this.currentSerialNumber = serialNumber;
        if(logger.isInfoEnabled()){
            logger.info("对静态请求，内存版本号、条目版本号确认完毕");
        }
    }

    /**
     * 确认条目版本号
     */
    private void checkEntrySynchronizate() {
        byte[] versionNumbers = jedisCluster.hget(Const.cacheKey.CACHE_SYNCHRONIZATE_VERSION, ENTRY);
        if (versionNumbers == null) {
            return;
        }
        Integer entryVersionNumber = Integer.valueOf(new String(versionNumbers));
        Integer ownEntryVersionNumber = entrySynchronizateService.getEntryVersionNumber();
        if (entryVersionNumber.compareTo(ownEntryVersionNumber) == 0) {
            return;
        }
        if (entryVersionNumber > ownEntryVersionNumber) {
            while (entryVersionNumber > ownEntryVersionNumber) {
                ownEntryVersionNumber++;
                byte[] messageBytes = jedisCluster.hget(Const.cacheKey.CACHE_MESSAGE_SYNCHRONIZATE_ENTRY, String.valueOf(ownEntryVersionNumber).getBytes());
                if (messageBytes == null) {
                    continue;
                }
                EntryMessageSynchronizate message = (EntryMessageSynchronizate) SerializeUtil.unserialize(
                        messageBytes);
                entrySynchronizateService.entrySynchronizateByMessage(message);
                ownEntryVersionNumber = entrySynchronizateService.getEntryVersionNumber();
            }
        }

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
