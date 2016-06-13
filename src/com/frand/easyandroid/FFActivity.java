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

import java.util.List;

import com.frand.easyandroid.command.FFActivityCommand;
import com.frand.easyandroid.command.FFIResponseListener;
import com.frand.easyandroid.command.FFRequest;
import com.frand.easyandroid.data.FFLogDataEntity;
import com.frand.easyandroid.db.FFDB;
import com.frand.easyandroid.helpers.BasePreferHelper;
import com.frand.easyandroid.http.FFRequestParams;
import com.frand.easyandroid.netstate.FFNetWorkUtil.netType;
import com.frand.easyandroid.util.FFAppUtil;
import com.frand.easyandroid.views.CustomToast;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

/**
 * 所有Activity的基类，为activity自动加载和绑定view
 * @author frand
 *
 */
public abstract class FFActivity extends FragmentActivity {
	
	private ProgressDialog progress;
	
	public void showProgress() {
		if(progress!=null&&progress.isShowing()) {
			progress.dismiss();
		}
		progress = new ProgressDialog(this);
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.show();
	}
	
	public void dismissProgress() {
		if(progress!=null&&progress.isShowing()) {
			progress.dismiss();
		}
	}
	
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
		lisenceProject();
	}

	protected void onAfterOnCreate(Bundle savedInstanceState) {
		loadDefaultLayout();
		getFFApplication().onActivityCreated(this);
	}

	protected void onAfterOnResume() {
		getFFApplication().onActivityResumed();
		if(moduleName.equals("Splash")) {
			// 注册activity启动控制控制器
			FFApplication.getApplication().registerCommand(FFActivityCommand.FFACTIVITYCOMMAND,
					FFActivityCommand.class);
		}
		sendCrashReport();
	}
	
	/**
	 * 初始化资源信息
	 */
	private void initResource() {
		initModuleName();
		getFFApplication().getInjector().injectResource(this);
	}
	
	private void lisenceProject() {
		if(FFApplication.prefer.getString(BasePreferHelper.STR_PROJECT_STATE, "1").equals("0")) {
			CustomToast.toast(this, "此版本为测试版");
		}
	}
	
	private void sendCrashReport() {
		if(!getClass().getName().equals(FFAppUtil.getPackageName(this)+".activities.SplashActivity")) {
			return;
		} else if (FFApplication.prefer.getString(BasePreferHelper.STR_ERROR_STATE, "1").equals("0")) {
			return;
		}
		FFDB ffdb = FFApplication.getmFfdbPool().getFreeDB();
		List<FFLogDataEntity> errorDataEntities = ffdb.query(
				FFLogDataEntity.class, false, "", "", "", "", "");
		FFApplication.getmFfdbPool().releaseDB(ffdb);
		if(errorDataEntities!=null&&errorDataEntities.size()>0) {
			FFLogDataEntity errorDataEntity = errorDataEntities.get(0);
			FFRequestParams params = new FFRequestParams();
			params.put("level", errorDataEntity.getLevel());
			params.put("time", errorDataEntity.getTime());
			params.put("tag", errorDataEntity.getTag());
			String message = errorDataEntity.getMessage()==null?"":errorDataEntity.getMessage();
			params.put("message", message.length()<2000?message:message.substring(0, 2000));
			params.put("appVer", errorDataEntity.getAppVer());
			params.put("brand", errorDataEntity.getBrand());
			params.put("model", errorDataEntity.getModel());
			params.put("systemVer", errorDataEntity.getSystemVer());
			params.put("os", "2");
			params.put("application", getPackageName());
//			new BaseHttpHelper(this).request(ReqAPI.ERRORLOG, ReqType.GET, params, new FFStringRespHandler() {
//				
//				@Override
//				public void onSuccess(String resp, int reqTag, String reqUrl) {
//					FFLogger.d("frand", "req succ resp "+resp);
//					FFDB ffdb = FFApplication.getmFfdbPool().getFreeDB();
//					ffdb.delete(FFLogDataEntity.class, "");
//					FFApplication.getmFfdbPool().releaseDB(ffdb);
//				}
//				
//				@Override
//				public void onStart(int reqTag, String reqUrl) {
//					FFLogger.d("frand", "req url start:"+reqUrl);
//				}
//				
//				@Override
//				public void onFinish(int reqTag, String reqUrl) {
//					FFLogger.d("frand", "req url finish:"+reqUrl);
//				}
//				
//				@Override
//				public void onFailure(Throwable error, int reqTag, String reqUrl) {
//					FFLogger.e(getClass().getName(), FFStringUtil.getErrorInfo(error));
//					error.printStackTrace();
//				}
//			});
		}
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
	 * 初始化模块名，根据模块名来加载layout
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
	 * @param activityResID
	 */
	public void doActivity(String activityKey) {
		FFRequest request = new FFRequest(activityKey);
		doCommand(FFActivityCommand.FFACTIVITYCOMMAND, request);
	}
	
	/**
	 * 运行activity
	 * @param activityKey
	 * @param inAnim 新页面进入的动画
	 * @param outAnim 旧页面退出的动画
	 */
	public void doActivity(String activityKey, int inAnim, int outAnim) {
		FFRequest request = new FFRequest(activityKey);
		request.setInAnim(inAnim);
		request.setOutAnim(outAnim);
		doCommand(FFActivityCommand.FFACTIVITYCOMMAND, request);
	}
	
	/**
	 * 运行activity
	 * @param activityKey
	 * @param bundle 启动时携带的bunddle参数
	 */
	public void doActivity(String activityKey, Bundle bundle) {
		FFRequest request = new FFRequest(activityKey);
		request.setRequestBundle(bundle);
		doCommand(FFActivityCommand.FFACTIVITYCOMMAND, request);
	}
	
	/**
	 * 运行activity
	 * @param activityKey
	 * @param bundle 启动时携带的bunddle参数
	 * @param inAnim 新页面进入的动画
	 * @param outAnim 旧页面退出的动画
	 */
	public void doActivity(String activityKey, Bundle bundle, int inAnim, int outAnim) {
		FFRequest request = new FFRequest(activityKey);
		request.setRequestBundle(bundle);
		request.setInAnim(inAnim);
		request.setOutAnim(outAnim);
		doCommand(FFActivityCommand.FFACTIVITYCOMMAND, request);
	}
	
	/**
	 * 运行activity
	 * @param activityKey
	 * @param finishBefore 是否要关闭以前的activity
	 */
	public void doActivity(String activityKey, boolean finishBefore) {
		FFRequest request = new FFRequest(activityKey, finishBefore);
		doCommand(FFActivityCommand.FFACTIVITYCOMMAND, request);
	}
	
	/**
	 * 运行activity
	 * @param activityKey
	 * @param bundle 启动时携带的bunddle参数
	 * @param finishBefore 是否要关闭以前的activity
	 */
	public void doActivity(String activityKey, Bundle bundle, boolean finishBefore) {
		FFRequest request = new FFRequest(activityKey, finishBefore);
		request.setRequestBundle(bundle);
		doCommand(FFActivityCommand.FFACTIVITYCOMMAND, request);
	}
	
	/**
	 * 运行activity
	 * @param activityKey
	 * @param bundle 启动时携带的bunddle参数
	 * @param finishBefore 是否要关闭以前的activity
	 * @param inAnim 新页面进入的动画
	 * @param outAnim 旧页面退出的动画
	 */
	public void doActivity(String activityKey, Bundle bundle,
			boolean finishBefore, int inAnim, int outAnim) {
		FFRequest request = new FFRequest(activityKey, finishBefore);
		request.setRequestBundle(bundle);
		request.setInAnim(inAnim);
		request.setOutAnim(outAnim);
		doCommand(FFActivityCommand.FFACTIVITYCOMMAND, request);
	}

	/**
	 * 开始执行命令
	 * @param commandKey 命令的唯一主键
	 * @param request 命令的主要内容
	 */
	public void doCommand(String commandKey, FFRequest request) {
		doCommand(commandKey, request, null);
	}

	/**
	 * 开始执行命令
	 * @param commandKey 命令的唯一主键
	 * @param request 命令的主要内容
	 * @param listener 执行命令完成后的回调
	 */
	public void doCommand(String commandKey, FFRequest request,
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

	/**
	 * 获取activity的主页面
	 * @return
	 */
	public View getContentView() {
		return ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
	}
}
