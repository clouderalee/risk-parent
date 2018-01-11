package com.cangoonline.engineflow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.cangoonline.engineflow.bean.BeanObject;
import com.cangoonline.engineflow.bean.FlowObject;
import com.cangoonline.engineflow.bean.ServiceObject;
import com.cangoonline.engineflow.common.BeanFactory;
import com.cangoonline.engineflow.common.EngineConstants;
import com.cangoonline.engineflow.common.Tools;

public class EngineHelper {
	private static final String FIELD_JOINT_CHAR = "_";
	private static final String defaultConfigPath = "classpath:engineFlow.xml";
	
	private static VariableProcessor globalVariableProcessor;
	private static ExcuteLogProcessor globalExcuteLogProcessor;
	private static Properties engineProperties;
	private static Map<String,BeanObject> beans;
	private static Map<String,ServiceObject> engineServices;

	private static boolean isInit = false;
	
	public static ExcuteLogProcessor getGlobalExcuteLogProcessor() {
		return globalExcuteLogProcessor;
	}
	public static VariableProcessor getGlobalVariableProcessor() {
		return globalVariableProcessor;
	}
	public static String getEngineProperty(String name){
		return engineProperties.getProperty(name);
	}
	public static boolean getEngineProperty(String name,boolean def){
		String property = getEngineProperty(name);
		return property==null?def:"true".equalsIgnoreCase(property.trim());
	}
	public static int getEngineProperty(String name,int def){
		String property = getEngineProperty(name);
		return property==null?def:Integer.parseInt(property.trim());
	}
	public static String getEngineProperty(String name,String def){
		return engineProperties.getProperty(name,def);
	}
	public static boolean existEngineService (String channel,String serviceCode){
		String key = channel + FIELD_JOINT_CHAR +serviceCode;
		return engineServices.containsKey(key);
	}
	public static ServiceObject getEngineService(String channel,String serviceCode){
		String key = channel + FIELD_JOINT_CHAR +serviceCode;
		if(engineServices.containsKey(key)){
			return engineServices.get(key);
		} return null;
	}
	public synchronized static void initEngineFlow() throws Exception {
		if(!isInit){
			initEngineFlow(defaultConfigPath);
			isInit = true;
		}
	}

