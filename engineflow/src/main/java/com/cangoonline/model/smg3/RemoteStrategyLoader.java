package com.cangoonline.model.smg3;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.experian.stratman.decisionagent.business.IStrategyLoader;
import com.experian.stratman.decisionagent.runtime.DecisionAgentConfig;

/**
 * 远程策略加载器
 * 
 * @author GuoZH
 * @version 1.0, 2016-12-27
 */
public class RemoteStrategyLoader implements IStrategyLoader {
	
	private static final Logger LOG = Logger.getLogger(RemoteStrategyLoader.class);
	
	private static final String STRATEGY_STRAT_URL = "strategy.url";

	@Override
	public InputStream getStream(String strategyName) throws IOException {
		if(strategyName.startsWith("/")){
			strategyName = strategyName.substring(1);
		}
		InputStream inputStream = null;
		int i = 1;
		while(true){
			String strategyUrl = DecisionAgentConfig.instance().getProperty(STRATEGY_STRAT_URL + i++, "none");
			if(strategyUrl.equals("none")){
				break;
			}else{
				try {
					if(!strategyUrl.endsWith("/")){
						strategyUrl = strategyUrl + "/";
					}
					URL url = new URL(strategyUrl + strategyName);
					inputStream = url.openStream();
					break;
				} catch (MalformedURLException e) {
					if(!strategyName.endsWith(".KEY")){
						LOG.error(strategyUrl, e);
					}
				} catch (IOException e) {
					if(!strategyName.endsWith(".KEY")){
						String message = e.getMessage();
						if(message.startsWith("Connection") || message.contains("ProtocolException")){
							LOG.error(strategyUrl, e);
						}
					}
				}
			}
		}
		if(inputStream == null){
			throw new FileNotFoundException("File not found in all configured urls");
		}
		return inputStream;
	}

}
