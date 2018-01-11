package com.cangoonline.risk.excel.jxl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * Excel数据读取
 */
public class ExcelReader {
	
	/**
	 * 获取所有行数数据
	 * @param xlsfilePath
	 * @param sheetId：第几张表ID
	 * @return
	 */
	public static List<List<String>> getAllRowStringData(String xlsfilePath,int sheetId){
		return getRowListData(xlsfilePath, sheetId, 0, 0);
	}
	
	public static List<List<String>> getRowListData(String xlsfilePath,int ignoreRow){
		return getRowListData(xlsfilePath, 0, ignoreRow, 0);
	}
	public static List<List<String>> getRowListData(String xlsfilePath,int sheetId,int ignoreRow){
		return getRowListData(xlsfilePath, sheetId, ignoreRow, 0);
	}
	public static List<List<String>> getRowListData(String xlsfilePath,int sheetId,int ignoreRow,int ignoreColunm){
		List<List<Cell>> list = getRowListCell(xlsfilePath, sheetId, ignoreRow, ignoreColunm);
		List<List<String>> returnList = new ArrayList<List<String>>();
		for (int i = 0; i < list.size(); i++) {
			List<Cell> cells = list.get(i);
			int size = cells.size();
			List<String> arr = new ArrayList<String>();
			for (int j = 0; j < size; j++) {
				Cell cell = cells.get(j);
				arr.add(getCellValue(cell).toString());
			}
			returnList.add(arr);
		}
		return returnList;
	}
	
	
	public static List<String[]> getAllRowArrayData(String xlsfilePath,int sheetId){
		return getRowListArrayData(xlsfilePath, sheetId, 0, 0);
	}
	
	public static List<String[]> getRowListArrayData(String xlsfilePath,int ignoreRow){
		return getRowListArrayData(xlsfilePath, 0, ignoreRow, 0);
	}
	public static List<String[]> getRowListArrayData(String xlsfilePath,int sheetId,int ignoreRow){
		return getRowListArrayData(xlsfilePath, sheetId, ignoreRow, 0);
	}
	public static List<String[]> getRowListArrayData(String xlsfilePath,int sheetId,int ignoreRow,int ignoreColunm){
		List<List<Cell>> list = getRowListCell(xlsfilePath, sheetId, ignoreRow, ignoreColunm);
		List<String[]> returnList = new ArrayList<String[]>();
		for (int i = 0; i < list.size(); i++) {
			List<Cell> cells = list.get(i);
			int size = cells.size();
			String[] arr = new String[size<=0?0:size];
			for (int j = 0; j < size; j++) {
				Cell cell = cells.get(j);
				arr[j] = getCellValue(cell).toString();
			}
			returnList.add(arr);
		}
		return returnList;
	}
	
	
	public static List<List<Cell>> getAllRowCellData(String xlsfilePath,int sheetId){
		return getRowListCell(xlsfilePath, sheetId, 0, 0);
	}
	
	public static List<List<Cell>> getRowListCell(String xlsfilePath,int ignoreRow){
		return getRowListCell(xlsfilePath, 0, ignoreRow, 0);
	}
	public static List<List<Cell>> getRowListCell(String xlsfilePath,int sheetId,int ignoreRow){
		return getRowListCell(xlsfilePath, sheetId, ignoreRow, 0);
	}
	public static List<List<Cell>> getRowListCell(String xlsfilePath,int sheetId,int ignoreRow,int ignoreColunm){
		List<List<Cell>> list = new ArrayList<List<Cell>>();
		try {
			Workbook workbook = getWorkbook(xlsfilePath);
			Sheet sheet = getSheet(workbook, sheetId);
			// 直接从第三行获取数据
            int rowSize = sheet.getRows();
            if(rowSize > ignoreRow){                
                for (int i = ignoreRow; i < rowSize; i++) {
                	Cell[] row = sheet.getRow(i);
                    list.add(Arrays.asList(row==null?new Cell[0]:row));
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	public static List<String> getOneColunmArrayData(Workbook workbook,int sheetId,int ignoreRow,int colunmId){
		List<String> stringList = new ArrayList<String>();
		List<Cell> list = getOneColunmCellData(workbook, sheetId, ignoreRow, colunmId);
		for (int i = 0; i <list.size(); i++) {
			Cell cell = list.get(i);
			
			try {
				stringList.add(getCellValue(cell).toString());
			} catch (Exception e) {
				stringList.add(cell.toString());
			}
		}
		return stringList;
	}
	
	public static List<Cell> getOneColunmCellData(Workbook workbook,int sheetId,int ignoreRow,int colunmId){
		Sheet sheet = getSheet(workbook, sheetId);
		int rowSize = sheet.getRows();
		
		List<Cell> list = new ArrayList<Cell>();
		if(rowSize > ignoreRow){
			for (int i = ignoreRow; i < rowSize; i++) {
				Cell[] row = sheet.getRow(i);
				if(row!=null&&row.length>colunmId){
					list.add(row[colunmId]);
				}
			}
		}
		return list;
	}
	
	/**
     * 读取表格为List Object
     * @param clazz
     * @param file
     * @param beanPropertys
     * @return
     */
    public static <T> List<T> parserExcel(Class<T> clazz, String xlsPath,int sheetId, String[] beanPropertys) {
        // 得到workbook
        List<T> list = new ArrayList<T>();
        try {
            Workbook workbook = getWorkbook(xlsPath);
            Sheet sheet = getSheet(workbook, sheetId);
            // 直接从第三行获取数据
            int rowSize = sheet.getRows();
            if(rowSize > 2){
                for (int i = 2; i < rowSize; i++) {
                    T t = clazz.newInstance();
                    Cell[] row = sheet.getRow(i);
                    int cellSize = row.length;
                    for(int j=0; j<cellSize; j++){
                        Object cellValue = getCellValue(row[j]);
                        setBeanProperty(t, beanPropertys[j], cellValue);
                    }                        
                    list.add(t);
                }
            }            

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }
        
	/**
     * 通用的读取excel单元格的处理方法
     * @param cell
     * @return
     */
	protected static Object getCellValue(Cell cell) {
        return cell.getContents();
    }
	public static Sheet getSheet(Workbook workbook,int index) {
		return JXLExcelUtils.getReadableSheet(workbook, index);
	}
	public static Workbook getWorkbook(String xlsfilePath) throws Exception{
        return JXLExcelUtils.getReadableWorkbook(xlsfilePath);
	}
	
	/**
	 * 给对象的指定字段设置值
	 */
	private static void setBeanProperty(Object bean,String fieldName ,Object fieldValue) throws Exception {
		Field[] fields = bean.getClass().getDeclaredFields();
		for (Field field : fields) {
			if(field.getName().equalsIgnoreCase(fieldName)){
				field.setAccessible(true);
				field.set(bean, fieldValue);
				break;
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		List<String[]> allRowArrayData = getAllRowArrayData("files/data2003.xls", 0);
		System.out.println(allRowArrayData);
		List<String> oneColunmArrayData = getOneColunmArrayData(getWorkbook("files/data2003.xls"), 0, 0, 0);
		System.out.println(oneColunmArrayData);
		List<String[]> rowListArrayData = getRowListArrayData("files/data2003.xls", 0);
		System.out.println(rowListArrayData);
	}
	
	
	
}
