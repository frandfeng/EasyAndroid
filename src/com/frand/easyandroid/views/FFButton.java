/*
 * Copyright (C) 2014-4-14 frandfeng
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
package com.frand.easyandroid.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.frand.easyandroid.util.FFBackUtil;

/**
 * @author frandfeng
 * @time 2014-4-14 下午1:59:44 class description
 *       此类为button的子类，用于为button统一设置字体，自动设置按下效果等功能 现在的button字体未添加 AutoChangeBack
 *       boolean属性用来判断是否自动变换按下的背景，如果自动，则不能覆盖OnTouchListener事件，否则不起作用
 *       现在支持的按下效果有BitmapDrawable/ColorDrawable/GradientDrawable
 */
public class FFButton extends Button {

	private boolean autoChangeBack = false;
	private Drawable mDrawable;

	public FFButton(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public FFButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDrawable = getBackground();
		autoChangeBack = attrs.getAttributeBooleanValue(null, "AutoChangeBack", false);
		if (autoChangeBack) {
			initViews();
		}
	}

	private void initViews() {
		setOnTouchListener(mOnTouchListener);
	}
	
	private final OnTouchListener mOnTouchListener = new OnTouchListener() {
		@SuppressWarnings("deprecation")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				Drawable selectedDrawable = FFBackUtil.getSelectedDrawable(mDrawable);
				v.setBackgroundDrawable(selectedDrawable);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setBackgroundDrawable(mDrawable);
			}
			return false;
		}
	};

}
