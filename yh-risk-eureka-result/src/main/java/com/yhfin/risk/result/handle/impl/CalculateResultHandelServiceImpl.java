package com.yhfin.risk.result.handle.impl;

import com.yhfin.risk.common.pojos.analy.EntryCalculateBaseInfo;
import com.yhfin.risk.common.pojos.calculate.EntryCalculateResult;
import com.yhfin.risk.common.requests.message.ResultMessageSynchronizate;
import com.yhfin.risk.common.responses.result.ResultHandleResult;
import com.yhfin.risk.common.utils.StringUtil;
import com.yhfin.risk.core.dao.IRiskDao;
import com.yhfin.risk.result.handle.ICalculateResultHandelService;
import com.yhfin.risk.result.message.IMessageService;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class CalculateResultHandelServiceImpl implements ICalculateResultHandelService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 当前流水号
     */
    private String currentSerialNumber;
    /**
     * 条目计算结果基本信息
     */
    private final String RISK_RESULT_BASE = "RISKRESULT_STATIC_MID_BASE";
    /**
     * 条目计算结果 阀值信息
     */
    private final String RISK_RESULT = "RISKRESULT_STATIC_MID_RESULT";


    private ExecutorService executorService = new ThreadPoolExecutor(3, 500, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<Runnable>(512), new ThreadPoolExecutor.AbortPolicy());

    @Autowired
    private IRiskDao riskDao;

    @Autowired
    private IMessageService messageService;

    /**
     * 接收计算结果基本信息
     *
     * @param calculateBaseInfos
     */
    @Override
    public void acceptCalculateResultBaseInfo(List<EntryCalculateBaseInfo> calculateBaseInfos) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "对接收到{}条计算结果基本信息进行处理", calculateBaseInfos.get(0).getSerialNumber(), calculateBaseInfos.get(0).getRequestId(), calculateBaseInfos.size());
        }
        checkTruncateTableData(calculateBaseInfos.get(0).getSerialNumber());
        forAllCalculateBaseInfo(calculateBaseInfos);
    }

    /**
     * 接收计算结果最终结果信息
     *
     * @param calculateResults
     */
    @Override
    public void acceptCalculateResultInfo(List<EntryCalculateResult> calculateResults) {
        if (logger.isInfoEnabled()) {
            logger.info(StringUtil.commonLogStart() + "对接收到{}条计算结果基本信息进行处理", calculateResults.get(0).getSerialNumber(), calculateResults.get(0).getRequestId(), calculateResults.size());
        }
        checkTruncateTableData(calculateResults.get(0).getSerialNumber());
        forAllCalculateResultInfo(calculateResults);
    }


    /**
     * 删除上一次表数据
     *
     * @param serialNumber
     */
    private synchronized void checkTruncateTableData(String serialNumber) {
        if (!StringUtils.equals(currentSerialNumber, serialNumber)) {
            riskDao.jdbcOperatons().update("TRUNCATE TABLE " + RISK_RESULT_BASE);
            riskDao.jdbcOperatons().update("TRUNCATE TABLE " + RISK_RESULT);
            this.currentSerialNumber = serialNumber;
        }
    }

    private void sendCalculateBaseInfos(List<EntryCalculateBaseInfo> calculateBaseInfos, boolean valid) {
        Map<String, List<EntryCalculateBaseInfo>> messageOrigin = calculateBaseInfos.stream().parallel().collect(Collectors.groupingBy(EntryCalculateBaseInfo::getFundId));
        for (Map.Entry<String, List<EntryCalculateBaseInfo>> entry : messageOrigin.entrySet()) {
            ResultMessageSynchronizate message = new ResultMessageSynchronizate();
            List<EntryCalculateBaseInfo> baseInfos = entry.getValue();
            String requestId = baseInfos.get(0).getRequestId();
            String serialNumber = baseInfos.get(0).getSerialNumber();
            ResultHandleResult result = new ResultHandleResult();
            message.setHandleResult(result);
            message.setRequestId(requestId);
            message.setSerialNumber(serialNumber);
            result.setRequestId(requestId);
            result.setSerialNumber(serialNumber);
            result.setFundId(entry.getKey());
            if (valid) {
                result.getSuccessBase().addAndGet(baseInfos.size());
            } else {
                result.getErrorBase().addAndGet(baseInfos.size());
            }
            messageService.resultMessageSynchronizate(message);
        }

    }

    private void sendCalculateResultInfos(List<EntryCalculateResult> calculateResults, boolean valid) {
        Map<String, List<EntryCalculateResult>> messageOrigin = calculateResults.stream().parallel().collect(Collectors.groupingBy(EntryCalculateResult::getFundId));
        for (Map.Entry<String, List<EntryCalculateResult>> entry : messageOrigin.entrySet()) {
            ResultMessageSynchronizate message = new ResultMessageSynchronizate();
            List<EntryCalculateResult> resultInfos = entry.getValue();
            String requestId = resultInfos.get(0).getRequestId();
            String serialNumber = resultInfos.get(0).getSerialNumber();
            ResultHandleResult result = new ResultHandleResult();
            message.setHandleResult(result);
            result.setRequestId(requestId);
            result.setSerialNumber(serialNumber);
            result.setFundId(entry.getKey());
            if (valid) {
                result.getSuccessResult().addAndGet(resultInfos.size());
            } else {
                result.getErrorResult().addAndGet(resultInfos.size());
            }
            messageService.resultMessageSynchronizate(message);
        }
    }


    private void forAllCalculateResultInfo(List<EntryCalculateResult> calculateResults) {
        String callSql = "{ call FORALL_RISK_STATIC_MID_BASE (?,?,?) }";
        try {

            Integer resultCode = (Integer) riskDao.jdbcOperatons().execute(callStateMentResultInfo(callSql, calculateResults),
                    new CallableStatementCallback() {
                        @Override
                        public Integer doInCallableStatement(CallableStatement cs)
                                throws SQLException, DataAccessException {
                            cs.execute();
                            int returnCode = cs.getInt(2);
                            String message = cs.getString(3);
                            if (StringUtils.isNotBlank(message)) {
                                if (logger.isErrorEnabled()) {
                                    logger.error("批量插入计算结果基本信息失败，" + message);
                                }
                            }
                            return returnCode;
                        }
                    });
            if (resultCode == 0) {
                if (logger.isErrorEnabled()) {
                    logger.error("批量插入计算结果基本信息失败");
                }
                CompletableFuture.runAsync(() -> {
                    sendCalculateResultInfos(calculateResults, false);
                }, executorService);
                return;
            }
            CompletableFuture.runAsync(() -> {
                sendCalculateResultInfos(calculateResults, true);
            }, executorService);

        } catch (SQLException e) {
            if (logger.isErrorEnabled()) {
                logger.error("批量插入计算结果信息失败，" + e, e);
            }
            CompletableFuture.runAsync(() -> {
                sendCalculateResultInfos(calculateResults, false);
            }, executorService);
        }
    }


    private void forAllCalculateBaseInfo(List<EntryCalculateBaseInfo> calculateBaseInfos) {
        String callSql = "{ call FORALL_RISK_STATIC_MID_RESULT (?,?,?) }";
        try {
            Integer resultCode = (Integer) riskDao.jdbcOperatons().execute(callStateMentBaseInfo(callSql, calculateBaseInfos),
                    new CallableStatementCallback() {
                        @Override
                        public Integer doInCallableStatement(CallableStatement cs)
                                throws SQLException, DataAccessException {
                            cs.execute();
                            int returnCode = cs.getInt(2);
                            String message = cs.getString(3);
                            if (StringUtils.isNotBlank(message)) {
                                if (logger.isErrorEnabled()) {
                                    logger.error("批量插入计算结果基本信息失败，" + message);
                                }
                            }
                            return returnCode;
                        }
                    });
            if (resultCode == 0) {
                if (logger.isErrorEnabled()) {
                    logger.error("批量插入计算结果基本信息失败");
                }
                CompletableFuture.runAsync(() -> {
                    sendCalculateBaseInfos(calculateBaseInfos, false);
                }, executorService);
                return;
            }
            CompletableFuture.runAsync(() -> {
                sendCalculateBaseInfos(calculateBaseInfos, true);
            }, executorService);
        } catch (SQLException e) {
            if (logger.isErrorEnabled()) {
                logger.error("批量插入计算结果基本信息失败，" + e, e);
            }
            CompletableFuture.runAsync(() -> {
                sendCalculateBaseInfos(calculateBaseInfos, false);
            }, executorService);
        }
    }


    private CallableStatementCreator callStateMentBaseInfo(String callSql, List<EntryCalculateBaseInfo> calculateBaseInfos) throws SQLException {
        return new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection conn) throws SQLException {
                CallableStatement cstmt = conn.prepareCall(callSql);
                StructDescriptor recDesc = StructDescriptor.createDescriptor("RISKRESULT_STATIC_MID_BASE_OBJ", conn);
                ArrayList<STRUCT> pstruct = new ArrayList<STRUCT>();
                for (EntryCalculateBaseInfo baseInfo : calculateBaseInfos) {
                    Object[] objs = new Object[10];
                    objs[0] = baseInfo.getResultId();
                    objs[1] = baseInfo.getRiskId();
                    objs[2] = baseInfo.getRiskDescription();
                    objs[3] = baseInfo.getCalculateDate();
                    objs[4] = baseInfo.getCalculateTime();
                    objs[5] = baseInfo.getDeclareType();
                    objs[6] = baseInfo.getResultUnit();
                    objs[7] = baseInfo.getCmpareBs();
                    objs[8] = baseInfo.getFundId();
                    objs[9] = baseInfo.getVcStockCode();
                    STRUCT struct = new STRUCT(recDesc, conn, objs);
                    pstruct.add(struct);
                }
                ArrayDescriptor tabDesc = ArrayDescriptor.createDescriptor("STATIC_MID_BASE_ARRAY", conn);
                ARRAY vArray = new ARRAY(tabDesc, conn, pstruct.toArray());
                cstmt.setArray(1, vArray);
                cstmt.registerOutParameter(2, Types.INTEGER);
                cstmt.registerOutParameter(3, Types.VARCHAR);
                return cstmt;
            }
        };
    }

    private CallableStatementCreator callStateMentResultInfo(String callSql, List<EntryCalculateResult> calculateResults) throws SQLException {
        return new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection conn) throws SQLException {
                CallableStatement cstmt = conn.prepareCall(callSql);
                StructDescriptor recDesc = StructDescriptor.createDescriptor("RISK_STATIC_MID_RESULT_OBJ", conn);
                ArrayList<STRUCT> pstruct = new ArrayList<STRUCT>();
                for (EntryCalculateResult result : calculateResults) {
                    Object[] objs = new Object[6];
                    objs[0] = result.getResultKey();
                    objs[1] = result.getThresholdValue();//TODO
                    objs[2] = result.getCalculateResult();
                    objs[3] = result.getMoleculeResult();
                    objs[4] = result.getDenominatorResult();
                    objs[5] = result.getThresholdType().getTypeCode();
                    STRUCT struct = new STRUCT(recDesc, conn, objs);
                    pstruct.add(struct);
                }
                ArrayDescriptor tabDesc = ArrayDescriptor.createDescriptor("STATIC_MID_RESULT_ARRAY", conn);
                ARRAY vArray = new ARRAY(tabDesc, conn, pstruct.toArray());
                cstmt.setArray(1, vArray);
                cstmt.registerOutParameter(2, Types.INTEGER);
                cstmt.registerOutParameter(3, Types.VARCHAR);
                return cstmt;
            }
        };
    }

}
