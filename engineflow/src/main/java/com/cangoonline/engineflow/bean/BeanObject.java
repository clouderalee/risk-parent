package com.cangoonline.engineflow.bean;

import java.util.Properties;

public class BeanObject {
	private String id;
	private String className;
	private String refName;
	private String describe;
	private String scope;
	private String createWay;
	private Object object;
	private Properties beanBropertyProperties;
	
	public BeanObject() {
	}
	
	public BeanObject(String id, String className, String refName,
			String describe, String scope ,Properties beanBropertyProperties) {
		this.id = id;
		this.className = className;
		this.refName = refName;
		this.describe = describe;
		this.scope = scope;
		this.beanBropertyProperties = beanBropertyProperties;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getRefName() {
		return refName;
	}
	public void setRefName(String refName) {
		this.refName = refName;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getScope() {
		return scope;
	}
	/**singleton,prototype**/
	public void setScope(String scope) {
		this.scope = scope;
	}


	public String getCreateWay() {
		return createWay;
	}


	public void setCreateWay(String createWay) {
		this.createWay = createWay;
	}


	public Object getObject() {
		return object;
	}


	public void setObject(Object object) {
		this.object = object;
	}


	public Properties getBeanBropertyProperties() {
		return beanBropertyProperties;
	}


	public void setBeanBropertyProperties(Properties beanBropertyProperties) {
		this.beanBropertyProperties = beanBropertyProperties;
	}
	
}
