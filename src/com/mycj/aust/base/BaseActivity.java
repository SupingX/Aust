package com.mycj.aust.base;



import com.laputa.blue.core.AbstractSimpleLaputaBlue;
import com.laputa.blue.util.XLog;
import com.laputa.dialog.AbstractLaputaDialog;
import com.laputa.dialog.LaputaAlertDialog;
import com.mycj.aust.R;
import com.mycj.aust.service.BlueService;
import com.mycj.aust.util.ToastUtil;
import com.mycj.aust.view.XLoadingAlertDialog;

import android.support.v4.app.FragmentActivity;




/**
 * Created by Administrator on 2015/11/20.
 */
public class BaseActivity extends FragmentActivity {
	
	  /** 显示自定义Toast提示(来自String) **/
    protected void showCustomToast(String text) {
     /*   View toastRoot = LayoutInflater.from(BaseActivity.this).inflate(
                R.layout.common_toast, null);
        ((TextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
        Toast toast = new Toast(BaseActivity.this);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastRoot);
        toast.show();*/
//    	ToastUtil.showCustomToast(this	, text);
		ToastUtil.showToast(this, text);
    }
    
    public BlueService getBlueService(){
    	BaseApp application = (BaseApp) getApplication();
    	return application.getXBlueService();
    }
    
	public AbstractLaputaDialog showAlertDialog(String msg){
		AbstractLaputaDialog dialog = new LaputaAlertDialog(this, R.layout.view_laputa_alert_dialog)
    	.builder()
    	.setCancelable(true)
//    	.setCanceledOnTouchOutside(true)
    	.setMsg(msg)
    	;
		dialog.show();
		return dialog;
	}
	
/*	public AbstractLaputaDialog showAlertDialog(String msg,boolean cancelable){
		AbstractLaputaDialog dialog = new LaputaAlertDialog(this, R.layout.view_laputa_alert_dialog)
    	.builder()
    	.setCancelable(cancelable)
    	.setCanceledOnTouchOutside(false)
    	.setMsg(msg)
    	;
		dialog.show();
		return dialog;
	}*/
	
	
	public XLoadingAlertDialog showLoadingDialog(){
		XLoadingAlertDialog dialog = new XLoadingAlertDialog(this)
		.builder().setLoadingShowing
		 (false).setTextAtAnimationEnd("");
		dialog.show();
		return dialog;
	}
	
	
    public boolean isBleEnable(){
    	boolean checkFeature = AbstractSimpleLaputaBlue.checkFeature(this);
    	boolean enable = AbstractSimpleLaputaBlue.isEnable(this);
    	return checkFeature && enable;
    }
    
    protected boolean isDebug = false;
    public void log(String msg){
		if (isDebug) {
			XLog.e("BaseApp",msg);
		}
	}
}
