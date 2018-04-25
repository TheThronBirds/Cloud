package com.yhfin.risk.notice.aop;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.common.consts.Const;
import com.yhfin.risk.common.requests.message.AbstractBaseMessageRequest;
import com.yhfin.risk.common.requests.message.EntryMessageSynchronizate;
import com.yhfin.risk.common.requests.message.MemoryMessageSynchronizate;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.common.utils.SerializeUtil;
import com.yhfin.risk.core.dao.IJedisClusterDao;
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

@Aspect
@Component
public class MessageSynchronizateAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());



    @Pointcut("execution(* com.yhfin.risk.notice.service.IMessageService.*(..))")
    public void messageSynchronizate() {

    }

    @Before("messageSynchronizate()")
    public void beforeMessageSynchronizate(JoinPoint joinPoint) {
        //校验参数
        Object[] args = joinPoint.getArgs();
        AbstractBaseMessageRequest message = (AbstractBaseMessageRequest) args[0];
        if (message == null || StringUtils.isBlank(message.getRequestId()) || StringUtils.isBlank(message.getSerialNumber())) {
            if(logger.isErrorEnabled()){
                logger.info("发送同步消息失败，请求序号为空或者流水号为空,{}", JSON.toJSONString(message));
            }
            throw new RuntimeException("发送同步消息失败，请求序号为空或者流水号为空");
        }
    }

}
