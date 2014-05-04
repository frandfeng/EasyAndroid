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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.http.NameValuePair;

import android.text.TextUtils;

import com.frand.easyandroid.db.annotation.FFPrimaryKey;
import com.frand.easyandroid.db.entity.FFArrayList;
import com.frand.easyandroid.db.util.FFDBUtils;
import com.frand.easyandroid.exception.FFDBException;
import com.frand.easyandroid.util.FFTextUtil;
import com.frand.easyandroid.util.FFFieldUtil;

public class FFUpdateSqlBuilder extends FFSqlBuilder {

	@Override
	public void onPreGetStatement() throws FFDBException,
			IllegalArgumentException, IllegalAccessException {
		if (getUpdateFields() == null) {
			setUpdateFields(getFieldsAndValue(entity));
		}
		super.onPreGetStatement();
	}

	@Override
	public String buildSql() throws FFDBException, IllegalArgumentException,IllegalAccessException {
		StringBuilder stringBuilder = new StringBuilder(256);
		stringBuilder.append("UPDATE ");
		stringBuilder.append(tableName).append(" SET ");
		FFArrayList needUpdate = getUpdateFields();
		for (int i = 0; i < needUpdate.size(); i++) {
			NameValuePair nameValuePair = needUpdate.get(i);
			stringBuilder.append(nameValuePair.getName()).append(" = ");
			String value = nameValuePair.getValue().toString();
			if(FFTextUtil.isNumeric(value)) {
				stringBuilder.append(value);
			} else {
				stringBuilder.append("'"+ nameValuePair.getValue() + "'");
			}
			if (i + 1 < needUpdate.size()) {
				stringBuilder.append(", ");
			}
		}
		if (!TextUtils.isEmpty(this.where)) {
			stringBuilder.append(buildConditionString());
		} else {
			stringBuilder.append(buildWhere(buildWhere(this.entity)));
		}
		return stringBuilder.toString();
	}

	/**
	 * 当where语句为空的时候自定义where语句，寻找与对象的primaryKey值相等的记录
	 * 
	 * @param entity
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws FFDBException
	 */
	public FFArrayList buildWhere(Object entity) throws IllegalArgumentException,
			IllegalAccessException, FFDBException {
		Class<?> clazz = entity.getClass();
		FFArrayList whereArrayList = new FFArrayList();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			if (!FFDBUtils.isTransient(field)&&FFFieldUtil.isBaseDateType(field)) {
				Annotation annotation = field.getAnnotation(FFPrimaryKey.class);
				if (annotation != null) {
					String columnName = FFDBUtils.getColumnByField(field);
					whereArrayList.add((columnName != null && !columnName.equals(""))
							? columnName : field.getName(), field.get(entity).toString());
				}
			}
		}
		if (whereArrayList.isEmpty()) {
			throw new FFDBException("不能创建Where条件，语句");
		}
		return whereArrayList;
	}
}
