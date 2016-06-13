package com.frand.easyandroid.views;

import com.frand.easyandroid.util.FFFontUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTv extends TextView {

	public CustomTv(Context context) {
		super(context);
		setTypeface(FFFontUtil.getTencentTypeface());
	}

	public CustomTv(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(FFFontUtil.getTencentTypeface());
	}

	public CustomTv(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTypeface(FFFontUtil.getTencentTypeface());
	}
	
}
