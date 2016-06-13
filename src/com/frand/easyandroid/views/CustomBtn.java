package com.frand.easyandroid.views;

import com.frand.easyandroid.util.FFBackUtil;
import com.frand.easyandroid.util.FFFontUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

@SuppressLint("ClickableViewAccessibility")
public class CustomBtn extends Button {
	
	public CustomBtn(Context context) {
		super(context);
		setTypeface(FFFontUtil.getTencentTypeface());
	}
	
	public CustomBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(FFFontUtil.getTencentTypeface());
	}
	
	public CustomBtn(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTypeface(FFFontUtil.getTencentTypeface());
	}
	
	@SuppressWarnings("deprecation")
	public void enablePressedEffect() {
		final Drawable backDrawable = getBackground();
		if(backDrawable instanceof BitmapDrawable) {
			setBackgroundDrawable(FFBackUtil.getStateListDrawable((BitmapDrawable) backDrawable));
		} else {
			setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View view, MotionEvent event) {
					if(event.getAction()==MotionEvent.ACTION_DOWN) {
						Drawable selecteDrawable = FFBackUtil.getStateListDrawable(view.getBackground());
						view.setBackgroundDrawable(selecteDrawable);
					} else if (event.getAction()==MotionEvent.ACTION_UP) {
						view.setBackgroundDrawable(backDrawable);
					}
					return false;
				}
			});
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Drawable[] drawables = getCompoundDrawables();
		if (drawables != null) {
			Drawable drawableLeft = drawables[0];
			if (drawableLeft != null) {
				float textWidth = getPaint().measureText(getText().toString());
				int drawablePadding = getCompoundDrawablePadding();
				int drawableWidth = 0;
				drawableWidth = drawableLeft.getIntrinsicWidth();
				float bodyWidth = textWidth + drawableWidth + drawablePadding;
				setPadding(0, 0, (int)(getWidth() - bodyWidth), 0);
				canvas.translate((getWidth() - bodyWidth) / 2, 0);
			}
			Drawable drawableTop = drawables[1];
			if (drawableTop != null) {
				float textHeight = getPaint().getTextSize()+5;
				int drawablePadding = getCompoundDrawablePadding();
				int drawableHeight = 0;
				drawableHeight = drawableTop.getIntrinsicHeight();
				float bodyHeight = textHeight + drawableHeight + drawablePadding;
				setPadding(0, 0, 0, (int)(getHeight() - bodyHeight));
				canvas.translate(0, (getHeight() - bodyHeight) / 2);
			}
			Drawable drawableRight = drawables[2];
			if (drawableRight != null) {
				float textWidth = getPaint().measureText(getText().toString());
				int drawablePadding = getCompoundDrawablePadding();
				int drawableWidth = 0;
				drawableWidth = drawableRight.getIntrinsicWidth();
				float bodyWidth = textWidth + drawableWidth + drawablePadding;
				setPadding(0, 0, (int)(getWidth() - bodyWidth), 0);
				canvas.translate((getWidth() - bodyWidth) / 2, 0);
			}
			Drawable drawableBottom = drawables[3];
			if (drawableBottom != null) {
				float textHeight = getPaint().getTextSize()+5;
				int drawablePadding = getCompoundDrawablePadding();
				int drawableHeight = 0;
				drawableHeight = drawableBottom.getIntrinsicHeight();
				float bodyHeight = textHeight + drawableHeight + drawablePadding;
				setPadding(0, 0, 0, (int)(getHeight() - bodyHeight));
				canvas.translate(0, (getHeight() - bodyHeight) / 2);
			}
		}
		super.onDraw(canvas);
	}
}
