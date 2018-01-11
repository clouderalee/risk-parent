package com.cangoonline.model.jpmml.common;

import org.jpmml.evaluator.EvaluatorUtil;

import com.google.common.base.Function;

public class CsvParserFunction {
	public static final Function<String, String> CSV_PARSER = new Function<String, String>(){
		public String apply(String string){
			if(("").equals(string) || ("N/A").equals(string) || ("NA").equals(string)){
				return null;
			}
			// Remove leading and trailing quotation marks
			string = stripQuotes(string, '\"');
			string = stripQuotes(string, '\"');
			// Standardize European-style decimal marks (',') to US-style decimal marks ('.')
			if(string.indexOf(',') > -1){
				String usString = string.replace(',', '.');
				try {
					Double.parseDouble(usString);
					string = usString;
				} catch(NumberFormatException nfe){
					System.out.println(nfe.getMessage());
				}
			}

			return string;
		}
		private String stripQuotes(String string, char quoteChar){

			if(string.length() > 1 && ((string.charAt(0) == quoteChar) && (string.charAt(string.length() - 1) == quoteChar))){
				return string.substring(1, string.length() - 1);
			}
			return string;
		}
	};
	
	public static final Function<Object, String> CSV_FORMATTER = new Function<Object, String>(){
		public String apply(Object object){
			object = EvaluatorUtil.decode(object);

			if(object == null){
				return "N/A";
			}
			return object.toString();
		}
	};
}
