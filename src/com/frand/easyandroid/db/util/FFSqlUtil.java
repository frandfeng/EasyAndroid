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

import com.frand.easyandroid.db.sql.FFDeleteSqlBuilder;
import com.frand.easyandroid.db.sql.FFInsertSqlBuilder;
import com.frand.easyandroid.db.sql.FFQuerySqlBuilder;
import com.frand.easyandroid.db.sql.FFSqlBuilder;
import com.frand.easyandroid.db.sql.FFUpdateSqlBuilder;

public class FFSqlUtil {
	public static final int INSERT = 0;
	/**
	 * 调用getSqlBuilder(int operate)返回查询sql语句构建器传入的参数
	 */
	public static final int SELECT = 1;
	/**
	 * 调用getSqlBuilder(int operate)返回删除sql语句构建器传入的参数
	 */
	public static final int DELETE = 2;
	/**
	 * 调用getSqlBuilder(int operate)返回更新sql语句构建器传入的参数
	 */
	public static final int UPDATE = 3;

	/**
	 * 获得sql构建器
	 * 
	 * @param operate
	 * @return 构建器
	 */
	public static synchronized FFSqlBuilder getSqlBuilder(int operate) {
		FFSqlBuilder sqlBuilder = null;
		switch (operate) {
		case INSERT:
			sqlBuilder = new FFInsertSqlBuilder();
			break;
		case SELECT:
			sqlBuilder = new FFQuerySqlBuilder();
			break;
		case DELETE:
			sqlBuilder = new FFDeleteSqlBuilder();
			break;
		case UPDATE:
			sqlBuilder = new FFUpdateSqlBuilder();
			break;
		default:
			break;
		}
		return sqlBuilder;
	}
}
