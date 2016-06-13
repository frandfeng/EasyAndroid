package com.frand.easyandroid.util;

import android.content.Context;
import android.graphics.Typeface;

public class FFFontUtil {

	private static Typeface tencentTypeface = Typeface.DEFAULT;
	
	public static Typeface getTencentTypeface() {
		return tencentTypeface;
	}

	/**
	 * 设置字体
	 * @param tencentTypeface
	 */
	public static void setTencentTypeface(Typeface tencentTypeface) {
		FFFontUtil.tencentTypeface = tencentTypeface;
	}

	/**
	 * 初始化字体
	 * @param context
	 * @param ttfPath
	 */
	public static void init(Context context, String ttfPath) {
		tencentTypeface = Typeface.createFromAsset(context.getAssets(), ttfPath);
	}
	
}
