/*
 * Copyright (C) 2014-4-22 frandfeng
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.frand.easyandroid.util;

import java.lang.reflect.Field;
import java.util.Date;

import com.frand.easyandroid.annotation.FFField;
import com.frand.easyandroid.annotation.FFTransparent;

/** 
 * @author frandfeng
 * @time 2014-4-22 下午2:30:58 
 * class description 
 */
public class FFFieldUtil {
	
	/**
	 * 检测实体属性是否已经被标注为 不被识别
	 * 
	 * @param field
	 *            字段
	 * @return
	 */
	public static boolean isTransient(Field field) {
		return field.getAnnotation(FFTransparent.class) != null;
	}
	
	/**
	 * 是否为基本的数据类型
	 * 
	 * @param field
	 * @return
	 */
	public static boolean isBaseDateType(Field field) {
		Class<?> clazz = field.getType();
		return clazz.equals(Integer.class) || clazz.equals(int.class)
				|| clazz.equals(Byte.class) || clazz.equals(byte.class)
				|| clazz.equals(Character.class) || clazz.equals(char.class)
				|| clazz.equals(Long.class) ||clazz.equals(long.class)
				|| clazz.equals(Double.class) || clazz.equals(double.class)
				|| clazz.equals(Float.class) || clazz.equals(float.class)
				|| clazz.equals(Short.class) || clazz.equals(short.class)
				|| clazz.equals(Boolean.class) || clazz.equals(boolean.class)
				|| clazz.equals(Date.class) || clazz.equals(java.sql.Date.class)
				|| clazz.isPrimitive() || clazz.equals(String.class);
	}
	
	/**
	 * 获得配置名
	 * 
	 * @param field
	 * @return
	 */
	public static String getFieldName(Field field) {
		FFField column = field.getAnnotation(FFField.class);
		if (column != null && column.name().trim().length() != 0) {
			return column.name();
		}
		return field.getName();
	}
}
