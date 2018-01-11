package com.cangoonline.engineflow.scriptext.regex;

/**
 * 正则表达式工具类
 *
 */
public class RegexHelper {

    public static boolean isMatche(String regex ,String value) {
        return value == null ? false : value.matches(regex);
    }

    public static boolean isMobileNO(String mobile) {
        String regex = "^1[3-9]\\d{9}$";
        return mobile == null ? false : mobile.matches(regex);
    }

    public static boolean isEmail(String email) {
        String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        return email == null ? false : email.matches(regex);
    }
    
    /**
     * @param value
     * @param mode > >= < <=
     * @return
     */
    public static boolean isDecimal(String value,String mode) {
    	String reg = null;
    	if(">".equals(mode)){
    		reg = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$";
    	}else if(">=".equals(mode)){
    		reg = "^[1-9]\\d*\\.\\d*|[-]{0,1}0\\.\\d*[0-9]\\d*$";
    	}else if("<".equals(mode)){
    		reg = "^-[1-9]\\d*\\.\\d*|-0\\.\\d*[1-9]\\d*$";
    	}else if("<=".equals(mode)){
    		reg = "^[1-9]\\d*\\.\\d*|[-]{0,1}0\\.\\d*[0-9]\\d*$";
    	}else{
    		reg = "^[-]{0,1}[1-9]\\d*\\.\\d*|[-]{0,1}0\\.\\d*[0-9]\\d*$";
    	}
    	return value.matches(reg);
    }
    
    /**
     * 浮点数（包括0.0不包括0）
     * @param value 
     * @return
     */
    public static boolean isDecimal(String value) {
		return isDecimal(value, null);
	}

    /**
     * @param value
     * @param mode > >= < <=
     * @return
     */
	public static boolean isInteger(String value,String mode) {
		String reg = null;
    	if(">".equals(mode)){
    		reg = "^[1-9]\\d*$";
    	}else if(">=".equals(mode)){
    		reg = "^[1-9]\\d*|[-]{0,1}0$";
    	}else if("<".equals(mode)){
    		reg = "^-[1-9]\\d*$";
    	}else if("<=".equals(mode)){
    		reg = "^-[1-9]\\d*|[-]{0,1}0$";
    	}else{
    		reg = "^[-]{0,1}[1-9]\\d*|[-]{0,1}0$";
    	}
		return value.matches(reg);
	}
	public static boolean isInteger(String value) {
		return isInteger(value,null);
	}
	
	
	public static boolean isDate(String value,String splirChar) {
		String reg = "^([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})"
				+ splirChar+"(((0[13578]|1[02])"
				+ splirChar+"(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)"
				+ splirChar+"(0[1-9]|[12][0-9]|30))|(02"
				+ splirChar+"(0[1-9]|[1][0-9]|2[0-8])))$";
		return value.matches(reg);
	}
	public static boolean isDate(String value) {
		return isDate(value,"-");
	}
	/**
	 * 是否是汉字
	 * @param value
	 * @param number 汉字最大个数
	 * @return
	 */
	public static boolean isChineseChar(String value,int number) {
		String reg = "^[\u4e00-\u9fa5]{1,"+number+"}$";
		return value.matches(reg);
	}
	public static boolean isChineseChar(String value) {
		return isChineseChar(value, Integer.MAX_VALUE);
	}
	

}
