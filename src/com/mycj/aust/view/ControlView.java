package com.mycj.aust.view;

import com.laputa.blue.util.XLog;
import com.mycj.aust.R;
import com.mycj.aust.util.Constant;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ControlView extends View {

	private Bitmap bg;
	private int bgHeight;
	private int bgWidth;
	private Paint p;
	private Paint pTemp;
	private RadialGradient mRadialGradient;
	private RadialGradient mRadialGradientTemp;
	
	private int progressTime = 10;
	private int progressTemp = 10;
	private int maxTime = Constant.MAX_TIME;
	private int maxTemp = Constant.MAX_TEMPEATURE;

	private final int DEFF = 10;
	private final int RADIUS = 20;
	private final double _PI = Math.PI;
//	private final double _2PI = Math.PI * 2;

	private int width;
	private int height;
	private Paint pTime;
	private Paint pTimeText;
	private Paint pTempText;
	private Rect rectTimeText;
	private Rect rectTempText;
	private Rect rectTimeTextUnit;
	private Rect rectTempTextUnit;
	private double radios;
	private Paint pTimeTextUnit;
	private Paint pTempTextUnit;
	
	private boolean isMovimgTime = false;
	private boolean isMovingTemp = false;

	public ControlView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ControlView(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init();
	}

	private void init() {
		setClickable(true);
		p = new Paint();
		p.setAntiAlias(true);
		p.setDither(true);
		p.setStrokeJoin(Join.ROUND);
		p.setStrokeCap(Cap.ROUND);
		p.setStrokeWidth(1);
		p.setColor(Color.WHITE);

		pTemp = new Paint();
		pTemp.setAntiAlias(true);
		pTemp.setDither(true);
		pTemp.setStrokeWidth(1);

		pTime = new Paint();
		pTime.setAntiAlias(true);
		pTime.setDither(true);
		pTime.setStrokeWidth(1);

		pTimeText = new Paint();
		pTimeText.setAntiAlias(true);
		pTimeText.setDither(true);
		pTimeText.setStrokeWidth(1);
		pTimeText.setTextSize(100);
		pTimeText.setColor(getResources().getColor(R.color.color_text_time));
		rectTimeText = new Rect();

		pTimeTextUnit = new Paint();
		pTimeTextUnit.setAntiAlias(true);
		pTimeTextUnit.setDither(true);
		pTimeTextUnit.setStrokeWidth(1);
		pTimeTextUnit.setTextSize(50);
		pTimeTextUnit
				.setColor(getResources().getColor(R.color.color_text_time));
		rectTimeTextUnit = new Rect();

		pTempText = new Paint();
		pTempText.setAntiAlias(true);
		pTempText.setDither(true);
		pTempText.setStrokeWidth(1);
		pTempText.setTextSize(100);
		pTempText.setColor(getResources().getColor(R.color.color_text_temp));
		rectTempText = new Rect();
		pTempTextUnit = new Paint();
		pTempTextUnit.setAntiAlias(true);
		pTempTextUnit.setDither(true);
		pTempTextUnit.setStrokeWidth(1);
		pTempTextUnit.setTextSize(40);
		pTempTextUnit
				.setColor(getResources().getColor(R.color.color_text_temp));
		rectTempTextUnit = new Rect();

		Resources resources = getResources();
		bg = BitmapFactory.decodeResource(resources, R.drawable.ic_bg_circle);
		bgHeight = bg.getHeight();
		bgWidth = bg.getWidth();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		width = getWidth();
		height = getHeight();
		radios = bgWidth * 1.0F / 2 - DEFF;
//		canvas.drawLine(0, height / 2, width, height / 2, p);
		canvas.drawLine(width / 2, (float)(height/2-radios), width / 2, (float)(height/2+radios ), p);

//		canvas.drawLine(0, height / 2 - bgHeight / 2, width, height / 2
//				- bgHeight / 2, p);
//		canvas.drawLine(width / 2 - bgWidth / 2, 0, width / 2 - bgWidth / 2,
//				height, p);
//		canvas.drawLine(0, height / 2 - bgHeight / 2 + bgHeight, width, height
//				/ 2 - bgHeight / 2 + bgHeight, p);
//		canvas.drawLine(width / 2 - bgWidth / 2 + bgWidth, 0, width / 2
//				- bgWidth / 2 + bgWidth, height, p);

		// 画BG
		canvas.drawBitmap(bg, (width - bgWidth) / 2, (height - bgHeight) / 2, p);

		// 画时间按钮

		PointF pfTime = getTimePointByProgress();
		float xTime = pfTime.x;
		float yTime = pfTime.y;
		mRadialGradient = new RadialGradient(xTime, yTime, RADIUS, new int[] {
				Color.parseColor("#FEFFFF"), Color.parseColor("#34CBF4"),
				Color.parseColor("#003787"), Color.parseColor("#002356") },
				null, TileMode.CLAMP);
		pTime.setShader(mRadialGradient);
		canvas.drawCircle(xTime, yTime, RADIUS, pTime);

		// 画温度按钮
		PointF pfTemp = getTempPointByProgress();
		float xTemp = pfTemp.x;
		float yTemp = pfTemp.y;
		mRadialGradientTemp = new RadialGradient(xTemp, yTemp, RADIUS,
				new int[] { Color.parseColor("#FEFFFF"),
						Color.parseColor("#FCBD5D"),
						Color.parseColor("#D14C00"),
						Color.parseColor("#7F1F00") }, null, TileMode.CLAMP);
		pTemp.setShader(mRadialGradientTemp);
		canvas.drawCircle(xTemp, yTemp, RADIUS, pTemp);

		// 画文字
		String time = String.valueOf(progressTime);
		pTimeText.getTextBounds(time, 0, time.length(), rectTimeText);
		String timeUnit = "  m";
		pTimeTextUnit.getTextBounds(timeUnit, 0, timeUnit.length(),
				rectTimeTextUnit);
		int x = (int) (width / 2 - radios / 2 - (rectTimeText.width() + rectTimeTextUnit
				.width()) / 2);
		int y = height / 2;
		canvas.drawText(time, x, y, pTimeText);
		int xUnit = x + rectTimeText.width();
		int yUnit = height / 2;
		canvas.drawText(timeUnit, xUnit, yUnit, pTimeTextUnit);

		String temp = String.valueOf(progressTemp);
		pTempText.getTextBounds(temp, 0, temp.length(), rectTempText);
		String tempUnit = " o";
		pTempTextUnit.getTextBounds(tempUnit, 0, tempUnit.length(),
				rectTempTextUnit);
		int _x = (int) (width / 2 + radios / 2 - (rectTempText.width() + rectTempTextUnit
				.width()) / 2);
		int _y = height / 2;
		canvas.drawText(temp, _x, _y, pTempText);
		int _xUnit = _x + rectTempText.width();
		int _yUnit = height / 2 - rectTempText.height() * 2/ 3;
		canvas.drawText(tempUnit, _xUnit, _yUnit, pTempTextUnit);
	}

	/**
	 * 根据当前时间确定按钮坐标
	 * 
	 * @param progress
	 * @return pfTime
	 */
	private PointF getTimePointByProgress() {
		double currentPI = (_PI * progressTime * 1.0f) / maxTime; // 角度
		float _x = 0;
		float _y = 0;
		if (currentPI > _PI / 2) {
			_x = (float) (width / 2 - radios * Math.cos(currentPI - _PI / 2));
			_y = (float) (height / 2 - radios * Math.sin(currentPI - _PI / 2));
		} else if (currentPI == _PI / 2) {
			_x = (float) (width / 2 - radios);
			_y = (float) (height / 2);
		} else {
			_x = (float) (width / 2 - radios * Math.sin(currentPI));
			_y = (float) (height / 2 + radios * Math.cos(currentPI));
		}
		PointF pf = new PointF(_x, _y);
		return pf;
	}
	
	

	/**
	 * 根据当前温度确定按钮坐标
	 * 
	 * @param progress
	 * @return pfTemp
	 */
	private PointF getTempPointByProgress() {

		double currentPI = (_PI * progressTemp * 1.0f) / maxTemp; // 角度
		float _x = 0;
		float _y = 0;
		if (currentPI > _PI / 2) {
			_x = (float) (width / 2 + radios * Math.cos(currentPI - _PI / 2));
			_y = (float) (height / 2 - radios * Math.sin(currentPI - _PI / 2));
		} else if (currentPI == _PI / 2) {
			_x = (float) (width / 2 + radios);
			_y = (float) (height / 2);
		} else {
			_x = (float) (width / 2 + radios * Math.sin(currentPI));
			_y = (float) (height / 2 + radios * Math.cos(currentPI));
		}
		PointF pf = new PointF(_x, _y);
		return pf;
	}
	
	
	private int getProgressTimeByPi(double pi){
		if (pi>_PI) {
			pi=_PI;
		}
		if (pi<0) {
			pi=0;
		}
		int index = (int) (maxTime * pi / _PI);
		
		return index;
		
	}
	private int getProgressTempByPi(double pi){
		int index = (int) (maxTemp * pi / _PI);
		
		return index;
		
	}



	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			XLog.e("ControlView", "ACTION_DOWN");
			if (x < width / 2 - DEFF) {
				isMovimgTime = true;
			} else if (x > width / 2 + DEFF) {
				isMovingTemp = true;
			} else {
				isMovimgTime = false;
				isMovingTemp = false;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (isMovimgTime) {
				if (x<width/2+DEFF 
//						&& progressTime <maxTime
						) {
				double pi = getPiForTime( new PointF(width/2, height/2),new PointF(x, y));
				progressTime = getProgressTimeByPi(pi);
				invalidate();
					
				}
			}
			if (isMovingTemp) {
				if (x>width/2-DEFF 
//						&& progressTemp<maxTemp
						) {
					double pi = getPiForTemp( new PointF(width/2, height/2),new PointF(x, y));
					progressTemp = getProgressTempByPi(pi);
					invalidate();
				}
				
			}
			break;
		case MotionEvent.ACTION_UP:
			XLog.e("ControlView", "ACTION_UP");
			if (mOnProgressChangedListener != null) {
				if (isMovimgTime) {
					mOnProgressChangedListener.onTimeChanged(progressTime);
					isMovimgTime = false;
				}
				if (isMovingTemp) {
					mOnProgressChangedListener.onTempChanged(progressTemp);
					isMovingTemp = false;
				}
			}
			
			break;

		default:
			break;
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 根据两点算出角度
	 * 
	 * @param center
	 * @param p
	 * @return
	 */
	private double getPiForTime(PointF center, PointF p) {
		double _x = Math.abs(center.x - p.x);
		double _y = Math.abs(center.y - p.y);
		if (_x == 0) {
			if (center.y - p.y > 0) {
				return 0;
			} else {
				return Math.PI;
			}
		}
		if (_y == 0) {
			return  Math.PI / 2;
		}
		if (center.x>p.x && center.y>p.y) {
			return Math.PI-Math.atan2(_x, _y);
		}
		if (center.x>p.x && center.y<p.y) {
			return Math.atan2(_x, _y);
		}
		
		if (center.x <p.x && center.y > p.y) {
			return Math.PI;
		}
		
		if (center.x <p.x && center.y <p.y) {
			return 0;
		}
		
		return Math.atan2(_x, _y);
	}
	
	private double getPiForTemp(PointF center, PointF p) {
		double _x = Math.abs(center.x - p.x);
		double _y = Math.abs(center.y - p.y);
		if (_x == 0) {
			if (center.y - p.y > 0) {
				return 0;
			} else {
				return Math.PI;
			}
		}
		if (_y == 0) {
			return  Math.PI / 2;
		}
		if (center.x<p.x && center.y>p.y) {
			return Math.PI-Math.atan2(_x, _y);
		}
		if (center.x>p.x && center.y<p.y) {
			return Math.atan2(_x, _y);
		}
		if (center.x>p.x && center.y>p.y) {
			return Math.PI;
		}
		if (center.x>p.x && center.y<p.y) {
			return 0;
		}
		
		return Math.atan2(_x, _y);
	}
	
	
	public int getTime(){
		return this.progressTime;
	}
	public int getTemp(){
		return this.progressTemp;
	}
	
	public void setTime(int time){
		if (time<0 ) {
			time= 0;
		}
		if (time > maxTime) {
			time = maxTime;
		}
		
		this.progressTime = time;
		invalidate();
	}
	
	public void setTemp(int temp){
		if (temp<0 ) {
			temp= 0;
		}
		if (temp > maxTemp) {
			temp = maxTemp;
		}
		
		this.progressTemp = temp;
		invalidate();
	}
	
	
	public interface OnProgressChangedListener{
		public void onTimeChanged(int time);
		public void onTempChanged(int temp);
	}
	private OnProgressChangedListener mOnProgressChangedListener;
	public void setOnProgressChanged(OnProgressChangedListener l){
		this.mOnProgressChangedListener = l;
	}
	
}
