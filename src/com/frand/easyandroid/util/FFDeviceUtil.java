package com.frand.easyandroid.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.View;

public class FFDeviceUtil {

	/**
	 * 获取设备的imei号
	 * @param context
	 * @return
	 */
	public static String getImei(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager)
				context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}
	
	/**
	 * 获取view的款和高
	 * @param v
	 * @return
	 */
	public static int[] getLocation(View v) {
	    int[] loc = new int[4];
	    int[] location = new int[2];
	    v.getLocationInWindow(location);
	    loc[0] = location[0];
	    loc[1] = location[1];
	    loc[2] = v.getWidth();
	    loc[3] = v.getHeight();
	    return loc;
	}
	
	/**
	 * 随机生成设备IMEI
	 * @return
	 */
	public static String generateImei() {
		String imei = "IMEI";
		for(int i=0; i<12; i++) {
			int random = (int)(Math.random() * 10);
			imei += String.valueOf(random);
		}
		return imei;
	}
	
	/**
	 * 获取设备品牌
	 * @return
	 */
	public static String getBrand() {
		return FFStringUtil.trim(Build.BRAND, 20);
	}
	
	/**
	 * 获取设备型号
	 * @return
	 */
	public static String getModel() {
		return FFStringUtil.trim(Build.MODEL, 20);
	}
	
	/**
	 * 获取设备系统版本
	 * @return
	 */
	public static String getSystemVer() {
		return FFStringUtil.trim(Build.VERSION.RELEASE, 10);
	}
	
	/**
	 * 获取设备屏幕宽度
	 * @param context
	 * @return
	 */
	public static int getDeviceWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}
	
	/**
	 * 获取设备屏幕密度
	 * @param context
	 * @return
	 */
	public static float getDesity(Context context) {
		return context.getResources().getDisplayMetrics().scaledDensity;
	}
	
	/**
	 * 获取设备屏幕高度
	 * @param context
	 * @return
	 */
	public static int getDeviceHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}
	
	/**
	 * 获取app的版本名称
	 * @param context
	 * @return
	 */
	public static String getAppVersion(Context context) {
		String versionName = "";
		try {   
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);   
			versionName = pi.versionName;   
			if (versionName == null || versionName.length() <= 0) return "";
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} 
		return FFStringUtil.trim(versionName, 10);
	}

	/**
	 * 检测app是否联网
	 * @param context
	 * @return
	 */
	public static boolean isnetWorkAvilable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		} else {
			NetworkInfo[] networkInfos = connectivityManager
					.getAllNetworkInfo();
			if (networkInfos != null) {
				for (int i = 0, count = networkInfos.length; i < count; i++) {
					if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
}
