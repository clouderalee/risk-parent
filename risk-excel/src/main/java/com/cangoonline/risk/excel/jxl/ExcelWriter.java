package com.cangoonline.risk.excel.jxl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelWriter {
	public static void writeValue(String xlsPath,int sheetId,Object value,int rowId,int colunmId){
		WritableWorkbook workbook = getWorkbook(xlsPath);
		WritableSheet sheet = getSheet(workbook, sheetId);
		try {
			addCell(value, rowId, colunmId, sheet);
			JXLExcelUtils.writeAndFlush(workbook);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void addCell(Object value, int rowId, int colunmId,
			WritableSheet sheet) throws WriteException, RowsExceededException {
		Label label =new Label(colunmId, rowId, String.valueOf(value));
		sheet.addCell(label);
	}

	private static WritableSheet getSheet(WritableWorkbook workbook, int sheetId) {
		return JXLExcelUtils.getWritableSheet(workbook, sheetId);
	}

	public static void writeRow(String xlsPath,int sheetId,Object[] value,int rowId,int colunmId){
		writeRow(xlsPath,sheetId, Arrays.asList(value==null?new Object[0]:value), rowId, colunmId);
	}
	public static void writeRow(String xlsPath,int sheetId,List<Object> values,int rowId,int colunmId) {
		WritableWorkbook workbook = getWorkbook(xlsPath);
		WritableSheet sheet = getSheet(workbook, sheetId);
		
		for (int i = 0; i < values.size(); i++) {
			Object obj = values.get(i);
			try {
				addCell(obj, rowId, colunmId+i, sheet);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		JXLExcelUtils.writeAndFlush(workbook);
	}

	public static void writeAll(String xlsPath,int sheetId,Object[][] values,int rowId,int colunmId){
		writeAll(xlsPath,sheetId, Arrays.asList(values==null?new Object[0][]:values), rowId, colunmId);
	}
	public static void writeAll(String xlsPath,int sheetId,List<Object[]> values,int rowId,int colunmId){
		WritableWorkbook workbook = getWorkbook(xlsPath);
		WritableSheet sheet = getSheet(workbook, sheetId);
		for (int i = 0; i < values.size(); i++) {
			Object[] objects = values.get(i);
			for (int j = 0; j < objects.length; j++) {
				try {
					addCell(objects[j], rowId+i, colunmId+j, sheet);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		JXLExcelUtils.writeAndFlush(workbook);
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
		WritableWorkbook workbook = getWorkbook(xlsPath);
		WritableSheet sheet = getSheet(workbook, sheetId);
        for (int j = 0; j < dataList.size(); j++) {
            Object t = dataList.get(j);
            for(int k=0; k<beanPropertys.length; k++){
                Object value = null;
				try {
					value = getBeanProperty(t, beanPropertys[k]);
					addCell(value, ignoreRow+j, k, sheet);
				} catch (Exception e) {
					e.printStackTrace();
				}
            }            
        }
        JXLExcelUtils.writeAndFlush(workbook);
    }
	
	private static WritableWorkbook getWorkbook(String xlsPath) {
		return JXLExcelUtils.getWritableWorkbook(xlsPath);
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
