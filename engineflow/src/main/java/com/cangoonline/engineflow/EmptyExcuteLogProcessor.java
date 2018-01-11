package com.cangoonline.engineflow;

import com.cangoonline.engineflow.bean.ExcuteLogObject;

public class EmptyExcuteLogProcessor implements ExcuteLogProcessor {

	@Override
	public void process(ExcuteLogObject excuteLog) throws Exception {
		System.out.println("Flow["+excuteLog.getFlowUniqueTag()+"] process reslt is "
				+excuteLog.getProcessMessage());
	}
	@Override
	public void processWithOutException(ExcuteLogObject excuteLog) {
		try {
			process(excuteLog);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
