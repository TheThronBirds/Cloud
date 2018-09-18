/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018/5/13/16:23
 * 项目名称: yhfin-risk-cloud-parent 
 * 文件名称: ISendStaticSingleFundtCalculateService.java
 * 文件描述: @Description 发送单个基金计算请求
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.notice.service.feign;

import com.yhfin.risk.core.common.pojos.dtos.notice.StaticSingleFundCalculateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 发送单个基金计算请求
 * 包名称：com.yhfin.risk.cloud.notice.service.feign
 * 类名称：ISendStaticSingleFundtCalculateService
 * 类描述：发送单个基金计算请求
 * 创建人：@author caohui
 * 创建时间：2018/5/13/16:23
 */
@FeignClient(value="risk-analy-calculate",fallbackFactory=SendStaticSingleFundCalculateServiceHystrix.class)
@RequestMapping("/yhfin/cloud/analyCalculate")
public interface ISendStaticSingleFundtCalculateService {
    /**
     * 发送静态风控请求到分析服务器
     * @Title staticSingleFundCalculate
     * @Description: 发送静态风控请求到分析服务器
     * @param  singleFundCalculate 单个基金静态分析请求
     * @return  接口返回结果
     * @author: caohui
     * @Date:  2018/5/13/17:14
     */
    @RequestMapping(value = "/staticSingleCalculate", method = RequestMethod.POST, produces = "application/json")
    ServerResponse<String> staticSingleFundCalculate(@RequestBody StaticSingleFundCalculateDTO singleFundCalculate);
}
