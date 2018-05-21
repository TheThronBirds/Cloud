/****************************************************
 * 创建人：  @author caohui    
 * 创建时间: 2018年5月15日/下午1:13:27
 * 项目名称: yhfin-risk-cloud-analy 
 * 文件名称: ISendEntryStaticCalculateService.java
 * 文件描述: @Description 发送单个条目计算请求给计算服务器
 * 公司名称: 深圳市赢和信息技术有限公司
 *
 * All rights Reserved, Designed By 深圳市赢和信息技术有限公司
 * @Copyright:2016-2018
 *
 ********************************************************/
package com.yhfin.risk.cloud.analy.service.feign;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.yhfin.risk.core.common.pojos.dtos.analy.FinalStaticEntryCalculateDTO;
import com.yhfin.risk.core.common.reponse.ServerResponse;

/**
 * 发送单个条目计算请求给计算服务器 包名称：com.yhfin.risk.cloud.analy.service.feign
 * 类名称：ISendEntryStaticCalculateService 类描述：发送单个条目计算请求给计算服务器 创建人：@author caohui
 * 创建时间：2018/5/13/12:20
 */
@FeignClient(value = "risk-calculate", fallbackFactory = SendEntryStaticCalculateServiceHystrix.class)
@RequestMapping("/yhfin/cloud/calculate")
public interface ISendEntryStaticCalculateService {
    /**
     * 发送单个条目计算请求给计算服务器
     *
     * @param finalStaticEntryCalculate 计算结果信息
     * @return 接口返回结果
     * @Title consiseCalculate
     * @Description: 发送单个条目计算请求给计算服务器
     * @author: caohui
     * @Date: 2018年5月15日/下午1:20:10
     */
    @RequestMapping(value = "/consiseCalculate", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse<String> consiseCalculate(@RequestBody FinalStaticEntryCalculateDTO finalStaticEntryCalculate);

    /**
     * 发送多个条目计算请求给计算服务器
     *
     * @param finalStaticEntryCalculates 计算结果信息
     * @return 接口返回结果
     * @Title consiseCalculates
     * @Description: 发送多个条目计算请求给计算服务器
     * @author: caohui
     * @Date: 2018年5月15日/下午1:23:12
     */
    @RequestMapping(value = "/consiseCalculates", method = RequestMethod.POST, produces = "application/json")
    public ServerResponse<String> consiseCalculates(@RequestBody List<FinalStaticEntryCalculateDTO> finalStaticEntryCalculates);
}
