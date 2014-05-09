/*
 * Copyright (C) 2014-4-8 frandfeng
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

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.frand.easyandroid.FFApplication;
import com.frand.easyandroid.log.FFLogger;

import android.content.Context;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

/** 
 * @author frandfeng
 * @time 2014-4-8 上午11:07:56 
 * class description 
 */
public class FFActivityScanner {
	
	private static Field dexField;
	private Context mContext;
	
	static {
		try {
			dexField = PathClassLoader.class.getDeclaredField("mDexs");
			dexField.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public FFActivityScanner(Context mContext) {
		this.mContext = mContext;
	}
	
	public void run() {
		try {
	        DexFile df = new DexFile(mContext.getPackageCodePath());
	        for (Enumeration<String> iter = df.entries(); iter.hasMoreElements();) {
	            String className = iter.nextElement();
	            String matchString = "^("+mContext.getPackageName().replace(".", "[.]")+"[.]activities[.].*?Activity)(\\$[0-9])*$";
				Matcher matcher = Pattern.compile(matchString).matcher(className);
				if(matcher.find()) {
					String key = matcher.group(1);
		            FFLogger.i(this, "register activity "+key);
					FFApplication.getApplication().registerActivity(key, Class.forName(key));
				}
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
