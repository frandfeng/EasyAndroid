package com.frand.easyandroid.log;

import android.content.Context;

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
	private FFDB ffdb;
	private Context context;

	public FFPrintToDBLogger(Context context) {
		this.context = context;
	}
	
	@Override
	public void v(String tag, String message) {
//		println(VERBOSE, tag, message);
	}

	@Override
	public void d(String tag, String message) {
//		println(DEBUG, tag, message);
	}

	@Override
	public void i(String tag, String message) {
//		println(INFO, tag, message);
	}

	@Override
	public void w(String tag, String message) {
//		println(WARN, tag, message);
	}

	@Override
	public void e(String tag, String message) {
		println(ERROR, tag, message);
	}

	@Override
	public void open() {
		ffdb = FFApplication.getApplication().getmFfdbPool().getFreeDB();
		ffdb.createTable(FFLogDataEntity.class);
	}

	@Override
	public void close() {
		ffdb.close();
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
		logDataEntity.setApplication(context.getPackageName());
		logDataEntity.setTag(tag);
		logDataEntity.setText(message);
		logDataEntity.setTime(FFDateUtil.getFormattedDate());
		logDataEntity.setVersionCode(FFAppUtil.getAppVersionCode(context));
		ffdb.insert(logDataEntity);
	}
}