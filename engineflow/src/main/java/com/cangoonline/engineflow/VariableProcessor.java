package com.cangoonline.engineflow;

import java.util.Map;

public interface VariableProcessor {
	/**
	 * 检查参数-参数校验
	 * @param map 数据集合
	 * @param object modelobject
	 * @throws Exception
	 */
	void checkParameter(Map<String,Object> map,Object object) throws Exception;
	
	/**
	 * 参数转换加工
	 * @param map 数据集合
	 * @param object modelobject
	 * @throws Exception
	 */
	void convertParameter(Map<String,Object> map,Object object) throws Exception;
	
	/**
	 * 参数分箱
	 * @param map 数据集合
	 * @param object modelobject
	 * @return 得到模型最终实际入参
	 * @throws Exception
	 */
	Map<String,Object> binningParameter(Map<String,Object> map,Object object) throws Exception;
	
	/**
	 * 按照输出字段配置，格式化结果
	 * @param map 数据集合
	 * @param object modelobject
	 * @return 格式化后的结果集合
	 * @throws Exception
	 */
	Map<String,Object> formatResult(Map<String,Object> map,Object object) throws Exception;
	
}
