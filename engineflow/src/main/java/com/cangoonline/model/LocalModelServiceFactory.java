package com.cangoonline.model;

import java.util.Properties;

import com.cangoonline.model.jpmml.JPmmlLocalModelService;
import com.cangoonline.model.smg3.Smg3LocalModelService;

public class LocalModelServiceFactory {
	
	/**Jpmml Service**/
	public static LocalModelService getJPmmlModelService(Properties properties) {
		return new JPmmlLocalModelService(properties.getProperty(ModelConfigKey.JPMML_KEY_MODELFILEPATH, ""));
	}
	public static LocalModelService getJPmmlModelService(String modelFilePath) {
		return new JPmmlLocalModelService(modelFilePath);
	}
	public static LocalModelService getJPmmlModelService() {
		return new JPmmlLocalModelService();
	}
	
	/**Smg3 Service**/
	public static LocalModelService getSmg3ModelService(Properties properties) {
		return new Smg3LocalModelService(properties);
	}
	public static LocalModelService getSmg3ModelService(String propertiesPath) {
		return new Smg3LocalModelService(propertiesPath);
	}
	public static LocalModelService getSmg3ModelService() {
		return new Smg3LocalModelService();
	}
	
}
