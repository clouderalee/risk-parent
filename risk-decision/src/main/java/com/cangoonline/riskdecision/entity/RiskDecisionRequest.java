package com.cangoonline.riskdecision.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by cango on 2017/11/14.
 */
public class RiskDecisionRequest implements Serializable {
    private String id;
    private String transactionNo;
    private String businessNo;
    private String serviceCode;
    private String channel;
    private String requestJson;
    private String responseJson;
    private String code;
    private String message;
    private Date requestDate;
    private Date responseDate;
    private String requestUri;
    private String requestIp;
    private long consumeTime;

    public RiskDecisionRequest(){}
    public RiskDecisionRequest(String transactionNo, String businessNo, String serviceCode, String channel,
                               String requestJson, Date requestDate, String requestUri, String requestIp) {
        this.transactionNo = transactionNo;
        this.businessNo = businessNo;
        this.serviceCode = serviceCode;
        this.channel = channel;
        this.requestJson = requestJson;
        this.requestDate = requestDate;
        this.requestUri = requestUri;
        this.requestIp = requestIp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }

    public String getResponseJson() {
        return responseJson;
    }

    public void setResponseJson(String responseJson) {
        this.responseJson = responseJson;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public long getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(long consumeTime) {
        this.consumeTime = consumeTime;
    }
}
