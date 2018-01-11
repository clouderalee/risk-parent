package com.cangoonline.model.smg3;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 字节区
 * Byte Array 物理定义
 * 
 * @author GuoZH
 * @version 1.0, 2016-03-02
 * @version 2.0, 2016-03-03
 * @version 3.0, 2016-04-07
 */
public class ByteArea {
	
	//名称
	private String name;
	
	//大小
	private int length;
	
	//内容
	private byte[] area;
	
	//偏移量
	private int offset;
	
	private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>();
	
	/**
	 * 构成方法
	 * 
	 * @param name    名称
	 * @param length  大小
	 */
	public ByteArea(String name, int length) {
		super();
		this.name = name;
		this.length = length;
		area = new byte[12 + length];
		System.arraycopy(getAsciiString(name, 8).getBytes(), 0, area, 0, 8);
		System.arraycopy(getBytes(length), 0, area, 8, 4);
	}

	/**
	 * 获取名称
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 获取大小
	 * 
	 * @return
	 */
	public int getLength() {
		return length;
	}

	/**
	 * 设置当前字节区在结果中的偏移量
	 * 
	 * @param offset  偏移量
	 */
	void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * 根据结果刷新当前字节区
	 * 
	 * @param datas   数据
	 */
	void flushArea(byte[] datas) {
		//只刷新 Data 区
		System.arraycopy(datas, offset + 12, area, 12, length);
	}

	/**
	 * 获取内容
	 * 
	 * @return
	 */
	public byte[] getArea() {
		return area;
	}
	
	/**
	 * 设置值
	 * 
	 * @param offset 偏移量
	 * @param length  大小
	 * @param value   值
	 */
	public void setValue(int offset, int length, Object value){
		setValue(offset, length, 0, value);
	}
	
	/**
	 * 设置值
	 * 
	 * @param offset  偏移量
	 * @param length  大小
	 * @param scale   小数位数
	 * @param value   值
	 */
	public void setValue(int offset, int length, int scale, Object value){
		String asciiStr = null;
		if(value instanceof String){//日期要以指定格式的字符串形式传进来
			asciiStr = getAsciiString((String) value, length);
		}else if(value instanceof Number){
			asciiStr = getAsciiString((Number) value, length, scale);
		}
		
		if(asciiStr != null){
			System.arraycopy(asciiStr.getBytes(), 0, area, offset + 12, length);
		}
	}
	
	/**
	 * 获取字符串形式的数字值
	 * 
	 * @param offset  偏移量
	 * @param length  大小
	 * @param scale   小数位数
	 * @return
	 */
	public String getStringOfNumber(int offset, int length, int scale){
		return getStringOfNumber(area, offset + 12, length, scale);
	}
	
	/**
	 * 获取字符串形式的数字值
	 * 
	 * @param datas   字节数组
	 * @param offset  偏移量
	 * @param length  大小
	 * @param scale   小数位数
	 * @return
	 */
	public static String getStringOfNumber(byte[] datas, int offset, int length, int scale){
		StringBuilder strBuilder = new StringBuilder(new String(datas, offset, length));
		
		if(scale > 0){//SDS中定义的小数
			//加小数点
			int index = length - scale;
			strBuilder.insert(index, '.');
			
			//有小数点，才删尾部0
			for(int i = strBuilder.length() - 1; i > index; i--){//检查到小数点后一位
				if(strBuilder.charAt(i) == '0'){
					strBuilder.deleteCharAt(i);
				}else{
					break;
				}
			}
			
			//删尾部小数点
			if(index == strBuilder.length() - 1){
				strBuilder.deleteCharAt(index);
			}
		}
		
		//删头部正号
		if(strBuilder.charAt(0) == '+'){
			strBuilder.deleteCharAt(0);
		}
		
		//删头部0
		while(strBuilder.length() > 1 && strBuilder.charAt(0) == '0'){// > 1 是为了如果全是 0 最终保留一位
			strBuilder.deleteCharAt(0);
		}
		
		return strBuilder.toString();
	}
	
	/**
	 * 获取数字
	 * 
	 * @param offset  偏移量
	 * @param length  大小
	 * @param scale   小数位数
	 * @return
	 */
	public double getDouble(int offset, int length, int scale){
		return getDouble(area, offset + 12, length, scale);
	}
	
	/**
	 * 获取数字
	 * 
	 * @param datas   字节数组
	 * @param offset  偏移量
	 * @param length  大小
	 * @param scale   小数位数
	 * @return
	 */
	public static double getDouble(byte[] datas, int offset, int length, int scale){
		StringBuilder strBuilder = new StringBuilder(new String(datas, offset, length));
		
		if(scale > 0){//SDS中定义的小数
			//加小数点
			strBuilder.insert(length - scale, '.');
		}
		
		return Double.parseDouble(strBuilder.toString());
	}
	
	/**
	 * 获取字符串
	 * 
	 * @param offset  偏移量
	 * @param length  大小
	 * @return
	 */
	public String getString(int offset, int length){
		return getString(area, offset + 12, length);
	}
	
	/**
	 * 获取字符串
	 * 
	 * @param datas   字节数组
	 * @param offset  偏移量
	 * @param length  大小
	 * @return
	 */
	public static String getString(byte[] datas, int offset, int length){
		StringBuilder strBuilder = new StringBuilder(new String(datas, offset, length));
		
		//从尾部删除编码为 0 的字符和空格字符
		for(int i = strBuilder.length() - 1; i >= 0; i--){
			char c = strBuilder.charAt(i);
			if(c == 0 || c == ' '){
				strBuilder.deleteCharAt(i);
			}else{
				break;
			}
		}
		
		return strBuilder.toString();
	}
	
