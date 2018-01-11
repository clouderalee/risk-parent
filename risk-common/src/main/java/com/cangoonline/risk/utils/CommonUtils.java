package com.cangoonline.risk.utils;

import java.util.Collection;
import java.util.Map;


public class CommonUtils {

    /**
     * 判断集合、数组、字符串是否不为空
     * @param obj
     * @return
     */
    public static boolean isNotEmpty(Object obj){
        return !isEmpty(obj);
    }
    /**
     * 判断集合、数组、字符串是否为空
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj){
        if(obj == null) return true;
        if(obj instanceof Object){
            return "".equals(obj.toString());
        }else if(obj instanceof Collection){
            return ((Collection) obj).isEmpty();
        }else if(obj.getClass().isArray()){
            return ((Object[]) obj).length == 0;
        }else if(obj instanceof Map){
            return ((Map) obj).isEmpty();
        }else{
            throw new IllegalArgumentException("不支持的此类["+obj.getClass().getName()+"]型判空.");
        }
    }


    public static int booleanToInt(Boolean bool) {
        return bool == null ? 0 : (bool ? 1 : 0);
    }

    public static boolean intToBoolean(Integer i) {
        return i == null ? false : (i != 0 ? true : false);
    }

    public static Number nvl(Number obj, Number value) {
        if (obj == null) {
            return value;
        } else {
            return obj;
        }
    }

    public static String nvl(String str, String value) {
        if (isEmpty(str)) {
            return value;
        } else {
            return str;
        }
    }

    public static String formatSerialNo(String serialNo) {
        int length = serialNo.length();
        if (length >= 30 && length < 32) {
            char[] temp = new char[32];
            for (int i = 0; i < 32 - length; i++) {
                temp[i] = '0';
            }
            serialNo.getChars(0, length, temp, 32 - length);
            return new String(temp);
        } else {
            return serialNo;
        }
    }
}
