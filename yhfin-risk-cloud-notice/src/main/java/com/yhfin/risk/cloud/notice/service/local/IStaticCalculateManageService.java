/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/16:30
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: IStaticCalculateManageService.java
 * 文件描述: @Description 静态风控请求计算管理服务
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.service.local;

import java.util.List;

import com.yhfin.risk.core.common.pojos.dtos.analy.SingleFundAnalyResultDTO;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticCalculateFinalDTO;
import com.yhfin.risk.core.common.pojos.dtos.notice.StaticSingleFundCalculateDTO;
import com.yhfin.risk.core.common.pojos.dtos.result.ResultHandleResultDTO;

/**
 * 静态风控请求计算管理服务 包名称：com.yhfin.risk.cloud.notice.service.local
 * 类名称：IStaticCalculateManageService 类描述：静态风控请求计算管理服务 创建人：@author caohui
 * 创建时间：2018/5/13/16:30
 */

public interface IStaticCalculateManageService {

    /**
     * 初始化静态计算请求
     *
     * @param staticSingleFundCalculateRequests 计算分析结果
     * @param requestId                         请求序号
     * @param serialNumber                      流水号
     * @Title initStaticManage
     * @Description: 初始化静态计算请求
     * @author: caohui
     * @Date: 2018/5/13/22:47
     */
    void initStaticManage(List<StaticSingleFundCalculateDTO> staticSingleFundCalculateRequests, String requestId,
                          String serialNumber);

    /**
     * 处理分析消息
     *
     * @param message 消息
     * @Title hander
     * @Description: 处理分析消息
     * @author: caohui
     * @Date: 2018/5/13/23:20
     */
    void hander(ResultHandleResultDTO message);

    /**
     * 处理结果消息
     *
     * @param message 消息
     * @Title hander
     * @Description: 处理结果消息
     * @author: caohui
     * @Date: 2018/5/13/23:20
     */
    void hander(SingleFundAnalyResultDTO message);

    /**
     * 更新基金发送状态是否成功
     *
     * @param fundId       基金序号
     * @param sendValid    是否发送成功
     * @param requestId    请求序号
     * @param serialNumber 流水号
     * @Title hander
     * @Description: 更新基金发送状态是否成功
     * @author: caohui
     * @Date: 2018年5月14日/下午5:01:15
     */
    void hander(String fundId, boolean sendValid, String requestId, String serialNumber);

    /**
     * 初始化通知通信状态
     *
     * @Title initNoticeState
     * @Description: 初始化通知通信状态
     * @author: caohui
     * @Date: 2018年5月14日/下午5:43:49
     */
    void initNoticeState();
}
