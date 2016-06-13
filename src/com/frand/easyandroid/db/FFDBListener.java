package com.frand.easyandroid.db;

import android.database.sqlite.SQLiteDatabase;

public interface FFDBListener {
	public void onCreate(SQLiteDatabase db);
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

}
