package com.cangoonline.model.smg3;

import java.nio.charset.Charset;
import java.util.Map;

import com.experian.stratman.datasources.runtime.IData;
import com.experian.stratman.decisionagent.business.OS390.Batch.BatchJSEMObjectInterface;
import com.experian.stratman.decisionagent.business.LookUpManager;
import com.experian.stratman.decisionagent.business.OS390.Batch.BatchJSEMByteInterface;
import com.experian.stratman.decisionagent.business.DAManagementInterface;

/**
 * 规则引擎
 * 
 * @author GuoZH
 * @version 1.0, 2015-09-26
 * @version 2.0, 2015-11-16
 * @version 3.0, 2016-09-02
 */
public class RuleEngine {
	
	//字符编码
	private static String codepage = Charset.defaultCharset().name();
	
	/**
	 * 执行规则
	 * Java 物理定义
	 * 
	 * @param daLogLevel    日志级别
	 * @param iDatas        数据区列表
	 * @return
	 */
	public static int executeRule(int daLogLevel, IData... iDatas){
		//instance() return Object is static final
		return BatchJSEMObjectInterface.instance().execute(iDatas, daLogLevel);
	}
	
	/**
	 * 执行规则
	 * Byte Array 物理定义
	 * 
	 * @param daLogLevel    日志级别
	 * @param byteAreas     字节区列表
	 * @return
	 */
	public static int executeRule(int daLogLevel, ByteArea... byteAreas){
		//instance() return Object is static final
		//合并字节区列表
		int length = 0;
		for(ByteArea byteArea: byteAreas){
			length += byteArea.getArea().length;
		}
		byte[] datas = new byte[length];
		int offset = 0;
		for(ByteArea byteArea: byteAreas){
			byte[] area = byteArea.getArea();
			System.arraycopy(area, 0, datas, offset, area.length);
			byteArea.setOffset(offset);//便于刷新
			offset += area.length;
		}
		
		//执行规则
		int returnCode = BatchJSEMByteInterface.instance().execute(datas, codepage.getBytes(), daLogLevel);
		
		//刷新字节区
		for(ByteArea byteArea: byteAreas){
			byteArea.flushArea(datas);
		}
		
		return returnCode;
	}
	
	/**
	 * 执行规则(基本模式)
	 * Byte Array 物理定义
	 * 
	 * @param datas         字节数据
	 * @param daLogLevel    日志级别
	 * @return
	 */
	public static int executeRule(byte[] datas, int daLogLevel){
		//instance() return Object is static final
		return BatchJSEMByteInterface.instance().execute(datas, codepage.getBytes(), daLogLevel);
	}
	
	/**
	 * 获取原因码和描述
	 * 
	 * @param reasonAlias   原因码别名
	 * @param signature     签名
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getReasonCodeDescs(String reasonAlias, String signature){
		//getInstance(String reasonAlias, String signature) is static synchronized
		LookUpManager lookUpManager = LookUpManager.getInstance(reasonAlias, signature);
		return lookUpManager.getAllReasonCodeDescs();
	}
	
	/**
	 * 载入策略
	 * @param alias         别名
	 * @return
	 */
	public static String loadStrategy(String alias){
		return DAManagementInterface.loadStrategy(alias);
	}
	
	/**
	 * 重载策略
	 * 热部署时，须调用此方法
	 * 
	 * @param alias         别名
	 * @return
	 */
	public static String reloadStrategy(String alias){
		//已载入，做重载操作
		if(DAManagementInterface.isStrategyLoaded(alias)){
			DAManagementInterface.unloadStrategy(alias);
			return DAManagementInterface.loadStrategy(alias);
		}else{//未载入，做载入操作
			return DAManagementInterface.loadStrategy(alias);
		}
	}
	
	/**
	 * 获取载入的策略
	 * 
	 * @return
	 */
	public static String[] getLoadedStrategies(){
		return DAManagementInterface.getLoadedStrategies();
	}
	
	/**
	 * 卸载策略
	 * 
	 * @param alias         别名
	 * @return
	 */
	public static void unloadStrategy(String alias){
		DAManagementInterface.unloadStrategy(alias);
	}
	
	/**
	 * 卸载所有策略
	 * 
	 */
	public static void unloadAllStrategies(){
		DAManagementInterface.unloadAllStrategies();
	}
	
	/**
	 * 获取策略创建日期，版本号信息
	 * 
	 * @param alias         别名
	 * @return
	 */
	public static String getStrategyInfo(String alias){
		//载入之后，才能获取策略信息
		if(!DAManagementInterface.isStrategyLoaded(alias)){
			DAManagementInterface.loadStrategy(alias);
		}
		return DAManagementInterface.getStrategyInfo(alias);
	}
	
	/**
	 * 打开策略性能分析器
	 * 
	 * @return
	 */
	public static boolean enableProfiler(){
		//不必重复打开分析器
		if(!DAManagementInterface.isProfilerEnabled()){
			return DAManagementInterface.enableProfiler();
		}
		return true;
	}
	
	/**
	 * 创建策略性能分析报告
	 * 
	 * @return
	 */
	public static boolean createProfilerReport(){
		boolean b = false;
		//分析器是开着的，才会有分析报告
		if(DAManagementInterface.isProfilerEnabled()){
			//创建分析报告之前，要先关闭分析器
			DAManagementInterface.disableProfiler();
			b = DAManagementInterface.createProfilerReport();
			DAManagementInterface.clearProfiler();
		}
		return b;
	}

}
