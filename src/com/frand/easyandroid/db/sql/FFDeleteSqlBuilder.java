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
package com.frand.easyandroid.db.sql;

import java.lang.reflect.Field;
import java.util.Date;

import com.frand.easyandroid.db.entity.FFArrayList;
import com.frand.easyandroid.db.util.FFDBUtils;
import com.frand.easyandroid.exception.FFDBException;
import com.frand.easyandroid.util.FFFieldUtil;

public class FFDeleteSqlBuilder extends FFSqlBuilder {

	public FFDeleteSqlBuilder() {
	}
	
	public FFDeleteSqlBuilder(String tableName) {
		super.tableName = tableName;
	}

	@Override
	public String buildSql() throws FFDBException, IllegalArgumentException,IllegalAccessException {
		StringBuilder stringBuilder = new StringBuilder(256);
		stringBuilder.append("DELETE FROM ");
		stringBuilder.append(tableName);
		if (entity == null) {
			stringBuilder.append(buildConditionString());
		} else {
			stringBuilder.append(buildWhere(buildWhere(this.entity)));
		}
		return stringBuilder.toString();
	}

	/**
	 * 创建Where语句
	 * 
	 * @param entity
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws FFDBException
	 */
	public FFArrayList buildWhere(Object entity)
			throws IllegalArgumentException, IllegalAccessException,
			FFDBException {
		Class<?> clazz = entity.getClass();
		FFArrayList whereArrayList = new FFArrayList();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			if (!FFDBUtils.isTransient(field)&&FFFieldUtil.isBaseDateType(field)) {
				// 如果ID不是自动增加的
				if (!FFDBUtils.isAutoIncrement(field)) {
					String columnName = FFDBUtils.getColumnByField(field);
					if (null != field.get(entity) && field.get(entity).toString().length() > 0) {
						if(field.getType().equals(Date.class)) {
							whereArrayList.add(columnName, FFDBUtils.dateToString(((Date)field.get(entity))));
						} else {
							whereArrayList.add(columnName, field.get(entity).toString());
						}
					}
				}
			}
		}
		if (whereArrayList.isEmpty()) {
			throw new FFDBException("不能创建Where条件，语句");
		}
		return whereArrayList;
	}
}
