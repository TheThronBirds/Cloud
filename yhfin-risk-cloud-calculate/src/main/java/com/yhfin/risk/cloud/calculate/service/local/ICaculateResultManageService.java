/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018年5月17日/上午10:17:13
 * 项目名称: yhfin-risk-cloud-calculate 
 * 文件名称: ICaculateResultManageService.java
 * 文件描述: @Description 计算结果处理服务
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.calculate.service.local;

import java.util.concurrent.BlockingDeque;

import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateResultDTO;

/**
 * 计算结果处理服务 包名称：com.yhfin.risk.cloud.calculate.service.local
 * 类名称：ICaculateResultManageService 类描述：计算结果处理服务 创建人：@author caohui
 * 创建时间：2018/5/13/12:20
 */
public interface ICaculateResultManageService {

    /**
     * 缓存计算结果信息
     *
     * @param calculateResultDTO 计算结果
     * @Title putFinalStaticEntryCalculateResultDTOs
     * @Description: 缓存计算结果信息
     * @author: caohui
     * @Date: 2018年5月17日/上午10:40:10
     */
    void putFinalStaticEntryCalculateResultDTOs(FinalStaticEntryCalculateResultDTO calculateResultDTO);
    
    /**
     * 获取缓存计算结果队列
     *
     * @Title getBlockingDeque
     * @Description: 获取缓存计算结果队列
     * @author: caohui
     * @Date: 2018年5月22日/上午10:45:09
     */
    BlockingDeque<?> getBlockingDeque();
}
