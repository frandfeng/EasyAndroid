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

import com.frand.easyandroid.util.FFLogger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class FFDBHelper extends SQLiteOpenHelper {

	/**
	 * Interface 数据库升级回调
	 */
	public interface FFDBListener {
		public void onCreate(SQLiteDatabase db);
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
	}
	/**
	 * 数据库創建更新监听器
	 */
	private FFDBListener mListener;
	
	/**
	 * 设置数据库更新监听器
	 * 
	 * @param mListener
	 *            数据库更新监听器
	 */
	public void setmListener(FFDBListener mListener) {
		this.mListener = mListener;
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 * @param name
	 *            数据库名字
	 * @param factory
	 *            可选的数据库游标工厂类，当查询(query)被提交时，该对象会被调用来实例化一个游标
	 * @param version
	 *            数据库版本
	 */
	public FFDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 * @param name
	 *            数据库名字
	 * @param factory
	 *            可选的数据库游标工厂类，当查询(query)被提交时，该对象会被调用来实例化一个游标
	 * @param version
	 *            数据库版本
	 * @param tadbUpdateListener
	 *            数据库更新监听器
	 */
	public FFDBHelper(Context context, String name, CursorFactory factory,
			int version, FFDBListener mListener) {
		super(context, name, factory, version);
		this.mListener = mListener;
	}

	public void onCreate(SQLiteDatabase db) {
		if(mListener!=null) {
			mListener.onCreate(db);
			FFLogger.i(this, "database onCreate path="+db.getPath());
		}
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (mListener != null) {
			mListener.onUpgrade(db, oldVersion, newVersion);
			FFLogger.i(this, "database onUpgrade version="+newVersion+"db path="+db.getPath());
		}
	}

}
