/*
 * Copyright (C) 2014-4-11 frandfeng
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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/** 
 * @author frandfeng
 * @time 2014-4-11
 * class description 
 */
public class FFBackUtil {
	
	/**
	 * control of color drawable background deep 
	 */
	private static final int COLOR_DEEP = 25;
	/**
	 * control of bitmap drawable background deep
	 */
	private static final int DRAW_DEEP = 25;
	
	public static void addClickedEffect(View view) {
		final Drawable backDrawable = view.getBackground();
		if(backDrawable instanceof BitmapDrawable) {
			view.setBackgroundDrawable(getStateListDrawable(view.getBackground()));
		} else {
			view.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View view, MotionEvent event) {
					if(event.getAction()==MotionEvent.ACTION_DOWN) {
						Drawable selecteDrawable = getSelectedDrawable(view.getBackground());
						view.setBackgroundDrawable(selecteDrawable);
					} else if (event.getAction()==MotionEvent.ACTION_UP) {
						view.setBackgroundDrawable(backDrawable);
					}
					return false;
				}
			});
		}
	}
	
	public static StateListDrawable getStateListDrawable(Drawable normal) {
		StateListDrawable listDrawable = new StateListDrawable();
		Drawable pressed = getSelectedDrawable(normal);
		listDrawable.addState(new int[] { android.R.attr.state_pressed }, pressed);
		listDrawable.addState(new int[] { android.R.attr.state_selected }, pressed);
		listDrawable.addState(new int[] { android.R.attr.state_enabled }, normal);
		return listDrawable;
	}
	
	/**
	 * 传入一个drawable，将此drawable颜色加深组成一个drawable返回，此函数经常用于为button设置按下效果
	 * @param mDrawable 传入的drawable，类型可以为bitmapDrawable/ColorDrawable/GradientDrawable
	 * 		/ninePatchDrawable/insetDrawable/clipDrawable/layerDrawable/AnimationDrawable/
	 *     levelListDrawable/PaintDrawable/PictureDrawable/RotateDrawable/未设置
	 * @return 返回的drawable，将其直接赋值给button的backgroundDrawable
	 */
	private static Drawable getSelectedDrawable(Drawable mDrawable) {
		Drawable resultDrawable = null;
		if(mDrawable instanceof BitmapDrawable) {
			resultDrawable = getSelectedDrawable((BitmapDrawable) mDrawable);
		} else if (mDrawable instanceof ColorDrawable&&Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
			resultDrawable = getSelectedDrawable((ColorDrawable) mDrawable);
		} else if (mDrawable instanceof GradientDrawable) {
			resultDrawable = getSelectedDrawable((GradientDrawable) mDrawable);
		} else {
			resultDrawable = mDrawable;
		}
		return resultDrawable;
	}
	
	/**
	 * 传入一个drawable，将此drawable颜色加深为另一个drawable返回，此函数经常用于为button设置按下效果
	 * @param mDrawable 传入的drawable，类型为bitmapDrawable
	 * @return 返回的drawable，将其直接赋值给button的backgroundDrawable
	 */
	@SuppressWarnings("deprecation")
	private static Drawable getSelectedDrawable(BitmapDrawable mDrawable) {
		Bitmap srcBitmap = mDrawable.getBitmap();
		Bitmap bmp = Bitmap.createBitmap(srcBitmap.getWidth(),
				srcBitmap.getHeight(), Config.ARGB_8888);
		ColorMatrix cMatrix = new ColorMatrix();
		cMatrix.set(new float[] {
				1, 0, 0, 0, -DRAW_DEEP,
				0, 1, 0, 0, -DRAW_DEEP,
				0, 0, 1, 0, -DRAW_DEEP,
				0, 0, 0, 1, 0 });
		Paint paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
		Canvas canvas = new Canvas(bmp);
		canvas.drawBitmap(srcBitmap, 0, 0, paint);
		return new BitmapDrawable(bmp);
	}
	
	/**
	 * 传入一个drawable，将此drawable颜色加深组成一个drawable返回，此函数经常用于为button设置按下效果
	 * @param mDrawable 传入的drawable，类型为ColorDrawable
	 * @return 返回的drawable，将其直接赋值给button的backgroundDrawable
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static Drawable getSelectedDrawable(ColorDrawable mDrawable) {
		return new ColorDrawable(deepColor(mDrawable.getColor()));
	}
	
	/**
	 * 传入一个drawable，将此drawable颜色加深组成一个statelistdrawable返回，此函数经常用于为button设置按下效果
	 * @param mDrawable 传入的drawable，类型为GradientDrawable
	 * @return 返回的statelistdrawable，将其直接赋值给button的backgroundDrawable
	 */
	private static Drawable getSelectedDrawable(GradientDrawable mDrawable) {
		GradientDrawable gradientDrawable = (GradientDrawable) mDrawable.getConstantState().newDrawable();
		gradientDrawable.mutate();
		gradientDrawable.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[] {
				1, 0, 0, 0, -DRAW_DEEP,
				0, 1, 0, 0, -DRAW_DEEP,
				0, 0, 1, 0, -DRAW_DEEP,
				0, 0, 0, 1, 0 })));
		return gradientDrawable;
	}
	
	/**
	 * 将srcColor的颜色加深
	 * @param srcColor 需要加深的颜色
	 * @return
	 */
	public static int deepColor(int srcColor) {
		return deepColor(srcColor, COLOR_DEEP);
	}
	
	/**
	 * 将srcColor的颜色加深
	 * @param srcColor 需要加深的颜色
	 * @param COLOR_DEEP 需要加深的程度
	 * @return
	 */
	private static int deepColor(int srcColor, final int color_deep) {
		int dstColor = 0;
		int srcAlpha = srcColor & 0xff000000;
		int srcRed = srcColor & 0x00ff0000;
		int srcGreen = srcColor & 0x0000ff00;
		int srcBlue = srcColor & 0x000000ff;
		int dstAlpha = srcAlpha;
		int dstRed = srcRed > COLOR_DEEP * 0x00010000 ? (srcRed - color_deep * 0x00010000) : 0;
		int dstGreen = srcGreen > COLOR_DEEP * 0x00000100 ? (srcGreen - color_deep * 0x00000100) : 0;
		int dstBlue = srcBlue > COLOR_DEEP * 0x00000001 ? (srcBlue - color_deep * 0x00000001) : 0;
		dstColor = dstAlpha+dstRed+dstGreen+dstBlue;
		return dstColor;
	}

}
