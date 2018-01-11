package com.cangoonline.riskdecision.flows.jpmml;

import com.cangoonline.engineflow.ModelActuator;
import com.cangoonline.model.LocalModelService;
import com.cangoonline.model.LocalModelServiceFactory;
import com.cangoonline.model.ModelConfigKey;
import com.cangoonline.risk.common.CodeException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
@Component
public class OpenScoringModelActuator implements ModelActuator {
    public Map<String, Object> execute(Map<String, Object> dataModel, Properties properties)
            throws Exception {
        Map<String, Object> result = null;

        if(!properties.containsKey(ModelConfigKey.JPMML_KEY_MODELFILEPATH)){
            throw new CodeException(405,"引擎流程配置错误");
        }

        //调用模型服务
        LocalModelService modelService = LocalModelServiceFactory.getJPmmlModelService(properties);
        result = (Map<String, Object>) modelService.executeRule(dataModel);
        return result;
    }
}
