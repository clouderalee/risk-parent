package com.cangoonline.engineflow.common;

import org.springframework.context.ApplicationContext;

public class SpringApplication {
	private static ApplicationContext context;
	public static void initApplication(ApplicationContext ac){
		if(context==null){
			context = ac;
		}
	}
	
	public static ApplicationContext getApplicationContext(){
		return context;
	}
	
	public static Object getBean(String refName){
		checkContext();
		return context.getBean(refName);
	}
	
	public static <T> T getBean(Class<T> clazz){
		return context.getBean(clazz);
	}
	
	private static void checkContext() {
		if(context==null){
			throw new RuntimeException("Spring ApplicationContext is uninitialized. ");
		}
	}
}
