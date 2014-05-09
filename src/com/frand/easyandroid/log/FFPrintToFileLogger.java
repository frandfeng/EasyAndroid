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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import android.content.Context;

import com.frand.easyandroid.FFApplication;
import com.frand.easyandroid.util.FFDateUtil;
import com.frand.easyandroid.util.FFDiskUtil;

public class FFPrintToFileLogger implements FFILogger {

	public static final int VERBOSE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 3;
	public static final int WARN = 4;
	public static final int ERROR = 5;
	private Context context;
	private String mPath;
	private Writer mWriter;

	public FFPrintToFileLogger(Context context) {
		this.context = context;
	}

	public void open() {
		try {
			File file = new File(FFDiskUtil.getExternalLoggerDir(context), FFDateUtil.getFormattedDate().trim()+".log");
			mPath = file.getAbsolutePath();
			mWriter = new BufferedWriter(new FileWriter(mPath), 2048);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getPath() {
		return mPath;
	}

	@Override
	public void v(String tag, String message) {
		println(VERBOSE, tag, message);
	}

	@Override
	public void d(String tag, String message) {
		println(DEBUG, tag, message);
	}

	@Override
	public void i(String tag, String message) {
		println(INFO, tag, message);
	}

	@Override
	public void w(String tag, String message) {
		println(WARN, tag, message);
	}

	@Override
	public void e(String tag, String message) {
		println(ERROR, tag, message);
	}

	@Override
	public void println(int priority, String tag, String message) {
		String printMessage = "";
		switch (priority) {
			case VERBOSE:
				printMessage = "[V]|";
				break;
			case DEBUG:
				printMessage = "[D]|";
				break;
			case INFO:
				printMessage = "[I]|";
				break;
			case WARN:
				printMessage = "[W]|";
				break;
			case ERROR:
				printMessage = "[E]|";
				break;
			default:
				break;
		}
		printMessage += tag + "|" + FFApplication.getApplication()
				.getApplicationContext().getPackageName() + "|" + message;
		println(printMessage);
	}

	public void println(String message) {
		try {
			mWriter.write(FFDateUtil.getFormattedDate());
			mWriter.write(message);
			mWriter.write('\n');
			mWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			mWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
