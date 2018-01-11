package com.cangoonline.engineflow.scriptext.javascript;

import java.util.Hashtable;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public abstract class SimpleJavaScriptRow {
	protected Context ctx;
	protected Scriptable scope;
	
	private static Map<String, Object> cache = new Hashtable<>();
	
	abstract boolean nextRow(int index,String javaScript ,Object result,Exception e);
	
	public SimpleJavaScriptRow() {
		//1.进入上下文
		ctx = Context.enter();
		//2.//初始化标准对象
		scope = ctx.initStandardObjects();
	}
	
	/**
	 * 定义一些工具类,在js脚本中使用
	 */
	protected void putTools(){
		putJavaObject("sout", System.out);
	}
	
	protected void putJavaObject(String key,Class<?> clazz){
		init();
		Object toolObject = null;
		if(cache.containsKey(key)){
			if(cache.get(key).getClass() == clazz){
				toolObject = cache.get(key);
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
		putJavaObject(key, toolObject);
		cache.put(key, toolObject);
	}
	
	protected void putJavaObject(String key,Object obj){
		init();
		Object jsObject = Context.javaToJS(obj, scope);
		ScriptableObject.putProperty(scope, key, jsObject);
	}
	public void init(){
		if(ctx == null) 
			ctx = Context.enter();
		if(scope == null) 
			scope = ctx.initStandardObjects();
	}
	public void destory(){
		Context.exit();
	}
	public Context getCtx() {
		return ctx;
	}
	public Scriptable getScope() {
		return scope;
	}
	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}
	public void setScope(Scriptable scope) {
		this.scope = scope;
	}
}