	/**
	 * 返回当前字节区的字符串
	 * 
	 */
	@Override
	public String toString(){
		return new String(area);
	}
	
	/**
	 * 获取日期
	 * 
	 * @param str     字符串
	 * @return
	 */
	public static Date getDate(String str){
		try {
			return getDateFormat().parse(str);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 获取线程单例的 DateFormat
	 * SDS: CA --> YYYYMMDD
	 * 
	 * @return
	 */
	private static DateFormat getDateFormat(){
		DateFormat dateFormat = threadLocal.get();
		if(dateFormat == null){
			threadLocal.set(dateFormat = new SimpleDateFormat("yyyyMMdd"));
		}
		return dateFormat;
	}

	/**
	 * 获取字符串的 ASCII 串
	 * SDS: CA, UTF-8, GBK
	 * 
	 * @param str     字符串
	 * @param length  大小
	 * @return
	 */
	public static String getAsciiString(String str, int length){
		StringBuilder strBuilder = new StringBuilder(str);
		int less = length - str.getBytes().length;//缺少的位数
		
		//字符串超长检查
		if(less < 0){
			throw new RuntimeException("String[" + str + "] byte over " + (-less) + " place.");
		}
		
		for(int i = 0; i < less; i++){
			strBuilder.append(' ');
		}
		
		return strBuilder.toString();
	}
	
	/**
	 * 获取数字的 ASCII 串
	 * SDS: AZDL, AZDU, ADNL
	 * 
	 * @param number  数字
	 * @param length  大小
	 * @param scale   小数位数
	 * @return
	 */
	public static String getAsciiString(Number number, int length, int scale){
		String str = new BigDecimal(number.toString()).toPlainString();//去除科学计数法
		StringBuilder strBuilder = new StringBuilder(str);
		
		if(strBuilder.charAt(0) != '-'){
			strBuilder.insert(0, '+');//补正号，为了兼容 ADNL
		}
		
		int intLess;//缺少的整数部分位数
		int decimalLess;//缺少的小数部分位数
		
		int len = strBuilder.length();//实际大小
		int index = strBuilder.indexOf(".");//小数点位置
		if(index == -1){
			intLess = length - scale - len;
			decimalLess = scale;
		}else{
			intLess = length - scale - index;
			decimalLess = scale - (len - index - 1);
			strBuilder.deleteCharAt(index);//删除小数点
		}
		
		//数字超长检查
		if(intLess < 0 || decimalLess < 0){
			throw new RuntimeException("Number[" + str + "] integer over " + (intLess > 0 ? 0 : -intLess) 
					+ " place, decimal over " + (decimalLess > 0 ? 0 : -decimalLess) + " place.");
		}
		
		//整数部分
		int start = 1;//符号位之后补0
		for(int i = 0; i < intLess; i++){
			strBuilder.insert(start, '0');
		}
		
		//小数部分
		for(int i = 0; i < decimalLess; i++){
			strBuilder.append('0');
		}
		
		return strBuilder.toString();
	}
	
	/**
	 * 获取字节数组
	 * SDS: MFBS, MFBU
	 * 
	 * @param num     整数
	 * @return
	 */
	public static byte[] getBytes(int num){
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (num >> 24);
		bytes[1] = (byte) (num >> 16);
		bytes[2] = (byte) (num >> 8);
		bytes[3] = (byte) num;
		return bytes;
	}
	
	/**
	 * 获取整数
	 * SDS: MFBS, MFBU
	 * 
	 * @param datas   字节数组
	 * @return
	 */
	public static int getInt(byte[] datas){
		if(datas.length != 4){
			throw new RuntimeException("not a int byte array.");
		}
		
		//拼成 32 位二进制字符串
		StringBuilder strBuilder = new StringBuilder();
		int offset = 0;
		for(byte b: datas){
			String str = Integer.toBinaryString(b);
			int less = 8 - str.length();
			for(int i = 0; i < less; i++){//少于 8 位，头部补 0
				strBuilder.insert(offset, '0');
			}
			if(less < 0){//超过 8 位，截取尾部 8 位
				str = str.substring(str.length() - 8);
			}
			strBuilder.append(str);
			offset += 8;
		}
		
		//从二进制字符串转为整数
		int n;
		if(strBuilder.charAt(0) == '1'){//负数二进制字符串不能直接转为整数
			for(int i = 0; i < strBuilder.length(); i++){//取反码
				if(strBuilder.charAt(i) == '1'){
					strBuilder.setCharAt(i, '0');
				}else if(strBuilder.charAt(i) == '0'){
					strBuilder.setCharAt(i, '1');
				}
			}
			n = -Integer.parseInt(strBuilder.toString(), 2) - 1;//-反码数-1 = 负数
		}else{
			n = Integer.parseInt(strBuilder.toString(), 2);
		}
		
		return n;
	}
	
	/**
	 * 获取整数
	 * SDS: MFBS, MFBU
	 * 
	 * @param datas   偏移量
	 * @return
	 */
	public int getInt(int offset){
		byte[] datas = new byte[4];
		System.arraycopy(area, offset + 12, datas, 0, 4);
		return getInt(datas);
	}

}
