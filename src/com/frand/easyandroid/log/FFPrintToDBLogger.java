package com.frand.easyandroid.log;

import android.content.Context;
import android.os.Build;

import com.frand.easyandroid.FFApplication;
import com.frand.easyandroid.data.FFLogDataEntity;
import com.frand.easyandroid.db.FFDB;
import com.frand.easyandroid.util.FFAppUtil;
import com.frand.easyandroid.util.FFDateUtil;

/**
 * 全部添加log到db中会导致stackOverflow的错误，只取W/E进行操作
 * @author Administrator
 *
 */
public class FFPrintToDBLogger implements FFILogger {

	public static final int VERBOSE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 3;
	public static final int WARN = 4;
	public static final int ERROR = 5;
	private Context context;

	public FFPrintToDBLogger(Context context) {
		this.context = context;
	}
	
	@Override
	public void v(String tag, String message) {
		println(VERBOSE, tag, message);
	}

	@Override
	public void d(String tag, String message) {
		println(DEBUG, tag, message);
	}

	@Override
	public void i(String tag, String message) {
		println(INFO, tag, message);
	}

	@Override
	public void w(String tag, String message) {
		println(WARN, tag, message);
	}

	@Override
	public void e(String tag, String message) {
		println(ERROR, tag, message);
	}

	@Override
	public void open() {
		FFDB ffdb = FFApplication.getmFfdbPool().getFreeDB();
		ffdb.createTable(FFLogDataEntity.class);
		FFApplication.getmFfdbPool().releaseDB(ffdb);
	}

	@Override
	public void close() {
	}

	@Override
	public void println(int priority, String tag, String message) {
		FFLogDataEntity logDataEntity = new FFLogDataEntity();
		switch (priority) {
		case VERBOSE:
			logDataEntity.setLevel("V");
			break;
		case DEBUG:
			logDataEntity.setLevel("D");
			break;
		case INFO:
			logDataEntity.setLevel("I");
			break;
		case WARN:
			logDataEntity.setLevel("W");
			break;
		case ERROR:
			logDataEntity.setLevel("E");
			break;
		default:
			break;
		}
		if(logDataEntity.getLevel().equals("E")) {
			logDataEntity.setTime(FFDateUtil.getFormattedDate());
			logDataEntity.setApplication(context.getPackageName());
			logDataEntity.setTag(tag);
			logDataEntity.setMessage(message);
			logDataEntity.setAppVer(FFAppUtil.getAppVersionName(context));
			logDataEntity.setBrand(Build.BRAND);
			logDataEntity.setModel(Build.MODEL);
			logDataEntity.setSystemVer(Build.VERSION.RELEASE);
			FFDB ffdb = FFApplication.getmFfdbPool().getFreeDB();
			ffdb.insert(logDataEntity);
			FFApplication.getmFfdbPool().releaseDB(ffdb);
		}
	}
}
