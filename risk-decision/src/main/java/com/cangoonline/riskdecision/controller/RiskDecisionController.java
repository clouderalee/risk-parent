package com.cangoonline.riskdecision.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cangoonline.risk.common.CodeException;
import com.cangoonline.risk.entity.ResponseJson;
import com.cangoonline.risk.utils.IPUtils;
import com.cangoonline.risk.utils.ResponseUtils;
import com.cangoonline.riskdecision.entity.RiskDecisionRequest;
import com.cangoonline.riskdecision.service.MapperService;
import com.cangoonline.riskdecision.service.RiskDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
@Controller
public class RiskDecisionController {

    @Autowired
    private RiskDecisionService riskDecisionService;

    @Autowired
    private MapperService mapperService;

    @ResponseBody
    @RequestMapping(value="index/{context}",method = RequestMethod.GET)
    public String index(@PathVariable String context){
        StringBuffer sb = new StringBuffer();
        sb.append("<center>");
        sb.append("<h3>");
        sb.append(context);
        sb.append("</h3>");
        sb.append("</center>");
        return sb.toString();
    }

    @RequestMapping(value = "asyncApply", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void asyncApply(HttpServletRequest request,HttpServletResponse response,
                           @RequestParam("requestBody") String requestBody,
                           @RequestParam("serviceCode") String serviceCode,
                           @RequestParam("channel") String channel,
                           @RequestParam("transactionNo") String transactionNo,
                           @RequestParam("businessNo") String businessNo, String requestDate) {

        try{

            //基本参数检查
            Map allParam = applyFieldCheck(requestBody, serviceCode, channel, transactionNo, businessNo, requestDate);
            //交易流水号
            mapperService.checkSyncApplyTranscationNo();
            //基本的数据检查成功以后开始做业务处理
            RiskDecisionRequest decisionRequest = new RiskDecisionRequest(transactionNo,businessNo,serviceCode,channel,
                    JSON.toJSONString(allParam),new Date(),request.getRequestURI() ,IPUtils.getIP(request));
            ResponseUtils.doResponse(request,response,riskDecisionService.asyncApply(decisionRequest));
        }catch (Exception e){
            processException(request,response,e);
        }

    }


    @RequestMapping(value = "syncApply", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void syncApply(HttpServletRequest request,HttpServletResponse response,
                          @RequestParam("requestBody") String requestBody,
                          @RequestParam("serviceCode") String serviceCode,
                          @RequestParam("channel") String channel,
                          @RequestParam("transactionNo") String transactionNo,
                          @RequestParam("businessNo") String businessNo, String requestDate) {

        try{

            //基本参数检查
            Map allParam = applyFieldCheck(requestBody, serviceCode, channel, transactionNo, businessNo, requestDate);
            //交易流水号
            mapperService.checkSyncApplyTranscationNo();
            //基本的数据检查成功以后开始做业务处理
            RiskDecisionRequest decisionRequest = new RiskDecisionRequest(transactionNo,businessNo,serviceCode,channel,
                    JSON.toJSONString(allParam),new Date(),request.getRequestURI() ,IPUtils.getIP(request));
            ResponseUtils.doResponse(request,response,riskDecisionService.syncApply(decisionRequest));
        }catch (Exception e){
            processException(request,response,e);
        }

    }

    private Map applyFieldCheck(String requestBody,String serviceCode, String channel,String transactionNo, String businessNo, String requestDate)
            throws CodeException {
        Map allParam = new HashMap();
        allParam.put("transactionNo",transactionNo);
        allParam.put("businessNo",businessNo);
        allParam.put("serviceCode",serviceCode);
        allParam.put("channel",channel);
        allParam.put("requestDate",requestDate);

        //校验requestBody是否满足json格式
        try{
            JSONObject requestBodyJson = JSON.parseObject(requestBody);
            allParam.put("requestBody",requestBodyJson);
        }catch (Exception e){
            throw new CodeException(403,"请求体格式异常");
        }

        return allParam;
    }

    private void processException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        ResponseUtils.doResponse(request,response,ResponseJson.setException(e,405));
    }


}
