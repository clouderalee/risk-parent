package com.cangoonline.engineflow.scriptext.javascript;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

import com.cangoonline.engineflow.common.Tools;

public class JavaScriptHelper {
	private static Map<String, Object> toolsCache = new Hashtable<>();
	
	public static <T> T excuteScript(String javaScript,Class<T> clazz){
		return Tools.castType(excuteScript(javaScript), clazz);
	}
	
	/**
	 * 单一执行
	 * @param script
	 * @param row
	 */
	public static void excuteScript(String script,SimpleJavaScriptRow row){
		excuteScript(Arrays.asList(new String[]{script}),row);
	}
	/**
	 * 批量执行
	 * @param scripts
	 * @param row
	 */
	public static void excuteScript(List<String> scripts,SimpleJavaScriptRow row){
		try {
			//1.初始化对象
			row.init();
			//2.内置java对象
			row.putTools();
			//3.执行js脚本
			for (int index = 0; index < scripts.size(); index++) {
				String javaScript = scripts.get(index);
				boolean nextFlag = true;
				try {
					nextFlag = row.nextRow(index, javaScript , jsClassToJava(row.getCtx().evaluateString(row.getScope(), javaScript, null, 0, null)) , null);
				} catch (Exception e) {
					nextFlag = row.nextRow(index, javaScript , null , e);
				}
				if(!nextFlag) break;
			}
		}finally{
			row.destory();
		}
	}
	
	public static void excuteScript(String script,JavaScriptRow row){
		excuteScript(Arrays.asList(new String[]{script}),row);
	}
	/**
	 * 批量执行
	 * @param scripts
	 * @param row
	 */
	public static void excuteScript(List<String> scripts,JavaScriptRow row){
		try {
			//1.进入上下文
			Context ctx = Context.enter();
			//2.//初始化标准对象
			Scriptable scope = ctx.initStandardObjects();
			//3.执行js脚本
			for (int index = 0; index < scripts.size(); index++) {
				String javaScript = scripts.get(index);
				boolean nextFlag = true;
				try {
					nextFlag = row.nextRow(index, javaScript , jsClassToJava(ctx.evaluateString(scope, javaScript, null, 0, null)) , null);
				} catch (Exception e) {
					nextFlag = row.nextRow(index, javaScript , null , e);
				}
				if(!nextFlag) break;
			}
		}finally{
			Context.exit();
		}
	}
	
	public static Object excuteScript(String javaScript){
		return excuteScript(javaScript, (Map<String,Object>)null);
	}
	/**
	 * 执行脚本 
	 * @param javaScript
	 * @param tools 内置对象 （Object or Class）
	 * @return
	 */
	public static Object excuteScript(String javaScript , Map<String,Object> tools){
		Object result = null;
		try {
			//1.进入上下文
			Context ctx = Context.enter();
			//2.//初始化标准对象
			Scriptable scope = ctx.initStandardObjects();
			//3.设置内置对象
			if(tools!=null){
				Set<Entry<String, Object>> entrySet = tools.entrySet();
				for (Entry<String, Object> entry : entrySet) {
					Object value = entry.getValue();
					if(value instanceof Type){
						putJavaObject(scope, entry.getKey(), (Class<?>)value);
					}else{
						putJavaObject(scope, entry.getKey(), value);
					}
				}
			}
			//4.执行js脚本
			result = jsClassToJava(ctx.evaluateString(scope, javaScript, null, 0, null));
		}finally{
			Context.exit();
		}
		return result;
	}
	
	/**
	 * js对象转换为javaObject
	 * @param result
	 * @return
	 */
	private static Object jsClassToJava(Object result) {
		if(result instanceof Undefined){
			result = null;
		}
		if(result instanceof NativeJavaObject){
			return ((NativeJavaObject)result).unwrap();
		}
		return result;
	}
	
	private static void putJavaObject(Scriptable scope ,String key,Class<?> clazz){
		Object toolObject = null;
		if(toolsCache.containsKey(key)){
			if(toolsCache.get(key).getClass() == clazz){
				toolObject = toolsCache.get(key);
			}
		}
		//缓存中没有时
		if(toolObject == null){
			try {
				toolObject = clazz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		putJavaObject(scope, key, toolObject);
		toolsCache.put(key, toolObject);
	}
	
	private static void putJavaObject(Scriptable scope, String key,Object obj ){
		Object jsObject = Context.javaToJS(obj, scope);
		ScriptableObject.putProperty(scope, key, jsObject);
	}

	public static void main(String[] args) {
		
		List<String> list = new ArrayList<>();
		list.add("1+4");
		list.add("2+4");
		list.add("3+4");
		list.add("true");
		list.add("23.97");
		list.add("'WO'");
		list.add("jt.isNotEmpty('abcd')");
		list.add("sout.println('Successful')");
		
		excuteScript(list, new SimpleJavaScriptRow() {
			@Override
			protected void putTools() {
				super.putTools();
				putJavaObject("jt", com.cangoonline.engineflow.common.Tools.class);
			}
			@Override
			public boolean nextRow(int index, String javaScript, Object result, Exception e) {
				System.out.println(index+" = "+result);
				if(result instanceof org.mozilla.javascript.NativeJavaObject) {
					System.out.println(((org.mozilla.javascript.NativeJavaObject)result).unwrap().getClass());
				}
				return true;
			}
		});
		
		excuteScript(list, new SimpleJavaScriptRow() {
			@Override
			protected void putTools() {
				super.putTools();
				putJavaObject("jt", com.cangoonline.engineflow.common.Tools.class);
			}
			@Override
			public boolean nextRow(int index, String javaScript, Object result, Exception e) {
				System.out.println(index+" = "+result);
				if(result instanceof org.mozilla.javascript.NativeJavaObject) {
					System.out.println(((org.mozilla.javascript.NativeJavaObject)result).unwrap().getClass());
				}
				return true;
			}
		});

	}
	
}
