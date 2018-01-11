package com.cangoonline.risk.utils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2017\12\13 0013.
 */
public class ClassUtils {

    private static Logger logger =  Logger.getLogger(ClassUtils.class.getName());

    /**
     * 将对象按照制定class类型进行适配转换
     * 当类型不匹配时，会自动进行优化
     * @param object 对象
     * @param clazz 类型
     * @return
     * @return
     */
    public static <T> T castType(Object object, Class<T> clazz) {
        return castType(object,clazz,false);
    }
    /**
     * 将对象按照制定class类型进行适配转换
     * @param object
     * @param clazz
     * @param isExactMatch 是否严格转换
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T castType(Object object,Class<T> clazz ,boolean isExactMatch) {

        if(object == null){
            return null;
        }
        String sResult = String.valueOf(object).trim();

        T castObject = null;
        Class<? extends Object> jsClazz = object.getClass();
        if(jsClazz == clazz){
            return clazz.cast(object);
        }else{
            //如果不是同一种类型，那么就试图进行类型转换
            if(clazz == String.class){
                castObject = clazz.cast(object.toString());
            }else if(clazz == Integer.class||clazz == int.class){
                if(isExactMatch){
                    castObject = (T) Integer.class.cast(Integer.parseInt(sResult));
                }else{
                    castObject = (T) Integer.class.cast((int)Double.parseDouble(sResult));
                }
            }else if(clazz == Float.class||clazz == float.class){
                castObject = (T) Float.class.cast(Float.parseFloat(sResult));
            }else if(clazz == Double.class||clazz == double.class){
                castObject = (T) Double.class.cast(Double.parseDouble(sResult));
            }else if(clazz == Long.class||clazz == long.class){
                if(isExactMatch){
                    castObject = (T) Long.class.cast(Long.parseLong(sResult));
                }else{
                    castObject = (T) Long.class.cast((long)Double.parseDouble(sResult));
                }
            }else if(clazz == Float.class||clazz == float.class){
                castObject = (T) Float.class.cast(Float.parseFloat(sResult));
            }else if(clazz == Character.class||clazz == char.class){
                castObject = (T) Character.class.cast(sResult.charAt(0));
            }else if(clazz == Byte.class||clazz == byte.class){
                if(isExactMatch){
                    castObject = (T) Byte.class.cast(Byte.parseByte(sResult.substring(0, 1)));
                }else{
                    castObject = (T) Byte.class.cast((byte)Double.parseDouble(sResult));
                }
            }else if(clazz == Short.class||clazz == short.class){
                if(isExactMatch){
                    castObject = (T) Short.class.cast(Short.parseShort(sResult));
                }else{
                    castObject = (T) Short.class.cast((short)Double.parseDouble(sResult));
                }
            }else if(clazz == Set.class){
                if(object instanceof Set){
                    castObject = clazz.cast((Set)object);
                }
            }else if(clazz == List.class){
                if(object instanceof List){
                    castObject = clazz.cast((List)object);
                }
            }else if(clazz == Date.class){
                if(object instanceof Date){
                    castObject = clazz.cast((Date)object);
                }
            }else if(clazz == Map.class){
                if(object instanceof Map){
                    castObject = clazz.cast((Map)object);
                }
            }else{
                String message = object.getClass().getName()+" not cast to "+clazz.getName();
                logger.finer(message);
                throw new ClassCastException(message);
            }
        }
        return castObject;
    }
}
