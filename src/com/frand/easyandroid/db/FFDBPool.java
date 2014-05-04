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

import java.util.Enumeration;
import java.util.Vector;

import com.frand.easyandroid.db.FFDBHelper.FFDBListener;
import com.frand.easyandroid.util.FFLogger;

import android.app.Application;
import android.content.Context;

public class FFDBPool {
	
	private static String dbName = "easyandroid";
	private static int dbVersion = 1;
	private static FFDBPool instance;
	private static FFDBListener mListener;
	private int initDBNum = 1; // 连接池的初始大小
	private int increDBNum = 1;// 连接池自动增加的大小
	private int maxDBNum = 10; // 连接池最大的大小
	private boolean isWrite = false;
	private Vector<FFDB> ffdbs = null; // 存放连接池中数据库连接的向量
	private Context context;

	public FFDBPool(Context context) {
		this.context = context;
	}

	public static FFDBPool getInstance(Application application, String dbName, int dbVersion) {
		if(instance==null) {
			FFDBPool.dbName = dbName;
			FFDBPool.dbVersion = dbVersion;
			FFDBPool.mListener = (FFDBListener) application;
			instance = new FFDBPool(application.getApplicationContext());
			FFLogger.i(instance, "db pool has been init");
		}
		return instance;
	}
	
	/**
	 * 
	 * 返回连接池的初始大小
	 * 
	 * @return 初始连接池中可获得的连接数量
	 */
	public int getInitDBNum() {
		return initDBNum;
	}
	
	/**
	 * 设置连接池的初始大小
	 * 
	 * @param 用于设置初始连接池中连接的数量
	 */
	public void setInitDBNum(int initDBNum) {
		this.initDBNum = initDBNum;
	}

	/**
	 * 返回连接池自动增加的大小 、
	 * 
	 * @return 连接池自动增加的大小
	 */
	public int getIncreDBNum() {
		return increDBNum;
	}

	/**
	 * 设置连接池自动增加的大小
	 * 
	 * @param 连接池自动增加的大小
	 */
	public void setIncreDBNum(int increDBNum) {
		this.increDBNum = increDBNum;
	}

	/**
	 * 
	 * 返回连接池中最大的可用连接数量
	 * 
	 * @return 连接池中最大的可用连接数量
	 */
	public int getMaxDBNum() {
		return maxDBNum;
	}

	/**
	 * 设置连接池中最大可用的连接数量
	 * 
	 * @param 设置连接池中最大可用的连接数量值
	 */
	public void setMaxDBNum(int maxDBNum) {
		this.maxDBNum = maxDBNum;
	}

	/**
	 * 
	 * 创建一个数据库连接池，连接池中的可用连接的数量采用类成员 initialSQLiteDatabase 中设置的值
	 */
	public synchronized void initDBs() {
		if (ffdbs==null) {
			ffdbs = new Vector<FFDB>();
			createDB(initDBNum);
			FFLogger.i(this, "ffdbs has been init");
		}
	}

	/**
	 * 创建由 numSQLiteDatabase 指定数目的数据库连接 , 并把这些连接 放入 numSQLiteDatabase 向量中
	 * 
	 * @param numSQLiteDatabase
	 *            要创建的数据库连接的数目
	 */
	private void createDB(int dbNum) {
		for (int i=0; i<dbNum&&ffdbs.size()<maxDBNum; i++) {
			newDB();
		}
	}

	/**
	 * 创建一个新的数据库连接并返回它
	 * 
	 * @return 返回一个新创建的数据库连接
	 */
	private void newDB() {
		FFDB ffdb = new FFDB(context, dbName, dbVersion, mListener);
		ffdb.openDatabase(isWrite);
		ffdbs.addElement(ffdb);
		FFLogger.i(this, "one ffdb has been created and it's path is"+ffdb.getmSQLiteDatabase().getPath());
	}

