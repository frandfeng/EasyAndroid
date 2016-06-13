package com.frand.easyandroid;

import com.frand.easyandroid.command.FFActivityCommand;
import com.frand.easyandroid.command.FFRequest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FFFragment extends Fragment {
	
	protected View mainView;
	private String moduleName = "";

	public String getModuleName() {
		return moduleName;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		onPreOnCreateView();
		super.onCreateView(inflater, container, savedInstanceState);
		return mainView;
	}
	
	protected void onPreOnCreateView() {
		initResource();
		loadDefaultLayout();
	}
	
	/**
	 * 初始化资源信息
	 */
	private void initResource() {
		initModuleName();
		((FFActivity)getActivity()).getFFApplication().getInjector().injectResource(this);
	}
	
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
			int layoutResID = ((FFActivity)getActivity()).getFFApplication().getLayoutLoader().getLayoutID(
					"fragment_" + moduleName);
			mainView = ((FFActivity)getActivity()).getLayoutInflater().inflate(layoutResID, null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		onAfterViewCreated();
	}
	
	/**
	 * 运行activity
	 * @param activityResID
	 */
	public void doActivity(String activityKey) {
		FFRequest request = new FFRequest(activityKey);
		if(getActivity()!=null) {
			((FFActivity)getActivity()).doCommand(FFActivityCommand.FFACTIVITYCOMMAND, request);
		}
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
		if(getActivity()!=null) {
			((FFActivity)getActivity()).doCommand(FFActivityCommand.FFACTIVITYCOMMAND, request);
		}
	}
	
	/**
	 * 运行activity
	 * @param activityKey
	 * @param bundle 启动时携带的bunddle参数
	 */
	public void doActivity(String activityKey, Bundle bundle) {
		FFRequest request = new FFRequest(activityKey);
		request.setRequestBundle(bundle);
		if(getActivity()!=null) {
			((FFActivity)getActivity()).doCommand(FFActivityCommand.FFACTIVITYCOMMAND, request);
		}
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
		if(getActivity()!=null) {
			((FFActivity)getActivity()).doCommand(FFActivityCommand.FFACTIVITYCOMMAND, request);
		}
	}
	
	/**
	 * 运行activity
	 * @param activityKey
	 * @param finishBefore 是否要关闭以前的activity
	 */
	public void doActivity(String activityKey, boolean finishBefore) {
		FFRequest request = new FFRequest(activityKey, finishBefore);
		if(getActivity()!=null) {
			((FFActivity)getActivity()).doCommand(FFActivityCommand.FFACTIVITYCOMMAND, request);
		}
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
		if(getActivity()!=null) {
			((FFActivity)getActivity()).doCommand(FFActivityCommand.FFACTIVITYCOMMAND, request);
		}
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
		if(getActivity()!=null) {
			((FFActivity)getActivity()).doCommand(FFActivityCommand.FFACTIVITYCOMMAND, request);
		}
	}
	
	protected void onAfterViewCreated() {
		((FFActivity)getActivity()).getFFApplication().getInjector().injectView(this, mainView);
	};
	
}
