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
package com.frand.easyandroid;

import java.util.Stack;

import com.frand.easyandroid.log.FFLogger;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

public class FFAppManager {
	
	private Stack<Activity> activityStack;
	private static FFAppManager instance;

	private FFAppManager() {

	}

	/**
	 * 单一实例
	 */
	public static FFAppManager getAppManager() {
		if (instance == null) {
			instance = new FFAppManager();
		}
		return instance;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		FFLogger.i(activity, "add activity "+activity.getClass().getName()
				.split("\\.")[activity.getClass().getName().split("\\.").length-1]);
		activityStack.add(activity);
		FFLogger.i(activity, "now stack size is " + activityStack.size());
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	 public void finishActivity() {
		 Activity activity = activityStack.lastElement();
		 finishActivity(activity);
	 }

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 移除指定的Activity
	 */
	public void removeActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	 public void finishActivity(Class<?> cls) {
		 for (Activity activity : activityStack) {
			 if (activity.getClass().equals(cls)) {
				 finishActivity(activity);
			 }
		 }
	 }

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		while (activityStack.size()>0) {
			activityStack.pop().finish();
		}
		activityStack.clear();
	}

	/**
	 * 退出应用程序
	 * @param context
	 * @param isBackground 是否开开启后台运行
	 */
	public void AppExit(Context context, Boolean isBackground) {
		try {
			finishAllActivity();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 注意，如果您有后台程序运行，请不要支持此句子
			if (!isBackground) {
				ActivityManager activityMgr = (ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE);
				activityMgr.killBackgroundProcesses(context.getPackageName());
				System.exit(0);
			}
		}
	}
}