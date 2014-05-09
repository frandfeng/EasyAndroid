/*
 * Copyright (C) 2013 frandfeng
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.frand.easyandroid.log.FFLogger;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FFAssetsUtil {

	/**
	 * 通过文件名从Assets中获得资源,以输入流的形式返回
	 * 
	 * @param context
	 * @param fileName
	 *            文件名应为assets文件下载绝对路径
	 * @return 以InputStream的形式返回
	 */
	public static InputStream getInputStreamForName(Context context,
			String fileName) {
		AssetManager assetManager = context.getAssets();
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open(fileName);
		} catch (IOException e) {
			FFLogger.d(FFAssetsUtil.class.getClass().getName(), e.getMessage());
		}
		return inputStream;
	}

	/**
	 * 通过文件名从Assets中获得资源，以字符串的形式返回
	 * 
	 * @param context
	 * @param fileName
	 *            文件名应为assets文件下载绝对路径
	 * @return 以字符串的形式返回
	 */
	public static String getStringForName(Context context, String fileName) {
		InputStream inputStream = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		try {
			inputStream = getInputStreamForName(context, fileName);
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			FFLogger.d(FFAssetsUtil.class.getClass().getName(), e.getMessage());
		}
		return outputStream.toString();
	}

	/**
	 * 通过文件名从Assets中获得资源，以位图的形式返回
	 * 
	 * @param context
	 * @param fileName
	 *            文件名应为assets文件下载绝对路径
	 * @return 以位图的形式返回
	 */
	public static Bitmap getBitmapForName(Context context, String fileName) {
		Bitmap bitmap = null;
		InputStream inputStream = null;
		try {
			inputStream = getInputStreamForName(context, fileName);
			bitmap = BitmapFactory.decodeStream(inputStream);
			inputStream.close();
		} catch (IOException e) {
			FFLogger.d(FFAssetsUtil.class.getClass().getName(), e.getMessage());
		}
		return bitmap;
	}

}
