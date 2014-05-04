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

import org.apache.http.NameValuePair;

import com.frand.easyandroid.db.annotation.FFPrimaryKey;
import com.frand.easyandroid.db.entity.FFArrayList;
import com.frand.easyandroid.db.util.FFDBUtils;
import com.frand.easyandroid.exception.FFDBException;
import com.frand.easyandroid.util.FFTextUtil;
import com.frand.easyandroid.util.FFFieldUtil;

import android.text.TextUtils;

public abstract class FFSqlBuilder {
	protected Boolean distinct;
	protected String where;
	protected String groupBy;
	protected String having;
	protected String orderBy;
	protected String limit;
	protected Class<?> clazz = null;
	protected String tableName = null;
	protected Object entity;
	protected FFArrayList updateFields;

	public FFSqlBuilder(Object entity) {
		this.entity = entity;
		setClazz(entity.getClass());
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
		setClazz(entity.getClass());
	}

	public void setCondition(boolean distinct, String where, String groupBy,
			String having, String orderBy, String limit) {
		this.distinct = distinct;
		this.where = where;
		this.groupBy = groupBy;
		this.having = having;
		this.orderBy = orderBy;
		this.limit = limit;
	}

	public FFArrayList getUpdateFields() {
		return updateFields;
	}

	public void setUpdateFields(FFArrayList updateFields) {
		this.updateFields = updateFields;
	}

	public FFSqlBuilder() {
	}

	public FFSqlBuilder(Class<?> clazz) {
		setTableName(clazz);
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setTableName(Class<?> clazz) {
		this.tableName = FFDBUtils.getTableName(clazz);
	}

	public String getTableName() {
		return tableName;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		setTableName(clazz);
		this.clazz = clazz;
	}

	/**
	 * 获取sql语句
	 * 
	 * @return
	 * @throws FFDBException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public String getSqlStatement() throws FFDBException,
			IllegalArgumentException, IllegalAccessException {
		onPreGetStatement();
		return buildSql();
	}

	/**
	 * 构建sql语句前执行方法
	 * 
	 * @return
	 * @throws FFDBException
	 */
	public void onPreGetStatement() throws FFDBException,
			IllegalArgumentException, IllegalAccessException {

	}

	/**
	 * 构建sql语句
	 * 
	 * @return
	 * @throws FFDBException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public abstract String buildSql() throws FFDBException,
			IllegalArgumentException, IllegalAccessException;

	/**
	 * 创建条件字句
	 * 
	 * @return 返回条件Sql
	 */
	protected String buildConditionString() {
		StringBuilder query = new StringBuilder(120);
		appendClause(query, " WHERE ", where);
		appendClause(query, " GROUP BY ", groupBy);
		appendClause(query, " HAVING ", having);
		appendClause(query, " ORDER BY ", orderBy);
		appendClause(query, " LIMIT ", limit);
		return query.toString();
	}

	protected void appendClause(StringBuilder s,String name, String clause) {
		if (!TextUtils.isEmpty(clause)) {
			s.append(name);
			s.append(clause);
		}
	}

	/**
	 * 用生成的conditions对象来构建where子句
	 * 
	 * @param conditions
	 *            TAArrayList类型的where数据
	 * @return 返回where子句
	 */
	public String buildWhere(FFArrayList conditions) {
		StringBuilder stringBuilder = new StringBuilder(256);
		if (conditions != null) {
			stringBuilder.append(" WHERE ");
			for (int i = 0; i < conditions.size(); i++) {
				NameValuePair nameValuePair = conditions.get(i);
				stringBuilder.append(nameValuePair.getName()).append(" = ")
					.append(FFTextUtil.isNumeric(nameValuePair
					.getValue().toString()) ? nameValuePair
					.getValue() : "'" + nameValuePair.getValue() + "'");
				if (i + 1 < conditions.size()) {
					stringBuilder.append(" AND ");
				}
			}
		}
		return stringBuilder.toString();
	}
	
	public static FFArrayList getFieldsAndValue(Object entity)
			throws FFDBException, IllegalArgumentException, IllegalAccessException {
		FFArrayList arrayList = new FFArrayList();
		if (entity == null) {
			throw new FFDBException("没有加载实体类！");
		}
		Class<?> clazz = entity.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (!FFDBUtils.isTransient(field)&&FFFieldUtil.isBaseDateType(field)) {
				FFPrimaryKey annotation = field.getAnnotation(FFPrimaryKey.class);
				if (annotation != null && annotation.autoIncrement()) {
				} else {
					String columnName = FFDBUtils.getColumnByField(field);
					field.setAccessible(true);
					if(columnName == null || columnName.equals("")) {
						columnName = field.getName();
					}
					String value = "";
					if(field.getType().equals(Date.class)) {
						value = field.get(entity)!=null?FFDBUtils.dateToString((Date)field.get(entity)):"";
					} else {
						value = field.get(entity)!=null?field.get(entity).toString():"";
					}
					arrayList.add(columnName, value);
				}
			}
		}
		return arrayList;
	}
}
