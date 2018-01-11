package com.cangoonline.disruptor.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cangoonline.disruptor.event.MessageEvent;
import com.cangoonline.disruptor.handler.MessageExceptionHandler;
import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

public class BatchProcessorService {

	private static int poolSize = 10;
	private static int ringBufferSize = 1024 * 1024;

	private ProducerType producerType = ProducerType.SINGLE;
	private WaitStrategy waitStrategy = new YieldingWaitStrategy();
	private ExceptionHandler<MessageEvent> exceptionHandler = new MessageExceptionHandler();
	private List<EventHandler<MessageEvent>> handlerList = new ArrayList();
	private List<BatchEventProcessor<MessageEvent>> batchProcessorList =  new ArrayList();
	private RingBuffer<MessageEvent> ringBuffer;
	private ExecutorService executor;

	private static final EventFactory<MessageEvent> eventFactory = new EventFactory<MessageEvent>(){
		@Override
		public MessageEvent newInstance() {
			return new MessageEvent();
		}
	};

	private boolean isStart;

	public BatchProcessorService() {

	}

	public BatchProcessorService(EventHandler<MessageEvent> ... handlers) {
		this(ProducerType.SINGLE, handlers);
	}

	public BatchProcessorService(ProducerType producerType , EventHandler<MessageEvent> ... handlers) {
		this.producerType = producerType;
		if(handlers!=null){
			handlerList.addAll(Arrays.asList(handlers));
		}
		ringBuffer = RingBuffer.create(producerType,eventFactory, ringBufferSize ,waitStrategy);
	}

	private void initEnv(){
		if(executor==null){
			executor = Executors.newFixedThreadPool(poolSize);
		}
		if(ringBuffer==null){
			ringBuffer = RingBuffer.create(producerType,eventFactory, ringBufferSize ,waitStrategy);
		}
	}
	private void init(){
		//创建消息处理器
		for (EventHandler<MessageEvent> eventHandler : handlerList) {
			addEventHandler(eventHandler);
		}
	}

	public synchronized void startService(){
		if(!isStart){
			init();
			isStart = true;
			System.out.println("BatchDisruptor Service 启动完成！");
		}
	}

	public void stopService(){
		stopService(10);
	}
	public synchronized void stopService(int timeout){
		try {
			System.out.println(timeout +"秒后即将关闭BatchProcessor服务...");
			Thread.sleep(1000*timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < batchProcessorList.size(); i++) {
			BatchEventProcessor<MessageEvent> batchProcessor = batchProcessorList.get(i);
			if(batchProcessor!=null){
				batchProcessor.halt();
				if(batchProcessorList.size() == (i+1)){
					System.out.println("BatchProcessor Service 关闭完成！");
				}
			}
		}
		if(executor!=null&&!executor.isShutdown()){
			executor.shutdown();
			System.out.println("executor Service 关闭完成！");
		}
	}

	public synchronized void addEventHandler(EventHandler<MessageEvent> handler) {
		initEnv();
		BatchEventProcessor<MessageEvent> batchProcessor = new BatchEventProcessor<MessageEvent>(
				ringBuffer, ringBuffer.newBarrier(), handler);
		batchProcessor.setExceptionHandler(exceptionHandler);
		ringBuffer.addGatingSequences(batchProcessor.getSequence());
		executor.submit(batchProcessor);
		batchProcessorList.add(batchProcessor);
	}

	public List<BatchEventProcessor<MessageEvent>> getBatchProcessorList() {
		return batchProcessorList;
	}

	public boolean isStart() {
		return isStart;
	}

	public static int getPoolSize() {
		return poolSize;
	}

	public static void setPoolSize(int poolSize) {
		BatchProcessorService.poolSize = poolSize;
	}

	public static int getRingBufferSize() {
		return ringBufferSize;
	}

	public static void setRingBufferSize(int ringBufferSize) {
		BatchProcessorService.ringBufferSize = ringBufferSize;
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

	public List<EventHandler<MessageEvent>> getHandlerList() {
		return handlerList;
	}

	public void setHandlerList(List<EventHandler<MessageEvent>> handlerList) {
		this.handlerList = handlerList;
	}

	public RingBuffer<MessageEvent> getRingBuffer() {
		return ringBuffer;
	}

	public void setRingBuffer(RingBuffer<MessageEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
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
