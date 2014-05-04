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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.database.Cursor;
import android.text.TextUtils;

import com.frand.easyandroid.db.annotation.FFColumn;
import com.frand.easyandroid.db.annotation.FFPrimaryKey;
import com.frand.easyandroid.db.annotation.FFTableName;
import com.frand.easyandroid.db.annotation.FFTransient;
import com.frand.easyandroid.db.entity.FFPKProperyEntity;
import com.frand.easyandroid.db.entity.FFPropertyEntity;
import com.frand.easyandroid.db.entity.FFTableInfoEntity;
import com.frand.easyandroid.db.entity.FFHashMap;
import com.frand.easyandroid.exception.FFDBException;
import com.frand.easyandroid.util.FFFieldUtil;

public class FFDBUtils {
	/**
	 * 通过Cursor获取一个实体数组
	 * 
	 * @param clazz
	 *            实体类型
	 * @param cursor
	 *            数据集合
	 * @return 相应实体List数组
	 */
	public static <T> List<T> getListEntity(Class<T> clazz, Cursor cursor) {
		List<T> queryList = FFEntityBuilder.buildQueryList(clazz, cursor);
		return queryList;
	}

	/**
	 * 返回数据表中一行的数据
	 * 
	 * @param cursor
	 *            数据集合
	 * @return TAHashMap类型数据
	 */
	public static FFHashMap<String> getRowData(Cursor cursor) {
		if (cursor != null && cursor.getColumnCount() > 0) {
			FFHashMap<String> hashMap = new FFHashMap<String>();
			int columnCount = cursor.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				hashMap.put(cursor.getColumnName(i), cursor.getString(i));
			}
			return hashMap;
		}
		return null;
	}

	/**
	 * 根据实体类 获得实体类对应的表名,如果类中有用FFTableName注解过的字段，则用其值作为表名
	 * 当没有注解的时候默认用类的名称作为表名,并把点（.）替换为下划线(_)作为表名
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getTableName(Class<?> clazz) {
		FFTableName table = (FFTableName) clazz.getAnnotation(FFTableName.class);
		if (table == null || TextUtils.isEmpty(table.name())) {
			return clazz.getName().toLowerCase(Locale.ENGLISH).replace('.', '_');
		}
		return table.name();
	}

	/**
	 * 返回主键字段,首先启用用FFPrimaryKey注解的字段值作为主键
	 * 如果没有次注解，用名称为_id或id为字段的值作为主键
	 * 如果这三个均没有，则表示此表没有主键
	 * 
	 * @param clazz
	 *            实体类型
	 * @return
	 */
	public static Field getPrimaryKeyField(Class<?> clazz) {
		Field primaryKeyField = null;
		Field[] fields = clazz.getDeclaredFields();
		if (fields != null) {
			for (Field field : fields) {
				if (field.getAnnotation(FFPrimaryKey.class) != null) {
					primaryKeyField = field;
					break;
				}
			}
			if (primaryKeyField == null) {
				for (Field field : fields) {
					if ("_id".equals(field.getName())||"id".equals(field.getName())) {
						primaryKeyField = field;
						break;
					}
				}
			}
		} else {
			throw new RuntimeException("this model[" + clazz + "] has no field");
		}
		return primaryKeyField;
	}

	/**
	 * 根据类返回主键名
	 * 
	 * @param clazz
	 *            实体类型
	 * @return
	 */
	public static String getPrimaryKeyFieldName(Class<?> clazz) {
		Field field = getPrimaryKeyField(clazz);
		return field == null ? "id" : field.getName();
	}

	/**
	 * 返回数据库字段数组
	 * 
	 * @param clazz
	 *            实体类型
	 * @return 数据库的字段数组
	 */
	public static List<FFPropertyEntity> getPropertyList(Class<?> clazz) {
		List<FFPropertyEntity> plist = new ArrayList<FFPropertyEntity>();
		try {
			Field[] fields = clazz.getDeclaredFields();
			String primaryKeyFieldName = getPrimaryKeyFieldName(clazz);
			for (Field field : fields) {
				if (!FFDBUtils.isTransient(field)) {
					if (FFFieldUtil.isBaseDateType(field)) {
						if (field.getName().equals(primaryKeyFieldName)) // 过滤主键
							continue;
						FFPKProperyEntity property = new FFPKProperyEntity();
						property.setColumnName(FFDBUtils.getColumnByField(field));
						property.setName(field.getName());
						property.setType(field.getType());
						property.setDefaultValue(FFDBUtils.getPropertyDefaultValue(field));
						plist.add(property);
					}
				}
			}
			return plist;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 构建创建表的sql语句
	 * 
	 * @param clazz
	 *            实体类型
	 * @return 创建表的sql语句
	 * @throws FFDBException
	 */
	public static String creatTableSql(Class<?> clazz) throws FFDBException {
		FFTableInfoEntity tableInfoEntity = FFTableUtil.getTableInfoEntity(clazz);
		FFPKProperyEntity pkProperyEntity = tableInfoEntity.getPkProperyEntity();
		StringBuffer strSQL = new StringBuffer();
		strSQL.append("CREATE TABLE IF NOT EXISTS ");
		strSQL.append(tableInfoEntity.getTableName());
		strSQL.append(" ( ");
		if (pkProperyEntity != null) {
			strSQL.append("\"").append(pkProperyEntity.getColumnName()).append("\" ");
			Class<?> primaryClazz = pkProperyEntity.getType();
			if (primaryClazz == int.class || primaryClazz == Integer.class) {
				strSQL.append("INTEGER ").append("PRIMARY KEY");
				if(pkProperyEntity.isAutoIncrement()) {
					strSQL.append(" AUTOINCREMENT,");
				} else {
					strSQL.append(",");
				}
			} else {
				strSQL.append("TEXT").append("PRIMARY KEY,");
			}
		} else {
			strSQL.append("\"").append("id").append("\" ")
					.append("INTEGER PRIMARY KEY AUTOINCREMENT,");
		}
		Collection<FFPropertyEntity> propertys = tableInfoEntity.getPropertieArrayList();
		for (FFPropertyEntity property : propertys) {
			strSQL.append("\"").append(property.getColumnName());
			strSQL.append("\",");
		}
		strSQL.deleteCharAt(strSQL.length() - 1);
		strSQL.append(" )");
		return strSQL.toString();
	}

	/**
	 * 检测 字段是否已经被标注为 非数据库字段
	 * 
	 * @param field
	 * @return
	 */
	public static boolean isTransient(Field field) {
		return field.getAnnotation(FFTransient.class) != null;
	}

	/**
	 * 检查是否是主键
	 * 
	 * @param field
	 * @return
	 */
	public static boolean isPrimaryKey(Field field) {
		return field.getAnnotation(FFPrimaryKey.class) != null;
	}

	/**
	 * 检查是否自增，查看FFPrimaryKey中的註解字段sutoIncrement是否為真，默認為假
	 * 
	 * @param field
	 * @return
	 */
	public static boolean isAutoIncrement(Field field) {
		boolean isAutoIncre = false;
		FFPrimaryKey primaryKey = field.getAnnotation(FFPrimaryKey.class);
		if (null != primaryKey) {
			isAutoIncre = true;
		}
		return isAutoIncre;
	}
	
	public static Date strToDate(String dateString) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzzZ yyyy", Locale.ENGLISH);
		Date date = null;
		try {
			date = simpleDateFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static String dateToString(Date date) {
		String dateString = "";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzzZ yyyy", Locale.ENGLISH);
		dateString = simpleDateFormat.format(date);
		return dateString;
	}
	
	/**
	 * 根据某个类的字段获取对应数据库表中列的名称
	 * 如果此类的字段有FFColumn注解name字段，则用其作为列名返回
	 * 如果没有，则直接用此字段的名称作为列名返回
	 * 
	 * @param field
	 * @return
	 */
	public static String getColumnByField(Field field) {
		String columnName = field.getName();
		FFColumn column = field.getAnnotation(FFColumn.class);
		if (column != null && column.name().trim().length() != 0) {
			columnName = column.name();
		}
		return columnName;
	}

	/**
	 * 获得默认值
	 * 
	 * @param field
	 * @return
	 */
	public static String getPropertyDefaultValue(Field field) {
		FFColumn column = field.getAnnotation(FFColumn.class);
		if (column != null && column.defaultValue().trim().length() != 0) {
			return column.defaultValue();
		}
		return null;
	}
}
