package com.yhfin.risk.calculate.accept;

import com.alibaba.fastjson.JSON;
import com.yhfin.risk.common.consts.Const;
import com.yhfin.risk.common.pojos.calculate.EntryCalculateResult;
import com.yhfin.risk.common.pojos.calculate.EntryConciseCalculateInfo;
import com.yhfin.risk.common.responses.ServerResponse;
import com.yhfin.risk.common.utils.StringUtil;
import com.yhfin.risk.core.calculate.reduce.IInstructionRequestCalculateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.json.Json;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 风控服务计算
 *
 * @author youlangta
 * @since 2018-04-13
 */
@RestController
@RequestMapping("/yhfin/calculate")
public class CalculateController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IInstructionRequestCalculateService calculateService;

    @RequestMapping(value = "/consiseCalculate", method = RequestMethod.POST)
    public ServerResponse<String> consiseCalculate(@RequestBody EntryConciseCalculateInfo conciseCalculateInfo) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "接收到计算请求，{}" + conciseCalculateInfo.getSerialNumber(), conciseCalculateInfo.getFundId(), JSON.toJSONString(conciseCalculateInfo));
        }

        EntryCalculateResult result = calculateService.calculateRequest(conciseCalculateInfo, conciseCalculateInfo.getFundId());
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "接收到计算请求，计算唯一标识{},计算结果:{}" + conciseCalculateInfo.getSerialNumber(), conciseCalculateInfo.getFundId(), conciseCalculateInfo.getResultKey(), JSON.toJSONString(result));
        }
        return ServerResponse.createBySuccess(conciseCalculateInfo.getRequestId(), conciseCalculateInfo.getSerialNumber());
    }


    @RequestMapping(value = "/consiseCalculates", method = RequestMethod.POST)
    public List<ServerResponse<String>> consiseCalculates(@RequestBody List<EntryConciseCalculateInfo> conciseCalculateInfos) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "接收到{}条计算请求" + conciseCalculateInfos.get(0).getSerialNumber(), conciseCalculateInfos.get(0).getFundId(), conciseCalculateInfos.size());
        }
        return conciseCalculateInfos.parallelStream().map((item) -> ServerResponse.createBySuccess(item.getRequestId(), item.getSerialNumber(), "")).collect(Collectors.toList());
    }

}
