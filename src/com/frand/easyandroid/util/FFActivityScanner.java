/*
 * Copyright (C) 2014-4-8 frandfeng
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
package com.frand.easyandroid.util;

import java.util.List;

import com.frand.easyandroid.FFApplication;
import com.frand.easyandroid.util.FFAppUtil.AppInfo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * @author frandfeng 扫面并注册所有的Activity
 * @time 2014-4-8 上午11:07:56 class description
 */
public class FFActivityScanner extends Thread {

	private Context mContext;

	public FFActivityScanner(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public void run() {
		List<AppInfo> infos = FFAppUtil.getAllApps(mContext);
        for(int i=0; i<infos.size(); i++) {
        	AppInfo info = infos.get(i);
        	try {
				PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(
						info.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
				ActivityInfo[] activityInfos = packageInfo.activities;
				if(activityInfos != null && activityInfos.length > 0) {
		            if(packageInfo.packageName.equals(FFAppUtil.getPackageName(mContext))) {
		            	for(int j=0; j<activityInfos.length; j++) {
		            		ActivityInfo activityInfo = activityInfos[j];
		            		FFApplication.getApplication().registerActivity(activityInfo.name,
		            				 Class.forName(activityInfo.name));
		            	}
		            }
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
        }
	}
}
