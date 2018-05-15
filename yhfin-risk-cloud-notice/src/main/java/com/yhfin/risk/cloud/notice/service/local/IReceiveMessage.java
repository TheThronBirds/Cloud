/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/15:32
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: IReceiveMessage.java
 * 文件描述: @Description 接收消息
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.service.local;

import com.yhfin.risk.core.common.pojos.dtos.analy.SingleFundAnalyResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.result.ResultHandleResultDTO;

/**
 * 接收消息
 * 包名称：com.yhfin.risk.cloud.notice.service.local
 * 类名称：IReceiveMessage
 * 类描述：接收消息
 * 创建人：@author caohui
 * 创建时间：2018/5/13/15:32
 */

public interface IReceiveMessage {


    /**
     * 接收分析结果消息
     *
     * @param message 消息体
     * @Title messageResult
     * @Description: 接收分析结果消息
     * @author: caohui
     * @Date: 2018/5/13/23:16
     */
    void messageAnaly(SingleFundAnalyResultDTO message);

    /**
     * 接收处理结果消息
     *
     * @param message 消息体
     * @Title messageResult
     * @Description: 接收处理结果消息
     * @author: caohui
     * @Date: 2018/5/13/23:16
     */
    void messageResult(ResultHandleResultDTO message);
}