	/**
	 * 通过调用 getFreeDBOrCreate() 函数返回一个可用的数据库连接 ,
	 * 如果当前没有可用的数据库连接，并且更多的数据库连接不能创 建（如连接池大小的限制），此函数等待一会再尝试获取。
	 * 
	 * @return 返回一个可用的数据库连接对象
	 */

	public synchronized FFDB getFreeDB() {
		FFDB sqliteDatabase = getFreeDBOrCreate();
		while (sqliteDatabase == null) {
			wait(250);
			sqliteDatabase = getFreeDBOrCreate();
		}
		return sqliteDatabase;
	}

	/**
	 * 本函数通过调用findFreeDBInPool返回一个可用的的数据库连接，如果 当前没有可用的数据库连接，本函数则根据
	 * increDBNum设置 的值创建几个数据库连接，并放入连接池中。 如果创建后，所有的连接仍都在使用中，则返回null
	 * 
	 * @return 返回一个可用的数据库连接
	 */
	private FFDB getFreeDBOrCreate() {
		FFDB sqLiteDatabase = findFreeDBInPool();
		if (sqLiteDatabase == null) {
			createDB(increDBNum);
			sqLiteDatabase = findFreeDBInPool();
		}
		return sqLiteDatabase;
	}

	/**
	 * 查找连接池中所有的连接，查找一个可用的数据库连接， 如果没有可用的连接，返回 null
	 * 
	 * @return 返回一个可用的数据库连接
	 */
	private FFDB findFreeDBInPool() {
		FFDB ffdb = null;
		Enumeration<FFDB> enumerate = ffdbs.elements();
		while (enumerate.hasMoreElements()) {
			FFDB mFfdb = (FFDB) enumerate.nextElement();
			if (!mFfdb.isBusy()) {
				ffdb = mFfdb;
				mFfdb.setBusy(true);
			}
		}
		return ffdb;
	}

	/**
	 * 此函数返回一个数据库连接到连接池中，并把此连接置为空闲。 所有使用连接池获得的数据库连接均应在不使用此连接时返回它。
	 * 
	 * @param 需返回到连接池中的连接对象
	 */
	public void releaseDB(FFDB mFfdb) {
		FFDB ffdb = null;
		Enumeration<FFDB> enumerate = ffdbs.elements();
		while (enumerate.hasMoreElements()) {
			ffdb = (FFDB) enumerate.nextElement();
			if(ffdb==mFfdb) {
				mFfdb.setBusy(false);
			}
			break;
		}
	}

	/**
	 * 刷新连接池中所有的连接对象
	 * 
	 */
	public synchronized void refreshDB() {
		Enumeration<FFDB> enumerate = ffdbs.elements();
		while (enumerate.hasMoreElements()) {
			// 获得一个连接对象
			FFDB ffdb = (FFDB) enumerate.nextElement();
			// 如果对象忙则等 5 秒 ,5 秒后直接刷新
			if (ffdb.isBusy()) {
				wait(5000); // 等 5 秒
			}
			ffdb.setBusy(false);
		}
	}

	/**
	 * 关闭连接池中所有的连接，并清空连接池。
	 */
	public synchronized void closeAllDB() {
		Enumeration<FFDB> enumerate = ffdbs.elements();
		while (enumerate.hasMoreElements()) {
			FFDB ffdb = (FFDB) enumerate.nextElement();
			if (ffdb.isBusy()) {
				wait(5000);
			}
			closeDB(ffdb);
			ffdbs.removeElement(ffdb);
		}
	}

	/**
	 * 关闭一个数据库连接
	 * 
	 * @param 需要关闭的数据库连接
	 */

	private void closeDB(FFDB ffdb) {
		ffdb.close();
		ffdb.setBusy(false);
	}

	/**
	 * 使程序等待给定的毫秒数
	 * 
	 * @param 给定的毫秒数
	 */
	private void wait(int mSeconds) {
		try {
			Thread.sleep(mSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
