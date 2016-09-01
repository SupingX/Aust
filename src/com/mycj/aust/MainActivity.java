package com.mycj.aust;


import com.laputa.blue.broadcast.LaputaBroadcast;
import com.laputa.blue.core.SimpleLaputaBlue;
import com.laputa.blue.util.BondedDeviceUtil;
import com.laputa.blue.util.XLog;
import com.mycj.aust.MassagerFragment.OnMassagerFragmentListener;
import com.mycj.aust.SettingFragment.OnSettingFragmentListener;
import com.mycj.aust.base.BaseActivity;
import com.mycj.aust.view.XLoadingAlertDialog;
import com.mycj.protocol.bean.MycjMassagerInfo;
import com.mycj.protocol.core.MassagerProtocolWriteManager;
import com.mycj.protocol.core.ProtocolBroadcast;
import com.mycj.protocol.core.ProtocolBroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * 
 * 
 *
 */
public class MainActivity extends BaseActivity {

	private FragmentManager fragmentManager;
	private MassagerFragment massagerFragment;
	private SettingFragment settingFragment;
	private ImageView ivMassager;
	private ImageView ivSetting;
	private TextView tvMassager;
	private TextView tvSetting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fragmentManager = getSupportFragmentManager();
		registerReceiver();
		initViews();
		updateMenu(0);
		checkBlue();
		

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (massagerFragment!=null && massagerFragment.isAdded()) {
			
			massagerFragment.updateUiByMassagerInfo(getBlueService()!=null?getBlueService().getMassagerInfo():null);
		}
	}

	private void checkBlue() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!isBleEnable()) {
					showAlertDialog("蓝牙不可用!");
				}
				
				disconnectTask = new Runnable() {
					
					@Override
					public void run() {
						if (getBlueService()!=null) {
							getBlueService().setMassagerInfo(null);
						}
						ProtocolBroadcast.newInstance(MainActivity.this).sendBroadcastForCurrentMassagerInfo(null);
					}
				};
			}
			
		}, 1234);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver();
	}

	private void registerReceiver() {
		registerReceiver(receiverBle, LaputaBroadcast.getIntentFilter());
		registerReceiver(receiverData, ProtocolBroadcast.getIntentFilter());
	}

	private void unregisterReceiver() {
		unregisterReceiver(receiverBle);
		unregisterReceiver(receiverData);
	}

	private void initViews() {
		LinearLayout llMassager = (LinearLayout) findViewById(R.id.ll_menu_massager);
		LinearLayout llSetting = (LinearLayout) findViewById(R.id.ll_menu_setting);
		llMassager.setClickable(true);
		llSetting.setClickable(true);
		ivMassager = (ImageView) findViewById(R.id.iv_menu_massager);
		ivSetting = (ImageView) findViewById(R.id.iv_menu_setting);
		tvMassager = (TextView) findViewById(R.id.tv_menu_massager);
		tvSetting = (TextView) findViewById(R.id.tv_menu_setting);
		llMassager.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateMenu(0);
			}
		});
		llSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateMenu(1);
			}
		});
	}
	private XLoadingAlertDialog showLoadingDialog;
