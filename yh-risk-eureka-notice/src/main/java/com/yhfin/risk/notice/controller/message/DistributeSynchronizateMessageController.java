package com.yhfin.risk.notice.controller.message;

import com.yhfin.risk.common.requests.message.EntryMessageSynchronizate;
import com.yhfin.risk.common.requests.message.MemoryMessageSynchronizate;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.notice.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分发同步消息：内存同步、条目同步
 * @author youlangta
 * @since 2018-04-12
 */
@RestController
@RequestMapping("yhfin/notice")
public class DistributeSynchronizateMessageController {

    @Autowired
    private IMessageService messageService;

    @RequestMapping(value = "/entryMessageSynchronizate",method = RequestMethod.POST)
    public ServerResponse<EntryMessageSynchronizate> entryMessageSynchronizate(@RequestBody EntryMessageSynchronizate message){
        return messageService.entryMessageSynchronizate(message);
    }

    @RequestMapping(value = "/memoryMessageSynchronizate",method = RequestMethod.POST)
    public ServerResponse<MemoryMessageSynchronizate> memoryMessageSynchronizate(@RequestBody MemoryMessageSynchronizate message){
        return messageService.memoryMessageSynchronizate(message);
    }

}
