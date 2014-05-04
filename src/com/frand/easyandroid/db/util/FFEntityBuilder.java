/*
 * Copyright (C) 2013 frandfeng
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
package com.frand.easyandroid.db.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.frand.easyandroid.util.FFFieldUtil;

import android.database.Cursor;

public class FFEntityBuilder {
	
	/**
	 * 通过Cursor获取一个实体数组,实体的类型为clazz
	 * 
	 * @param clazz
	 *            实体类型
	 * @param cursor
	 *            数据集合
	 * @return 相应实体List数组
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> buildQueryList(Class<T> clazz, Cursor cursor) {
		List<T> queryList = new ArrayList<T>();
		if (cursor.moveToFirst()) {
			do {
				queryList.add((T) buildQueryOneEntity(clazz, cursor));
			} while (cursor.moveToNext());
		}
		return queryList;
	}

	/**
	 * 通过Cursor获取一个实体
	 * 
	 * @param clazz
	 *            实体类型
	 * @param cursor
	 *            数据集合
	 * @return 相应实体
	 */
	@SuppressWarnings("unchecked")
	public static <T> T buildQueryOneEntity(Class<?> clazz, Cursor cursor) {
		Field[] fields = clazz.getDeclaredFields();
		T entityT = null;
		try {
			entityT = (T) clazz.newInstance();
			for (Field field : fields) {
				field.setAccessible(true);
				if (!FFDBUtils.isTransient(field)&&FFFieldUtil.isBaseDateType(field)) {
					String columnName = FFDBUtils.getColumnByField(field);
					field.setAccessible(true);
					setValue(field, columnName, entityT, cursor);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entityT;
	}

	/**
	 * 设置值到字段
	 * 
	 * @param field
	 *            需要设置的字段
	 * @param columnName
	 *            数据库字段名
	 * @param entityT
	 *            实体模版
	 * @param cursor
	 *            数据集合
	 */
	private static <T> void setValue(Field field, String columnName, T entityT,
			Cursor cursor) {
		try {
			int columnIndex = cursor.getColumnIndexOrThrow((columnName != null &&
					!columnName.equals("")) ? columnName : field.getName());
			Class<?> clazz = field.getType();
			if (clazz.equals(String.class)) {
				field.set(entityT, cursor.getString(columnIndex));
			} else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
				field.set(entityT, cursor.getInt(columnIndex));
			} else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
				field.set(entityT, cursor.getFloat(columnIndex));
			} else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
				field.set(entityT, cursor.getDouble(columnIndex));
			} else if (clazz.equals(Short.class) || clazz.equals(Short.class)) {
				field.set(entityT, cursor.getShort(columnIndex));
			} else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
				field.set(entityT, cursor.getLong(columnIndex));
			} else if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
				field.set(entityT, cursor.getBlob(columnIndex));
			} else if (clazz.equals(Boolean.class)||clazz.equals(boolean.class)) {
				Boolean testBoolean = Boolean.valueOf(cursor.getString(columnIndex));
				field.set(entityT, testBoolean);
			} else if (clazz.equals(Date.class)) {
				String dateString = cursor.getString(columnIndex);
				Date date = FFDBUtils.strToDate(dateString);
				field.set(entityT, date);
			} else if (clazz.equals(Character.class) || clazz.equals(char.class)) {
				Character c1 = cursor.getString(columnIndex).trim()
						.toCharArray()[0];
				field.set(entityT, c1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}