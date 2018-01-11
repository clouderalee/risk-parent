package com.cangoonline.model.smg3;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.experian.stratman.datasources.runtime.IData;

/**
 * 数据区
 * Java 物理定义
 * 
 * @author GuoZH
 * @version 1.0, 2015-06-29
 * @version 2.0, 2015-08-26
 * @version 3.0, 2015-11-16
 */
public class DataArea implements IData {

	//布局
	private String layout;

	//内容
	private Map<String, Object> area;

	/**
	 * 构造方法
	 * 
	 * @param layout  布局
	 */
	public DataArea(String layout) {
		super();
		this.layout = layout;
		this.area = new HashMap<String, Object>();
	}

	/**
	 * 获取布局
	 * 
	 * @return
	 */
	@Override
	public String getLayout() {
		return layout;
	}

	/**
	 * 获取值
	 * 
	 * @param key     键
	 * @return
	 */
	@Override
	public Object getValue(String key) {
		return area.get(key);
	}

	/**
	 * 设置值
	 * 
	 * @param key     键
	 * @param value   值
	 * @return
	 */
	@Override
	public void setValue(String key, Object value) {
		area.put(key, value);
	}

	/**
	 * 获取内容
	 * 
	 * @return
	 */
	public Map<String, Object> getArea() {
		return area;
	}
	
	/**
	 * 获取字符串
	 * 
	 * @param key     键
	 * @return
	 */
	public String getString(String key) {
		Object obj = area.get(key);
		if(obj instanceof String){
			return (String) obj;
		}
		throw new RuntimeException(obj + " is not a string.");
	}
	
	/**
	 * 获取数字
	 * 
	 * @param key     键
	 * @return
	 */
	public double getDouble(String key) {
		Object obj = area.get(key);
		if(obj instanceof Number){
			return ((Number) obj).doubleValue();
		}
		throw new RuntimeException(obj + " is not a number.");
	}
	
	/**
	 * 获取日期
	 * 
	 * @param key     键
	 * @return
	 */
	public Date getDate(String key) {
		Object obj = area.get(key);
		if(obj instanceof Date){
			return (Date) obj;
		}
		throw new RuntimeException(obj + " is not a date.");
	}

}
