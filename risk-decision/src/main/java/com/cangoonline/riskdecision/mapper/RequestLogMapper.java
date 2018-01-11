package com.cangoonline.riskdecision.mapper;

import com.cangoonline.riskdecision.entity.RiskDecisionRequest;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
public interface RequestLogMapper {

    void saveRiskDecisionRequest(RiskDecisionRequest request);

    int updateRiskDecisionRequest(RiskDecisionRequest request);


}
