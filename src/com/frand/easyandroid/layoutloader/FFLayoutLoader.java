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
package com.frand.easyandroid.layoutloader;

import java.lang.reflect.Field;

import com.frand.easyandroid.exception.FFNoLayoutException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class FFLayoutLoader implements FFILayoutLoader {

	private static FFLayoutLoader instance;
	private Context mContext;

	private FFLayoutLoader(Context context) {
		this.mContext = context;
	}

	/**
	 * 获得系统资源类
	 * 
	 * @return TASystemResLoader对象
	 */

	public static FFLayoutLoader getInstance(Context context) {
		if (instance == null) {
			instance = new FFLayoutLoader(context);
		}
		return instance;
	}

	/**
	 * 获得布局ID
	 * 
	 * @param context
	 * @param cls
	 * @param resIDName
	 * @return
	 * @throws NameNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws TAResIDNotFoundException
	 */
	public int getLayoutID(String resIDName) throws NameNotFoundException,
			ClassNotFoundException, IllegalArgumentException,
			IllegalAccessException, FFNoLayoutException {
		int resID = readResID("layout", resIDName);
		if (resID == 0) {
			throw new FFNoLayoutException();
		}
		return resID;
	}

	/**
	 * @param context
	 * @param resIDName
	 *            资源id名字
	 * @return
	 * @throws NameNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public int readResID(String type, String resIDName)
			throws NameNotFoundException, ClassNotFoundException,
			IllegalArgumentException, IllegalAccessException {
		String packageName;
		PackageManager pm = mContext.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
		packageName = pi.packageName;
		if (packageName == null || packageName.equalsIgnoreCase("")) {
			throw new NameNotFoundException("没有获取到系统包名！");
		}
		packageName = packageName + ".R";
		Class<?> clazz = Class.forName(packageName);
		Class<?> cls = readResClass(clazz, packageName + "$" + type);
		if (cls == null) {
			throw new NameNotFoundException("没发现资源包名！");
		}
		return readResID(cls, resIDName);
	}

	/**
	 * 返回资源在R文件中生成的类
	 * 
	 * @param cls
	 *            资源类名
	 * @param respackageName
	 *            资源的包名
	 * @return 返回资源在R文件中生成的类
	 */
	public Class<?> readResClass(Class<?> cls, String respackageName) {
		Class<?>[] classes = cls.getDeclaredClasses();
		for (int i = 0; i < classes.length; i++) {
			Class<?> tempClass = classes[i];
			if (tempClass.getName().equalsIgnoreCase(respackageName)) {
				return tempClass;
			}
		}
		return null;
	}

	/**
	 * 读取R资源文件
	 * 
	 * @param cls
	 * @param string
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public int readResID(Class<?> cls, String resIDName)
			throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = cls.getDeclaredFields();
		for (int j = 0; j < fields.length; j++) {
			if (fields[j].getName().equalsIgnoreCase(resIDName)) {
				return fields[j].getInt(cls);
			}
		}
		return 0;
	}

}
