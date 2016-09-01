package com.mycj.aust.util;


import com.mycj.aust.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {
	
	private static Toast toast;
	private static Toast customToast;
	private static TextView tv;
	
	public static void showToast(Context context, String text, int duration) {
		if (toast == null) {
			toast = Toast.makeText(context, text, duration);
		} else {
			toast.setText(text);
			toast.setDuration(duration);
		}
		toast.show();
	}
	
	@SuppressLint("InflateParams")
	public static void showCustomToast(Context context,String text) {
	       
			if (toast == null) {
				 View toastRoot = LayoutInflater.from(context).inflate(
			                R.layout.view_common_toast, null);
			        tv = ((TextView) toastRoot.findViewById(R.id.toast_text));
			        tv.setText(text);
			        customToast = new Toast(context);
			        customToast.setGravity(Gravity.CENTER, 0, 0);
			        customToast.setDuration(Toast.LENGTH_SHORT);
			        customToast.setView(toastRoot);
			}else{
				tv.setText(text);
			}
			customToast.show();
	    }
	
	
	public static void showToast(Context context, String text) {
		if (toast == null) {
			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		} else {
			toast.setText(text);
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		toast.show();
	}
	
	public static void showToast(Context context, int text, int duration) {
		if (toast == null) {
			toast = Toast.makeText(context, text, duration);
		} else {
			toast.setText(text);
			toast.setDuration(duration);
		}
		toast.show();
	}
	
	public static void showToast(Context context, int text) {
		if (toast == null) {
			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		} else {
			toast.setText(text);
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		toast.show();
	}
}
