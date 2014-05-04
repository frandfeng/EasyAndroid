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
import java.util.HashMap;
import java.util.List;

import com.frand.easyandroid.db.entity.FFPKProperyEntity;
import com.frand.easyandroid.db.entity.FFPropertyEntity;
import com.frand.easyandroid.db.entity.FFTableInfoEntity;
import com.frand.easyandroid.exception.FFDBException;

public class FFTableUtil {
	/**
	 * 表名为键，表信息为值的HashMap
	 */
	private static final HashMap<String, FFTableInfoEntity> tableInfoEntityMap = new HashMap<String, FFTableInfoEntity>();

	/**
	 * 将一个类中的所有字段转化为表信息进行输出
	 * 表信息中包括表的名称，表对应的类的名称
	 * 表的主键信息（列名，字段名，类型，是否自增）
	 * 表中的咧信息列表
	 * 
	 * @param clazz
	 *            实体类型
	 * @return 表信息
	 * @throws FFDBException
	 */
	public static FFTableInfoEntity getTableInfoEntity(Class<?> clazz)
			throws FFDBException {
		FFTableInfoEntity tableInfoEntity = tableInfoEntityMap.get(clazz.getName());
		if (tableInfoEntity == null) {
			tableInfoEntity = new FFTableInfoEntity();
			tableInfoEntity.setTableName(FFDBUtils.getTableName(clazz));
			tableInfoEntity.setClassName(clazz.getName());
			Field idField = FFDBUtils.getPrimaryKeyField(clazz);
			if (idField != null) {
				FFPKProperyEntity pkProperyEntity = new FFPKProperyEntity();
				pkProperyEntity.setColumnName(FFDBUtils.getColumnByField(idField));
				pkProperyEntity.setName(idField.getName());
				pkProperyEntity.setType(idField.getType());
				pkProperyEntity.setAutoIncrement(FFDBUtils.isAutoIncrement(idField));
				tableInfoEntity.setPkProperyEntity(pkProperyEntity);
			} else {
				tableInfoEntity.setPkProperyEntity(null);
			}
			List<FFPropertyEntity> propertyList = FFDBUtils.getPropertyList(clazz);
			if (propertyList != null) {
				tableInfoEntity.setPropertieArrayList(propertyList);
			}
			tableInfoEntityMap.put(clazz.getName(), tableInfoEntity);
		}
		if (tableInfoEntity == null
				|| tableInfoEntity.getPropertieArrayList() == null
				|| tableInfoEntity.getPropertieArrayList().size() == 0) {
			throw new FFDBException("不能创建+" + clazz + "的表信息");
		}
		return tableInfoEntity;
	}
}
