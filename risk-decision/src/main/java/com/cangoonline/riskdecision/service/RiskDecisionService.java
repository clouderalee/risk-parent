package com.cangoonline.riskdecision.service;

import com.cangoonline.risk.entity.ResponseJson;
import com.cangoonline.riskdecision.entity.RiskDecisionRequest;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
public interface RiskDecisionService {

    ResponseJson asyncApply(RiskDecisionRequest request);

    ResponseJson syncApply(RiskDecisionRequest request);
}
