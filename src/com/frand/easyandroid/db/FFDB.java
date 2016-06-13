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
package com.frand.easyandroid.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.frand.easyandroid.db.entity.FFArrayList;
import com.frand.easyandroid.db.entity.FFDBMasterEntity;
import com.frand.easyandroid.db.entity.FFHashMap;
import com.frand.easyandroid.db.entity.FFMapArrayList;
import com.frand.easyandroid.db.sql.FFSqlBuilder;
import com.frand.easyandroid.db.util.FFDBUtils;
import com.frand.easyandroid.db.util.FFSqlUtil;
import com.frand.easyandroid.exception.FFDBException;
import com.frand.easyandroid.log.FFLogger;

public class FFDB {
	private String sqlString = "";
	private Cursor sqlCursor = null;
	private boolean isBusy = false;
	private SQLiteDatabase mSQLiteDatabase = null;
	private FFDBHelper mDatabaseHelper = null;

	public boolean isBusy() {
		return isBusy;
	}

	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 * @param params
	 *            数据参数信息
	 */
	public FFDB(Context context, String dbName, int dbVersion, FFDBListener mListener) {
		this.mDatabaseHelper = new FFDBHelper(context, dbName, null, dbVersion, mListener);
	}

	/**
	 * 打开数据库如果是 isWrite为true,则磁盘满时抛出错误
	 * 
	 * @param isWrite
	 * @return
	 */
	public SQLiteDatabase openDatabase(Boolean isWrite) {
		if (isWrite) {
			mSQLiteDatabase = openWritable();
		} else {
			mSQLiteDatabase = openReadable();
		}
		return mSQLiteDatabase;
	}