	@SuppressWarnings("unchecked")
	public static void initEngineFlow(String engineConfigPath) throws Exception {
		logger("==================############引擎服务开始进行初始化############==================");
		engineServices = new HashMap<>();
		engineProperties = new Properties();
		beans = new HashMap<String, BeanObject>();
		Document document = getEngineFLowDocument(engineConfigPath);

		Element rootElement = document.getRootElement();

		//解析properties
		logger("==========>初始化全局参数：");
		Element propertiesElement = rootElement.getChild("properties");
		List<Element> propertyList = propertiesElement.getChildren("property");
		setProperties(engineProperties, propertyList);
		if(!engineProperties.containsKey(EngineConstants.TAG_BEANTYPE)){
			engineProperties.put(EngineConstants.TAG_BEANTYPE, EngineConstants.V_BEANTYPE_REFLECT);
		}
		logger("===========全局参数装载完成===========");
		
		//解析beans
		logger("==========>初始化bean对象：");
		Element beansElement = rootElement.getChild("beans");
		List<Element> beanList = beansElement.getChildren("bean");
		for (Element element : beanList) {
			boolean bIsValid = Tools.str2Boolean(element.getAttributeValue("isValid", "true"));
			if(!bIsValid) continue;
			String beanId = element.getAttributeValue(EngineConstants.TAG_ID);
			if(Tools.isEmpty(beanId)||beans.containsKey(beanId)){
				throw new RuntimeException("engineFLow.xml init bean fail：beanId is error or repeated. ");
			}
			String bClassName = element.getAttributeValue("class");
            String bRef = element.getAttributeValue("ref");
            if(Tools.isEmpty(bClassName)&&Tools.isEmpty(bRef)){
            	throw new RuntimeException("engineFLow.xml init bean fail. ");
            }
            String bDescribe = element.getAttributeValue("describe", "");
            String bScope = element.getAttributeValue("scope", "singleton");
            
            //初始化bean属性
            List<Element> beanBropertyList = element.getChildren("property");
            Properties beanBropertyProperties = new Properties();
    		setProperties(beanBropertyProperties, beanBropertyList);
            
            BeanObject beanObject = new BeanObject(beanId, bClassName, bRef, bDescribe, bScope,beanBropertyProperties);
           
            try{
            	beanObject.setCreateWay(getEngineProperty(EngineConstants.TAG_BEANTYPE));
            	BeanObject object = BeanFactory.create(beanObject);
            	if(object==null||object.getObject()==null){
            		throw new Exception();
            	}
            	object.setClassName(object.getObject().getClass().getName());
            	beans.put(beanId, object);
            	logger(beanId+" = "+object.getClassName());
            }catch(Exception e){
            	e.printStackTrace();
            	// TODO 异常处理
            	throw new RuntimeException("engineFLow.xml init bean fail. ");
            }
        }
		logger("===========Bean对象全部装载完成===========");

		//全局变量加工处理器
		String varProcessorRef = engineProperties.getProperty(EngineConstants.TAG_VARPROCESSOR);
		if(Tools.isNotEmpty(varProcessorRef)){
			globalVariableProcessor = getVarProcessor(varProcessorRef);
			if(globalVariableProcessor == null){
				throw new RuntimeException("全局变量加工处理器初始化失败");
			}
		}else{
			//没有配置，默认设置空的处理器
			globalVariableProcessor = new EmptyVariableProcessor();
		}
		logger("全局变量加工处理器["+globalVariableProcessor.getClass().getName()+"]初始化完成！");
		
		//设置全局日志处理器
		String logProcessorRef = engineProperties.getProperty(EngineConstants.TAG_LOGPROCESSOR);
		if(Tools.isNotEmpty(logProcessorRef)){
			globalExcuteLogProcessor = getLogProcessor(logProcessorRef);
			if(globalExcuteLogProcessor == null){
				throw new RuntimeException("全局日志处理器初始化失败");
			}
		}else{
			//没有配置，默认设置空的处理器
			globalExcuteLogProcessor = new EmptyExcuteLogProcessor();
		}
		logger("全局日志处理器["+globalVariableProcessor.getClass().getName()+"]初始化完成！");

		//解析services
		logger("==========>初始化Service引擎服务：");
		Element servicesElement = rootElement.getChild("services");
		List<Element> serviceList = servicesElement.getChildren("service");
		for (Element service : serviceList) {
			String isValid = service.getAttributeValue("isValid", "true");
			if(!"true".equals(isValid)) continue;
			String channel = service.getAttributeValue("channel");
			String serviceCode = service.getAttributeValue("serviceCode");
			if(Tools.isEmpty(channel)||Tools.isEmpty(serviceCode)){
				throw new RuntimeException("engineFLow.xml parser exception:channel & serviceCode can not is empty ");
			}
			String name = service.getAttributeValue("name",channel+FIELD_JOINT_CHAR+serviceCode);
			String describe = service.getAttributeValue("describe",name);
			
			//设置Service级别变量处理器
			varProcessorRef = service.getAttributeValue(EngineConstants.TAG_VARPROCESSOR);
			VariableProcessor serviceVarProcessor = null;
			if(Tools.isNotEmpty(varProcessorRef)){
				serviceVarProcessor = getVarProcessor(varProcessorRef);
				if(serviceVarProcessor == null){
					throw new RuntimeException("Service("+channel+","+serviceCode+")变量加工处理器初始化失败");
				}
			}else{
				//没有配置则继承全局的变量处理器
				serviceVarProcessor = getGlobalVariableProcessor();
			}
			//Service级别日志处理器
			logProcessorRef = service.getAttributeValue(EngineConstants.TAG_VARPROCESSOR);
			ExcuteLogProcessor serviceLogProcessor = null;
			if(Tools.isNotEmpty(logProcessorRef)){
				serviceLogProcessor = getLogProcessor(logProcessorRef);
				if(serviceLogProcessor == null){
					throw new RuntimeException("Service("+channel+","+serviceCode+")变量加工处理器初始化失败");
				}
			}else{
				//没有配置则继承全局的变量处理器
				serviceLogProcessor = getGlobalExcuteLogProcessor();
			}
			
			//解析flows
			List<Element> flowList = service.getChildren("flow");
			List<FlowObject> flows = new ArrayList<FlowObject>();
			for (Element flow : flowList) {
				Integer id = Integer.parseInt(flow.getAttributeValue("id"));
				String fname = flow.getAttributeValue("name",flow.getAttributeValue("id"));
				String fdescribe = flow.getAttributeValue("describe",fname);
				String isParamInto = flow.getAttributeValue("paramInto","true");
				String isFinishFlow = flow.getAttributeValue("isFinishFlow","false");
				String beanRef = flow.getAttributeValue("bean");
				if(Tools.isEmpty(beanRef)){
					throw new RuntimeException("engineFLow.xml parser exception:flow bean is empty.");
				}

				Properties flowProperties = new Properties();
				List<Element> flowPropertyList = flow.getChildren("property");
				setProperties(flowProperties, flowPropertyList);

				//获取流程处理的对象实例
				ModelActuator engineService = null;
				engineService = getEngineTemplate(beanRef);
				if(engineService == null){
					throw new RuntimeException("engineFLow.xml parser error:"+ModelActuator.class.getSimpleName()+" is null");
				}
				
				//配置Flow级别变量处理器
				varProcessorRef = flow.getAttributeValue(EngineConstants.TAG_VARPROCESSOR);
				VariableProcessor flowVarProcessor = null;
				if(Tools.isNotEmpty(varProcessorRef)){
					flowVarProcessor = getVarProcessor(varProcessorRef);
					if(flowVarProcessor == null){
						throw new RuntimeException("flow("+channel+","+serviceCode+","+fname+")变量加工处理器初始化失败");
					}
				}else{
					//没有配置则继承全局的变量处理器
					flowVarProcessor = serviceVarProcessor;
				}
				
				//日志处理器
				logProcessorRef = flow.getAttributeValue(EngineConstants.TAG_LOGPROCESSOR);
				ExcuteLogProcessor flowLogProcessor = null;
				if(Tools.isNotEmpty(logProcessorRef)){
					flowLogProcessor = getLogProcessor(logProcessorRef);
					if(flowLogProcessor == null){
						throw new RuntimeException("flow("+channel+","+serviceCode+","+fname+")执行日志处理器初始化失败");
					}
				}else{
					//没有配置则继承全局的变量处理器
					flowLogProcessor = serviceLogProcessor;
				}

				FlowObject flowObject = new FlowObject(id, engineService.getClass().getName(), beanRef);
				flowObject.setEngineService(engineService);
				flowObject.setFlowProperties(flowProperties);
				flowObject.setName(fname);
				flowObject.setDescribe(fdescribe);
				flowObject.setParamInto(!"false".equalsIgnoreCase(isParamInto));
				flowObject.setFinishFlow("true".equalsIgnoreCase(isFinishFlow));
				flowObject.setFlowVariableProcessor(flowVarProcessor);
				flowObject.setFlowExcuteLogProcessor(flowLogProcessor);
				flows.add(flowObject);
				logger("----------------Flow流程("+channel+","+serviceCode+","+fname+","+id+")初始化完成-------------------");
			}

			//排序flow
			Collections.sort(flows, new Comparator<FlowObject>() {
				@Override
				public int compare(FlowObject o1, FlowObject o2) {
					return o1.getId()-o2.getId();
				}
			});
			//默认最后一个finalFlow
			flows.get(flows.size()-1).setFinishFlow(true);

			//加入到service
			ServiceObject serviceObject = new ServiceObject(channel, serviceCode, name, describe);
			serviceObject.setServiceVariableProcessor(serviceVarProcessor);
			serviceObject.setServiceExcuteLogProcessor(serviceLogProcessor);
			serviceObject.setFlows(flows);
			
			//加入到engine
			engineServices.put(channel+FIELD_JOINT_CHAR+serviceCode, serviceObject);
			logger("Service引擎服务["+channel+"-"+serviceCode+"]装载完成！");
		}
		logger("===========Service引擎服务全部装载完成===========");
		logger("==================############引擎服务初始化完成############==================");

	}
	
