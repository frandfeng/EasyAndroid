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

import org.apache.http.NameValuePair;

import com.frand.easyandroid.db.entity.FFArrayList;
import com.frand.easyandroid.exception.FFDBException;
import com.frand.easyandroid.util.FFTextUtil;

public class FFInsertSqlBuilder extends FFSqlBuilder {
	@Override
	public void onPreGetStatement() throws FFDBException,
			IllegalArgumentException, IllegalAccessException {
		if (getUpdateFields() == null) {
			setUpdateFields(getFieldsAndValue(entity));
		}
		super.onPreGetStatement();
	}

	@Override
	public String buildSql() throws FFDBException, IllegalArgumentException,
			IllegalAccessException {
		StringBuilder sql = new StringBuilder(512);
		StringBuilder columns = new StringBuilder(256);
		StringBuilder values = new StringBuilder(256);
		columns.append(" (");
		values.append("(");
		FFArrayList updateFields = getUpdateFields();
		for (int i = 0; i < updateFields.size(); i++) {
			NameValuePair nameValuePair = updateFields.get(i);
			columns.append(nameValuePair.getName());
			values.append(FFTextUtil.isNumeric(nameValuePair.getValue().toString()) ? nameValuePair
					.getValue() : "'" + nameValuePair.getValue() + "'");
			if (i + 1 < updateFields.size()) {
				columns.append(", ");
				values.append(", ");
			}
		}
		columns.append(") values ");
		values.append(")");
		sql.append("INSERT INTO ").append(tableName).append(columns).append(values);
		return sql.toString();
	}
}