	/**
	 * 以读写方式打开数据库，一旦数据库的磁盘空间满了，数据库就不能以只能读而不能写抛出错误。
	 * 
	 * @param dbUpdateListener
	 * @return
	 */
	public SQLiteDatabase openWritable() {
		try {
			mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mSQLiteDatabase;
	}

	/**
	 * 以读写方式打开数据库，如果数据库的磁盘空间满了，就会打开失败，当打开失败后会继续尝试以只读方式打开数据库。如果该问题成功解决，
	 * 则只读数据库对象就会关闭，然后返回一个可读写的数据库对象。
	 * 
	 * @param dbUpdateListener
	 * @return
	 */
	public SQLiteDatabase openReadable() {
		try {
			mSQLiteDatabase = mDatabaseHelper.getReadableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mSQLiteDatabase;
	}

	/**
	 * 取得数据库的表信息
	 *  SQLite数据库中一个特殊的名叫 SQLITE_MASTER的表，它定义数据库的模式
	 *  此表不能进行手动的增删改查，它又数据库在增删表，增删索引时来自动更新
	 *  其数据类型跟FFDBMasterEntity一致，type可以是table或index
	 *  临时表不会出现在该表中。临时表放在SQLITE_TEMP_MASTER的表中
	 * @return
	 */
	public ArrayList<FFDBMasterEntity> getTables() {
		ArrayList<FFDBMasterEntity> ffdbMasterArrayList = new ArrayList<FFDBMasterEntity>();
		sqlString = "select * from sqlite_master where type='table' order by name";
		FFLogger.i(FFDB.this, sqlString);
		free();
		sqlCursor = mSQLiteDatabase.rawQuery(sqlString, null);
		if (sqlCursor != null) {
			while (sqlCursor.moveToNext()) {
				if (sqlCursor!=null && sqlCursor.getColumnCount()>0) {
					FFDBMasterEntity tadbMasterEntity = new FFDBMasterEntity();
					tadbMasterEntity.setType(sqlCursor.getString(0));
					tadbMasterEntity.setName(sqlCursor.getString(1));
					tadbMasterEntity.setTbl_name(sqlCursor.getString(2));
					tadbMasterEntity.setRootpage(sqlCursor.getInt(3));
					tadbMasterEntity.setSql(sqlCursor.getString(4));
					ffdbMasterArrayList.add(tadbMasterEntity);
				}
			}
		} else {
			FFLogger.e(FFDB.this, "数据库未打开！");
		}
		return ffdbMasterArrayList;
	}

	/**
	 * 创建表
	 * 
	 * @param clazz
	 * @return 为true创建成功，为false创建失败
	 */
	public Boolean createTable(Class<?> clazz) {
		return createTable(clazz, null);
	}
	
	/**
	 * 创建表
	 * 
	 * @param clazz
	 * @return 为true创建成功，为false创建失败
	 */
	public Boolean createTable(Class<?> clazz, String tableName) {
		Boolean isSuccess = false;
		try {
			sqlString = FFDBUtils.creatTableSql(clazz, tableName);
			isSuccess = execute(sqlString, null);
		} catch (FFDBException e) {
			isSuccess = false;
			e.printStackTrace();
		}
		return isSuccess;
	}

	/**
	 * 判断是否存在某个表,为true则存在，否则不存在
	 * 
	 * @param clazz
	 * @return true则存在，否则不存在
	 */
	public boolean hasTable(Class<?> clazz) {
		String tableName = FFDBUtils.getTableName(clazz);
		return hasTable(tableName);
	}
	
	/**
	 * 判断是否存在某个表,为true则存在，否则不存在
	 * 
	 * @param tableName
	 *            需要判断的表名
	 * @return true则存在，否则不存在
	 */
	public boolean hasTable(String tableName) {
		if (tableName != null && !tableName.equalsIgnoreCase("")) {
			if (mSQLiteDatabase.isOpen()) {
				tableName = tableName.trim();
				sqlString = "select count(*) as c from Sqlite_master where type ='table' and name ='"
						+ tableName + "' ";
				free();
				sqlCursor = mSQLiteDatabase.rawQuery(sqlString, null);
				if (sqlCursor.moveToNext()) {
					int count = sqlCursor.getInt(0);
					if (count > 0) {
						return true;
					}
				}
			} else {
				FFLogger.e(FFDB.this, "数据库未打开！");
			}
		} else {
			FFLogger.e(FFDB.this, "判断数据表名不能为空！");
		}
		return false;
	}
	
	public Boolean dropTable(Class<?> clazz) {
		String tableName = FFDBUtils.getTableName(clazz);
		return dropTable(tableName);
	}

	/**
	 * 删除表
	 * 
	 * @param tableName
	 * @return 为true创建成功，为false创建失败
	 */
	public Boolean dropTable(String tableName) {
		sqlString = "DROP TABLE " + tableName;
		return execute(sqlString, null);
	}
	
	/**
	 * 插入记录
	 * 
	 * @param entity
	 *            插入的实体
	 * @return
	 */
	public Boolean insert(Object entity) {
		return insert(entity, null);
	}
	
	/**
	 * 插入记录
	 * 
	 * @param entity
	 *            插入的实体
	 * @return
	 */
	public Boolean insert(Object entity, String tableName) {
		return insert(entity, null, tableName);
	}
	
	/**
	 * 插入记录
	 * 
	 * @param entity
	 *            传入数据实体
	 * @param updateFields
	 *            插入到的字段,可设置为空
	 * @return 返回true执行成功，否则执行失败
	 */
	public Boolean insert(Object entity, FFArrayList updateFields, String tableName) {
		FFSqlBuilder sqlBuilder = null;
		sqlBuilder = FFSqlUtil.getSqlBuilder(FFSqlUtil.INSERT, tableName);
		sqlBuilder.setEntity(entity);
		sqlBuilder.setUpdateFields(updateFields);
		return execute(sqlBuilder);
	}

	/**
	 * 插入记录
	 * 
	 * @param table
	 *            需要插入到的表
	 * @param nullColumnHack
	 *            不允许为空的行
	 * @param values
	 *            插入的值
	 * @return
	 */
	public Boolean insert(String table, String nullColumnHack,
			ContentValues values) {
		if (mSQLiteDatabase.isOpen()) {
			return mSQLiteDatabase.insert(table, nullColumnHack, values) > 0;
		} else {
			FFLogger.e(FFDB.this, "数据库未打开！");
			return false;
		}
	}

	/**
	 * 插入记录
	 * 
	 * @param table
	 *            需要插入到的表
	 * @param nullColumnHack
	 *            不允许为空的行
	 * @param values
	 *            插入的值
	 * @return
	 */
	public Boolean insertOrThrow(String table, String nullColumnHack,
			ContentValues values) {
		if (mSQLiteDatabase.isOpen()) {
			return mSQLiteDatabase.insertOrThrow(table, nullColumnHack, values) > 0;
		} else {
			FFLogger.e(FFDB.this, "数据库未打开！");
			return false;
		}
	}

	/**
	 * 删除记录
	 * 
	 * @param entity
	 * @return 返回true执行成功，否则执行失败
	 */
	public Boolean delete(Object entity) {
		return delete(entity, null);
	}
	
	/**
	 * 删除记录
	 * 
	 * @param entity
	 * @return 返回true执行成功，否则执行失败
	 */
	public Boolean delete(Object entity, String tableName) {
		if (mSQLiteDatabase.isOpen()) {
			FFSqlBuilder getSqlBuilder = FFSqlUtil.getSqlBuilder(FFSqlUtil.DELETE, tableName);
			getSqlBuilder.setEntity(entity);
			return execute(getSqlBuilder);
		} else {
			return false;
		}
	}
	
	/**
	 * 删除记录
	 * 
	 * @param clazz
	 * @param where
	 *            where语句
	 * @return 返回true执行成功，否则执行失败
	 */
	public Boolean delete(Class<?> clazz, String where) {
		if (mSQLiteDatabase.isOpen()) {
			FFSqlBuilder sqlBuilder = FFSqlUtil.getSqlBuilder(FFSqlUtil.DELETE, null);
			sqlBuilder.setClazz(clazz);
			sqlBuilder.setCondition(false, where, null, null, null, null);
			return execute(sqlBuilder);
		} else {
			return false;
		}
	}

	/**
	 * 删除记录
	 * 
	 * @param table
	 *            被删除的表名
	 * @param whereClause
	 *            设置的WHERE子句时，删除指定的数据 ,如果null会删除所有的行。
	 * @param whereArgs
	 * 
	 * @return 返回true执行成功，否则执行失败
	 */
	public Boolean delete(String table, String whereClause, String[] whereArgs) {
		if (mSQLiteDatabase.isOpen()) {
			return mSQLiteDatabase.delete(table, whereClause, whereArgs) > 0;
		} else {
			FFLogger.e(FFDB.this, "数据库未打开！");
			return false;
		}
	}

	/**
	 * 更新记录 这种更新方式只有才主键不是自增的情况下可用
	 * 
	 * @param entity
	 *            更新的数据
	 * @return 返回true执行成功，否则执行失败
	 */
	public Boolean update(Object entity) {
		return update(entity, null);
	}

	/**
	 * 更新记录
	 * 
	 * @param entity
	 *            更新的数据
	 * @param where
	 *            where语句
	 * @return
	 */
	public Boolean update(Object entity, String where) {
		if (mSQLiteDatabase.isOpen()) {
			FFSqlBuilder sqlBuilder = FFSqlUtil.getSqlBuilder(FFSqlUtil.UPDATE, null);
			sqlBuilder.setEntity(entity);
			sqlBuilder.setCondition(false, where, null, null, null, null);
			return execute(sqlBuilder);
		} else {
			return false;
		}
	}

	/**
	 * 更新记录
	 * 
	 * @param table
	 *            表名字
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return 返回true执行成功，否则执行失败
	 */
	public Boolean update(String table, ContentValues values,
			String whereClause, String[] whereArgs) {
		if (mSQLiteDatabase.isOpen()) {
			return mSQLiteDatabase
					.update(table, values, whereClause, whereArgs) > 0;
		} else {
			FFLogger.e(FFDB.this, "数据库未打开！");
			return false;
		}
	}
	
	/**
	 * 执行查询，主要是SELECT, SHOW 等指令 返回数据集
	 * 
	 * @param clazz
	 * @return
	 */
	public <T> List<T> query(Class<?> clazz) {
		return query(clazz, "", false, "", "", "", "", "");
	}
	
	/**
	 * 执行查询，主要是SELECT, SHOW 等指令 返回数据集
	 * 
	 * @param clazz
	 * @return
	 */
	public <T> List<T> query(Class<?> clazz, String tableName) {
		return query(clazz, tableName, false, "", "", "", "", "");
	}

	/**
	 * 执行查询，主要是SELECT, SHOW 等指令 返回数据集
	 * 
	 * @param sql
	 *            sql语句
	 * @param selectionArgs
	 * @return
	 */
	public FFMapArrayList<String> query(String sql, String[] selectionArgs) {
		if (mSQLiteDatabase.isOpen()) {
			if (sql != null && !sql.equalsIgnoreCase("")) {
				sqlString = sql;
			}
			free();
			FFLogger.i(this, sql);
			sqlCursor = mSQLiteDatabase.rawQuery(sqlString, selectionArgs);
			if (sqlCursor != null) {
				return getQueryCursorData();
			} else {
				FFLogger.e(FFDB.this, "执行" + sql + "错误");
			}
		} else {
			FFLogger.e(FFDB.this, "数据库未打开！");
		}
		return null;
	}

	/**
	 * 查询记录
	 * 
	 * @param table
	 *            表名
	 * @param columns
	 *            需要查询的列
	 * @param selection
	 *            格式化的作为 SQL WHERE子句(不含WHERE本身)。 传递null返回给定表的所有行。
	 * @param selectionArgs
	 * @param groupBy
	 *            groupBy语句
	 * @param having
	 *            having语句
	 * @param orderBy
	 *            orderBy语句
	 * @return
	 */
	public ArrayList<FFHashMap<String>> query(String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		if (mSQLiteDatabase.isOpen()) {
			sqlCursor = mSQLiteDatabase.query(table, columns, selection,
					selectionArgs, groupBy, having, orderBy);
			if (sqlCursor != null) {
				return getQueryCursorData();
			} else {
				FFLogger.e(FFDB.this, "查询" + table + "错误");
			}
		} else {
			FFLogger.e(FFDB.this, "数据库未打开！");
		}
		return null;
	}

	/**
	 * 查询记录
	 * 
	 * @param distinct
	 *            限制重复，如过为true则限制,false则不用管
	 * @param table
	 *            表名
	 * @param columns
	 *            需要查询的列
	 * @param selection
	 *            格式化的作为 SQL WHERE子句(不含WHERE本身)。 传递null返回给定表的所有行。
	 * @param selectionArgs
	 * @param groupBy
	 *            groupBy语句
	 * @param having
	 *            having语句
	 * @param orderBy
	 *            orderBy语句
	 * @param limit
	 *            limit语句
	 * @return
	 */
	public ArrayList<FFHashMap<String>> query(String table, boolean distinct,
			String[] columns, String selection, String[] selectionArgs,
			String groupBy, String having, String orderBy, String limit) {
		if (mSQLiteDatabase.isOpen()) {
			free();
			sqlCursor = mSQLiteDatabase.query(distinct, table, columns,
					selection, selectionArgs, groupBy, having, orderBy, limit);
			if (sqlCursor != null) {
				isBusy = false;
				return getQueryCursorData();
			} else {
				FFLogger.e(FFDB.this, "查询" + table + "错误");
			}
		} else {
			FFLogger.e(FFDB.this, "数据库未打开！");
		}
		isBusy = false;
		return null;
	}

	/**
	 * 查询记录
	 * 
	 * @param table
	 *            表名
	 * @param columns
	 *            需要查询的列
	 * @param selection
	 *            格式化的作为 SQL WHERE子句(不含WHERE本身)。 传递null返回给定表的所有行。
	 * @param selectionArgs
	 * @param groupBy
	 *            groupBy语句
	 * @param having
	 *            having语句
	 * @param orderBy
	 *            orderBy语句
	 * @param limit
	 *            limit语句
	 * @return
	 */
	public ArrayList<FFHashMap<String>> query(String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit) {
		if (mSQLiteDatabase.isOpen()) {
			free();
			sqlCursor = mSQLiteDatabase.query(table, columns, selection,
					selectionArgs, groupBy, having, orderBy, limit);
			if (sqlCursor != null) {
				isBusy = false;
				return getQueryCursorData();
			} else {
				FFLogger.e(FFDB.this, "查询" + table + "错误");
			}
		} else {
			FFLogger.e(FFDB.this, "数据库未打开！");
		}
		isBusy = false;
		return null;
	}

	/**
	 * 查询记录
	 * 
	 * @param cursorFactory
	 * @param distinct
	 *            限制重复，如过为true则限制,false则不用管
	 * @param table
	 *            表名
	 * @param columns
	 *            需要查询的列
	 * @param selection
	 *            格式化的作为 SQL WHERE子句(不含WHERE本身)。 传递null返回给定表的所有行。
	 * @param selectionArgs
	 * @param groupBy
	 *            groupBy语句
	 * @param having
	 *            having语句
	 * @param orderBy
	 *            orderBy语句
	 * @param limit
	 *            limit语句
	 * @return
	 */
	public ArrayList<FFHashMap<String>> queryWithFactory(
			CursorFactory cursorFactory, boolean distinct, String table,
			String[] columns, String selection, String[] selectionArgs,
			String groupBy, String having, String orderBy, String limit) {
		if (mSQLiteDatabase.isOpen()) {
			free();
			sqlCursor = mSQLiteDatabase.queryWithFactory(cursorFactory,
					distinct, table, columns, selection, selectionArgs,
					groupBy, having, orderBy, limit);
			if (sqlCursor != null) {
				isBusy = false;
				return getQueryCursorData();
			} else {
				FFLogger.e(FFDB.this, "查询" + table + "错误");
			}
		} else {
			FFLogger.e(FFDB.this, "数据库未打开！");
		}
		isBusy = false;
		return null;

	}
	
	public <T> List<T> query(Class<?> clazz, boolean distinct, String where,
			String groupBy, String having, String orderBy, String limit) {
		return query(clazz, "", distinct, where, groupBy, having, orderBy, limit);
	}
	/**
	 * 执行查询，主要是SELECT, SHOW 等指令 返回数据集
	 * 
	 * @param clazz
	 * @param distinct
	 *            限制重复，如过为true则限制,false则不用管
	 * @param where
	 *            where语句
	 * @param groupBy
	 *            groupBy语句
	 * @param having
	 *            having语句
	 * @param orderBy
	 *            orderBy语句
	 * @param limit
	 *            limit语句
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> query(Class<?> clazz, String tableName, boolean distinct, String where,
			String groupBy, String having, String orderBy, String limit) {
		List<T> list = null;
		if (mSQLiteDatabase.isOpen()) {
			FFSqlBuilder sqlBuilder = FFSqlUtil.getSqlBuilder(FFSqlUtil.SELECT, tableName);
			sqlBuilder.setClazz(clazz);
			sqlBuilder.setCondition(distinct, where, groupBy, having, orderBy, limit);
			try {
				sqlString = sqlBuilder.getSqlStatement();
				FFLogger.i(FFDB.this, "执行" + sqlString);
				free();
				sqlCursor = mSQLiteDatabase.rawQuery(sqlString, null);
				list = (List<T>) FFDBUtils.getListEntity(clazz, sqlCursor);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			FFLogger.e(this, "数据库未打开");
		}
		isBusy = false;
		return list;
	}
	
	/**
	 * 获得所有的查询数据集中的数据
	 * 
	 * @return
	 */
	private FFMapArrayList<String> getQueryCursorData() {
		FFMapArrayList<String> arrayList = null;
		if (sqlCursor != null) {
			try {
				arrayList = new FFMapArrayList<String>();
				sqlCursor.moveToFirst();
				while (sqlCursor.moveToNext()) {
					arrayList.add(FFDBUtils.getRowData(sqlCursor));
				}
			} catch (Exception e) {
				e.printStackTrace();
				FFLogger.e(FFDB.this, "当前数据集获取失败！");
			}
		} else {
			FFLogger.e(FFDB.this, "当前数据集不存在！");
		}
		return arrayList;
	}
	
	/**
	 * 执行INSERT, UPDATE 以及DELETE操作
	 * 
	 * @param getSqlBuilder
	 *            Sql语句构建器
	 * @return
	 */
	public Boolean execute(FFSqlBuilder getSqlBuilder) {
		Boolean isSuccess = false;
		try {
			sqlString = getSqlBuilder.getSqlStatement();
			isSuccess = execute(sqlString, null);
		} catch (Exception e) {
			isSuccess = false;
			e.printStackTrace();
		}
		isBusy = false;
		return isSuccess;
	}
	
	/**
	 * INSERT, UPDATE 以及DELETE
	 * 
	 * @param sql
	 *            语句
	 * @param bindArgs
	 * @throws TADBNotOpenException
	 */
	public boolean execute(String sql, String[] bindArgs) {
		boolean isSucc = false;
		FFLogger.i(FFDB.this, "准备执行SQL[" + sql + "]语句");
		if (mSQLiteDatabase.isOpen()) {
			if (sql != null && !sql.equalsIgnoreCase("")) {
				if (bindArgs != null) {
					mSQLiteDatabase.execSQL(sql, bindArgs);
				} else {
					mSQLiteDatabase.execSQL(sql);
				}
				isSucc = true;
			}
		} else {
			isSucc = false;
		}
		isBusy = false;
		return isSucc;
	}

	/**
	 * 获取最近一次查询的sql语句
	 * 
	 * @return sql 语句
	 */
	public String getLastSql() {
		return sqlString;
	}

	/**
	 * 获得当前查询数据集合
	 * 
	 * @return
	 */
	public Cursor getQueryCursor() {
		return sqlCursor;
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		mSQLiteDatabase.close();
	}

	/**
	 * 释放查询结果
	 */
	public void free() {
		if (sqlCursor != null) {
			try {
				sqlCursor.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
