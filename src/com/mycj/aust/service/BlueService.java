package com.mycj.aust.service;

import java.util.HashSet;
import java.util.Set;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.laputa.blue.core.AbstractSimpleLaputaBlue;
import com.laputa.blue.core.Configration;
import com.laputa.blue.core.OnBlueChangedListener;
import com.laputa.blue.core.SimpleLaputaBlue;
import com.laputa.blue.util.BondedDeviceUtil;
import com.laputa.blue.util.XLog;
import com.mycj.protocol.bean.MycjMassagerInfo;
import com.mycj.protocol.core.MassagerProtocolNotifyManager;
import com.mycj.protocol.core.ProtocolBroadcast;
import com.mycj.protocol.notify.OnProtocolNotifyListenerBasedapter;

public class BlueService extends Service {

	private AbstractSimpleLaputaBlue simpleLaputaBlue;
	public AbstractSimpleLaputaBlue getAbstractSimpleLaputaBlue(){
		return simpleLaputaBlue;
	}
	private MycjMassagerInfo currentInfo ;
	public MycjMassagerInfo getMassagerInfo(){
		return this.currentInfo;
	}
	public void setMassagerInfo(MycjMassagerInfo info){
		this.currentInfo = info;
	}
/*	private static Handler mHandler = new Handler() {
	};*/

