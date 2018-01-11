package com.cangoonline.engineflow;

import java.util.Map;

public class EmptyVariableProcessor implements VariableProcessor{
	@Override
	public void checkParameter(Map<String, Object> map, Object object) throws Exception {
	}
	@Override
	public void convertParameter(Map<String, Object> map, Object object) throws Exception {
	}
	@Override
	public Map<String, Object> binningParameter(Map<String, Object> map, Object object) throws Exception {
		return map;
	}
	@Override
	public Map<String, Object> formatResult(Map<String, Object> map, Object object) throws Exception {
		return map;
	}
}
