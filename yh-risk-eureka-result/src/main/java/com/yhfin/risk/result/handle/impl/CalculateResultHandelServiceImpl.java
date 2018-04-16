package com.yhfin.risk.result.handle.impl;

import com.yhfin.risk.common.pojos.analy.EntryCalculateBaseInfo;
import com.yhfin.risk.common.pojos.calculate.EntryCalculateResult;
import com.yhfin.risk.common.utils.StringUtil;
import com.yhfin.risk.result.handle.ICalculateResultHandelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CalculateResultHandelServiceImpl implements ICalculateResultHandelService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 接收计算结果基本信息
     *
     * @param calculateBaseInfos
     */
    @Override
    public void acceptCalculateResultBaseInfo(List<EntryCalculateBaseInfo> calculateBaseInfos) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "对接收到{}条计算结果基本信息进行处理" + calculateBaseInfos.get(0).getSerialNumber(), calculateBaseInfos.get(0).getRequestId(), calculateBaseInfos.size());
        }


    }

    /**
     * 接收计算结果最终结果信息
     *
     * @param calculateResults
     */
    @Override
    public void acceptCalculateResultInfo(List<EntryCalculateResult> calculateResults) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "对接收到{}条计算结果基本信息进行处理" + calculateResults.get(0).getSerialNumber(), calculateResults.get(0).getRequestId(), calculateResults.size());
        }


    }
}
