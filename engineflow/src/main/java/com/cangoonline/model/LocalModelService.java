package com.cangoonline.model;

import java.util.Map;

public interface LocalModelService {
	Object executeRule(Map<String,Object> input) throws Exception;
}
