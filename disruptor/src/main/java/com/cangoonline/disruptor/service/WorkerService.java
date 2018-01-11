package com.cangoonline.disruptor.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cangoonline.disruptor.consumer.MessageConsumer;
import com.cangoonline.disruptor.event.MessageEvent;
import com.cangoonline.disruptor.handler.MessageExceptionHandler;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

public class WorkerService {
	private int poolSize = 10;
	private int ringBufferSize = 1024 * 1024;
	
	private ProducerType producerType = ProducerType.SINGLE;
	private WaitStrategy waitStrategy = new YieldingWaitStrategy();
	private ExceptionHandler<MessageEvent> exceptionHandler = new MessageExceptionHandler();
	private List<MessageConsumer> consumers;
	private ExecutorService executor;
	private WorkerPool<MessageEvent> workerPool;
	private RingBuffer<MessageEvent> ringBuffer;
	
	private static final EventFactory<MessageEvent> eventFactory = new EventFactory<MessageEvent>(){
		@Override
		public MessageEvent newInstance() {
			return new MessageEvent();
		}
	};
	
	private boolean isStart;

	public WorkerService(){}
	public WorkerService(MessageConsumer consumer , int threadCount) {
		this(ProducerType.SINGLE, consumer, threadCount);
	}
	public WorkerService(ProducerType producerType ,MessageConsumer consumer , int threadCount) {
		this.producerType = producerType;
		this.consumers = new ArrayList<>();
		for (int i = 0; i < threadCount; i++) {
			this.consumers.add(consumer);
		}
	}
	public WorkerService(MessageConsumer ... consumers) {
		this(ProducerType.SINGLE, consumers);
	}
	public WorkerService(ProducerType producerType , MessageConsumer ... consumers) {
		this.producerType = producerType;
		this.consumers = Arrays.asList(consumers);
	}
	public WorkerService(List<MessageConsumer> consumers) {
		this(ProducerType.SINGLE, consumers);
	}
	public WorkerService(ProducerType producerType , List<MessageConsumer> consumers) {
		this.producerType = producerType;
		this.consumers = consumers;
	}

	private void init(){
		if(executor == null){
			executor = Executors.newFixedThreadPool(poolSize);
		}
		if(ringBuffer == null){
			ringBuffer = RingBuffer.create(producerType,eventFactory, ringBufferSize ,waitStrategy);
		}
		workerPool = new WorkerPool(ringBuffer, ringBuffer.newBarrier(), exceptionHandler, consumers.toArray(new MessageConsumer[]{}));
	}
	
	public synchronized void startService(){
		if(!isStart){
			init();
			workerPool.start(executor);
			isStart = true;
			System.out.println("WorkerService starting success!");
		}
	}
	
	public synchronized void stopService(){
		if(workerPool!=null){
			workerPool.drainAndHalt();
			System.out.println("worker Service stop success!");
		}
		if(executor!=null&&!executor.isShutdown()){
			executor.shutdown();
			System.out.println("executor Service stop success!");
		}
	}

	public WorkerPool<MessageEvent> getWorkerPool() {
		return workerPool;
	}

	public static EventFactory<MessageEvent> getEventFactory() {
		return eventFactory;
	}

	public boolean isStart() {
		return isStart;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public int getRingBufferSize() {
		return ringBufferSize;
	}

	public void setRingBufferSize(int ringBufferSize) {
		this.ringBufferSize = ringBufferSize;
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

	public List<MessageConsumer> getConsumers() {
		return consumers;
	}

	public void setConsumers(List<MessageConsumer> consumers) {
		this.consumers = consumers;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public RingBuffer<MessageEvent> getRingBuffer() {
		return ringBuffer;
	}

	public void setRingBuffer(RingBuffer<MessageEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}
}
