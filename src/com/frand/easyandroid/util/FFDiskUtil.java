/*
 * Copyright (C) 2014-4-18 frandfeng
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
package com.frand.easyandroid.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;

/** 
 * @author frandfeng
 * @time 2014-4-18 下午4:32:38 
 * class description 
 */
public class FFDiskUtil {
	
	public static final String DOWNLOAD = "/download";
	public static final String CACHE = "/cache";
	
	public static String getDownLoadPath(Context context) {
		return getPackagePath(context)+DOWNLOAD;
	}
	
	public static String getCachePath(Context context) {
		return getPackagePath(context)+CACHE;
	}
	
	private static String getPackagePath(Context context) {
		return getSDPath()+"/Android/data/"+context.getPackageName();
	}
	
	private static String getSDPath() {
		File sdDir = null;
		//判断sd卡是否存在,如果存在且已分区，返回sd卡的目录，如果没有，则直接返回一个字串作为目录
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();//获取跟目录
		} else {
			sdDir = new File("/mnt/sdcard");
		}
		return sdDir.toString();
	}
	public static String getPath(Context context, String folder) {
		return context.getExternalCacheDir().getAbsolutePath()+"/../"+folder+"/";
	}
	
}
