package com.cangoonline.engineflow.scriptext.freemark;

public class Funcations {
	public static boolean isEmpty(Object obj){
		return obj == null || "".equals(obj);
	}
	public static boolean isSpace(Object obj){
		if(obj!=null&&"".equals(obj.toString().trim())){
			return true;
		}
		return false;
	}
	public static boolean isNull(Object obj){
		return obj == null;
	}
	public static String max(String ... arr){
		if(arr==null||arr.length<=1)
			throw new RuntimeException("没有传入这参数");
		double[] dArr = new double[arr.length];
		for (int i = 0; i < arr.length; i++) {
			dArr[0] = Double.parseDouble(arr[i].trim());
		}
		int index = 0;
		for (int i = 0; i < arr.length-1; i++) {
			if(dArr[i+1]>dArr[index]){
				index = i+1;
			}
		}
		return arr[index].trim();
	}
	public static double max(double ... arr){
		if(arr==null||arr.length<=1)
			throw new RuntimeException("没有传入正确的参数");
		double max = arr[0];
		for (int i = 0; i < arr.length-1; i++) {
			if(arr[i+1]>max){
				max = arr[i+1];
			}
		}
		return max;
	}
	public static String min(String ... arr){
		if(arr==null||arr.length<=1)
			throw new RuntimeException("没有传入正确的参数");
		double[] dArr = new double[arr.length];
		for (int i = 0; i < arr.length; i++) {
			dArr[0] = Double.parseDouble(arr[i].trim());
		}
		int index = 0;
		for (int i = 0; i < arr.length-1; i++) {
			if(dArr[i+1]<dArr[index]){
				index = i+1;
			}
		}
		return arr[index].trim();
	}
	public static double min(double ... arr){
		if(arr==null||arr.length<=1)
			throw new RuntimeException("没有传入正确的参数");
		double min = arr[0];
		for (int i = 0; i < arr.length-1; i++) {
			if(arr[i+1]<min){
				min = arr[i+1];
			}
		}
		return min;
	}
	public static boolean in(double value,double ... arr){
		if(arr==null||arr.length<=0)
			throw new RuntimeException("没有传入正确的参数");
		for (double d : arr) {
			if(d == value){
				return true;
			}
		}
		return false;
	}
	public static boolean in(String ... arr){
		if(arr==null||arr.length<=1)
			throw new RuntimeException("没有传入正确的参数");
		String value = arr[0];
		for (int i = 1; i < arr.length; i++) {
			if(value.equals(arr[i]))
				return true;
		}
		return false;
	}
		
}
