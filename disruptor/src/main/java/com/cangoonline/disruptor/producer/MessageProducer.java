package com.cangoonline.disruptor.producer;

import java.util.Map;

import com.cangoonline.disruptor.event.MessageEvent;
import com.cangoonline.disruptor.model.Message;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public class MessageProducer {
	private RingBuffer<MessageEvent> ringBuffer;

	private static final EventTranslatorOneArg<MessageEvent, Message> TRANSLATOR_ONE = new EventTranslatorOneArg<MessageEvent, Message>() {
		@Override
		public void translateTo(MessageEvent event, long sequence, Message message) {
			event.setMessage(message);
		}
	};
	
	private static final EventTranslatorTwoArg<MessageEvent, Message , Map<String,Object>> TRANSLATOR_TOW = new EventTranslatorTwoArg<MessageEvent, Message , Map<String,Object>>() {
		@Override
		public void translateTo(MessageEvent event, long paramLong, Message message, Map<String,Object> messageProperties) {
			event.setMessage(message);
			event.setMessageProperties(messageProperties);
		}
	};
	
	public MessageProducer() {
	}
	public MessageProducer(RingBuffer<MessageEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}
	public MessageProducer(Disruptor<MessageEvent> disruptor) {
		this.ringBuffer = disruptor.getRingBuffer();
	}
	
	public void publish(Message message) {
		checkMessage(message);
		ringBuffer.publishEvent(TRANSLATOR_ONE, message);
	}
	public void publish(Message message , Map<String,Object> messageProperties) {
		checkMessage(message);
		ringBuffer.publishEvent(TRANSLATOR_TOW, message , messageProperties);
	}
	
	private void checkMessage(Message message){
		if(isEmpty(message.getMessageType())
				||isEmpty(message.getBody())
				||isEmpty(message.getMessageId())){
			throw new RuntimeException("message["+message+"] is Invalid");
		}
	}
	
	private boolean isEmpty(Object obj){
		return obj==null||"".equals(obj);
	}
	
	
}
