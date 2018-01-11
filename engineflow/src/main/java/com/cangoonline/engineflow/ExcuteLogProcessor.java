package com.cangoonline.engineflow;

import com.cangoonline.engineflow.bean.ExcuteLogObject;

public interface ExcuteLogProcessor {
	public void process(ExcuteLogObject excuteLog) throws Exception;
	public void processWithOutException(ExcuteLogObject excuteLog);
}
