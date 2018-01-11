package com.cangoonline.model.jpmml.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dmg.pmml.FieldName;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class CsvHelper {
	private CsvHelper(){}
	public static CsvTable readCsvTable(InputStream is) throws IOException {
		return readCsvTable(is, null);
	}

	public static CsvTable readCsvTable(InputStream is, String separator)  throws IOException {
		CsvTable table = new CsvTable();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			Splitter splitter = null;
			while(true){
                String line = reader.readLine();
                if(line==null||"".equals(line.trim()))
                    break;
                if(separator == null)
                    separator = getSeparator(line);
                if(splitter == null)
                    splitter = Splitter.on(separator);
                List<String> row = Lists.newArrayList(splitter.split(line));
                table.add(row);
            }
		}finally {
			try {
				if(reader!=null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		table.setSeparator(separator);
		return table;
	}

	public static void writeCsvTable(CsvTable table, OutputStream os) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			Joiner joiner = Joiner.on(table.getSeparator());

			for(int i = 0; i < table.size(); i++){
				List<String> row = table.get(i);
				if(i > 0){
					writer.write('\n');
				}
				writer.write(joiner.join(row));
			}
		}finally {
			try {
				if(writer!=null) writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Map<FieldName, String>> parseRecords(List<List<String>> table, Function<String, String> function){
		List<Map<FieldName, String>> records = new ArrayList(table.size() - 1);
		List<String> headerRow = table.get(0);
		Set<String> uniqueHeaderRow = new LinkedHashSet(headerRow);
		if(uniqueHeaderRow.size() < headerRow.size()){
			Set<String> duplicateHeaderCells = new LinkedHashSet();
			for(int column = 0; column < headerRow.size(); column++){
				String headerCell = headerRow.get(column);
				if(Collections.frequency(headerRow, headerCell) != 1){
					duplicateHeaderCells.add(headerCell);
				}
			}
			if(duplicateHeaderCells.size() > 0){
				throw new IllegalArgumentException("Expected unique cell names, but got non-unique cell name(s) " + duplicateHeaderCells);
			}
		}
		for(int row = 1; row < table.size(); row++){
			List<String> bodyRow = table.get(row);

			if(headerRow.size() != bodyRow.size()){
				throw new IllegalArgumentException("Expected " + headerRow.size() + " cells, but got " + bodyRow.size() + " cells (data record " + (row - 1) + ")");
			}

			Map<FieldName, String> record = new LinkedHashMap();

			for(int column = 0; column < headerRow.size(); column++){
				FieldName name = FieldName.create(headerRow.get(column));
				String value = function.apply(bodyRow.get(column));

				record.put(name, value);
			}

			records.add(record);
		}

		return records;
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<List<String>> formatRecords(List<Map<FieldName, ?>> records, List<FieldName> names, Function<Object, String> function){
		List<List<String>> table = new ArrayList(1 + records.size());
		List<String> headerRow = new ArrayList(names.size());
		for(FieldName name : names){
			headerRow.add(name != null ? name.getValue() : "(null)");
		}
		table.add(headerRow);
		for(Map<FieldName, ?> record : records){
			List<String> bodyRow = new ArrayList(names.size());
			for(FieldName name : names){
				bodyRow.add(function.apply(record.get(name)));
			}
			table.add(bodyRow);
		}
		return table;
	}




	private static String getSeparator(String line){
		String[] separators = {"\t", ";", ","};
		for(String separator : separators){
			String[] cells = line.split(separator);
			if(cells.length > 1){
				return separator;
			}
		}
		throw new IllegalArgumentException();
	}
	
	public static void main(String[] args) {
		
	}
}
