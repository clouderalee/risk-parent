package com.cangoonline.riskdecision.controller;

import com.alibaba.fastjson.JSONObject;
import com.cangoonline.risk.entity.ResponseJson;
import com.cangoonline.risk.service.EmailService;
import com.cangoonline.riskdecision.common.TaskConstants;
import com.cangoonline.riskdecision.mapper.TaskMapper;
import com.cangoonline.riskdecision.mapper.dto.ResultNotifyDataDto;
import com.cangoonline.riskdecision.message.consumer.RiskDecisionTaskConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
@RestController
public class ResultNotifyController {
    private static Logger logger = LoggerFactory.getLogger(ResultNotifyController.class);
    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private EmailService emailService;

    @ResponseBody
    @RequestMapping(value = "/decisionNotify/{transcationNo}",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public String notifyDecisionResult(@PathVariable String transcationNo){
        ResponseJson responseJson = new ResponseJson();
        //查询是否存在这笔交易
        ResultNotifyDataDto result = taskMapper.getResultNotifyDataDtoByTranscationNo(transcationNo);

        if(result==null){
            return responseJson.setError(405,"交易不存在").toString();
        }

        //再次通知
        String data = result.getData();
        String notifyUrl = result.getNotifyAddress();

        try{
            // TODO 结果通知接口 - 邮件代替
            emailService.sendMail("结果通知",data,notifyUrl);

            taskMapper.updateTaskStatusById(result.getId(), TaskConstants.NOTIFY_SUCCESS);
            logger.info("任务[{}]重新结果通知成功...",result.getId());
        }catch (Exception e){
            logger.info("任务[{}]重新结果通知失败...",result.getId());
            e.printStackTrace();
            responseJson = responseJson.setError(405,"主动回调通知失败");
        }
        return responseJson.toString();

    }

    @ResponseBody
    @RequestMapping(value = "/decisionResult/{transcationNo}",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public String getDecisionResult(@PathVariable String transcationNo){
        ResponseJson responseJson = new ResponseJson();
        //查询是否存在这笔交易
        ResultNotifyDataDto result = taskMapper.getResultNotifyDataDtoByTranscationNo(transcationNo);
        if(result==null){
            return responseJson.setError(405,"交易不存在").toString();
        }else{
            return result.getData();
        }
    }
}
