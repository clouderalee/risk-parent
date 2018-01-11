package com.cangoonline.engineflow;

import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class EngineContextListener implements ServletContextListener{
	private static Logger logger = Logger.getLogger(EngineContextListener.class.getName());
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		//初始化EngineFlow
		ServletContext servletContext = event.getServletContext();
		String engineConfigPath = servletContext.getInitParameter("engineConfigPath");
		if(engineConfigPath == null || "".equals(engineConfigPath.trim())){
			engineConfigPath = "classpath:engineFlow.xml"; 
		}
		try {
			EngineHelper.initEngineFlow(engineConfigPath);
			logger("EngineFlow 初始化完成!!!");
		} catch (Exception e) {
			throw new RuntimeException("EngineFlow初始化失败！！！", e);
		}
	}

	private void logger(String message) {
		logger.info(message);
	}

}
