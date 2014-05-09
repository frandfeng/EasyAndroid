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

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.frand.easyandroid.command.FFActivityCommand;
import com.frand.easyandroid.command.FFCommandExecutor;
import com.frand.easyandroid.command.FFICommand;
import com.frand.easyandroid.command.FFIResponseListener;
import com.frand.easyandroid.command.FFRequest;
import com.frand.easyandroid.command.FFResponse;
import com.frand.easyandroid.config.FFIConfig;
import com.frand.easyandroid.config.FFPreferConfig;
import com.frand.easyandroid.config.FFProperConfig;
import com.frand.easyandroid.db.FFDB;
import com.frand.easyandroid.db.FFDBPool;
import com.frand.easyandroid.exception.FFAppException;
import com.frand.easyandroid.exception.FFNoCommandException;
import com.frand.easyandroid.layoutloader.FFLayoutLoader;
import com.frand.easyandroid.layoutloader.FFILayoutLoader;
import com.frand.easyandroid.log.FFLogger;
import com.frand.easyandroid.netstate.FFNetChangeObserver;
import com.frand.easyandroid.netstate.FFNetWorkUtil;
import com.frand.easyandroid.netstate.FFNetworkStateReceiver;
import com.frand.easyandroid.netstate.FFNetWorkUtil.netType;
import com.frand.easyandroid.util.FFActivityScanner;
import com.frand.easyandroid.util.FFInjector;

public class FFApplication extends Application implements FFIResponseListener {
	/** 配置器 为Preference */
	public static final int PREFERENCECONFIG = 0;
	/** 配置器 为PROPERTIESCONFIG */
	public static final int PROPERTIESCONFIG = 1;
	private static FFActivity currentActivity;
	private static final HashMap<String, Class<?>> registeredActivities
			= new HashMap<String, Class<?>>();
	public static int display_width = 0;
	public static int display_height = 0;
	public static FFDB ffdb;
	/** 获取布局文件ID加载器 */
	private FFILayoutLoader mLayoutLoader;
	/** 加载类注入器 */
	private FFInjector mInjector;
	/** App异常崩溃处理器 */
	private UncaughtExceptionHandler uncaughtExceptionHandler;
	/** 单例模式 */
	private static FFApplication application;
	private FFCommandExecutor mCommandExecutor;
	/** 数据库链接池 */
	private FFDBPool mFfdbPool;
	/** ThinkAndroid 应用程序运行Activity管理器 */
	private FFAppManager mAppManager;
	private Boolean networkAvailable = false;
	private FFNetChangeObserver ffNetChangeObserver;

	public FFDBPool getmFfdbPool() {
		if (mFfdbPool == null) {
			mFfdbPool = FFDBPool.getInstance(this);
		}
		return mFfdbPool;
	}

	@Override
	public void onCreate() {
		onPreCreateApplication();
		super.onCreate();
		doOncreate();
		onAfterCreateApplication();
	}

	private void doOncreate() {
		// 初始化此单例 application
		application = this;
		// 初始化preference,如果要设置值直接设置,不用在此初始化，为了保证值统一，直接在preferHelper中去设置
		// getConfig(PREFERENCECONFIG).setString("key", "value");
		display_width = getResources().getDisplayMetrics().widthPixels;
		display_height = getResources().getDisplayMetrics().heightPixels;
		initLogger();
		initDB();
		// 初始化网络状态，初始化网络断开或链接的监听器并设置监听
		networkAvailable = FFNetWorkUtil.isNetworkConnected(this);
		ffNetChangeObserver = new FFNetChangeObserver() {
			@Override
			public void onConnect(netType type) {
				super.onConnect(type);
				FFApplication.this.onConnect(type);
			}
			@Override
			public void onDisConnect() {
				super.onDisConnect();
				FFApplication.this.onDisConnect();
			}
		};
		FFNetworkStateReceiver.setObserver(ffNetChangeObserver);
		// 注册App异常崩溃处理器
		Thread.setDefaultUncaughtExceptionHandler(getUncaughtExceptionHandler());
		// 开始初始化命令执行器，生成一个线程池来运行命令
		mCommandExecutor = FFCommandExecutor.getInstance();
		// 注册activity启动控制控制器
		registerCommand(FFActivityCommand.FFACTIVITYCOMMAND,
				FFActivityCommand.class);
	}
	
