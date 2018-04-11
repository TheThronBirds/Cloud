package com.yhfin.risk.aop;

import com.yhfin.risk.core.dao.IJedisClusterDao;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 基金静态计算请求
 * @author youlangta
 * @since 2018-04-11
 */
@Aspect
@Component
public class AnalyInstructionCalculateAspect {

    @Autowired
    private IJedisClusterDao jedisCluster;

    @Pointcut("execution(* com.yhfin.risk.core.analy.optimize.IEntryAnalyService.stockInstructionCalculateRequestSingleFund(..))")
    public void calculateAop() {
    }

    @Before("calculateAop()")
    public void beforeCalculate(JoinPoint joinPoint){
        //TODO  版本管理   内存版本号     条目版本号


    }


}
