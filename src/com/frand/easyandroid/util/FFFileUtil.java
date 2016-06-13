package com.frand.easyandroid.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;

public class FFFileUtil {
	
	/**
	 * 保存字串到cache文件当中
	 * @param context
	 * @param strings 需要保存的字串
	 * @param fileName 要保存到的文件
	 */
	public static void saveStringsToCache(Context context, String strings, String fileName) {
		try {
			File file = new File(FFDiskUtil.getExternalCacheDir(context), fileName);
			if(file.exists()) {
				file.delete();
				file.createNewFile();
			}
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			byte[] bytes = strings.getBytes();
			fileOutputStream.write(bytes);
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 从cache文件中获取字串
	 * @param context
	 * @param fileName 需要从中获取的文件
	 * @return
	 */
	public static String getStringFromCache(Context context, String fileName) {
		String content = "";
		FileInputStream fileInputStream;
		try {
			File cacheFile = new File(FFDiskUtil.getExternalCacheDir(context), fileName);
			if(!cacheFile.exists()) {
				cacheFile.createNewFile();
			}
			fileInputStream = new FileInputStream(cacheFile);
			int len = 0;
			byte[] bytes = new byte[1024];
			StringBuffer stringBuffer = new StringBuffer();
			while ((len=fileInputStream.read(bytes))>0) {
				stringBuffer.append(new String(bytes, 0, len));
			}
			fileInputStream.close();
			content = new String(stringBuffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

}
