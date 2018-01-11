package com.cangoonline.disruptor.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.cangoonline.disruptor.event.MessageEvent;
import com.lmax.disruptor.ExceptionHandler;

public class MessageExceptionHandler implements ExceptionHandler<MessageEvent>{
	private static Logger logger = Logger.getLogger(MessageExceptionHandler.class.getName());
	
	@Override
	public void handleEventException(Throwable e, long sequence, MessageEvent messageEvent) {
		this.logger.log(Level.INFO, "MessageExceptionHandler processing: " + sequence + " " + messageEvent, e);
	}

	@Override
	public void handleOnStartException(Throwable e) {
		this.logger.log(Level.INFO, "Exception during onStart()", e);
	}

	@Override
	public void handleOnShutdownException(Throwable e) {
		this.logger.log(Level.INFO, "Exception during onShutdown()", e);
	}

}
