package com.cangoonline.riskdecision.flows.smg3;

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
public class AutoApproveModelActuator implements ModelActuator {

    public Map<String, Object> execute(Map<String, Object> dataModel, Properties properties) throws Exception {

        Map<String, Object> result = null;

        if(!(properties.containsKey(ModelConfigKey.SMG3_KEY_ALIASNAME)
                &&properties.containsKey(ModelConfigKey.SMG3_KEY_SIGNATURENAME)
                &&properties.containsKey(ModelConfigKey.SMG3_KEY_INPUTDATAAREANAME)
                &&properties.containsKey(ModelConfigKey.SMG3_KEY_OUTPUTDATAAREANAME))){
            throw new CodeException(405,"引擎流程配置错误");
        }

        //调用模型服务
        LocalModelService modelService = LocalModelServiceFactory.getSmg3ModelService(properties);
        Object objectResult = modelService.executeRule(dataModel);
        if(objectResult==null||!(objectResult instanceof Map )){
            throw new CodeException(404,"自动审批模型执行失败");
        }else{
            result = (Map<String, Object>) objectResult;
        }
        return result;
    }
}
