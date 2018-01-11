package com.cangoonline.risk.excel.jxl;

import java.io.File;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class JXLExcelUtils {
	
    	public static void writeAndFlush(WritableWorkbook workbook) {
    		try {
    			workbook.write();
    			workbook.close();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
        
    	public static Sheet getReadableSheet(Workbook workbook,int index) {
    		Sheet sheet = workbook.getSheet(index);
    		return sheet;
    	}
    	public static WritableSheet getWritableSheet(WritableWorkbook workbook,int index) {
    		WritableSheet writableSheet = null;
			try {
				writableSheet = workbook.getSheet(index);
			} catch (IndexOutOfBoundsException e) {
				if(writableSheet==null){
					writableSheet = workbook.createSheet("sheet"+(index+1), index);
				}
			}
    		return writableSheet;
    	}
    	public static WritableWorkbook getWritableWorkbook(String xlsPath)  {
    		WritableWorkbook workbook = null;
    		File file = new File(xlsPath);
    		try {
    			if(xlsPath.toLowerCase().endsWith(".xls")){
    				if(!(file.exists()&&file.isFile())){
            			file.getParentFile().mkdirs();
            			file.createNewFile();
            			workbook= Workbook.createWorkbook(file);
            		}else{
            			Workbook w = Workbook.getWorkbook(file);
            			workbook= Workbook.createWorkbook(file,w);
            		}
    			} else 
    				throw new Exception("Unsupported 2007/2010/2013 excel file type !");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return workbook;
    		
    	}
    	public static Workbook getReadableWorkbook(String xlsPath)  {
    		Workbook workbook = null;
    		File file = new File(xlsPath);
    		try {
        		if(file.exists()&&file.isFile()){
        			if(xlsPath.toLowerCase().endsWith(".xls"))
        				workbook= Workbook.getWorkbook(file);
        			else
        				throw new Exception("Unsupported 2007/2010/2013 excel file type !");
        		}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		return workbook;
    	}
}