	private static ExcuteLogProcessor getLogProcessor(String logProcessorRef) {
		BeanObject beanObject = beans.get(logProcessorRef);
		Object object = beanObject.getObject();
		if(object!=null&&object instanceof ExcuteLogProcessor){
			return (ExcuteLogProcessor) object;
		}
		return null;
	}
	private static VariableProcessor getVarProcessor(String varProcessorRef) {
		BeanObject beanObject = beans.get(varProcessorRef);
		Object object = beanObject.getObject();
		if(object!=null&&object instanceof VariableProcessor){
			return (VariableProcessor) object;
		}
		return null;
	}

	private static Object getObjectByRef(String beanRef) {
		BeanObject beanObject = beans.get(beanRef);
		if(beanObject == null){
			return null;
		}
		Object object = beanObject.getObject();
		if(object==null){
			return null;
		}
		try {
			String scope = beanObject.getScope();
			switch (scope) {
			case EngineConstants.V_SCOPE_SINGLETON:
				return object;
			case EngineConstants.V_SCOPE_PROTOTYPE:
				return BeanFactory.create(beanObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}
	private static ModelActuator getEngineTemplate(String beanRef) {
		Object object = getObjectByRef(beanRef);
		if(object instanceof ModelActuator){
			return (ModelActuator) object;
		}
		throw new RuntimeException("engineFLow.xml parser exception:flow class need to impl "+ModelActuator.class.getName()+"");
	}
	private static void setProperties(Properties properties, List<Element> propertyList) {
		for (Element element : propertyList) {
            String fName = element.getAttributeValue("name");
            String fValue = element.getAttributeValue("value", "");
            if(Tools.isNotEmpty(fName)){
            	logger(fName+" = "+ fValue);
                properties.put(fName, fValue);
            }
        }
	}

	private static Document getEngineFLowDocument(String engineConfigPath) throws JDOMException, IOException {
		//创建一个SAXBuilder对象
		SAXBuilder saxBuilder = new SAXBuilder();

		Document doc = null;
		if(engineConfigPath.startsWith("classpath:")){
			doc = saxBuilder.build(EngineHelper.class
					.getClassLoader().getResourceAsStream(engineConfigPath.substring(10)));
		}else  if(engineConfigPath.startsWith("/")){
			doc = saxBuilder.build(EngineHelper.class
					.getClassLoader().getResourceAsStream(engineConfigPath));
		}else{
			doc = saxBuilder.build(new File(engineConfigPath));
		}

		return doc;
	}
	
	private static void logger(String message){
		System.out.println(message);
	}

	public static boolean isInit() {
		return isInit;
	}

}
