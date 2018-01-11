package com.cangoonline.risk.excel.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class POIExcelUtils {
	
        /**
         * General method of reading excel cells
         * @param cell
         * @return
         */
		@SuppressWarnings("deprecation")
		public static Object getCellValue(Cell cell) {
            Object result = null;
            if (cell != null) {
                switch (cell.getCellType()) {
                case 1://Cell.CELL_TYPE_STRING
                    result = cell.getStringCellValue();
                    break;
                case 0://Cell.CELL_TYPE_NUMERIC
                    //对日期进行判断和解析
                    if(HSSFDateUtil.isCellDateFormatted(cell)){
                        double cellValue = cell.getNumericCellValue();
                        result = new SimpleDateFormat("yyyy/MM/dd").format(HSSFDateUtil.getJavaDate(cellValue));
                    }
                    break;
                case 4://Cell.CELL_TYPE_BOOLEAN
                    result = cell.getBooleanCellValue();
                    break;
                case 2://Cell.CELL_TYPE_FORMULA
                    result = cell.getCellFormula();
                    break;
                case 5://Cell.CELL_TYPE_ERROR
                    result = cell.getErrorCellValue();
                    break;
                case 3://Cell.CELL_TYPE_BLANK
                    break;
                default:
                    break;
                }
            }
            return result;
        }
        
        public static void setCellValue(Object obj, Cell cell) {
    		if(obj instanceof Double){
    			cell.setCellValue(((Double) obj).doubleValue());
    		}else if(obj instanceof Integer){
    			cell.setCellValue(((Integer) obj).intValue());
    		}else if(obj instanceof Long){
    			cell.setCellValue(((Long) obj).longValue());
    		}else if(obj instanceof Date){
    			cell.setCellValue(((Date) obj));
    		}else{
    			cell.setCellValue(String.valueOf(obj));
    		}
    	}
        
        /**
         * Write a workbook to file
         * @param xlsPath
         * @param workbook
         */
    	public static void writeAndFlush(String xlsPath, Workbook workbook) {
    		try {
    			FileOutputStream out = new FileOutputStream(new File(xlsPath));
    			workbook.write(out);
    			out.flush();
    			out.close();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
        
    	/**
    	 * Get Sheet by index
    	 * @param workbook
    	 * @param index
    	 * @return
    	 */
    	public static Sheet getSheet(Workbook workbook,int index) {
    		Sheet sheet = null;
    		try {
    			sheet = workbook.getSheetAt(index);
    		} catch (Exception e) {
    			if(sheet==null){
    				sheet = workbook.createSheet();
    				workbook.setSheetName(index, "sheet"+(index+1));
    			}
    		}
    		return sheet;
    	}
        
    	/**
    	 * Get Readable, writable, or appended Workbook
    	 * @param xlsPath
    	 * @return
    	 */
    	public static Workbook getWorkbook(String xlsPath)  {
    		
    		Workbook workbook = null;
    		File file = new File(xlsPath);
    		xlsPath = xlsPath.substring(xlsPath.lastIndexOf(".")).toLowerCase();
    	
    		try {
	    		if(file.exists()&&file.isFile()){
						workbook = WorkbookFactory.create(new FileInputStream(file));
	    		}else{
	    			if(".xls".equals(xlsPath)){
	        			workbook = new HSSFWorkbook();
	        		}else if(".xlsx".equals(xlsPath)){
	        			workbook = new XSSFWorkbook();
	        		}else{
	        			throw new Exception("Unsupported excel file type !");
	        		}
	    		}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		return workbook;
    	}
}
