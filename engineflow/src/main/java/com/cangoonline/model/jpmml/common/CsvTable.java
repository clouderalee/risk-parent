package com.cangoonline.model.jpmml.common;

import java.util.ArrayList;
import java.util.List;

public class CsvTable extends ArrayList<List<String>> {
	private static final long serialVersionUID = -323329794228262167L;
	private String separator = null;
    public CsvTable(){
        super(1024);
    }
    public String getSeparator(){
        return this.separator;
    }
    public void setSeparator(String separator){
        this.separator = separator;
    }
}
