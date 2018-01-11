package com.cangoonline.riskdecision.service;

import com.cangoonline.risk.common.CodeException;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
public interface MapperService {

    void checkAsyncApplyTranscationNo() throws CodeException;
    void checkSyncApplyTranscationNo() throws CodeException;

    String getRiskDecisionRequestLogId() throws CodeException;
    String getRiskDecisionTaskObjectId() throws CodeException;

}
