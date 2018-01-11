package com.cangoonline.engineflow.scriptext.freemark;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kancy on 2017\11\14 0014.
 */
public class FreeMarkHelper {
	
	private static final String templateName = FreeMarkHelper.class.getName();
	
    private static Configuration configuration = FreemarkConfiguration.newInstance();
    
    public static String renderScript(Map<String, Object> dataModel, String freeMarkScript)
            throws IOException, TemplateException {
        StringWriter result = new StringWriter();
        Template t = new Template(templateName, new StringReader(freeMarkScript), configuration);
        t.process(dataModel, result);
        return result.toString();
    }
    
    public static void main(String[] args) {
    	String freeMarkScript = "我是${name}!";
		Map<String,Object> dataModel = new HashMap<>();
		dataModel.put("name", "Hello World");
		
		String renderScript = null;
		try {
			renderScript = renderScript(dataModel, freeMarkScript);
			System.out.println(renderScript);
		} catch (TemplateException e){
			//template_exception_handler 会处理
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
