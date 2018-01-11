package com.cangoonline.model.smg3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.experian.stratman.decisionagent.business.IStrategyLoader;
import com.experian.stratman.decisionagent.runtime.DecisionAgentConfig;

/**
 * 本地策略加载器
 * 与 com.experian.stratman.decisionagent.business.AlternateStrategyLoader 效果一样
 * 
 * @author GuoZH
 * @version 1.0, 2016-12-27
 */
public class LocalStrategyLoader implements IStrategyLoader {
	
	private static final String STRATEGY_STRAT_PATH = "strategy.path";

	@Override
	public InputStream getStream(String strategyName) throws IOException {
		if(strategyName.startsWith("/")){
			strategyName = strategyName.substring(1);
		}
		InputStream inputStream = null;
		int i = 1;
		while(true){
			String strategyPath = DecisionAgentConfig.instance().getProperty(STRATEGY_STRAT_PATH + i++, "none");
			if(strategyPath.equals("none")){
				break;
			}else{
				if(!strategyPath.endsWith("/")){
					strategyPath = strategyPath + "/";
				}
				File file = new File(strategyPath, strategyName);
				if(file.exists()){
					inputStream = new FileInputStream(file);
					break;
				}
			}
		}
		if(inputStream == null){
			throw new FileNotFoundException("File not found in all configured directories");
		}
		return inputStream;
	}

}
