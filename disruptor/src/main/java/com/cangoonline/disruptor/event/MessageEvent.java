package com.cangoonline.disruptor.event;

import java.util.Map;

import com.cangoonline.disruptor.model.Message;

public class MessageEvent {
	private Message message;
	
	private Map<String,Object> messageProperties;
	
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	public Map<String, Object> getMessageProperties() {
		return messageProperties;
	}
	public void setMessageProperties(Map<String, Object> messageProperties) {
		this.messageProperties = messageProperties;
	}
}
