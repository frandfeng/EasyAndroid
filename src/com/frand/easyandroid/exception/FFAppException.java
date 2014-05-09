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
package com.frand.easyandroid.exception;

import java.lang.Thread.UncaughtExceptionHandler;

import com.frand.easyandroid.annotation.FFEventListener;
import com.frand.easyandroid.log.FFLogger;
import com.frand.easyandroid.util.FFStringUtil;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

public class FFAppException implements UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";
	private static FFAppException instance;
	private Context mContext;
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private FFAppException(Context context) {
		init(context);
	}

	public static FFAppException getInstance(Context context) {
		if (instance == null) {
			instance = new FFAppException(context);
		}
		return instance;
	}

	private void init(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handledException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ex.printStackTrace();
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */
	private boolean handledException(final Throwable ex) {
		if (ex == null) {
			return true;
		}
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				FFLogger.e(this, FFStringUtil.getErrorInfo(ex));
				Toast.makeText(mContext, "恭喜您中奖了", Toast.LENGTH_SHORT).show();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
				Looper.loop();
			};
		}.start();
		return true;
	}
}