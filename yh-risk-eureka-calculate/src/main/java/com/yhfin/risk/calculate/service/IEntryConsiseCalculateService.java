package com.yhfin.risk.calculate.service;

import com.yhfin.risk.common.pojos.calculate.EntryConciseCalculateInfo;
import com.yhfin.risk.common.responses.ServerResponse;

/**
 * 计算请求
 *
 * @author youlangta
 * @since 2018-04-14
 */
public interface IEntryConsiseCalculateService {

    ServerResponse<String> consiseCalculate(EntryConciseCalculateInfo conciseCalculateInfo);
}
