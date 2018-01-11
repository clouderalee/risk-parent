package com.cangoonline.risk.common;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.StringUtils;

public class PropertyConfigurer extends PropertyPlaceholderConfigurer{
	private static Properties properties = new Properties();
	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactoryToProcess,
			Properties props) throws BeansException {
		super.processProperties(beanFactoryToProcess, props);
		properties.putAll(props);
	}
	
	public static String getProperty(String name) {  
        return properties.getProperty(name);
    }
	
	public static String getProperty(String name,String defaultValue) {  
        return properties.getProperty(name,defaultValue);
    }
	
	public static Integer getProperty(String name,int defaultValue) {
		String v = properties.getProperty(name);
		if(StringUtils.isEmpty(v)){
			return defaultValue;
		}
		return Integer.parseInt(v);
    }
	public static Boolean getProperty(String name,boolean defaultValue) {
		String v = properties.getProperty(name);
		if(StringUtils.isEmpty(v)){
			return defaultValue;
		}
		return "true".equalsIgnoreCase(v.trim());
	}
	
}
