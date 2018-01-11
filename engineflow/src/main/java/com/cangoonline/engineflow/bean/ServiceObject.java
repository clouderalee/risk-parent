package com.cangoonline.engineflow.bean;

import java.util.List;

import com.cangoonline.engineflow.ExcuteLogProcessor;
import com.cangoonline.engineflow.VariableProcessor;

public class ServiceObject {
	private String channel;
	private String serviceCode;
	private String name;
	private String describe;
	private List<FlowObject> flows;
	private VariableProcessor serviceVariableProcessor;
	private ExcuteLogProcessor serviceExcuteLogProcessor;
	
	public ServiceObject(String channel, String serviceCode, String name, String describe) {
		this.channel = channel;
		this.serviceCode = serviceCode;
		this.name = name;
		this.describe = describe;
	}
	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public VariableProcessor getServiceVariableProcessor() {
		return serviceVariableProcessor;
	}
	public void setServiceVariableProcessor(VariableProcessor serviceVariableProcessor) {
		this.serviceVariableProcessor = serviceVariableProcessor;
	}
	public List<FlowObject> getFlows() {
		return flows;
	}

	public ExcuteLogProcessor getServiceExcuteLogProcessor() {
		return serviceExcuteLogProcessor;
	}
	public void setServiceExcuteLogProcessor(ExcuteLogProcessor serviceExcuteLogProcessor) {
		this.serviceExcuteLogProcessor = serviceExcuteLogProcessor;
	}
	public void setFlows(List<FlowObject> flows) {
		this.flows = flows;
		for (FlowObject flowObject : flows) {
			flowObject.setServiceObject(this);
		}
	}
}
