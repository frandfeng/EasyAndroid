package com.frand.easyandroid.views;

import com.frand.easyandroid.util.FFFontUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {
	
	private static int layout_id = 0;
	private static int text_id = 0;
	
	public static void inits(int layout_id, int text_id) {
		CustomToast.layout_id = layout_id;
		CustomToast.text_id = text_id;
	}

	public static void toast(Context context, String text) {
		if(context==null) return;
		View view = LayoutInflater.from(context).inflate(layout_id, null);
		if(view==null) return;
		TextView textView = (TextView) view.findViewById(text_id);
		textView.setTypeface(FFFontUtil.getTencentTypeface());
		textView.setText(text);
		Toast toast = new Toast(context); // 创建一个toast
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(view); // 为toast设置一个view
		toast.show();
	}
}
