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
import java.io.IOException;

import com.frand.easyandroid.log.FFLogger;

import android.content.Context;
import android.os.Environment;

/** 
 * @author frandfeng
 * @time 2014-4-18 下午4:32:38 
 * class description 
 */
public class FFDiskUtil {
	
	public static final String DOWNLOAD = "download";
	public static final String CACHE = "cache";
	public static final String LOGGER = "logger";
	
	public static File getExternalDownLoadDir(Context context) {
		return getExternalFileDir(context, DOWNLOAD);
	}
	
	public static File getExternalCacheDir(Context context) {
		return getExternalFileDir(context, CACHE);
	}
	
	public static File getExternalLoggerDir(Context context) {
		return getExternalFileDir(context, LOGGER);
	}
	
	public static File getExternalFileDir(Context context, String fileName) {
		File fileDir = new File(getExternalPackageDir(context), fileName);
		if (!fileDir.exists()) {
			if (!fileDir.mkdirs()) {
				FFLogger.i(FFDiskUtil.class.getName(), "Unable to create external cache directory");
				return null;
			}
			try {
				new File(fileDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				FFLogger.i(FFDiskUtil.class.getName(), "Can't create \".nomedia\" file in application external cache directory");
			}
		}
		return fileDir;
	}
	
	public static File getExternalPackageDir(Context context) {
		return new File(getExternalDataDir(), context.getPackageName());
	}
	
	public static File getExternalDataDir() {
		File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		return dataDir;
	}
}
