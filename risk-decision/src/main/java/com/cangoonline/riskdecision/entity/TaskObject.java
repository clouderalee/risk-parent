package com.cangoonline.riskdecision.entity;

import java.util.Date;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
public class TaskObject {
    private String id;
    private String transactionNo;
    private String businessNo;
    private String serviceCode;
    private String channel;
    private int taskStatus;
    private int consumeTimes;
    private int publishTimes;
    private Date inputDate;
    private Date updateDate;
    private String resultCode;
    private String resultMessage;
    private int taskType;

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

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getConsumeTimes() {
        return consumeTimes;
    }

    public void setConsumeTimes(int consumeTimes) {
        this.consumeTimes = consumeTimes;
    }

    public int getPublishTimes() {
        return publishTimes;
    }

    public void setPublishTimes(int publishTimes) {
        this.publishTimes = publishTimes;
    }

    public Date getInputDate() {
        return inputDate;
    }

    public void setInputDate(Date inputDate) {
        this.inputDate = inputDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }
}
