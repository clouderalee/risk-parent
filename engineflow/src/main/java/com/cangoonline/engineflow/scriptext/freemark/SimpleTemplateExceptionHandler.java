package com.cangoonline.engineflow.scriptext.freemark;

import java.io.IOException;
import java.io.Writer;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class SimpleTemplateExceptionHandler implements TemplateExceptionHandler{

	@Override
	public void handleTemplateException(TemplateException paramTemplateException, Environment paramEnvironment,
			Writer paramWriter) throws TemplateException {
		System.out.println("手动处理异常开始...");
		paramTemplateException.printStackTrace();
		try {
			paramWriter.write("null");
			paramWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("手动处理异常结束...");
	}

}
