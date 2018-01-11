package com.cangoonline.engineflow.common;

import java.util.Properties;

/**
 * 当类集成此类时，注入属性
 * @author Administrator
 *
 */
public abstract class PropertiesBean {
	private Properties properties;

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}
