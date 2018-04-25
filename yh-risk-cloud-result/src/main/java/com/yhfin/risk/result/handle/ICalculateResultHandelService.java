package com.yhfin.risk.result.handle;

import com.yhfin.risk.common.pojos.analy.EntryCalculateBaseInfo;
import com.yhfin.risk.common.pojos.calculate.EntryCalculateResult;

import java.util.List;

/**
 * 计算结果处理
 * @author youlangta
 * @since 2018-04-16
 */
public interface ICalculateResultHandelService {
    /**
     * 接收计算结果基本信息
     */
    void acceptCalculateResultBaseInfo(List<EntryCalculateBaseInfo> calculateBaseInfos);
    /**
     * 接收计算结果最终结果信息
     */
    void acceptCalculateResultInfo(List<EntryCalculateResult> calculateResults);

}
