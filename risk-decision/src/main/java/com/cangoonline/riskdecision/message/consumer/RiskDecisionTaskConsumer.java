package com.cangoonline.riskdecision.message.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cangoonline.disruptor.consumer.MessageConsumer;
import com.cangoonline.disruptor.event.MessageEvent;
import com.cangoonline.disruptor.model.Message;
import com.cangoonline.engineflow.EngineHelper;
import com.cangoonline.engineflow.ExcuteLogProcessor;
import com.cangoonline.engineflow.ModelActuator;
import com.cangoonline.engineflow.VariableProcessor;
import com.cangoonline.engineflow.bean.ExcuteLogObject;
import com.cangoonline.engineflow.bean.FlowObject;
import com.cangoonline.engineflow.bean.ServiceObject;
import com.cangoonline.risk.common.CodeException;
import com.cangoonline.risk.entity.ResponseJson;
import com.cangoonline.risk.service.EmailService;
import com.cangoonline.riskdecision.common.TaskConstants;
import com.cangoonline.riskdecision.entity.RelativeData;
import com.cangoonline.riskdecision.entity.TaskObject;
import com.cangoonline.riskdecision.mapper.TaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
public class RiskDecisionTaskConsumer extends MessageConsumer {
    private static Logger logger = LoggerFactory.getLogger(RiskDecisionTaskConsumer.class);
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private EmailService emailService;
    private int maxNotifyTimes;

    public void consume(MessageEvent messageEvent) throws Exception {
        Message message = messageEvent.getMessage();

        if(!TaskConstants.TASK_TYPE_TASK_S.equals(message.getMessageType())){
            logger.warn("RiskDecisionTaskConsumer 无法处理[{}]类型的消息事件！",TaskConstants.TASK_TYPE_TASK);
            return ;
        }

        Object body = message.getBody();
        String taskId = null;
        if(body instanceof String){
            taskId = String.valueOf(body);
        } else if(body instanceof TaskObject){
            TaskObject temp = (TaskObject) body;
            taskId = temp.getId();
        }else if(body instanceof byte[]){
            taskId = new String((byte[])body);
        }else{
            throw new Exception("无法识别的消息事件:"+messageEvent);
        }

        //通过taskId从数据库中获取TaskObject
        TaskObject taskObject = taskMapper.getTaskObjectById(taskId);
        if(TaskConstants.PUBLISH_SUCCESS == taskObject.getTaskStatus()){
            doTask(taskObject);
        }else{
            logger.warn("RiskDecisionTaskConsumer消费时，发现该任务[{}]的任务状态[{}]非法，请检查...",
                    taskObject.getId(),taskObject.getTaskStatus());
            return ;
        }

    }

    private void doTask(TaskObject taskObject) {
        Map<String, Object> modelResult = null;
        try {
            modelResult = processTask(taskObject);
            logger.info("任务[{}]处理成功...", taskObject.getId());
        } catch (Exception e) {
            logger.info("任务[{}]处理失败...", taskObject.getId());
            e.printStackTrace();
            ResponseJson responseJson = ResponseJson.setException(e, 405);
            taskObject.setResultCode(String.valueOf(responseJson.getCode()));
            taskObject.setResultMessage(responseJson.getMessage());
        }

        taskObject.setUpdateDate(new Date());
        taskObject.setConsumeTimes(taskObject.getConsumeTimes() + 1);
        taskObject.setTaskType(TaskConstants.HANDLE_COMPLETE);
        taskMapper.updateTaskObject(taskObject);

        //5.结果通知
        String notifyAddress = taskMapper.getNotifyAddress(taskObject.getChannel(), taskObject.getServiceCode());
        resultNotify(taskObject, modelResult, notifyAddress);
        logger.info("任务[{}]消费完成...", taskObject.getId());
    }

    private Map<String,Object> processTask(TaskObject taskObject) throws Exception{
        String taskId = taskObject.getId();

        //1.数据检查完毕，更新任务状态为处理中，开始处理任务
        taskMapper.updateTaskStatusById(taskId,TaskConstants.HANDLING);

        //2.解析请求数据
        RelativeData relativeData = taskMapper.getRiskDecisionTaskData(taskId);
        JSONObject taskDataJson = JSON.parseObject(relativeData.getData());

        //3.根据taskDataJson数据，进行衍生变量加工处理
        Map<String,Object> modelInputParams = descendableVariableProcessor(taskObject,taskDataJson);

        //4.拿到全部变量之后，送入引擎程序进行计算
        Map<String,Object> modelResult = modelEngineProcessor(taskObject,modelInputParams);

        return modelResult;

    }
    /*
         * 获取初始化状态的RelativeData
         */
    private RelativeData getInitRelativeTaskData(TaskObject taskObject,String jsonData) {
        RelativeData relativeData = new RelativeData();
        relativeData.setRelative(taskObject.getId());
        relativeData.setType(TaskConstants.DATA_TYPE_RISK_DECISION_APPLY_RESULT);
        relativeData.setData(jsonData);
        return relativeData;
    }

