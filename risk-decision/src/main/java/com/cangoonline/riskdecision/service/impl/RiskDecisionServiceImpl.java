package com.cangoonline.riskdecision.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cangoonline.disruptor.model.Message;
import com.cangoonline.risk.common.CodeException;
import com.cangoonline.risk.entity.ResponseJson;
import com.cangoonline.riskdecision.common.Constants;
import com.cangoonline.riskdecision.common.TaskConstants;
import com.cangoonline.riskdecision.entity.RelativeData;
import com.cangoonline.riskdecision.entity.RiskDecisionRequest;
import com.cangoonline.riskdecision.entity.TaskObject;
import com.cangoonline.riskdecision.mapper.RequestLogMapper;
import com.cangoonline.riskdecision.mapper.TaskMapper;
import com.cangoonline.riskdecision.message.consumer.RiskDecisionTaskConsumer;
import com.cangoonline.riskdecision.service.MapperService;
import com.cangoonline.riskdecision.service.RiskDecisionService;
import com.cangoonline.riskdecision.service.local.MessageProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
@Service
public class RiskDecisionServiceImpl implements RiskDecisionService{
    private static Logger logger = LoggerFactory.getLogger(RiskDecisionServiceImpl.class);
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
    @Autowired
    private RequestLogMapper requestLogMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private MapperService mapperService;
    @Autowired
    private MessageProducerService messageProducerService;

    public ResponseJson asyncApply(RiskDecisionRequest request) {


        ResponseJson responseJson = new ResponseJson();
        try{

            //基本校验通过后，申请受理预成功 （保存申请日志）
            request.setCode("0");
            request.setMessage("ok");
            request.setResponseDate(new Date());
            request.setId(mapperService.getRiskDecisionRequestLogId());
            requestLogMapper.saveRiskDecisionRequest(request);

            //新建消息任务对象并且保存
            TaskObject taskObject = getInitTaskObject(request);

            //TODO 检查服务开关？？？
            taskObject.setTaskStatus(TaskConstants.PUBLISH_SUCCESS);
            taskObject.setPublishTimes(taskObject.getPublishTimes()+1);

            RelativeData relativeData = getInitRelativeTaskData(taskObject,request);
            taskMapper.saveTaskObject(taskObject);
            taskMapper.saveRelativeData(relativeData);

            //发布数据消息到工作者队列中，并且更新任务状态
            publishMessage(taskObject);

        }catch (Exception e){
            //TODO 目前只存在数据库类型异常
            e.printStackTrace();
            responseJson = ResponseJson.setException(e,405);
        }

        //body中写入交易流水号
        responseJson.setBody(request.getTransactionNo());
        return responseJson;
    }

    /**
     * 发布消息的处理逻辑
     * @param taskObject
     */
    private void publishMessage(TaskObject taskObject) throws Exception {
        String taskId = taskObject.getId();
        try{
            Message message = new Message();
            message.setBody(taskId);
            message.setMessageType(String.valueOf(TaskConstants.TASK_TYPE_TASK));
            messageProducerService.publish(message);
            logger.info("任务[{}]发布成功...",taskId);
        }catch(Exception e){
            logger.info("任务[{}]发布失败...",taskId);
            e.printStackTrace();
            //TODO 发布失败，进行回滚状态
            taskMapper.updateTaskStatusById(taskId,TaskConstants.PUBLISH_FAILURE);
            throw e;
        }

    }

    public ResponseJson syncApply(RiskDecisionRequest request) {
        ResponseJson responseJson = new ResponseJson();
        Map<String,Object> body = new HashMap();
        try{

            //基本校验通过后，申请受理预成功 （保存申请日志）
            request.setCode("0");
            request.setMessage("ok");
            request.setResponseDate(new Date());
            request.setId(mapperService.getRiskDecisionRequestLogId());
            requestLogMapper.saveRiskDecisionRequest(request);

            //调用模型,把结果数据放到body中

        }catch (Exception e){
            //TODO 目前只存在数据库类型异常
            e.printStackTrace();
            responseJson = ResponseJson.setException(e,405);
        }

        //填充请求头数据
        body.put("transactionNo",request.getTransactionNo());
        body.put("businessNo",request.getBusinessNo());
        body.put("requestDate",sf.format(new Date()));
        responseJson.setBody(body);

        //更新日志数据
        request.setResponseDate(new Date());
        request.setCode(String.valueOf(responseJson.getCode()));
        request.setMessage(responseJson.getMessage());
        request.setResponseJson(JSONObject.toJSONString(body));
        requestLogMapper.updateRiskDecisionRequest(request);

        return responseJson;
    }


    /*
     * 获取初始化状态的RelativeData
     */
    private RelativeData getInitRelativeTaskData(TaskObject taskObject,RiskDecisionRequest request) {
        RelativeData relativeData = new RelativeData();
        relativeData.setRelative(taskObject.getId());
        relativeData.setType(TaskConstants.DATA_TYPE_RISK_DECISION_APPLY);
        JSONObject jsonObject = JSON.parseObject(request.getRequestJson());
        relativeData.setData(jsonObject.getJSONObject(Constants.REQUEST_BODY_KEY).toJSONString());
        return relativeData;
    }

    /*
     * 获取初始化状态的TaskObject
     */
    private TaskObject getInitTaskObject(RiskDecisionRequest request) throws CodeException {
        TaskObject task = new TaskObject();
        task.setId(mapperService.getRiskDecisionTaskObjectId());
        task.setBusinessNo(request.getBusinessNo());
        task.setTransactionNo(request.getTransactionNo());
        task.setServiceCode(request.getServiceCode());
        task.setChannel(request.getChannel());
        task.setInputDate(new Date());
        task.setUpdateDate(new Date());
        task.setPublishTimes(0);
        task.setConsumeTimes(0);
        task.setTaskStatus(0);
        task.setTaskType(TaskConstants.TASK_TYPE_TASK);
        return task;
    }
}
