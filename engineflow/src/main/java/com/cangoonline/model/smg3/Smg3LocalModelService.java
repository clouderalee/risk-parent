package com.cangoonline.model.smg3;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.cangoonline.model.LocalModelService;
import com.cangoonline.model.ModelConfigKey;

public class Smg3LocalModelService implements LocalModelService {
	private int logLevel = 0;
	
	private String signatureName;
	private String aliasName;
	
	private String ctrlAreaName = "OCONTROL";
	private String inputDataAreaName;
	private String outputDataAreaName;
	
	public Smg3LocalModelService() {
		
	}
	public Smg3LocalModelService(String propertiesPath) {
		try {
			Properties properties = null;
			InputStream inputStream = null;
			if(propertiesPath.startsWith("classpath")){
				String filePath = propertiesPath.substring(10);
				inputStream = Smg3LocalModelService.class.getClassLoader().getResourceAsStream(filePath);
				properties = new Properties();
			}else{
				inputStream  = new FileInputStream(propertiesPath);
				properties = new Properties();
			}
			properties.load(inputStream);
			init(properties);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public Smg3LocalModelService(Properties properties) {
		init(properties);
	}
	
	private void init(Properties properties) {
		logLevel = Integer.parseInt(properties.getProperty(ModelConfigKey.SMG3_KEY_LOGLEVEL, "0").trim());
		signatureName = properties.getProperty(ModelConfigKey.SMG3_KEY_SIGNATURENAME, "").trim();
		aliasName = properties.getProperty(ModelConfigKey.SMG3_KEY_ALIASNAME, "").trim();
		ctrlAreaName = properties.getProperty(ModelConfigKey.SMG3_KEY_ALIASNAME, "OCONTROL").trim();
		inputDataAreaName = properties.getProperty(ModelConfigKey.SMG3_KEY_INPUTDATAAREANAME, "").trim();
		outputDataAreaName = properties.getProperty(ModelConfigKey.SMG3_KEY_OUTPUTDATAAREANAME, "").trim();
	}

	public synchronized void reloadProperties(Properties properties){
		init(properties);
	}

	@Override
	public Object executeRule(Map<String, Object> input) {
		return executeRule(input, logLevel, signatureName, aliasName, ctrlAreaName, inputDataAreaName, outputDataAreaName);
	}
	
	public static Map<String,Object> executeSmg3Rule(Map<String, Object> input,Properties properties) {
		int logLevel = Integer.parseInt(properties.getProperty(ModelConfigKey.SMG3_KEY_LOGLEVEL, "0").trim());
		String signatureName = properties.getProperty(ModelConfigKey.SMG3_KEY_SIGNATURENAME, "").trim();
		String aliasName = properties.getProperty(ModelConfigKey.SMG3_KEY_ALIASNAME, "").trim();
		String ctrlAreaName = properties.getProperty(ModelConfigKey.SMG3_KEY_ALIASNAME, "OCONTROL").trim();
		String inputDataAreaName = properties.getProperty(ModelConfigKey.SMG3_KEY_INPUTDATAAREANAME, "").trim();
		String outputDataAreaName = properties.getProperty(ModelConfigKey.SMG3_KEY_OUTPUTDATAAREANAME, "").trim();
		return executeRule(input, logLevel, signatureName, aliasName, ctrlAreaName, inputDataAreaName, outputDataAreaName);
	}
	
	private static Map<String,Object> executeRule(Map<String, Object> input,int logLevel,String signatureName,
			String aliasName,String ctrlAreaName,String inputDataAreaName,String outputDataAreaName){
		
		//1.初始化缓存区域
		DataArea ctrlArea = new DataArea(ctrlAreaName);
		DataArea inArea = new DataArea(inputDataAreaName);
		DataArea outArea = new DataArea(outputDataAreaName);
		ctrlArea.setValue("ALIAS", aliasName);
		ctrlArea.setValue("SIGNATURE", signatureName);
		
		//2.填充数据区域
		fillDataArea(inArea,input);
		
		//3.执行规则文件
		RuleEngine.executeRule(logLevel, ctrlArea, inArea, outArea);
		
		//4.返回结果数据
		return outArea.getArea();
	}

	private static void fillDataArea(DataArea inArea, Map<String, Object> input) {
		Set<Entry<String,Object>> entrySet = input.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			inArea.setValue(entry.getKey(), entry.getValue());
		}
	}
	
	public int getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	public String getSignatureName() {
		return signatureName;
	}

	public void setSignatureName(String signatureName) {
		this.signatureName = signatureName;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getCtrlAreaName() {
		return ctrlAreaName;
	}

	public void setCtrlAreaName(String ctrlAreaName) {
		this.ctrlAreaName = ctrlAreaName;
	}

	public String getInputDataAreaName() {
		return inputDataAreaName;
	}

	public void setInputDataAreaName(String inputDataAreaName) {
		this.inputDataAreaName = inputDataAreaName;
	}

	public String getOutputDataAreaName() {
		return outputDataAreaName;
	}

	public void setOutputDataAreaName(String outputDataAreaName) {
		this.outputDataAreaName = outputDataAreaName;
	}
	
}
