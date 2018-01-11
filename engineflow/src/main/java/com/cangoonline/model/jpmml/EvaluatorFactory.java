package com.cangoonline.model.jpmml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.model.PMMLUtil;

public class EvaluatorFactory {
	
	public static Evaluator getEvaluator(File file) throws Exception{
		Evaluator evaluator = null;
		ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();
		evaluator = modelEvaluatorFactory.newModelEvaluator(PMMLUtil.unmarshal(new FileInputStream(file)));
		evaluator.verify();
		return evaluator;
	}
	
	public static Evaluator getEvaluator(InputStream inputStream) throws Exception{
		Evaluator evaluator = null;
		ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();
		evaluator = modelEvaluatorFactory.newModelEvaluator(PMMLUtil.unmarshal(inputStream));
		evaluator.verify();
		return evaluator;
	}
	
	public static Evaluator getEvaluator(String modelFilePath) throws Exception{
		Evaluator evaluator = null;
		ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();
		InputStream inputStream = null;
		try {
			if(modelFilePath == null || "".equals(modelFilePath))
				return null;
			modelFilePath = modelFilePath.trim();
			if(modelFilePath.startsWith("classpath:")){
				inputStream = EvaluatorFactory.class.getClassLoader().getResourceAsStream(modelFilePath.substring(10));
			}else{
				inputStream = new FileInputStream(modelFilePath);
			}
			evaluator = modelEvaluatorFactory.newModelEvaluator(PMMLUtil.unmarshal(inputStream));
			evaluator.verify();
		}catch (Exception e) {
			throw e;
		}
        return evaluator;
	}

}
