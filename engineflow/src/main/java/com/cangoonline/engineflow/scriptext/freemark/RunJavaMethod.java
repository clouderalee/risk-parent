package com.cangoonline.engineflow.scriptext.freemark;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.cangoonline.engineflow.common.Tools;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class RunJavaMethod implements TemplateMethodModelEx{
	public Object execute(List<String> param) throws TemplateModelException {
		
		if(param==null||param.size()<1)
			throw new RuntimeException("RunJavaMethod参数格式错误.");
		Object result = null;
		try {
			String className = param.get(0);
			Class<?> clazz = Class.forName(className);
			Object object = clazz.newInstance();
			if(!(object instanceof RunObject)){
				throw new RuntimeException(clazz+" not instanceof com.freemark.extend.method.RunObject");
			}
			
			String method = null;
			if(param.get(1).contains("=")){
				method = "execute";
				//参数复制
				for (int i = 1; i < param.size(); i++) {
					String p = param.get(i);
					String[] f = p.split("=");
					Field field = clazz.getDeclaredField(f[0].trim());
					if(field!=null){
						field.setAccessible(true);
						field.set(object, castObject(field.getType(), f[1].trim()));
					}
				}
			}else{
				method = param.get(1).trim();
				if(param.size()>2){
					//参数复制
					for (int i = 2; i < param.size(); i++) {
						String p = param.get(i);
						String[] f = p.split("=");
						Field field = clazz.getDeclaredField(f[0].trim());
						if(field!=null){
							field.setAccessible(true);
							field.set(object, castObject(field.getType(), f[1].trim()));
						}
					}
				}
			}
			
			//调用方法
			Method m = clazz.getDeclaredMethod(method);
			result = m.invoke(object);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("RunJavaMethod运行错误:"+e.getMessage());
		}
		return result;
	}
	
	/**
	 * 字符串转化为指定类型对象
	 * @param clazz
	 * @param object
	 * @return
	 */
	private Object castObject(Class<?> clazz , String object){
		return Tools.castType(object, clazz, true);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object exec(List args) throws TemplateModelException {
		if(args==null||args.size()<=0){
			return null;
		}
		List<String> param = new ArrayList<String>();
		for (Object object : args) {
			SimpleScalar simpleScalar = (SimpleScalar) object;
			param.add(simpleScalar.getAsString());
		}
		return execute(param);
	}
}
