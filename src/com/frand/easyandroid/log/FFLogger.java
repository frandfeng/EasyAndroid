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
package com.frand.easyandroid.log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;

public class FFLogger {

	public static final int VERBOSE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 3;
	public static final int WARN = 4;
	public static final int ERROR = 5;
	private static HashMap<String, FFILogger> loggerHashMap = new HashMap<String, FFILogger>();
	
	public static void addPrintToLoggerCatLogger(boolean isPrintToLoggerCat) {
		if(isPrintToLoggerCat) {
			addLogger(new FFPrintToLogCatLogger());
		}
	}
	
	public static void addPrintToFileLogger(Context context, boolean isPrintToFileLogger) {
		if(isPrintToFileLogger) {
			addLogger(new FFPrintToFileLogger(context));
		}
	}
	
	public static void addPrintToDBLogger(Context context, boolean isPrintToDBLogger) {
		if(isPrintToDBLogger) {
			addLogger(new FFPrintToDBLogger(context));
		}
	}
	
	public static void addLogger(FFILogger logger) {
		String loggerName = logger.getClass().getName();
		if (!loggerHashMap.containsKey(loggerName)) {
			logger.open();
			loggerHashMap.put(loggerName, logger);
		}
	}

	public static void removeLogger(FFILogger logger) {
		String loggerName = logger.getClass().getName();
		if (loggerHashMap.containsKey(loggerName)) {
			logger.close();
			loggerHashMap.remove(loggerName);
		}
	}

	public static void v(Object object, String message) {
		printLoger(VERBOSE, object, message);
	}

	public static void d(Object object, String message) {
		printLoger(DEBUG, object, message);
	}

	public static void i(Object object, String message) {
		printLoger(INFO, object, message);
	}

	public static void w(Object object, String message) {
		printLoger(WARN, object, message);
	}

	public static void e(Object object, String message) {
		printLoger(ERROR, object, message);
	}

	public static void v(String tag, String message) {
		printLoger(VERBOSE, tag, message);
	}

	public static void d(String tag, String message) {
		printLoger(DEBUG, tag, message);
	}

	public static void i(String tag, String message) {
		printLoger(INFO, tag, message);
	}

	public static void w(String tag, String message) {
		printLoger(WARN, tag, message);
	}

	public static void e(String tag, String message) {
		printLoger(ERROR, tag, message);
	}

	public static void println(int priority, String tag, String message) {
		printLoger(priority, tag, message);
	}

	private static void printLoger(int priority, Object object, String message) {
		Class<?> cls = object.getClass();
		String tag = cls.getName();
		String arrays[] = tag.split("\\.");
		tag = arrays[arrays.length - 1];
		printLoger(priority, tag, message);
	}

	private static void printLoger(int priority, String tag, String message) {
		Iterator<Entry<String, FFILogger>> iter = loggerHashMap.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<String, FFILogger> entry = iter.next();
			FFILogger logger = entry.getValue();
			if (logger != null) {
				printLoger(logger, priority, tag, message);
			}
		}
	}

	private static void printLoger(FFILogger logger, int priority, String tag,
			String message) {
		switch (priority) {
		case VERBOSE:
			logger.v(tag, message);
			break;
		case DEBUG:
			logger.d(tag, message);
			break;
		case INFO:
			logger.i(tag, message);
			break;
		case WARN:
			logger.w(tag, message);
			break;
		case ERROR:
			logger.e(tag, message);
			break;
		default:
			break;
		}
	}
}