	/**
	 * 初始化数据库
	 * 注意：初始化数据库完成后查看是否需要将log打印到数据库中，如果配置中没有初始化数据库，则将log打印到数据库无效
	 */
	private void initDB() {
		if(getConfig(PROPERTIESCONFIG).getBoolean("isNeedDB", false)==true) {
			getmFfdbPool().initDBs(getConfig(PROPERTIESCONFIG).getString("dbName", getApplicationContext().getPackageName()),
					getConfig(PROPERTIESCONFIG).getInt("dbVersion", 1));
			ffdb = getmFfdbPool().getFreeDB();
			FFLogger.addPrintToDBLogger(getApplicationContext(), getConfig(PROPERTIESCONFIG).getBoolean("isPrintToDBLogger", false));
		}
	}
	
	private void initLogger() {
		FFLogger.addPrintToLoggerCatLogger(getConfig(PROPERTIESCONFIG).getBoolean("isPrintToLoggerCat", false));
		FFLogger.addPrintToFileLogger(getApplicationContext(), getConfig(PROPERTIESCONFIG).getBoolean("isPrintToFileLogger", false));
	}
	
	/**
	 * 当前没有网络连接
	 */
	public void onDisConnect() {
		networkAvailable = false;
		if (currentActivity != null) {
			currentActivity.onDisConnect();
		}
	}

	/**
	 * 网络连接连接时调用
	 */
	protected void onConnect(netType type) {
		networkAvailable = true;
		if (currentActivity != null) {
			currentActivity.onConnect(type);
		}
	}

	/**
	 * 获取Application
	 * 
	 * @return
	 */
	public static FFApplication getApplication() {
		return application;
	}

	protected void onAfterCreateApplication() {
		getAppManager();
	}

	protected void onPreCreateApplication() {
	}

	public FFIConfig getPreferenceConfig() {
		return getConfig(PREFERENCECONFIG);
	}

	public FFIConfig getPropertiesConfig() {
		return getConfig(PROPERTIESCONFIG);
	}

	public FFIConfig getConfig(int confingType) {
		FFIConfig mCurrentConfig = null;
		if (confingType == PREFERENCECONFIG) {
			mCurrentConfig = FFPreferConfig.getPreferenceConfig(this);
		} else if (confingType == PROPERTIESCONFIG) {
			mCurrentConfig = FFProperConfig.getPropertiesConfig(this);
		} else {
			mCurrentConfig = FFProperConfig.getPropertiesConfig(this);
		}
		if (!mCurrentConfig.isLoadConfig()) {
			mCurrentConfig.loadConfig();
		}
		return mCurrentConfig;
	}

	public FFILayoutLoader getLayoutLoader() {
		if (mLayoutLoader == null) {
			mLayoutLoader = FFLayoutLoader.getInstance(this);
		}
		return mLayoutLoader;
	}

	public void setLayoutLoader(FFILayoutLoader layoutLoader) {
		this.mLayoutLoader = layoutLoader;
	}

	/**
	 * 设置 App异常崩溃处理器
	 * 
	 * @param uncaughtExceptionHandler
	 */
	public void setUncaughtExceptionHandler(
			UncaughtExceptionHandler uncaughtExceptionHandler) {
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;
	}

	private UncaughtExceptionHandler getUncaughtExceptionHandler() {
		if (uncaughtExceptionHandler == null) {
			uncaughtExceptionHandler = FFAppException.getInstance(this);
		}
		return uncaughtExceptionHandler;
	}

	public FFInjector getInjector() {
		if (mInjector == null) {
			mInjector = FFInjector.getInstance();
		}
		return mInjector;
	}

	public void setInjector(FFInjector injector) {
		this.mInjector = injector;
	}

