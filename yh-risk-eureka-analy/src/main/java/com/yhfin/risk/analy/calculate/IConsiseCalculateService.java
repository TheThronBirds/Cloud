package com.yhfin.risk.analy.calculate;

import com.yhfin.risk.common.pojos.analy.EntryCalculateBaseInfo;
import com.yhfin.risk.common.pojos.calculate.EntryConciseCalculateInfo;
import com.yhfin.risk.common.responses.ServerResponse;

import java.util.List;

/**
 * 发送计算请求
 *
 *  @author youlangta
 *  @since 2018-04-13
 */
public interface IConsiseCalculateService {

    /**
     * 发送计算请求
     * @param conciseCalculateInfo
     * @return
     */
    ServerResponse<String> sendConsiseCalculate(EntryConciseCalculateInfo conciseCalculateInfo);
    /**
     * 合并发送计算请求
     * @param conciseCalculateInfos
     * @return
     */
    List<ServerResponse<String>> sendConsiseCalculates(List<EntryConciseCalculateInfo> conciseCalculateInfos);

    /**
     * 发送计算请求结果基本信息
     * @param calculateBaseInfo
     * @return
     */
    ServerResponse<String> sendConsiseCalculateBaseInfo(EntryCalculateBaseInfo calculateBaseInfo);
    /**
     * 合并发送计算请求基本结果信息
     * @param calculateBaseInfos
     * @return
     */
    List<ServerResponse<String>> sendConsiseCalculateBaseInfos(List<EntryCalculateBaseInfo> calculateBaseInfos);

}
