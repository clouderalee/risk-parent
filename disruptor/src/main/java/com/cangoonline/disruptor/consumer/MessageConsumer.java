package com.cangoonline.disruptor.consumer;

import com.cangoonline.disruptor.event.MessageEvent;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.LifecycleAware;
import com.lmax.disruptor.WorkHandler;

public abstract class MessageConsumer implements LifecycleAware,EventHandler<MessageEvent>,WorkHandler<MessageEvent>{
	
	public abstract void consume(MessageEvent messageEvent) throws Exception;
	
	public void onEvent(MessageEvent messageEvent) throws Exception {
		consume(messageEvent);
	}
	public void onEvent(MessageEvent messageEvent, long sequence, boolean endOfBatch) throws Exception {
		onEvent(messageEvent);
	}
}
