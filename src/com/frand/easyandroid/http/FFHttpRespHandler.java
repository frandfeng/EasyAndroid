/*
 * Copyright (C) 2014-4-21 frandfeng
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
package com.frand.easyandroid.http;

import org.apache.http.HttpResponse;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/** 
 * @author frandfeng
 * @time 2014-4-21 上午9:25:16 
 * class description 
 */
public abstract class FFHttpRespHandler {

	protected static Handler handler;
	protected static final int START_MESSAGE = 0;
	protected static final int PROGRESS_MESSAGE = 1;
	protected static final int FAILURE_MESSAGE = 2;
	protected static final int SUCCESS_MESSAGE = 3;
	protected static final int FINISH_MESSAGE = 4;
	
	/**
	 * FFBaseRespHandler的构造函数，用来初始化此类中的的成员变量
	 */
	@SuppressLint("HandlerLeak")
	public FFHttpRespHandler() {
		if (Looper.myLooper() != null) {
			handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					FFHttpRespHandler.this.handleMessage(msg);
				}
			};
		}
	}

	protected abstract void onStart(int reqTag, String reqUrl);

	protected abstract void onFailure(Throwable error, int reqTag, String reqUrl);
	
	protected abstract void onSuccess(String resp, int reqTag, String reqUrl);
	
	protected abstract void onFinish(int reqTag, String reqUrl);
	
	protected void onProgress(long totalSize, long currentSize,
			long speed, int reqTag, String reqUrl) {};
	
	protected void sendRespMsg(HttpResponse response, int reqTag, String reqUrl) {};
	
	/**
	 * 请求开始时，将请求的标志和路径封装成一个对象
	 * 以START_MESSAGE为key值发送到handler当中去统一处理
	 * @param reqTag 请求的标志
	 * @param reqUrl 请求的路径
	 */
	public void sendStartMsg(int reqTag, String reqUrl) {
		sendMessage(obtainMessage(START_MESSAGE, new Object[] {reqTag, reqUrl}));
	};
	
	/**
	 * 下载正在进行时，将下载文件的总大小，现在已经下载的大小和速度组成一个对象
	 * 以PROGRESS_MESSAGE为key值发送到handler当中去统一处理
	 * @param reqTag 请求的标志
	 * @param reqUrl 请求的路径
	 */
	protected void sendProgressMsg(long totalSize, long currentSize,
			long speed, int reqTag, String reqUrl) {
		sendMessage(obtainMessage(PROGRESS_MESSAGE, new Object[] { totalSize,
				currentSize, speed, reqTag, reqUrl }));
	}
	
	/**
	 * 请求失败时，将请求失败的原因，标志和路径封装成一个对象
	 * 以FAILURE_MESSAGE为key值发送到handler当中去统一处理
	 * @param reqTag 请求的标志
	 * @param reqUrl 请求的路径
	 */
	protected void sendFailureMsg(Throwable cause, int reqTag, String reqUrl) {
		sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[] {cause, reqTag, reqUrl}));
	}
	
	/**
	 * 请求成功时，将请求成功的结果，标志和路径封装成一个对象
	 * 以SUCCESS_MESSAGE为key值发送到handler当中去统一处理
	 * @param response 请求成功的结果
	 * @param reqTag 请求的标志
	 * @param reqUrl 请求的路径
	 */
	protected void sendSuccMsg(String response, int reqTag, String reqUrl) {
		sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[] {response, reqTag, reqUrl}));
	}
	
	/**
	 * 请求成功时，将请求成功的结果，标志和路径封装成一个对象
	 * 以SUCCESS_MESSAGE为key值发送到handler当中去统一处理
	 * @param response 请求成功的结果
	 * @param reqTag 请求的标志
	 * @param reqUrl 请求的路径
	 */
	protected void sendFinishMsg(int reqTag, String reqUrl) {
		sendMessage(obtainMessage(FINISH_MESSAGE, new Object[] {reqTag, reqUrl}));
	}
	
	/**
	 * 将response 将在请求过程中所标记的过程状态和过程结果封装在一个message中返回
	 * @param processCode 请求过程标记，如START_MESSAGE等
	 * @param processObject 请求过程中封装的一些内容，如url，result等
	 * @return
	 */
	protected Message obtainMessage(int processCode, Object processObject) {
		Message msg = null;
		if (handler != null) {
			msg = handler.obtainMessage(processCode, processObject);
		} else {
			msg = Message.obtain();
			msg.what = processCode;
			msg.obj = processObject;
		}
		return msg;
	}

	/**
	 * 发送封装好的msg到handler当中去处理，如果handler是空，则直接发送到函数中去处理
	 * @param msg
	 */
	protected void sendMessage(Message msg) {
		if (handler != null) {
			handler.sendMessage(msg);
		} else {
			handleMessage(msg);
		}
	}
	
	/**
	 * 处理发送过来的msg，有两种来源，一种是直接调用此函数
	 * 一种是将msg发送到handler当中去，通过handler来调用
	 * @param msg
	 */
	protected void handleMessage(Message msg) {
		Object[] response;
		switch (msg.what) {
		case START_MESSAGE:
			response = (Object[]) msg.obj;
			onStart((Integer) response[0], (String) response[1]);
			break;
		case FAILURE_MESSAGE:
			response = (Object[]) msg.obj;
			onFailure((Throwable) response[0], (Integer) response[1], (String) response[2]);
			break;
		case SUCCESS_MESSAGE:
			response = (Object[]) msg.obj;
			onSuccess((String) response[0], (Integer) response[1], (String) response[2]);
			break;
		case FINISH_MESSAGE:
			response = (Object[]) msg.obj;
			onFinish((Integer) response[0], (String) response[1]);
			break;
		case PROGRESS_MESSAGE:
			response = (Object[]) msg.obj;
			onProgress((Long) response[0], (Long) response[1],
					(Long) response[2], (Integer) response[3], (String) response[4]);
			break;
		}
	}
}
