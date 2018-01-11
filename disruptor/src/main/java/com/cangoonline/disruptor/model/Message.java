package com.cangoonline.disruptor.model;

import java.util.concurrent.atomic.AtomicLong;

public class Message implements  java.io.Serializable , Cloneable{
	private static final long serialVersionUID = -4273336616393916679L;
	
	private String messageId;
	private String messageType;
	private int code = 0;
	private String message = "ok";
	private Object body;
	
	private AtomicLong messageSequence = new AtomicLong(0);
	
	public Message() {
		messageSequence.incrementAndGet();
		setMessageId(generateMessageId());
	}
	
	public Message(String messageType, Object body) {
		this();
		this.messageType = messageType;
		this.body = body;
	}

	public Message(Object body) {
		this(null,body);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}
	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	
	public AtomicLong getMessageSequence() {
		return messageSequence;
	}

	@Override
	public String toString() {
		return "Message [messageId=" + messageId + ", messageType=" + messageType + ", code=" + code + ", message="
				+ message + ", body=" + body + ", messageSequence=" + messageSequence.get() + "]";
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
		} return null;
	}
	
	private String generateMessageId(){
		return java.util.UUID.randomUUID().toString().replace("-", "");
	}
	
}
