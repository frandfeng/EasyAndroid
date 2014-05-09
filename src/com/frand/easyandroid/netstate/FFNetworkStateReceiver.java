/*
 * Copyright frandfeng
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
package com.frand.easyandroid.netstate;

import com.frand.easyandroid.log.FFLogger;
import com.frand.easyandroid.netstate.FFNetWorkUtil.netType;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class FFNetworkStateReceiver extends BroadcastReceiver {
	
	private static FFNetworkStateReceiver networkStateReceiver;
	private static FFNetChangeObserver observer;
	private Boolean networkAvailable = false;
	private netType netType;

	/**
	 * 获取当前网络状态，true为网络连接成功，否则网络连接失败
	 * @return
	 */
	public Boolean isNetworkAvailable() {
		return networkAvailable;
	}

	public netType getAPNType() {
		return netType;
	}

	public static void setObserver(FFNetChangeObserver observer) {
		FFNetworkStateReceiver.observer = observer;
	}
	
	public static FFNetworkStateReceiver getInstance() {
		if(networkStateReceiver==null) {
			networkStateReceiver = new FFNetworkStateReceiver();
		}
		return networkStateReceiver;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)) {
			FFLogger.i(FFNetworkStateReceiver.this, "网络状态改变.");
			if (!FFNetWorkUtil.isNetworkConnected(context)) {
				FFLogger.i(FFNetworkStateReceiver.this, "没有网络连接.");
				networkAvailable = false;
			} else {
				FFLogger.i(FFNetworkStateReceiver.this, "网络连接成功.");
				netType = FFNetWorkUtil.getAPNType(context);
				networkAvailable = true;
			}
			notifyObserver();
		}
	}

	private void notifyObserver() {
		if (observer != null) {
			if (isNetworkAvailable()) {
				observer.onConnect(netType);
			} else {
				observer.onDisConnect();
			}
		}
	}
}