    /**
     * 结果通知接口
     * @param taskObject
     * @param modelResult
     * @param notifyUrl
     */
    private void resultNotify(TaskObject taskObject, Map<String, Object> modelResult, String notifyUrl) {
        //组装结果通知报文数据
        Map<String, Object> body = new HashMap();
        body.putAll(modelResult);
        body.put("transactionNo",taskObject.getTransactionNo());
        body.put("businessNo",taskObject.getBusinessNo());
        body.put("requestDate",sf.format(new Date()));

        JSONObject json = new JSONObject();
        json.put("code",Integer.parseInt(taskObject.getResultCode()));
        json.put("message",taskObject.getResultMessage());
        json.put("body",body);

        //将结果通知报文存到数据库
        RelativeData relativeTaskResultData = getInitRelativeTaskData(taskObject, json.toJSONString());
        taskMapper.saveRelativeData(relativeTaskResultData);

        //循环通知

        int currentIndex = 1;
        while(currentIndex <= getMaxNotifyTimes()){
            currentIndex ++;
            try{

                // TODO 结果通知接口调用 notifyUrl
                // TODO 结果通知接口 - 邮件代替
                emailService.sendMail("结果通知",json.toJSONString(),notifyUrl);

                taskMapper.updateTaskStatusById(taskObject.getId(),TaskConstants.NOTIFY_SUCCESS);
                logger.info("任务[{}]结果通知完成...",taskObject.getId());
            }catch (Exception e){
                if(currentIndex > getMaxNotifyTimes()){
                    logger.info("任务[{}]结果通知失败..",taskObject.getId());
                    e.printStackTrace();
                    // TODO 当前结通知失败时，给第三方提供结果获取接口调用（task_data表需要添加类型字段）
                    taskMapper.updateTaskStatusById(taskObject.getId(),TaskConstants.NOTIFY_FAILURE);
                }
            }
        }
    }

    /**
     * 调用引擎程序进行计算
     * @param modelInputParams
     * @param taskObject
     * @return
     */
    private Map<String,Object> modelEngineProcessor(TaskObject taskObject ,Map<String, Object> modelInputParams) throws Exception {
        // TODO 调用引擎程序进行计算
        String serviceCode = taskObject.getServiceCode();
        String channel = taskObject.getChannel();

        if(!EngineHelper.isInit()){
            EngineHelper.initEngineFlow();
        }
        ServiceObject serviceObject = EngineHelper.getEngineService(channel, serviceCode);
        List<FlowObject> flows = serviceObject.getFlows();

        if(flows==null){
            throw  new CodeException(405,"引擎流程配置错误");
        }

        Map<String, Object> finalResult = null;
        for (int i = 0; i <flows.size() ; i++) {

            //finalResult：每一次调用模型的结果集（会覆盖）
            //modelInputParams：会加入前一个模型格式化后的调用的结果
            finalResult =  doEngineFlow(flows.get(i),taskObject,modelInputParams);
            //出现异常时，跳出循环
        }

        return finalResult;
    }

    private Map<String,Object> doEngineFlow(FlowObject flowObject, TaskObject taskObject, Map<String, Object> modelInputParams) throws CodeException {

        ExcuteLogObject excuteLog = new ExcuteLogObject();
        Properties flowProperties = flowObject.getFlowProperties();
        VariableProcessor variableProcessor = flowObject.getFlowVariableProcessor();
        ExcuteLogProcessor excuteLogProcessor = flowObject.getFlowExcuteLogProcessor();
        ModelActuator modelActuator = flowObject.getEngineService();

        //初始化执行日志
        excuteLog.setRelativeObject(taskObject.getId());
        excuteLog.setSeqNo(flowObject.getId());
        excuteLog.setCurrentFlowObject(flowObject);
        excuteLog.setFlowUniqueTag(flowObject.getFlowUniqueTag());
        excuteLog.setFlowInput(JSON.toJSONString(modelInputParams));

        Map<String, Object> executeResult = null;//模型执行的原始数据
        Map<String, Object> formatResultMap = null;//模型执行格式化结果数据
        ResponseJson responseJson = null;
        try {

            //变量处理器-校验参数
            variableProcessor.checkParameter(modelInputParams, taskObject.getId());
            //变量处理器-转换参数（会新增衍生变量等）
            variableProcessor.convertParameter(modelInputParams, taskObject.getId());
            //变量处理器-变量分箱处理（一变多处理）
            Map<String, Object> finalModelInputParams =
                    variableProcessor.binningParameter(modelInputParams, taskObject.getId());

            //传入最终的的数据进行模型调用
            executeResult = modelActuator.execute(finalModelInputParams, flowProperties);
            excuteLog.setFlowOutput(JSON.toJSONString(executeResult));

            //变量处理器-变量分箱处理
            formatResultMap = variableProcessor.formatResult(executeResult, taskObject.getId());
            excuteLog.setFlowOutput(JSON.toJSONString(formatResultMap));

            //是否结束流程-返回结果
            if(!flowObject.isFinishFlow()){
                //模型结果是否作为下一个模型的输入参数
                if(flowObject.isParamInto()){
                    modelInputParams.putAll(formatResultMap);
                }
            }

        } catch (Exception e) {
            logger.error("该任务[{}]在执行[{}]模型执行时未成功...",taskObject.getId(),flowObject.getFlowUniqueTag());
            responseJson = ResponseJson.setException(e, 405, e.getMessage());

            // TODO 抛出异常
            throw new CodeException(responseJson.getCode(),responseJson.getMessage());

        }finally {
            //记录该模型执行的日志信息
            excuteLog.setProcessCode(String.valueOf(responseJson.getCode()));
            excuteLog.setProcessMessage(responseJson.getMessage());
            excuteLog.setEndTime(new Date());
            excuteLogProcessor.processWithOutException(excuteLog);
        }
        return formatResultMap;
    }

    /**
     * 衍生变量加工处理
     * @param taskDataJson
     */
    private Map<String,Object> descendableVariableProcessor(TaskObject taskObject ,JSONObject taskDataJson) throws Exception {
        // TODO 衍生变量加工



        return new HashMap();
    }

    public void onStart() {

    }

    public void onShutdown() {

    }

    public int getMaxNotifyTimes() {
        return maxNotifyTimes;
    }

    public void setMaxNotifyTimes(int maxNotifyTimes) {
        this.maxNotifyTimes = maxNotifyTimes;
    }


}
