package com.cangoonline.riskdecision.service.impl;

import com.cangoonline.risk.common.CodeException;
import com.cangoonline.risk.common.DBKeyHelper;
import com.cangoonline.riskdecision.mapper.RequestLogMapper;
import com.cangoonline.riskdecision.mapper.TaskMapper;
import com.cangoonline.riskdecision.service.MapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 处理数据库
 * Created by Administrator on 2017\12\11 0011.
 */
@Service
public class MapperServiceImpl implements MapperService{

    @Autowired
    private RequestLogMapper requestLogMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private DBKeyHelper dbKeyHelper;


    public void checkAsyncApplyTranscationNo() throws CodeException {

    }

    public void checkSyncApplyTranscationNo() throws CodeException {

    }

    public String getRiskDecisionRequestLogId() throws CodeException {
        return getSerialnoId("t_crd_apply_log","id","R");
    }

    public String getRiskDecisionTaskObjectId() throws CodeException {
        return getSerialnoId("t_crd_task","id","T");
    }

    private String getSerialnoId(String tableName,String colName,String sPrefix) throws CodeException {
        String serialNo = null;
        try{
            serialNo = dbKeyHelper.getSerialNo(tableName,colName,sPrefix);
        }catch (Exception e){
            e.printStackTrace();
            CodeException.throwException(405,"服务器错误");
        }
        return serialNo;
    }
}