	@Override
	public void onCreate() {
		super.onCreate();
		acquireWakeLock();
		simpleLaputaBlue = new SimpleLaputaBlue(this, new Configration(),
				new OnBlueChangedListener() {

					@Override
					public void reconnect(HashSet<String> devices) {
						final String addressA = BondedDeviceUtil.get(1,
								getApplicationContext());
						if (BluetoothAdapter.checkBluetoothAddress(addressA)) {
							try {
								// 当前app存贮的蓝牙
								BluetoothDevice remoteDevice = simpleLaputaBlue
										.getAdapter().getRemoteDevice(addressA);
								// 所有的绑定蓝牙列表
								Set<BluetoothDevice> bondedDevices = simpleLaputaBlue
										.getAdapter().getBondedDevices();
								//
								if (bondedDevices.contains(remoteDevice)) {
									XLog.e("_____已绑定 ：" + addressA);
									if (!ifAllConnected()) {
										connect(remoteDevice);
										return;
									}
								} else {
									XLog.e("_____未绑定 ：" + addressA);
									// 当搜索列表中包含保存的addressA,并且未连接，就连接。
									if (devices.contains(addressA)) {
										if (!ifAllConnected()) {
											connect(remoteDevice);
										}
									} else {
										XLog.i("搜索列表无：" + addressA);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								XLog.e("重新连接失败！");
							}

						} else {
							XLog.i("蓝牙地址不匹配，没有addessA" + addressA);
						}
					}

					@Override
					public void onStateChanged(String address, int state) {

					}

					@Override
					public void onServiceDiscovered(String address) {

					}

					@Override
					public void onCharacteristicChanged(String address,
							byte[] value) {
//						parseData(value);
						parseDataNew(value);

					}

					@Override
					public boolean isAllConnected() {
						return ifAllConnected();
					}
				});
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return new BlueBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	public class BlueBinder extends Binder {
		public BlueService getXBlueService() {
			return BlueService.this;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		releaseWakeLock();
		closeAll();

	}

	public void startScan() {
		simpleLaputaBlue.scanDevice(true);
	}

	public void write(byte[] data) {

		simpleLaputaBlue.write(BondedDeviceUtil.get(1, this), data);
	}

	public void write(byte[][] data) {
		new WriteAsyncTask().execute(data);
	}

	public void stopScan() {
		simpleLaputaBlue.scanDevice(false);
	}

	public void connect(BluetoothDevice device) {
		simpleLaputaBlue.connect(device.getAddress());
	}

	public boolean ifAllConnected() {
		return simpleLaputaBlue.isConnected(BondedDeviceUtil.get(1, this));
	}

	public void startAutoConnect(){
		simpleLaputaBlue.startAutoConnectTask();
	}
	
	public void stopAutoConnect(){
		simpleLaputaBlue.stopAutoConnectTask();
	}
	
	public void closeAll() {
		simpleLaputaBlue.closeAll();
	}

	private class WriteAsyncTask extends AsyncTask<byte[][], Void, Void> {

		@Override
		protected Void doInBackground(byte[][]... params) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			byte[][] bs = params[0];
			if (bs != null && bs.length > 0) {
				for (int j = 0; j < bs.length; j++) {
					write(bs[j]);
				}
			}
			return null;
		}
	}

	/**
	 * <p>
	 * 解析数据
	 * </p>
	 * 
	 * @param data
	 * @param gatt
	 */
//	private void parseData(byte[] data) {
//		ProtocolNotifyBroadcast manager = ProtocolNotifyBroadcast.newInstance(this);
//		final MassagerProtocolNotify notify = MassagerProtocolNotify.newInstance();
//		manager.setOnDataChangedListener(new OnDataChangedListener() {
//			
//			@Override
//			public void onChanged(byte[] data, Result result) {
//				if (result == Result.MassagerInfo) {
//					Log.e("", "--->---->---->");
//					currentInfo = notify.notifyCurrentMassagerInfo(data);
//					if (currentInfo.getOpen()==0) {
////						BaseApp.temp = info.getTemperature();
////						BaseApp.time = info.getLeftTime();
////						Log.e("", "---> temp : " +BaseApp.temp );
////						Log.e("", "---> time : " +BaseApp.time );
////						info.setPattern(-1);
//						currentInfo = null;
//					}
//				} else if (result == Result.ChangePatternCallBack) {
//					int[] notifyChangePattern = notify.notifyChangePattern(data);
//					if (notifyChangePattern[0]==1) {
//						if (currentInfo != null) {
//							currentInfo.setPattern(notifyChangePattern[1]);
//						}
//					}
//				}else if (result == Result.ChangePowerCallBack) {
//					int[] notifyChangePower = notify.notifyChangePower(data);
//					if (notifyChangePower[0]==1) {
//						if (currentInfo != null) {
//							XLog.e("力度  ----> "+notifyChangePower[1]);
//							currentInfo.setPower(notifyChangePower[1]);
//						}
//					}
//				}else if (result == Result.ChangeTemperatureCallBack) {
//					int[] notifyChangeTemp = notify.notifyChangeTemperature(data);
//					if (notifyChangeTemp[0]==1) {
//						if (currentInfo != null) {
//							currentInfo.setTemperature(notifyChangeTemp[1]);
//							currentInfo.setTempUnit(notifyChangeTemp[2]);
//						}
//					}
//				}else if (result == Result.ChangeTimeCallBack) {
//					int[] notifyTime = notify.notifyTime(data);
//					if (notifyTime[0]==1) {
//						if (currentInfo != null) {
//							currentInfo.setLeftTime(notifyTime[0]);
//							currentInfo.setSettingTime(notifyTime[1]);
//							
//						}
//					}
//				}else if (result == Result.ChangeTimeCallBack) {
//					int[] notifyTime = notify.notifyChangeTime(data);
//					if (notifyTime[0]==1) {
//						if (currentInfo != null) {
//							currentInfo.setLeftTime(notifyTime[1]);
//							currentInfo.setSettingTime(notifyTime[1]);
//							
//						}
//					}
//				}else if (result == Result.Pattern) {
//					int notifyPattern = notify.notifyPattern(data);
//					if (currentInfo != null) {
//						currentInfo.setPattern(notifyPattern);
//						
//					}
//				}else if (result == Result.Power) {
//					int power = notify.notifyPower(data);
//					if (currentInfo != null) {
//						currentInfo.setPower(power);
//					}
//				}else if (result == Result.Time) {
//					int[] notifyTime = notify.notifyTime(data);
//					if (currentInfo!=null) {
//						currentInfo.setSettingTime(notifyTime[1]);
//						currentInfo.setLeftTime(notifyTime[0]);
//					}
//				}else if (result == Result.Temperature) {
//					int[] notifyTemperature = notify.notifyTemperature(data);
//					if (currentInfo!=null) {
//						currentInfo.setTemperature(notifyTemperature[0]);
//					}
//				}
//			}
//		});
//		
//		manager.parseData(data);
		/*
		 * ProtoclNotify notify = ProtoclNotify.instance(); XBlueBroadcastUtils
		 * xBlueBroadcastUtils = XBlueBroadcastUtils.instance(); int dataType =
		 * notify.getDataTypeByData(data); if (dataType ==
		 * ProtoclNotify.TYPE_MASSAGE) { MassageInfo massageinfo =
		 * notify.notifyMassageInfo(data); Log.e("", ">>>massageinfo : " +
		 * massageinfo); if (massageinfo != null) {
		 * xBlueBroadcastUtils.sendBroadcastForMassageInfo(this,massageinfo); }
		 * } else if (dataType == ProtoclNotify.TYPE_MODE_CHANGED) { int mode =
		 * notify.notifyModelChanged(data); if (mode!=-1) {
		 * xBlueBroadcastUtils.sendBroadcastForMode(this,mode); } } else if
		 * (dataType == ProtoclNotify.TYPE_POWER_CHANGED) { int power =
		 * notify.notifyPowerChanged(data); if (power!= -1) {
		 * xBlueBroadcastUtils.sendBroadcastForPower(this,power); } }else if
		 * (dataType == ProtoclNotify.TYPE_TIME_CHANGED) { int[] times =
		 * notify.notifyTimeChanged(data); if (times != null) {
		 * xBlueBroadcastUtils.sendBroadcastForTime(this,times); } }else if
		 * (dataType == ProtoclNotify.TYPE_HEART_RATE_CHANGED) { int hr =
		 * notify.notifyHeartRateChanged(data); if (hr != -1) {
		 * xBlueBroadcastUtils.sendBroadcastForHeartRate(this,hr); } }else
		 * if(dataType == ProtoclNotify.TYPE_BATTERY_CHANGED){ int []
		 * batteryData = notify.notifyBatteryChanged(data); if
		 * (batteryData!=null) {
		 * xBlueBroadcastUtils.sendBroadcastForBattery(this,batteryData); } }
		 */

//	}
	
	
	private void parseDataNew(byte[] data){
		if (notify ==null) {
//			ProtocolBroadcast broadcast = ProtocolBroadcast.newInstance(getApplicationContext());
			ProtocolBroadcast broadcast = new ProtocolBroadcast(getApplicationContext()){
				@Override
				public void sendBroadcastForPower(int power) {
					super.sendBroadcastForPower(power);
				}

				@Override
				public void sendBroadcastForChangePowerCallBack(int status,
						int power) {
					super.sendBroadcastForChangePowerCallBack(status, power);
				}

				@Override
				public void sendBroadcastForCurrentMassagerInfo(
						MycjMassagerInfo info) {
					int power= info.getPower();
					info.setPower(power);
					super.sendBroadcastForCurrentMassagerInfo(info);
				}
				
				
			};
			notify = new MassagerProtocolNotifyManager( broadcast,new OnProtocolNotifyListenerBasedapter() {
	
				@Override
				public void onParseChangePatternCallBack(String desc, int success,
						int pattern) {
					super.onParseChangePatternCallBack(desc, success, pattern);
					
					if (success==1) {
						if (currentInfo != null) {
							currentInfo.setPattern(pattern);
						}
					}
					
				}
	
				@Override
				public void onParseMassagerInfo(String desc, MycjMassagerInfo info) {
					super.onParseMassagerInfo(desc, info);
					currentInfo = info;
					currentInfo.setPower(info.getPower());
					Log.e("", "--->---->---->");
					if (info.getOpen()==0) {
	//					BaseApp.temp = info.getTemperature();
	//					BaseApp.time = info.getLeftTime();
	//					Log.e("", "---> temp : " +BaseApp.temp );
	//					Log.e("", "---> time : " +BaseApp.time );
	//					info.setPattern(-1);
						
						Log.e("", "==结束按摩==");
						currentInfo = null;
					}
				}
	
				@Override
				public void onParsePattern(String desc, int pattern) {
					super.onParsePattern(desc, pattern);
					if (currentInfo != null) {
						currentInfo.setPattern(pattern);
					}
				}
	
				@Override
				public void onParsePower(String desc, int power) {
					super.onParsePower(desc, power);
					if (currentInfo != null) {
						currentInfo.setPower(power*2);
					}
				}
	
				@Override
				public void onParseChangePowerCallBack(String desc, int success,
						int power) {
					super.onParseChangePowerCallBack(desc, success, power);
					if (success==1) {
						if (currentInfo != null) {
							XLog.e("力度  ----> "+power);
							currentInfo.setPower(power*2);
						}
					}
				}
	
				@Override
				public void onParseTime(String desc, int leftTime, int settingTime) {
					super.onParseTime(desc, leftTime, settingTime);
					if (currentInfo!=null) {
						currentInfo.setSettingTime(settingTime);
						currentInfo.setLeftTime(leftTime);
					}
				}
	
				@Override
				public void onParseChangeTimeCallBack(String desc, int success,
						int settingTime) {
					super.onParseChangeTimeCallBack(desc, success, settingTime);
					if (settingTime==1) {
						if (currentInfo != null) {
							currentInfo.setLeftTime(settingTime);
							currentInfo.setSettingTime(settingTime);
							
						}
					}
				}
	
				@Override
				public void onParseTemperature(String desc, int temp, int tempUnit) {
					super.onParseTemperature(desc, temp, tempUnit);
					if (currentInfo!=null) {
						currentInfo.setTemperature(temp);
						currentInfo.setTempUnit(tempUnit);
					}
				
				}
	
				@Override
				public void onParseChangeTemperatureCallBack(String desc,int success, int temp,
						int tempUnit) {
					super.onParseChangeTemperatureCallBack(desc, success,temp, tempUnit);
					if (success==1) {
						if (currentInfo != null) {
							currentInfo.setTemperature(temp);
							currentInfo.setTempUnit(tempUnit);
						}
					}
					
				}
			});
		}
		
		notify.parse(data);;
	}

	WakeLock wakeLock = null;
	private MassagerProtocolNotifyManager notify;

	// 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
	private void acquireWakeLock() {
		if (null == wakeLock) {
			PowerManager pm = (PowerManager) this
					.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, "PostLocationService");
			if (null != wakeLock) {
				wakeLock.acquire();
			}
		}
	}

	// 释放设备电源锁
	private void releaseWakeLock() {
		if (null != wakeLock) {
			wakeLock.release();
			wakeLock = null;
		}
	}

}
