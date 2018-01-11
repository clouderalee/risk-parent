package com.cangoonline.disruptor.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.cangoonline.disruptor.event.MessageEvent;
import com.cangoonline.disruptor.handler.MessageExceptionHandler;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class DisruptorService {
	private static int poolSize = 10;
	private static int ringBufferSize = 1024 * 1024;

	private ProducerType producerType = ProducerType.SINGLE;
	private WaitStrategy waitStrategy = new YieldingWaitStrategy();
	private ExceptionHandler<MessageEvent> exceptionHandler = new MessageExceptionHandler();
	private ExecutorService executor = Executors.newFixedThreadPool(poolSize);
	private RingBuffer<MessageEvent> ringBuffer;
	private Disruptor<MessageEvent> disruptor;

	private static final EventFactory<MessageEvent> eventFactory = new EventFactory<MessageEvent>(){
		@Override
		public MessageEvent newInstance() {
			return new MessageEvent();
		}
	};

	private boolean isStart;
	private boolean isInit;

	public DisruptorService() {

	}

	public synchronized void initService(){
		if(!isInit){
			if(executor==null){
				executor = Executors.newFixedThreadPool(poolSize);
			}
			if(disruptor==null){
				disruptor = new Disruptor<MessageEvent>(eventFactory, ringBufferSize, executor, producerType, waitStrategy);
				disruptor.setDefaultExceptionHandler(exceptionHandler);
				ringBuffer = disruptor.getRingBuffer();
				isInit = true;
			}
		}
	}

	public synchronized void startService(){
		if(!isInit){
			System.out.println("disruptor Service 没有初始化，无法启动！");
		}
		if(!isStart){
			ringBuffer = disruptor.start();
			isStart = true;
			System.out.println("disruptor Service 启动完成！");
		}
	}

	/**
	 * 10秒后结束运行
	 */
	public void stopService(){
		stopService(10);
	}

	/**
	 * 延迟停止服务
	 * @param timeout （单位秒）
	 */
	public synchronized void stopService(int timeout){
		if(disruptor!=null){
			try {
				System.out.println(timeout +"秒后即将关闭disruptor服务...");
				disruptor.shutdown(timeout, TimeUnit.SECONDS);
				System.out.println("disruptor Service 关闭完成！");
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
		}
		if(executor!=null&&!executor.isShutdown()){
			executor.shutdown();
			System.out.println("executor Service 关闭完成！");
		}
	}

	public RingBuffer<MessageEvent> getRingBuffer() {
		return ringBuffer;
	}

	public Disruptor<MessageEvent> getDisruptor() {
		return disruptor;
	}

	public boolean isStart() {
		return isStart;
	}

	public boolean isInit() {
		return isInit;
	}

	public static int getPoolSize() {
		return poolSize;
	}

	public static void setPoolSize(int poolSize) {
		DisruptorService.poolSize = poolSize;
	}

	public static int getRingBufferSize() {
		return ringBufferSize;
	}

	public static void setRingBufferSize(int ringBufferSize) {
		DisruptorService.ringBufferSize = ringBufferSize;
	}

	public ProducerType getProducerType() {
		return producerType;
	}

	public void setProducerType(ProducerType producerType) {
		this.producerType = producerType;
	}

	public WaitStrategy getWaitStrategy() {
		return waitStrategy;
	}

	public void setWaitStrategy(WaitStrategy waitStrategy) {
		this.waitStrategy = waitStrategy;
	}

	public ExceptionHandler<MessageEvent> getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(ExceptionHandler<MessageEvent> exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public static EventFactory<MessageEvent> getEventFactory() {
		return eventFactory;
	}
}
