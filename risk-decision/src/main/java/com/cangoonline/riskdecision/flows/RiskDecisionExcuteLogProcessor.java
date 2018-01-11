package com.cangoonline.riskdecision.flows;

import com.cangoonline.engineflow.ExcuteLogProcessor;
import com.cangoonline.engineflow.bean.ExcuteLogObject;
import com.cangoonline.riskdecision.entity.RiskModelExcuteLog;
import com.cangoonline.riskdecision.mapper.ExcuteLogMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
public class RiskDecisionExcuteLogProcessor implements ExcuteLogProcessor {

    @Autowired
    private ExcuteLogMapper excuteLogMapper;

    private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public void process(ExcuteLogObject excuteLog) throws Exception {

        RiskModelExcuteLog log = new RiskModelExcuteLog();
        log.setInputDate(sf.format(excuteLog.getStartTime()));
        log.setRelativeId(excuteLog.getRelativeObject().toString());
        log.setSeqNo(excuteLog.getSeqNo());
        log.setSetpName(excuteLog.getFlowUniqueTag());
        log.setInput(excuteLog.getFlowInput());
        log.setOutput(excuteLog.getFlowOutput());
        log.setResult(excuteLog.getFlowResult());
        log.setCode(excuteLog.getProcessCode());
        log.setMessage(excuteLog.getProcessMessage());
        log.setUpdateDate(sf.format(new Date()));

        //保存日志
        excuteLogMapper.saveRiskModelExcuteLog(log);
    }

    public void processWithOutException(ExcuteLogObject excuteLog) {
        try {
            process(excuteLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
