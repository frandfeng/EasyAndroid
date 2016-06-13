package com.frand.easyandroid.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class FFAppUtil {

	/**
	 * 获取app的version code
	 * @param context
	 * @return
	 */
	public static int getAppVersionCode(Context context) {
		int versionCode = 0;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionCode = pi.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 获取app的version Name
	 * @param context
	 * @return
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 获取app的名称
	 * @param context
	 * @return
	 */
	public static String getAppName(Context context) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = context.getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(
					context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		return (String) packageManager.getApplicationLabel(applicationInfo);
	}

	public static String getPackageName(Context context) {
		return context.getPackageName();
	}
	
	/**
	 * 安装apk
	 * @param context
	 * @param apkPath
	 */
	public static void installApk(Context context, String apkPath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(apkPath)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 获取到所有安装了的应用程序的信息
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAllApps(Context context) {
		List<AppInfo> list = new ArrayList<AppInfo>();
		AppInfo myAppInfo;
		// 获取到所有安装了的应用程序的信息，包括那些卸载了的，但没有清除数据的应用程序
		List<PackageInfo> packageInfos = context.getPackageManager()
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (PackageInfo info : packageInfos) {
			myAppInfo = new AppInfo();
			// 拿到包名
			String packageName = info.packageName;
			// 拿到应用程序的信息
			ApplicationInfo appInfo = info.applicationInfo;
			// 拿到应用程序的图标
			Drawable icon = appInfo.loadIcon(context.getPackageManager());
			// 拿到应用程序的程序名
			String appName = appInfo.loadLabel(context.getPackageManager())
					.toString();
			myAppInfo.setAppName(appName);
			myAppInfo.setPackageName(packageName);
			myAppInfo.setIcon(icon);
			if (filterApp(appInfo)) {
				myAppInfo.setSystemApp(false);
			} else {
				myAppInfo.setSystemApp(true);
			}
			list.add(myAppInfo);
		}
		return list;
	}

	// 判断是不是系统应用
	public static boolean filterApp(ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return true;
		}
		return false;
	}

	public static class AppInfo {
		private Drawable icon;
		private String appName;
		private String packageName;
		private boolean isSystemApp;

		public Drawable getIcon() {
			return icon;
		}

		public void setIcon(Drawable icon) {
			this.icon = icon;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public String getPackageName() {
			return packageName;
		}

		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}

		public boolean isSystemApp() {
			return isSystemApp;
		}

		public void setSystemApp(boolean isSystemApp) {
			this.isSystemApp = isSystemApp;
		}
	}
}
