package com.cangoonline.risk.excel.poi;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


/**
 * Excel数据读取
 * 读取2003excel需要jar:
 * 		poi-3.16.jar,
 * 		poi-ooxml-3.16.jar
 * 读取2007excel需要jar:
 * 		commons-collections4-4.1.jar,
 * 		poi-3.16.jar,
 * 		poi-ooxml-3.16.jar,
 * 		poi-ooxml-schemas-3.16.jar,
 * 		xmlbeans-2.6.0.jar
 * @author kancy
 *
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
			Sheet sheet = workbook.getSheetAt(sheetId);
			// 直接从第三行获取数据
            int rowSize = sheet.getPhysicalNumberOfRows();
            if(rowSize > ignoreRow){                
                for (int i = ignoreRow; i < rowSize; i++) {
                    Row row = sheet.getRow(i);
                    int cellSize = row.getPhysicalNumberOfCells();
                    List<Cell> cells = new ArrayList<Cell>();
                    if(cellSize > ignoreColunm){
                        for(int j=ignoreColunm; j< cellSize; j++){
                        	cells.add(row.getCell(j));
                        }                        
                    }
                    list.add(cells);
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
		Sheet sheet = workbook.getSheetAt(sheetId);
		int rowSize = sheet.getPhysicalNumberOfRows();
		
		List<Cell> list = new ArrayList<Cell>();
		if(rowSize > ignoreRow){
			for (int i = ignoreRow; i < rowSize; i++) {
				Row row = sheet.getRow(i);
				int numberOfCells = row.getPhysicalNumberOfCells();
				if(numberOfCells>colunmId){
					list.add(row.getCell(colunmId));
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
        public static <T> List<T> parserExcel(Class<T> clazz, File file, String[] beanPropertys) {
            // 得到workbook
            List<T> list = new ArrayList<T>();
            try {
                Workbook workbook = WorkbookFactory.create(file);
                Sheet sheet = workbook.getSheetAt(0);
                // 直接从第三行获取数据
                int rowSize = sheet.getPhysicalNumberOfRows();
                if(rowSize > 2){
                    for (int i = 2; i < rowSize; i++) {
                        T t = clazz.newInstance();
                        Row row = sheet.getRow(i);
                        int cellSize = row.getPhysicalNumberOfCells();
                        for(int j=0; j<cellSize; j++){
                            
                            Object cellValue = getCellValue(row.getCell(j));
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
        return POIExcelUtils.getCellValue(cell);
    }
	
	public static Workbook getWorkbook(String xlsfilePath) throws Exception{
        return POIExcelUtils.getWorkbook(xlsfilePath);
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
	
}
