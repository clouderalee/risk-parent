package com.cangoonline.engineflow;

import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.cangoonline.engineflow.common.SpringApplication;

public class SpringWebContextListener implements ServletContextListener{
	private static Logger logger = Logger.getLogger(SpringWebContextListener.class.getName());
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		logger("SpringApplication destroyed success.");
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

			WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
			if(applicationContext!=null){
				SpringApplication.initApplication(applicationContext);
				logger("SpringApplication 初始化完成!!!");
			}

			EngineHelper.initEngineFlow(engineConfigPath);
			logger("EngineFlow 初始化完成!!!");
		} catch (Exception e) {
			e.printStackTrace();
//			throw new RuntimeException("EngineFlow初始化失败！！！", e);
		}
		
	}

	private void logger(String message) {
		logger.info(message);
	}
	
}
