package com.mycj.aust.view;


import com.mycj.aust.R;
import com.mycj.aust.view.FreshCircleView.OnAnimatorCancelListener;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class XLoadingAlertDialog {
	private Context context;
	private Dialog dialog;
	private Display display;
	private FreshCircleView fcv;
	private TextView tvInfo;
	private boolean isShowingLoading = false;

	public XLoadingAlertDialog(Context context) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();

	}

	Handler mHandler = new Handler() {
	};
	private ImageView ivLoading;
	private ObjectAnimator oa;

	/*
	 * public XplAlertDialog builder(String msg) { // 获取Dialog布局 View view =
	 * LayoutInflater.from(context).inflate(R.layout.view_xpl_alert_dialog,
	 * null); fcv = (FreshCircleView) view.findViewById(R.id.fcv); tvInfo =
	 * (TextView) view.findViewById(R.id.tv_info);
	 * tvInfo.setVisibility(View.VISIBLE); // fcv.setCurrentLineSize(12); //
	 * 设置Dialog最小宽度为屏幕宽度 // view.setMinimumWidth(display.getWidth()); //
	 * 获取View控件
	 * 
	 * // 定义Dialog布局和参数 dialog = new Dialog(context, R.style.XplAlertDialog);
	 * dialog.setCancelable(false); dialog.setCanceledOnTouchOutside(false);
	 * dialog.setContentView(view); Window dialogWindow = dialog.getWindow();
	 * dialogWindow.setGravity(Gravity.CENTER); WindowManager.LayoutParams lp =
	 * dialogWindow.getAttributes(); lp.x = 0; lp.y = 0;
	 * dialogWindow.setAttributes(lp); return this; }
	 */

	public XLoadingAlertDialog setTextAtAnimationEnd(String msg) {
		if (tvInfo != null) {
			tvInfo.setText(msg);
		}
		return this;
	}

	public XLoadingAlertDialog setLoadingShowing(boolean isShowLoading) {
		this.isShowingLoading = isShowLoading;
		if (fcv != null) {
			if (isShowingLoading) {
				fcv.setVisibility(View.VISIBLE);
			} else {
				fcv.setVisibility(View.INVISIBLE);
			}
		}
		return this;
	}

	public XLoadingAlertDialog builder() {
		// 获取Dialog布局
		View view = LayoutInflater.from(context).inflate(R.layout.view_loading_dialog, null);
		fcv = (FreshCircleView) view.findViewById(R.id.fcv);
		ivLoading = (ImageView) view.findViewById(R.id.iv_loading);
		startAnimation();
		tvInfo = (TextView) view.findViewById(R.id.tv_info);
		fcv.setVisibility(View.VISIBLE);
		tvInfo.setVisibility(View.INVISIBLE);
		// fcv.setCurrentLineSize(12);
		// 设置Dialog最小宽度为屏幕宽度
		// view.setMinimumWidth(display.getWidth());
		// 获取View控件
		// 定义Dialog布局和参数
		dialog = new Dialog(context, R.style.XplAlertDialog);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);

		return this;
	}

	private void startAnimation() {
		oa = ObjectAnimator.ofFloat(ivLoading, "rotation", 0,360f);
		oa.setDuration(1500);
		oa.setInterpolator(new LinearInterpolator());
		oa.setRepeatCount(-1);
		oa.start();
	}

	public void cancel() {
		if (oa!=null) {
			oa.cancel();
		}
		fcv.stopLoading();
	}
	
//	interface OnCancelResultListener {
//		void onCancelFail(String msg);
//		void onCancelSuccess(String msg);
//	}
//	private OnCancelResultListener mOnCancelResultListener;
//	public void setOnCancelResultListener(OnCancelResultListener l){
//		this.mOnCancelResultListener = l;
//	}

	public void initListener() {
		if (isShowingLoading) {

			fcv.setOnAnimatorCancelListener(new OnAnimatorCancelListener() {

				@Override
				public void onCancel() {
					fcv.setVisibility(View.INVISIBLE);
					tvInfo.setVisibility(View.VISIBLE);
					fcv.setVisibility(View.GONE);
					tvInfo.setVisibility(View.GONE);
					final ObjectAnimator a = ObjectAnimator.ofFloat(tvInfo, "alpha", 0.1f, 1f);
					a.setDuration(200);
					a.addListener(new AnimatorListener() {

						@Override
						public void onAnimationStart(Animator animation) {

						}

						@Override
						public void onAnimationRepeat(Animator animation) {

						}

						@Override
						public void onAnimationEnd(Animator animation) {
							mHandler.postDelayed(new Runnable() {

								@Override
								public void run() {
									dismiss();
								}
							}, 1500);
						}

						@Override
						public void onAnimationCancel(Animator animation) {

						}
					});
					a.start();

				}
			});
		}
		fcv.startLoading();
	}

	public XLoadingAlertDialog setTitle(String title) {
		return this;
	}

	public XLoadingAlertDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public XLoadingAlertDialog setCanceledOnTouchOutside(boolean cancel) {
		dialog.setCanceledOnTouchOutside(cancel);
		return this;
	}

	public void show() {
		initListener();
		dialog.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}

	public interface OnButtonClickListener {

	}

	public enum SheetItemColor {
		Blue("#037BFF"), Red("#FD4A2E");

		private String name;

		private SheetItemColor(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
