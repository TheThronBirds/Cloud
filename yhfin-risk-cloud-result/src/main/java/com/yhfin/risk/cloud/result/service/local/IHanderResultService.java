/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/14/0:08
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: IHanderResultService.java
 * 文件描述: @Description 结果处理
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2017
 *
 ********************************************************/
package com.yhfin.risk.cloud.result.service.local;

import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateResultDTO;

import java.util.List;

/**
 * 结果处理
 * 包名称：com.yhfin.risk.cloud.result.service.local
 * 类名称：IHanderResultService
 * 类描述：结果处理
 * 创建人：@author caohui
 * 创建时间：2018/5/14/0:08
 */

public interface IHanderResultService {

    /**
     * 处理计算结果
     * @Title handerResults
     * @Description: 处理计算结果
     * @param  calculateResultDTOList 结果列表
     * @return
     * @author: caohui
     * @Date:  2018/5/14/0:06
     */
    void handerResults(List<FinalStaticEntryCalculateResultDTO> calculateResultDTOList);
    

    /**
     * 处理计算结果
     * @Title handerResult
     * @Description: 处理计算结果
     * @param  calculateResultDTOList 结果列表
     * @return
     * @author: caohui
     * @Date:  2018/5/14/0:06
     */
    void handerResult(FinalStaticEntryCalculateResultDTO calculateResult);
}
