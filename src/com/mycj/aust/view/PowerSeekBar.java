package com.mycj.aust.view;

import com.mycj.aust.R;
import com.mycj.aust.util.DisplayUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

public class PowerSeekBar extends SeekBar {

	private Bitmap bg;
	private Drawable thumb;
	private float scale;
	public PowerSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
//		init(getContext());
	}

	public PowerSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
//		init(getContext());
	}

	public PowerSeekBar(Context context) {
		super(context);
//		init(getContext());
	
	}
	
/*	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init(getContext());
	}*/
	
	private void init(Context context) {
		bg = BitmapFactory.decodeResource(getResources(), R.drawable.ic_btn_power_length);
		Drawable s  =getResources().getDrawable(R.drawable.ic_btn_power_length);
		int intrinsicWidth = s.getIntrinsicWidth();

		scale = context.getResources().getDisplayMetrics().density;   
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		init(getContext());
	}
/*	
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		thumb = getThumb();
		Log.e("", "bg.getWidth() :" + bg.getWidth());
		Log.e("", "bg.getHeight() :" + bg.getHeight());
		Log.e("", "scale :" + scale);
		int intrinsicWidth = thumb.getIntrinsicWidth();
		setMeasuredDimension(bg.getWidth(), bg.getHeight());
	}*/
}
