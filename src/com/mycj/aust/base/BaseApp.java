package com.mycj.aust.base;
import com.laputa.blue.util.XLog;
import com.mycj.aust.service.BlueService;
import com.mycj.protocol.util.LogUtil;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;




public class BaseApp extends Application{
	public static int temp = 10;
	public static int time = 10*60;
	
	private boolean isDebug = false;
	public static int STATUS_CLOSE = 0x00;
	public static int STATUS_OPEN = 0x01;
	public static int STATUS_OPENING = 0x11;
	public static int STATUS_CLOSEING = 0x111;
	public static int status = STATUS_CLOSE;
	private  BlueService xBlueService;
	public BlueService getXBlueService(){
		log("Baseapp xBlueService:" + xBlueService);
		return this.xBlueService;
	}
	private ServiceConnection xBlueConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			xBlueService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if (service instanceof BlueService.BlueBinder) {
				BlueService.BlueBinder binder = (BlueService.BlueBinder) service;
				xBlueService = binder.getXBlueService();
				log("Baseapp xBlueService:" + xBlueService);
			}
		}
	};
	
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		Intent xplIntent = new Intent(this,BlueService.class);
		bindService(xplIntent, xBlueConnection, Context.BIND_AUTO_CREATE);
		XLog.DEV_MODE = true;
		LogUtil.isDebug = true;
	}
	
	private void log(String msg){
		if (isDebug) {
			XLog.e("BaseApp",msg);
		}
	}
	
}
