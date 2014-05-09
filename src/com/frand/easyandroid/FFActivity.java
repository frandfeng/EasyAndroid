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

import com.frand.easyandroid.command.FFActivityCommand;
import com.frand.easyandroid.command.FFIResponseListener;
import com.frand.easyandroid.command.FFRequest;
import com.frand.easyandroid.netstate.FFNetWorkUtil.netType;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public abstract class FFActivity extends Activity {
	
	private String moduleName = "";

	public String getModuleName() {
		return moduleName;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		onPreOnCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		onAfterOnCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		onAfterOnResume();
	}

	public FFApplication getFFApplication() {
		return (FFApplication) getApplication();
	}

	protected void onPreOnCreate(Bundle savedInstanceState) {
		initResource();
	}

	protected void onAfterOnCreate(Bundle savedInstanceState) {
		getFFApplication().onActivityCreated(this);
	}

	protected void onAfterOnResume() {
		getFFApplication().onActivityResumed();
	}
	
	private void initResource() {
		initModuleName();
		getFFApplication().getInjector().injectResource(this);
		loadDefaultLayout();
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		onAfterSetContentView();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		onAfterSetContentView();
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		onAfterSetContentView();
	}

	protected void onAfterSetContentView() {
		getFFApplication().getInjector().injectView(this);
	};

	/**
	 * 初始化模块名
	 */
	private String initModuleName() {
		String moduleName = this.moduleName;
		if (moduleName == null || moduleName.equalsIgnoreCase("")) {
			moduleName = getClass().getName().substring(0,
					getClass().getName().length() - 8);
			String arrays[] = moduleName.split("\\.");
			this.moduleName = moduleName = arrays[arrays.length - 1];
		}
		return moduleName;
	}

	/**
	 * 加载默认的layout
	 */
	private void loadDefaultLayout() {
		try {
			int layoutResID = getFFApplication().getLayoutLoader().getLayoutID(
					"activity_" + moduleName);
			setContentView(layoutResID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 运行activity
	 * 
	 * @param activityResID
	 */
	public final void doActivity(String activityKey) {
		FFRequest request = new FFRequest(activityKey);
		doCommand(FFActivityCommand.FFACTIVITYCOMMAND, request);
	}
	
	/**
	 * 运行activity
	 * 
	 * @param activityResID
	 */
	public final void doActivity(String activityKey, boolean record) {
		FFRequest request = new FFRequest(activityKey, record);
		doCommand(FFActivityCommand.FFACTIVITYCOMMAND, request);
	}

	public final void doCommand(String commandKey, FFRequest request) {
		doCommand(commandKey, request, null);
	}

	public final void doCommand(String commandKey, FFRequest request,
			FFIResponseListener listener) {
		getFFApplication().doCommand(commandKey, request, listener);
	}
	/**
	 * 网络连接连接时调用
	 */
	public void onConnect(netType type) {
	}

	/**
	 * 当前没有网络连接
	 */
	public void onDisConnect() {
	}

	@Override
	public void finish() {
		getFFApplication().onActivityFinished(this);
		super.finish();
	}
	
	/**
	 * 退出应用程序
	 * 
	 * @param isBackground
	 *            是否开开启后台运行,如果为true则为后台运行
	 */
	public void exitApp(Boolean isBackground) {
		getFFApplication().exitApp(isBackground);
	}

	public final View getContentView() {
		return ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
