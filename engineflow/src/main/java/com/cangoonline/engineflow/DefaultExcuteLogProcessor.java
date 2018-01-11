package com.cangoonline.engineflow;

import com.cangoonline.engineflow.bean.ExcuteLogObject;

public class DefaultExcuteLogProcessor implements ExcuteLogProcessor{

	@Override
	public void process(ExcuteLogObject excuteLog) throws Exception {
		String flowUniqueTag = excuteLog.getFlowUniqueTag();
		System.out.println("===========>打印执行Flow["+flowUniqueTag+"]的处理结果:");
		System.out.println("inputParam="+excuteLog.getFlowInput());
		System.out.println("outputParam="+excuteLog.getFlowOutput());
		System.out.println("resultParam="+excuteLog.getFlowResult());
		System.out.println("处理结果代码:"+excuteLog.getProcessCode());
		System.out.println("处理结果说明:"+excuteLog.getProcessMessage());
		System.out.println("处理Flow["+flowUniqueTag+"]用时:"+ getConsumeTime(excuteLog) +" 毫秒");
		System.out.println("==========================================================");
	}

	@Override
	public void processWithOutException(ExcuteLogObject excuteLog) {
		try {
			process(excuteLog);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private long getConsumeTime(ExcuteLogObject excuteLog) {
		return excuteLog.getEndTime().getTime() - excuteLog.getStartTime().getTime();
	}

}
