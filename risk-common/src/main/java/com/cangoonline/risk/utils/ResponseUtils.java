package com.cangoonline.risk.utils;

import com.alibaba.fastjson.JSON;
import com.cangoonline.risk.entity.ResponseJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseUtils {
	private static final Logger logger = LoggerFactory.getLogger(ResponseUtils.class);
	
	public static void doResponse(HttpServletRequest request, HttpServletResponse response, Object obj) {
		
		PrintWriter out = null;
		try {
			String jsonStr = JSON.toJSONString(obj);
			response.setContentType("text/html; charset=utf-8");
			// 设置P3P头实现跨域cookie写入
			response.setHeader("P3P", "CP=CAO PSA OUR");
			// 设置跨域规则
			response.setHeader("Access-Control-Allow-Origin", "*");
			out = response.getWriter();
			String cb = request.getParameter("callback");
            if(cb != null){//如果是跨域
            	StringBuffer sb = new StringBuffer(cb);  
            	sb.append("(");
            	sb.append(jsonStr.toString()); 
            	sb.append(")");
            	out.write(sb.toString());
            }else{//不跨域的情况  
            	out.write(jsonStr.toString());
            }
			out.flush();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}finally {
			if(out!=null) out.close();
		}
	}
	
	public static ResponseJson getResponseJson(){
		return new ResponseJson();
	}
    
}