	public void onActivityCreated(FFActivity activity) {
		mAppManager.addActivity(activity);
	}
	
	public void onActivityFinished(FFActivity activity) {
		mAppManager.removeActivity(activity);
		FFLogger.i(this, "activity removed "+activity.getModuleName());
	}
	
	public void onActivityResumed() {
		currentActivity = (FFActivity) mAppManager.currentActivity();
	}

	public void doCommand(String commandKey, FFRequest request,
			FFIResponseListener listener) {
		FFLogger.i(FFApplication.this, "commandKey=" + commandKey + "request="
				+ request.getRequestKey()+"isDestroyBefore="+request.isDestroyBefore());
		FFLogger.i(FFApplication.this, "Enqueue-ing command");
		try {
			if(listener==null) listener = this;
			FFCommandExecutor.getInstance().enqueueCommand(commandKey, request, listener);
		} catch (FFNoCommandException e) {
			e.printStackTrace();
		}
		FFLogger.i(FFApplication.this, "Enqueued command");
	}

	private static void processResponse(Message msg) {
		FFResponse response = (FFResponse) msg.obj;
		if (response != null) {
			Class<?> cls = registeredActivities.get(response
					.getResponseKey());
			if (cls != null) {
				Intent launcherIntent = new Intent(currentActivity, cls);
				currentActivity.startActivity(launcherIntent);
				if(response.isDestroyBefore()) {
					currentActivity.finish();
				}
			}
		}
	}

	public void registerActivity(String activityKey,
			Class<?> clz) {
		if(!registeredActivities.containsKey(activityKey)) {
			registeredActivities.put(activityKey, clz);
		}
	}
	
	public void unregisterActivity(String activityKey) {
		registeredActivities.remove(activityKey);
	}

	public void registerCommand(int resID, Class<? extends FFICommand> command) {
		String commandKey = getString(resID);
		registerCommand(commandKey, command);
	}
	
	/**
	 * 注册命令类型，在注册命令类型的同时将所有的在该类型下的命令注册进去
	 * @param commandKey
	 * @param command
	 */
	public void registerCommand(String commandKey,
			Class<? extends FFICommand> command) {
		if (command != null) {
			mCommandExecutor.registerCommand(commandKey, command);
		}
		if(commandKey.equals(FFActivityCommand.FFACTIVITYCOMMAND)) {
			new FFActivityScanner(this).run();
		} else {
		}
	}
	
	public static String[] getClassInPackage(String packageName) {
		String[] result;
		String classPath = packageName.replace(".", "/")+"/activities/";
		FFLogger.d("frand", "class path = "+classPath);
		File file = new File(classPath);
		result = file.list();
		return result;
	}

	public void unregisterCommand(int resID) {
		String commandKey = getString(resID);
		unregisterCommand(commandKey);
	}

	public void unregisterCommand(String commandKey) {
		mCommandExecutor.unregisterCommand(commandKey);
	}

	private static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			processResponse(msg);
		}
	};

	private void handleResponse(FFResponse response) {
		Message msg = new Message();
		msg.what = 0;
		msg.obj = response;
		handler.sendMessage(msg);
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onSuccess(FFResponse response) {
		handleResponse(response);
	}

	@Override
	public void onRuning(FFResponse response) {
	}

	@Override
	public void onFailure(FFResponse response) {
		handleResponse(response);
	}

	public FFAppManager getAppManager() {
		if (mAppManager == null) {
			mAppManager = FFAppManager.getAppManager();
		}
		return mAppManager;
	}

	/**
	 * 退出应用程序
	 * 
	 * @param isBackground
	 *            是否开开启后台运行,如果为true则为后台运行
	 */
	public void exitApp(Boolean isBackground) {
		mAppManager.AppExit(this, isBackground);
	}

	/**
	 * 获取当前网络状态，true为网络连接成功，否则网络连接失败
	 * 
	 * @return
	 */
	public Boolean isNetworkAvailable() {
		return networkAvailable;
	}

	@Override
	public void onFinish() {
	}
}
