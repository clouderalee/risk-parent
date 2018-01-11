package com.cangoonline.engineflow;

import java.util.Map;
import java.util.Properties;

/**
 * 所有模型执行的高级接口
 * @author Administrator
 *
 */
public interface ModelActuator {
	/**
	 * 根据配置文件信息执行结果
	 * @param properties （模型文件头信息）
	 * @param dataModel 模型文件的数据模型数据
	 * @return
	 * @throws Exception
	 */
	Map<String,Object> execute(Map<String,Object> dataModel,Properties properties) throws Exception;
	
}
