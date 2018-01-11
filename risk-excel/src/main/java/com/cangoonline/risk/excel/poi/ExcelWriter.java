package com.cangoonline.risk.excel.poi;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelWriter {
	public static void writeValue(String xlsPath,int sheetId,Object value,int rowId,int colunmId){
		Workbook workbook = POIExcelUtils.getWorkbook(xlsPath);
		Sheet sheet = POIExcelUtils.getSheet(workbook, sheetId);
		Row row = sheet.createRow(rowId);
		Cell cell = row.createCell(colunmId);
		POIExcelUtils.setCellValue(value, cell);
		POIExcelUtils.writeAndFlush(xlsPath, workbook);
	}
	public static void writeRow(String xlsPath,int sheetId,Object[] value,int rowId,int colunmId){
		writeRow(xlsPath,sheetId, Arrays.asList(value==null?new Object[0]:value), rowId, colunmId);
	}
	public static void writeRow(String xlsPath,int sheetId,List<Object> value,int rowId,int colunmId){
		Workbook workbook = POIExcelUtils.getWorkbook(xlsPath);
		Sheet sheet = POIExcelUtils.getSheet(workbook, sheetId);
		Row row = sheet.createRow(rowId);
		for (int i = 0; i < value.size(); i++) {
			Object obj = value.get(i);
			Cell cell = row.createCell(colunmId+i);
			POIExcelUtils.setCellValue(obj, cell);
		}
		POIExcelUtils.writeAndFlush(xlsPath, workbook);
	}

	public static void writeAll(String xlsPath,int sheetId,Object[][] values,int rowId,int colunmId){
		writeAll(xlsPath,sheetId, Arrays.asList(values==null?new Object[0][]:values), rowId, colunmId);
	}
	public static void writeAll(String xlsPath,int sheetId,List<Object[]> values,int rowId,int colunmId){
		Workbook workbook = POIExcelUtils.getWorkbook(xlsPath);
		Sheet sheet = POIExcelUtils.getSheet(workbook, sheetId);
		for (int i = 0; i < values.size(); i++) {
			Row row = sheet.createRow(rowId+i);
			Object[] objects = values.get(i);
			for (int j = 0; j < objects.length; j++) {
				Cell cell = row.createCell(colunmId+j);
				POIExcelUtils.setCellValue(objects[j], cell);
			}
		}
		POIExcelUtils.writeAndFlush(xlsPath, workbook);
	}
	
	   /**
     * 使用批量导入方法时，请注意需要导入的Bean的字段和excel对应
     * @param workbook
     * @param dataList
     * @param beanPropertys
     * @return
     */
	public static void writeObjectData(String xlsPath, int sheetId,int ignoreRow, Object[] dataList, String[] beanPropertys) {
		writeObjectData(xlsPath,sheetId, ignoreRow,Arrays.asList(dataList==null?new Object[0]:dataList), beanPropertys);
		
	}
	public static void writeObjectData(String xlsPath, int sheetId,int ignoreRow, List<Object> dataList, String[] beanPropertys) {
		Workbook workbook = POIExcelUtils.getWorkbook(xlsPath);
		Sheet sheet = POIExcelUtils.getSheet(workbook, sheetId);
        for (int j = 0; j < dataList.size(); j++) {
            Row rowData = sheet.createRow(j + ignoreRow);
            Object t = dataList.get(j);
            for(int k=0; k<beanPropertys.length; k++){
                Object value = null;
				try {
					value = getBeanProperty(t, beanPropertys[k]);
				} catch (Exception e) {
					e.printStackTrace();
				}
                Cell cellData = rowData.createCell(k);
                POIExcelUtils.setCellValue(value, cellData);
            }            
        }
        POIExcelUtils.writeAndFlush(xlsPath, workbook);
    }
	
	/**
	 * 获取对象的指定字段值
	 */
	private static Object getBeanProperty(Object bean,String fieldName) throws Exception {
		Field[] fields = bean.getClass().getDeclaredFields();
		for (Field field : fields) {
			if(field.getName().equalsIgnoreCase(fieldName)){
				field.setAccessible(true);
				return field.get(bean);
			}
		}
		return null;
		
	}
}
