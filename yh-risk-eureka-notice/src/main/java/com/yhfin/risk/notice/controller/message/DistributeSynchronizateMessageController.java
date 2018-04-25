package com.yhfin.risk.notice.controller.message;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yhfin.risk.common.consts.Const;
import com.yhfin.risk.common.requests.message.AbstractBaseMessageRequest;
import com.yhfin.risk.common.requests.message.EntryMessageSynchronizate;
import com.yhfin.risk.common.requests.message.MemoryMessageSynchronizate;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.common.utils.StringUtil;
import com.yhfin.risk.core.sql.build.IEntrySqlBuildService;
import com.yhfin.risk.notice.service.IMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分发同步消息：内存同步、条目同步
 *
 * @author youlangta
 * @since 2018-04-12
 */
@RestController
@RequestMapping("yhfin/notice")
public class DistributeSynchronizateMessageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IMessageService messageService;

    @Autowired
    private IEntrySqlBuildService entrySqlBuildService;

    @RequestMapping(value = "/entryMessageSynchronizate", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "entryMessageSynchronizateFallBack")
    public ServerResponse<EntryMessageSynchronizate> entryMessageSynchronizate(@RequestBody EntryMessageSynchronizate message) {
        entrySqlBuildService.buildEntrySql(message);
        return messageService.entryMessageSynchronizate(message);
    }

    @RequestMapping(value = "/memoryMessageSynchronizate", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "memoryMessageSynchronizateFallBack")
    public ServerResponse<MemoryMessageSynchronizate> memoryMessageSynchronizate(@RequestBody MemoryMessageSynchronizate message) {
        return messageService.memoryMessageSynchronizate(message);
    }

    public ServerResponse<EntryMessageSynchronizate> entryMessageSynchronizateFallBack(EntryMessageSynchronizate message, Throwable e) {
        return messageSynchronizateFallBack(message, e);
    }

    public ServerResponse<MemoryMessageSynchronizate> memoryMessageSynchronizateFallBack(MemoryMessageSynchronizate message, Throwable e) {
        return messageSynchronizateFallBack(message, e);
    }

    private ServerResponse messageSynchronizateFallBack(AbstractBaseMessageRequest message, Throwable e) {
        if (logger.isErrorEnabled()) {
            logger.error(StringUtil.commonLogStart() + "消息发送失败，{}", message.getSerialNumber(), message.getRequestId(), e.getMessage());
            logger.error("" + e, e);
        }
        return ServerResponse.createByError(message.getRequestId(), message.getSerialNumber(), Const.exceptionErrorCode.NOTICE_ERROR_CODE, e.getMessage(), message);

    }

}
