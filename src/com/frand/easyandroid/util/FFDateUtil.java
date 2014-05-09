package com.frand.easyandroid.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FFDateUtil {

	private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat(
			"[yyyy-MM-dd HH-mm-ss] ", Locale.CHINESE);
	
	public static String getFormattedDate() {
		return TIMESTAMP_FMT.format(new Date());
	}
}