//	private AbstractLaputaDialog loadingDialog;
	private void updateMenu(int i) {
		if (massagerFragment == null) {
			massagerFragment = new MassagerFragment();
			massagerFragment.setOnMassagerFragmentListener(new OnMassagerFragmentListener() {
				@Override
				public void showLoading(String msg) {
//					loadingDialog = showAlertDialog(msg,false);
					
					showLoadingDialog = showLoadingDialog();
				}
				
				@Override
				public void closeLoading() {
					/*if (loadingDialog!= null) {
						loadingDialog.dismiss();
					
					}*/
					if (showLoadingDialog!= null) {
						showLoadingDialog.dismiss();
						
					}
				}
			});
		}
		if (settingFragment == null) {
			settingFragment = new SettingFragment();
			settingFragment.setOnSettingFragmentListener(new OnSettingFragmentListener() {
				
				@Override
				public void onRemoveDevice() {
					BondedDeviceUtil.save(1, "", MainActivity.this);
					if (getBlueService()!=null ) {
						getBlueService().closeAll();
					}
				}
			});
		}

		Resources resources = getResources();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if (i == 0) {

			if (massagerFragment.isAdded()) {
				transaction.show(massagerFragment);
			} else {
				transaction.replace(R.id.frame_fragment, massagerFragment,
						MassagerFragment.class.getSimpleName());
			}
			transaction.commit();

			ivMassager.setImageResource(R.drawable.ic_menu_massager_selected);
			tvMassager.setTextColor(resources
					.getColor(R.color.color_menu_selected));
			ivSetting.setImageResource(R.drawable.ic_menu_setting_normal);
			tvSetting.setTextColor(resources
					.getColor(R.color.color_menu_normal));
			
			/*if (blueService!=null) {
				if (massagerFragment.isAdded()) {
					massagerFragment.updateUiByMassagerInfo(blueService.getMassagerInfo());
				}
			}*/
			
			byte[] writeForCurrentMassager = MassagerProtocolWriteManager.newInstance().writeForCurrentMassager();
			if (getBlueService() != null && getBlueService().ifAllConnected()) {
				getBlueService().write(writeForCurrentMassager);
			}
			
		} else {

			if (settingFragment.isAdded()) {
				transaction.show(settingFragment);
			} else {
				transaction.replace(R.id.frame_fragment, settingFragment,
						SettingFragment.class.getSimpleName());
			}
			
		/*	if (blueService != null ) {
				settingFragment.updateBleInfo(blueService.ifAllConnected());
				settingFragment.updateBleAddress(BondedDeviceUtil.get(1, MainActivity.this));
			}*/

			
			transaction.commit();

			ivMassager.setImageResource(R.drawable.ic_menu_massager_normal);
			tvMassager.setTextColor(resources
					.getColor(R.color.color_menu_normal));
			ivSetting.setImageResource(R.drawable.ic_menu_setting_selected);
			tvSetting.setTextColor(resources
					.getColor(R.color.color_menu_selected));
		}
	}

	private static  Handler mHandler = new Handler() {
	};
	private ProtocolBroadcastReceiver receiverData = new ProtocolBroadcastReceiver() {

		@Override
		protected void onChangeTime(final int time, int timeSetting) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (massagerFragment.isAdded()) {
						massagerFragment.updateTime(time);
					}
				}
			});
		}

		@Override
		protected void onChangeTemperature(final int temp, int tempUnit) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (massagerFragment.isAdded()) {
						massagerFragment.updateTemperature(temp);
					}
				}
			});
		}

		@Override
		protected void onChangePower(final int power) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					
					if (massagerFragment.isAdded()) {
//						ToastUtil.showCustomToast(MainActivity.this, "power改变了:"+power);
						massagerFragment.updatePower(power);
						XLog.e("MainActivity", "ccccccccccccccccc_力度改变了" + power);
					}
				}
			});
		}

		@Override
		protected void onChangePattern(final int pattern) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					
					if (massagerFragment.isAdded()) {
						XLog.e("MainActivity", "ccccccccccccccccc_模式改变了" + pattern);
						massagerFragment.updatePatternFromMassager(pattern);
					}else{
						XLog.e("MainActivity", "ccccccccccccccccc_isAdded" + "没有add");
					}
				}
			});
		}

		@Override
		protected void onChangeMassagerInfo(final MycjMassagerInfo info) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (massagerFragment.isAdded()) {
						massagerFragment.updateUiByMassagerInfo(info);
						
						if (info!= null && info.getOpen()==1) {
							if (showLoadingDialog!= null) {
								showLoadingDialog.dismiss();
							}
						}
					}
				}
			});

		}


		@Override
		protected void onChangeTimeCallBack(final int stauts, final int time) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (massagerFragment.isAdded()) {
						massagerFragment.updateTime(time);
					}
					
					if (stauts == 1) {
//						showCustomToast("改变时间成功");
						if (showLoadingDialog!= null) {
							showLoadingDialog.dismiss();
						}


					} else {
						showCustomToast("改变时间失败");
					}

				}
			});

		}

		@Override
		protected void onChangeTempeatureCallBack(final int stauts,
				final int temp, int tempUnit) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (massagerFragment.isAdded()) {
						massagerFragment.updateTemperature(temp);
					}
					if (stauts == 1) {
//						showCustomToast("改变温度成功");
						if (showLoadingDialog!= null) {
							showLoadingDialog.dismiss();
						}


					} else {
						showCustomToast("改变温度失败");
					}

				}
			});
		}

		@Override
		protected void onChangePowerCallBack(final int stauts, final int power) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (massagerFragment.isAdded()) {
						massagerFragment.updatePower(power);
					}
					if (stauts == 1) {
//						showCustomToast("改变力度成功");
						XLog.e("MainActivity", "nnnnnnnnnnnnnnnnnnnnnnnn_力度改变成功" + power);
						if (showLoadingDialog!= null) {
							showLoadingDialog.dismiss();
						}


					} else {
						XLog.e("MainActivity", "nnnnnnnnnnnnnnnnnnnnnnnn_力度改变失败" + power);
//						showCustomToast("改变力度失败");
					}

				}
			});
		}
		
		
		
		@Override
		protected void onChangePatternCallBack(final int stauts,
				final int pattern) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (massagerFragment.isAdded()) {
						massagerFragment.updatePatternFromMassager(pattern);
					}
					if (stauts == 1) {
						XLog.e("MainActivity", "nnnnnnnnnnnnnnnnnnnnnnnn_模式改变成功" + pattern);
						
						
						// 改变模式后力度变为1
						/*if (getBlueService()!= null && getBlueService().ifAllConnected()) {
							getBlueService().write(MassagerProtocolWrite.newInstance().writeForPower());
						}*/
						
						
//						showCustomToast("改变模式成功");
						if (showLoadingDialog!= null) {
							showLoadingDialog.dismiss();
						}

					} else {
						showCustomToast("改变模式失败");
						XLog.e("MainActivity", "nnnnnnnnnnnnnnnnnnnnnnnn_模式改变失败" + pattern);
					}

				}
			});
		}

	};
	
	private BroadcastReceiver receiverBle = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			String action = intent.getAction();
			if (action.equals(LaputaBroadcast.ACTION_LAPUTA_STATE)) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						int state = intent.getExtras().getInt(
								LaputaBroadcast.EXTRA_LAPUTA_STATE);
						if (state == SimpleLaputaBlue.STATE_SERVICE_DISCOVERED) {
							
							mHandler.removeCallbacks(disconnectTask);
							
							if (settingFragment.isAdded()) {
								settingFragment.updateBleInfo(true);
							}
						} else {
							if (settingFragment.isAdded()) {
								settingFragment.updateBleInfo(false);
							}
							
							mHandler.postDelayed(disconnectTask, 30*000);
							
							
//							mHandler.postDelayed(new Runnable() {
//								
//								@Override
//								public void run() {
//									if (massagerFragment.isAdded()) {
//										ToastUtil.showCustomToast(MainActivity.this, "蓝牙掉线");
//										blueService.setMassagerInfo(null);
//										massagerFragment.updatePatternFromMassager(-1);
//									}
//								}
//							}, 30*1000);
						}
					}
				});
			}
		}
	};
	
	private Runnable disconnectTask;
	private long lastTime = 0;
	public void onBackPressed() {
		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis - lastTime <500) {
			if (getBlueService()!=null &&getBlueService().ifAllConnected()) {
				getBlueService().closeAll();
			}
			finish();
		}else{
			showCustomToast("快速点击返回退出app");
			lastTime = currentTimeMillis;
		}
		
		
	};
}
