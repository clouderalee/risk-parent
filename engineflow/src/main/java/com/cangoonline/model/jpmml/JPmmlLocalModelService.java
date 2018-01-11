package com.cangoonline.model.jpmml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.EvaluatorUtil;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.ResultField;

import com.cangoonline.model.LocalModelService;
import com.cangoonline.model.jpmml.common.CsvHelper;
import com.cangoonline.model.jpmml.common.CsvParserFunction;
import com.cangoonline.model.jpmml.common.CsvTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class JPmmlLocalModelService implements LocalModelService{
	private static final String separator = ",";

	private String modelFilePath;
	
	public JPmmlLocalModelService() {
	}
	public JPmmlLocalModelService(String modelFilePath) {
		this.modelFilePath = modelFilePath;
	}

	@Override
	public Object executeRule(Map<String, Object> inputParams) throws Exception {
		return excute(EvaluatorFactory.getEvaluator(modelFilePath), inputParams);
	}
	
	public static Map<String, Object> excute(String modelFile, Map<String, Object> inputParams) throws Exception {
		return excute(EvaluatorFactory.getEvaluator(modelFile), inputParams);
	}
	public static Map<String, Object> excute(File file, Map<String, Object> inputParams) throws Exception {
		return excute(EvaluatorFactory.getEvaluator(file), inputParams);
	}
	public static Map<String, Object> excute(InputStream inputStream, Map<String, Object> inputParams) throws Exception {
		return excute(EvaluatorFactory.getEvaluator(inputStream), inputParams);
	}
	public static Map<String, Object> excute(Evaluator evaluator, Map<String, Object> inputParams) throws Exception {
        Map<String, Object> result = null;
        try {
            Map<FieldName, FieldValue> arguments = new LinkedHashMap<FieldName, FieldValue>();
            List<InputField> inputFields = evaluator.getInputFields();
            for (InputField inputField : inputFields) {
                FieldName name = inputField.getName();
                FieldValue value = EvaluatorUtil.prepare(inputField, inputParams.get(inputField.getName().getValue()));
                arguments.put(name, value);
            }
            Map<FieldName, ?> evaluateResult = evaluator.evaluate(arguments);
            result = toStringObjectMap(evaluateResult);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
	
	
	public static void batchTestExcute(String modelFilePath, String inputCsvFile, String outputCsvFile) throws Exception {
		batchTestExcute(EvaluatorFactory.getEvaluator(modelFilePath), new File(inputCsvFile), new File(outputCsvFile));
	}
	public static void batchTestExcute(String modelFilePath, File inputCsvFile, File outputCsvFile) throws Exception {
		batchTestExcute(EvaluatorFactory.getEvaluator(modelFilePath), inputCsvFile, outputCsvFile);
	}
	
	 /**
     * 批量测试模型
     * @param evaluator
     * @param inputCsvFile 输入来自文件
     * @param outputCsvFile 输出来自文件
     * @throws Exception
     */
    public static void batchTestExcute(Evaluator evaluator, File inputCsvFile, File outputCsvFile) throws Exception {
        //输入字段
        ArrayList<List<String>> inputCsvTable = CsvHelper.readCsvTable(new FileInputStream(inputCsvFile), separator);
        List<Map<FieldName, String>> inputRecords = CsvHelper.parseRecords(inputCsvTable, CsvParserFunction.CSV_PARSER);

        List<InputField> inputFields = evaluator.getInputFields();
        checkInputFields(inputRecords, inputFields);

        //输出的字段和值
        List<Map<FieldName, ?>> outputRecords = new ArrayList<Map<FieldName, ?>>(inputRecords.size());

        //循环调用模型
        Map<FieldName, FieldValue> arguments = new LinkedHashMap<FieldName, FieldValue>();
        for(Map<FieldName, ?> inputRecord : inputRecords){
            arguments.clear();
            for(InputField inputField : inputFields){
                FieldName name = inputField.getName();
                FieldValue value = EvaluatorUtil.prepare(inputField, inputRecord.get(name));
                arguments.put(name, value);
            }
            Map<FieldName, ?> result = evaluator.evaluate(arguments);
            outputRecords.add(result);
            System.out.println("执行结果："+result);
        }

        //将结果写入到CSV
        CsvTable outputCsvTable = new CsvTable();
        outputCsvTable.setSeparator(separator );

        List<ResultField> resultFields = Lists.newArrayList(Iterables.concat(evaluator.getTargetFields(), evaluator.getOutputFields()));
        outputCsvTable.addAll(CsvHelper.formatRecords(outputRecords, EvaluatorUtil.getNames(resultFields), CsvParserFunction.CSV_FORMATTER));

        //复制输入字段到结果CSV
        if((inputCsvTable.size() == outputCsvTable.size())){
            for(int i = 0; i < inputCsvTable.size(); i++){
                List<String> inputRow = inputCsvTable.get(i);
                List<String> outputRow = outputCsvTable.get(i);
                outputRow.addAll(0, inputRow);
            }
        }
        CsvHelper.writeCsvTable(outputCsvTable, new FileOutputStream(outputCsvFile));

    }
	
	private static Map<String, Object> toStringObjectMap(Map<FieldName, ?> evaluateResult) throws Exception {
        //转化Map
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Set<FieldName> fieldNames = evaluateResult.keySet();
        for (FieldName f:fieldNames ) {
            String key = f.getValue();
            resultMap.put(key,evaluateResult.get(f));
        }
        return resultMap;
    }
	
	private static void checkInputFields(List<Map<FieldName, String>> inputRecords,List<InputField> inputFields) {
        if(inputRecords.size() > 0){
            Map<FieldName, String> inputRecord = inputRecords.get(0);
			Sets.SetView<FieldName> missingInputFields = Sets.difference(new LinkedHashSet<FieldName>(EvaluatorUtil.getNames(inputFields)), inputRecord.keySet());
            if((missingInputFields.size() > 0)){
                throw new IllegalArgumentException("Missing input field(s): " + missingInputFields.toString());
            }
        }
    }
	
	public String getModelFilePath() {
		return modelFilePath;
	}
	public void setModelFilePath(String modelFilePath) {
		this.modelFilePath = modelFilePath;
	}

}
