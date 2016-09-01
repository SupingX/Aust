package com.mycj.aust;

import java.util.ArrayList;
import java.util.List;


import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.laputa.blue.broadcast.LaputaBroadcast;
import com.laputa.blue.core.SimpleLaputaBlue;
import com.laputa.blue.util.BondedDeviceUtil;
import com.mycj.aust.base.BaseActivity;
import com.mycj.aust.view.AlphaImageView;

public class DeviceActivity extends BaseActivity {
	
	private static Handler  mHandler = new Handler(){};
	private DeviceAdapter adapter ;
	private List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
	private ListView lvDevice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		initViews();
		checkBlue();
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (getBlueService()!=null) {
					getBlueService().startScan();
				}
				
			}
		}, 1111);
		registerReceiver(receiverNew, LaputaBroadcast.getIntentFilter());
	}
	private void checkBlue() {
			if (!isBleEnable()) {
				showAlertDialog("蓝牙不可用!");
			}
	}
	@Override
	protected void onStart() {
		super.onStart();
	
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopRotation();
		unregisterReceiver(receiverNew);
	}
	
	/*private void setDrawable(TextView tv , int resourceid){
		 Resources res = getResources();
		 Drawable img = res.getDrawable(resourceid);
		 // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		 img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
		 tv.setCompoundDrawables(img, null, null, null); //
	}*/
	
	
	private void initViews() {
		ivScan = (AlphaImageView) findViewById(R.id.iv_scan);
		ivBakc = (AlphaImageView) findViewById(R.id.iv_back);
		ivScan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.e("", "sousuo");
				if(getBlueService()!=null){
					getBlueService().startScan();	
					startRotation(v);
					
//					startScan();
				}
			}
		});
		ivBakc.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		
				Log.e("", "tuichu");
				finish();
			}
		});
		
		
		  lvDevice = (ListView) findViewById(R.id.lv);
		  adapter = new DeviceAdapter(devices, this);
		  lvDevice.setAdapter(adapter);
		  lvDevice.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				showLoading();
			
				
				//防止被header干扰
				Adapter adapter2 = parent.getAdapter();
				BluetoothDevice device = (BluetoothDevice) adapter2.getItem(position);
//				showCustomToast("position : "+position);
//				BluetoothDevice device = devices.get(position-1);
				if (device != null) {
					BondedDeviceUtil.save(1, device.getAddress(), getApplicationContext());
//					showCustomToast("连接蓝牙"+device.getAddress());
					if (getBlueService()!=null) {
						getBlueService().stopAutoConnect();
						getBlueService().stopScan();
						getBlueService().connect(device);
					
					}
				}
				
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						if (!getBlueService().ifAllConnected()) {
							getBlueService().startAutoConnect();
							closeLoading();
							showCustomToast("连接超时...");
						}
					}
				}, 20*1000);
			}
		});
	}
	
	private void showLoading(){
		if (dialog == null) {
			dialog = new ProgressDialog(this);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setMessage("正在连接...");
		}
	
		dialog.show();
	}
	
	private void closeLoading(){
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
	
	
//	public void doScan(View v){
//		finish();
//	}
//	
//	public void doBack(View v){
//		getBlueService().startScan();	
//		startRotation(v);
//	}
	
	private void startRotation(View v){
		if (null==a ) {
			a = ObjectAnimator.ofFloat(v, "rotation", 0,360);
			a.setDuration(1000);
			a.setInterpolator(new LinearInterpolator());
			a.setRepeatCount(-1);
		}
		a.start();
	}
	
	private void stopRotation(){
		if (a!= null) {
			a.cancel();
		}
	}

	private BroadcastReceiver receiverNew = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, final Intent intent) {
			String action = intent.getAction();
			if (action.equals(LaputaBroadcast.ACTION_LAPUTA_DEVICE_FOUND)) {
				Log.e("", "__________- --___------__________-");
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						BluetoothDevice device = intent
								.getParcelableExtra(LaputaBroadcast.EXTRA_LAPUTA_DEVICE);
						if (device != null && !devices.contains(device)) {
							devices.add(device);
						}
						adapter.notifyDataSetChanged();
					}
				});
			} else if (action.equals(LaputaBroadcast.ACTION_LAPUTA_STATE)) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						int state = intent.getExtras().getInt(LaputaBroadcast.EXTRA_LAPUTA_STATE);
						if (state == SimpleLaputaBlue.STATE_SERVICE_DISCOVERED) {
							closeLoading();
//							startActivity(new Intent(DeviceActivity.this,MainActivity.class));
//							overridePendingTransition(R.anim.activity_from_left_to_right_enter, R.anim.activity_from_left_to_right_exit);
							finish();
							showCustomToast("连接成功!");
						}else{
						}
					}
				});
			}else if (action.equals(LaputaBroadcast.ACTION_LAPUTA_IS_SCANING)) {
				final boolean isScanning = intent.getExtras().getBoolean(LaputaBroadcast.EXTRA_LAPUTA_SCANING);
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						if (isScanning) {
							startRotation(ivScan);
						}else{
							stopRotation();
						}
					}
				});
			}
				
		}

	};
	private ProgressDialog dialog;
	private ObjectAnimator a;
	private AlphaImageView ivBakc;
	private AlphaImageView ivScan;
	
}
