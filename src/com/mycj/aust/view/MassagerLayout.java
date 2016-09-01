package com.mycj.aust.view;

import com.mycj.aust.util.DisplayUtil;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

public class MassagerLayout extends LinearLayout{

	public MassagerLayout(Context context) {
		super(context);
//		init(context);
	}

	public MassagerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
//		init(context);
	}

	public MassagerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
//		init(context);
	}
	
	private void init(Context context){
		setOrientation(HORIZONTAL);
		int childCount = getChildCount();
		this.setWeightSum(4);
		Point screenMetrics = DisplayUtil.getScreenMetrics(context);
		int width = (int) (screenMetrics.x *1.0 / childCount);
		Log.e("", "x : " + screenMetrics.x);
		Log.e("", "width : " + width);
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
			params.gravity = Gravity.CENTER;
			child.setLayoutParams(params );
//			child.setBackgroundColor(Color.rgb(72+i*5, 33-i*3, 122+2*i));
		}
		postInvalidate();
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init(getContext());
	}

	
	
}
