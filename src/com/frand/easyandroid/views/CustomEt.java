package com.frand.easyandroid.views;

import com.frand.easyandroid.util.FFFontUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class CustomEt extends EditText {

	public CustomEt(Context context) {
		super(context);
		setTypeface(FFFontUtil.getTencentTypeface());
	}

	public CustomEt(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(FFFontUtil.getTencentTypeface());
	}

	public CustomEt(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTypeface(FFFontUtil.getTencentTypeface());
	}
	
}
