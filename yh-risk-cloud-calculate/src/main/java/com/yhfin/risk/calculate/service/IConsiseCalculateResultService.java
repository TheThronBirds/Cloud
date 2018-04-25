package com.yhfin.risk.calculate.service;

import com.yhfin.risk.common.pojos.calculate.EntryCalculateResult;
import com.yhfin.risk.common.responses.ServerResponse;

/**
 * 计算结果发送处理
 * @author youlangta
 * @since 2018-04-16
 */
public interface IConsiseCalculateResultService {

    ServerResponse<String> consiseCalculateResult(EntryCalculateResult conciseCalculateResult);
}
