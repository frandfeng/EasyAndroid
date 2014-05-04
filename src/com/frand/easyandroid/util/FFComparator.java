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
package com.frand.easyandroid.util;

import java.lang.reflect.Field;
import java.util.Comparator;

import com.frand.easyandroid.annotation.FFCompareAnnotation;

public class FFComparator<T> implements Comparator<T> {
	/** 升序排列 */
	public static final int ASC_SORT_TYPE = 1;
	/** 降序排列 */
	public static final int DES_SORT_TYPE = 2;
	/** 排序字段 */
	private int sortType = 1;
	/** 选择进行排序属性标识 */
	private int sortFlag = 0;

	/**
	 * 默认构造函数
	 */
	public FFComparator() {

	}

	/**
	 * 带排序规则的构造函数
	 * 
	 * @param sortType
	 *            排序规则
	 */
	public FFComparator(int sortType) {
		this.sortType = sortType;
	}

	/**
	 * 带排序规则的与排序标识构造函数
	 * 
	 * @param sortType
	 *            排序规则
	 * @param sortFlag
	 *            排序标识
	 */
	public FFComparator(int sortType, int sortFlag) {
		this.sortType = sortType;
		this.sortFlag = sortFlag;
	}

	@Override
	public int compare(T object1, T object2) {
		int compareValue1 = 0;
		int compareValue2 = 0;
		try {
			compareValue1 = getCompareValue(object1);
			compareValue2 = getCompareValue(object2);
		} catch (Exception e) {
			e.printStackTrace();
			compareValue1 = 0;
			compareValue2 = 0;
		}
		if (sortType == 2) {
			return compareValue2 - compareValue1;
		} else {
			return compareValue1 - compareValue2;
		}
	}

	public int getCompareValue(T object) throws IllegalArgumentException,
			IllegalAccessException {
		int compareValue = 0;
		Field[] fields = object.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (field.isAnnotationPresent(FFCompareAnnotation.class)) {
					FFCompareAnnotation taCompareInject = field
							.getAnnotation(FFCompareAnnotation.class);
					int sortFlag = taCompareInject.sortFlag();
					if(sortFlag==this.sortFlag) {
						field.setAccessible(true);
						if (field.getType().equals(Integer.TYPE)) {
							compareValue = field.getInt(object);
						} else if (field.getType().equals(Boolean.TYPE)) {
							Boolean b = field.getBoolean(object);
							if (b) {
								compareValue = 1;
							} else {
								compareValue = 0;
							}
						} else if (field.getType().equals(String.class)) {
							String fieldValueString = (String) field.get(object);
							compareValue = Integer.parseInt(fieldValueString);
						}
					}
				}
			}
		}
		return compareValue;
	}

	public int getSortType() {
		return sortType;
	}

	public void setSortType(int sortType) {
		this.sortType = sortType;
	}

}