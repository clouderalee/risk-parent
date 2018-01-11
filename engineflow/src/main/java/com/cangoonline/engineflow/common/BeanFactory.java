package com.cangoonline.engineflow.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.cangoonline.engineflow.bean.BeanObject;

public class BeanFactory {
	
	/**
	 * 更具bean信息创建bean对象
	 * @param beanObject
	 * @return 对象，null,异常
	 * @throws Exception
	 */
	public static BeanObject create(BeanObject beanObject) throws Exception{
		String createWay = beanObject.getCreateWay();
		Object object = null;
		switch (createWay) {
		case EngineConstants.V_BEANTYPE_REFLECT:
			object = getObjectByReflect(beanObject);
			break;
		case EngineConstants.V_BEANTYPE_SPRING:
			object = getObjectBySpring(beanObject);
			break;
		case EngineConstants.V_BEANTYPE_AUTO:
			object = getObjectByAuto(beanObject);
			break;
		}
		beanObject.setObject(object);
		return beanObject;
	}

	/**
	 * 先按照beanName 和 beanType寻找
	 * 在spring中找不到时，通过反射创建一个对象
	 * @param beanObject
	 * @return
	 * @throws Exception
     */
	private static Object getObjectByAuto(BeanObject beanObject) throws Exception {
		Object newInstance = null;
		String className = beanObject.getClassName();
		String refName = beanObject.getRefName();

		try{
			if(refName!=null){
				newInstance = SpringApplication.getBean(refName);
			}else{
				newInstance = SpringApplication.getBean(Class.forName(className));
			}
		}catch (Exception e){
			System.err.println(e.getMessage());
		}

		//在spring中找不到时，通过反射创建一个对象
		if(newInstance!=null) {
			fillFieldProperty(beanObject.getBeanBropertyProperties(), newInstance);
			return newInstance;
		}else{
			if(className!=null){
				newInstance = getObjectByReflect(beanObject);
				if(newInstance!=null) return newInstance;
			}
		}
		return newInstance;
	}

	/**
	 * 如果按照beanName 和 beanType都找不到则抛出异常，不通过反射创建对象
	 * @param beanObject
	 * @return
	 * @throws Exception
     */
	private static Object getObjectBySpring(BeanObject beanObject) throws Exception {
		Object newInstance = null;
		
		String className = beanObject.getClassName();
		String refName = beanObject.getRefName();

		if(refName!=null){
			try{
				newInstance = SpringApplication.getBean(refName);
			}catch (Exception e){
				System.err.println(e.getMessage());
			}
			if(newInstance!=null) {
				fillFieldProperty(beanObject.getBeanBropertyProperties(), newInstance);
				return newInstance;
			}
		}

		if(className!=null){
			//如果按照beanName 和 beanType都找不到则抛出异常
			newInstance = getObjectByReflect(beanObject);
		}
		return newInstance;
	}

	private static Object getObjectByReflect(BeanObject beanObject) throws Exception{
		Object newInstance = null;
		try {
			Class<?> clazz = Class.forName(beanObject.getClassName());
			newInstance = clazz.newInstance();
			//填充属性值
			Properties beanBropertyProperties = beanObject.getBeanBropertyProperties();
			fillFieldProperty(beanBropertyProperties, newInstance);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("无法通过反射创建该类型["+beanObject.getClassName()+"]的对象.",e);
		}
		
		return newInstance;
	}

	private static void fillFieldProperty(Properties beanBropertyProperties, Object newInstance)
			throws Exception {
		
		if(newInstance instanceof PropertiesBean){
			Method method = PropertiesBean.class.getMethod("setProperties", Properties.class);
			method.invoke(newInstance, beanBropertyProperties);
		}else{
			//如果没有继承PropertiesBean，就填充成员属性
			//暂时只支持几种常见的基本数据类型
			Field[] fields = newInstance.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				String fieldName = field.getName();
				if(beanBropertyProperties.containsKey(fieldName)){
					String value = beanBropertyProperties.getProperty(fieldName);
					if(value!=null&&value.length()>0){
						setFieldVaue(newInstance,field,value);
					}
				}
			}
		}
	}
	private static SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sf2 = new SimpleDateFormat("yyyy/MM/dd");
	
	private static void setFieldVaue(Object object, Field field, String value) throws Exception {
		Class<?> fieldType = field.getType();
		if(fieldType==Date.class){
			if(value!=null){
				try {
					field.set(object, sf1.parse(value.trim()));
				} catch (ParseException e) {
					field.set(object, sf2.parse(value.trim()));
				}
			}
		}else{
			Object castObject = Tools.castType(value, fieldType, false);
			field.set(object, castObject);
		}
	}
	
}
