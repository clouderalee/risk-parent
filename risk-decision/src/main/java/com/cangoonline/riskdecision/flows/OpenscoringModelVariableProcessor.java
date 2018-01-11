package com.cangoonline.riskdecision.flows;

import com.cangoonline.engineflow.VariableProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
public class OpenscoringModelVariableProcessor implements VariableProcessor{
    private static Logger logger = LoggerFactory.getLogger(OpenscoringModelVariableProcessor.class);

    public void checkParameter(Map<String, Object> map, Object object) throws Exception {
        logger.info("Openscoring模型[{}]参数校验完成...",object);
    }

    public void convertParameter(Map<String, Object> map, Object object) throws Exception {
        logger.info("Openscoring模型[{}]参数转换完成...",object);
    }

    public Map<String, Object> binningParameter(Map<String, Object> map, Object object) throws Exception {

        Map<String, Object> modelInputParams = new HashMap();

        //具体的变量分箱逻辑



        logger.info("Openscoring模型[{}]参数转换完成...",object);
        return modelInputParams;
    }

    public Map<String, Object> formatResult(Map<String, Object> map, Object object) throws Exception {
        Map<String, Object> resultMap = new HashMap();

        //具体的结果参数格式化逻辑


        return resultMap;
    }
}
