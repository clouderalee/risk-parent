package com.cangoonline.engineflow.scriptext.freemark;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;

public class FreemarkConfiguration extends Configuration{
	private static FreemarkConfiguration cfg;
	private static Properties freemarkProperties;
	private String configPath = "classpath:freemarker.properties";
	private FreemarkConfiguration(){}
	private FreemarkConfiguration(String configPath) {
		this.configPath = configPath;
	}
	private synchronized static void init() {
		try {
			freemarkProperties = new Properties();
			if(cfg.configPath.startsWith("classpath:")){
				freemarkProperties.load(FreemarkConfiguration.class.getClassLoader().getResourceAsStream(cfg.configPath.substring(10).trim()));
			}else{
				freemarkProperties.load(new FileInputStream(new File(cfg.configPath)));
			}
			//设置常规配置
			Set<Entry<Object, Object>> entrySet = freemarkProperties.entrySet();
			for (Entry<Object, Object> entry : entrySet) {
				Object key = entry.getKey();
				Object value = entry.getValue();
				try {
					cfg.setSetting(String.valueOf(key), String.valueOf(value));
				} catch (freemarker.core._MiscTemplateException e) {
					continue;
				}
			}
			
			//设置共享变量
			String shared_variables = freemarkProperties.getProperty("shared_variables", "").trim();;
			for (String exp : shared_variables.split(",")) {
				if(Funcations.isEmpty(exp)||Funcations.isSpace(exp)) 
	        		continue;
				String[] item = exp.split("=",2);
				cfg.setSharedVariable(item[0].trim(), item[1].trim());
			}
			//设置共享的静态类
			BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
	        TemplateHashModel staticModels = wrapper.getStaticModels();
	        String shared_static_classes = freemarkProperties.getProperty("shared_static_classes", "").trim();;
	        for (String exp : shared_static_classes.split(",")) {
	        	if(Funcations.isEmpty(exp)||Funcations.isSpace(exp)) 
	        		continue;
				String[] item = exp.split("=",2);
				Class<?> clazz = Class.forName(item[1].trim());
				TemplateHashModel staticObject =(TemplateHashModel) staticModels.get(clazz.getName());
				cfg.setSharedVariable(item[0].trim(), staticObject);
			}
	        //所有类的访问权限
	        String shared_all_switch = freemarkProperties.getProperty("shared_all_switch", "false").trim();
	        if("true".equalsIgnoreCase(shared_all_switch)){
	        	String shared_all_name = freemarkProperties.getProperty("shared_all_name", "statics").trim();
	        	cfg.setSharedVariable(shared_all_name, staticModels);
	        }
	        
	        //配置自定义方法函数
	        String run_method_name = freemarkProperties.getProperty("run_method_name", "RunMethod").trim();
	        if(Funcations.isEmpty(run_method_name)){
	        	cfg.setSharedVariable("RunMethod", new RunJavaMethod());
	        }else{
	        	cfg.setSharedVariable(run_method_name, new RunJavaMethod());
	        }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static FreemarkConfiguration newInstance(){
		return newInstance("classpath:freemarker.properties");
	}
	public static FreemarkConfiguration newInstance(String configPath){
		if(cfg==null) {
			cfg = new FreemarkConfiguration(configPath);
			init();
		}
		return cfg;
	}
	
	public static void main(String[] args) throws Exception {
		FreemarkConfiguration freemarkConfiguration = FreemarkConfiguration.newInstance("classpath:com/cangoonline/engineflow/scriptext/freemark/freemarker.properties");
		System.out.println(freemarkConfiguration);
		
		String script = "我是${name}!";
		Map<String,Object> dataModel = new HashMap<>();
		dataModel.put("name", "Hello World");
		
		StringWriter result = new StringWriter();
		Template t = new Template("temp", new StringReader(script ), freemarkConfiguration);
        t.process(dataModel, result);
        System.out.println(result.toString());
	}
	
}
