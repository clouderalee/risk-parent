package com.cangoonline.engineflow.bean;

import java.util.Date;

public class ExcuteLogObject {
	
	private Object relativeObject;

	private int seqNo;
	
	private FlowObject currentFlowObject;
	
	private String flowUniqueTag;
	
	private String flowInput;
	private String flowOutput;
	private String flowResult;
	
	private String processCode;
	private String processMessage;
	
	private Date startTime;
	private Date endTime;
	
	public ExcuteLogObject() {
		startTime = new Date();
		endTime = new Date();
	}

	public Object getRelativeObject() {
		return relativeObject;
	}

	public void setRelativeObject(Object relativeObject) {
		this.relativeObject = relativeObject;
	}

	public FlowObject getCurrentFlowObject() {
		return currentFlowObject;
	}
	public void setCurrentFlowObject(FlowObject currentFlowObject) {
		this.currentFlowObject = currentFlowObject;
	}
	public String getFlowUniqueTag() {
		return flowUniqueTag;
	}
	public void setFlowUniqueTag(String flowUniqueTag) {
		this.flowUniqueTag = flowUniqueTag;
	}
	public String getFlowInput() {
		return flowInput;
	}
	public void setFlowInput(String flowInput) {
		this.flowInput = flowInput;
	}
	public String getFlowOutput() {
		return flowOutput;
	}
	public void setFlowOutput(String flowOutput) {
		this.flowOutput = flowOutput;
	}
	public String getFlowResult() {
		return flowResult;
	}
	public void setFlowResult(String flowResult) {
		this.flowResult = flowResult;
	}
	public String getProcessCode() {
		return processCode;
	}
	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}
	public String getProcessMessage() {
		return processMessage;
	}
	public void setProcessMessage(String processMessage) {
		this.processMessage = processMessage;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}
}
