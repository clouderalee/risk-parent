package com.cangoonline.engineflow.bean;

import java.util.Properties;

import com.cangoonline.engineflow.ExcuteLogProcessor;
import com.cangoonline.engineflow.ModelActuator;
import com.cangoonline.engineflow.VariableProcessor;

public class FlowObject {
	private Integer id;
	private String name;
	private String describe;
	private String className;
	private String refName;
	private boolean isParamInto;//输入参数包含结果
	private boolean isFinishFlow;//结束流程处理时，返回此流程处理的结果
	
	private Properties flowProperties;
	private ModelActuator engineService;
	private ServiceObject serviceObject;
	private VariableProcessor flowVariableProcessor;
	private ExcuteLogProcessor flowExcuteLogProcessor;
	
	public FlowObject(Integer id, String className, String refName) {
		this.id = id;
		this.className = className;
		this.refName = refName;
	}
	
	public String getEngineProperty(String name){
		return flowProperties.getProperty(name);
	}
	public boolean getEngineProperty(String name,boolean def){
		String property = getEngineProperty(name);
		return property==null?def:"true".equalsIgnoreCase(property.trim());
	}
	public int getEngineProperty(String name,int def){
		String property = getEngineProperty(name);
		return property==null?def:Integer.parseInt(property.trim());
	}
	public String getEngineProperty(String name,String def){
		return flowProperties.getProperty(name,def);
	}
	public String getFlowUniqueTag(){
		return this.name+"-"+this.id;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
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
	public ModelActuator getEngineService() {
		return engineService;
	}
	public void setEngineService(ModelActuator engineService) {
		this.engineService = engineService;
	}
	public Properties getFlowProperties() {
		return flowProperties;
	}
	public void setFlowProperties(Properties flowProperties) {
		this.flowProperties = flowProperties;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public boolean isParamInto() {
		return isParamInto;
	}

	public void setParamInto(boolean isParamInto) {
		this.isParamInto = isParamInto;
	}

	public boolean isFinishFlow() {
		return isFinishFlow;
	}

	public void setFinishFlow(boolean isFinishFlow) {
		this.isFinishFlow = isFinishFlow;
	}
	public ServiceObject getServiceObject() {
		return serviceObject;
	}

	public void setServiceObject(ServiceObject serviceObject) {
		this.serviceObject = serviceObject;
	}
	
	public VariableProcessor getFlowVariableProcessor() {
		return flowVariableProcessor;
	}

	public void setFlowVariableProcessor(VariableProcessor flowVariableProcessor) {
		this.flowVariableProcessor = flowVariableProcessor;
	}

	public ExcuteLogProcessor getFlowExcuteLogProcessor() {
		return flowExcuteLogProcessor;
	}

	public void setFlowExcuteLogProcessor(ExcuteLogProcessor flowExcuteLogProcessor) {
		this.flowExcuteLogProcessor = flowExcuteLogProcessor;
	}

	@Override
	public String toString() {
		return "Flow [id=" + id + ", className=" + className + ", refName=" + refName + "]";
	}
}
