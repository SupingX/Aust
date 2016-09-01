package com.mycj.aust;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laputa.blue.util.BondedDeviceUtil;
import com.mycj.aust.base.BaseFragment;
import com.mycj.protocol.core.MassagerProtocolWriteManager;

public class SettingFragment extends BaseFragment{
	private TextView tvBleAddress;
	private TextView tvBleInfo;
	private ImageView ivDelete;
//	private BlueService blueService;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_setting, container,false);
		
		RelativeLayout rlAddDevice = (RelativeLayout) view.findViewById(R.id.rl_add_device);
		rlAddDevice.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(),DeviceActivity.class));
				getActivity().overridePendingTransition(R.anim.activity_from_right_to_left_enter, R.anim.activity_from_right_to_left_exit);
			}
		});
		tvBleAddress = (TextView) view.findViewById(R.id.tv_ble_address);
		tvBleInfo = (TextView) view.findViewById(R.id.tv_ble_info);
		ivDelete = (ImageView) view.findViewById(R.id.iv_delete);

		ivDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialogForRemove();
			}
		});
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateBleInfo(getBlueService()!=null && getBlueService().ifAllConnected());
		updateBleAddress(BondedDeviceUtil.get(1, getActivity()));
	}
	
	public void updateBleAddress(String address) {
		if (TextUtils.isEmpty(address)) {
			address = "选择设备";
		}
		if (tvBleAddress!= null) {
			tvBleAddress.setText(address);
		}
	}

	public void updateBleInfo(boolean connect) {
		if (tvBleInfo!=null) {
			if (connect) {
				tvBleInfo.setText("已连接");
				tvBleInfo.setTextColor(getResources().getColor(R.color.color_text_temp));
			}else{
				tvBleInfo.setText("未连接");
				tvBleInfo.setTextColor(Color.WHITE);
			}
		}
	
	}
	
	public interface OnSettingFragmentListener {
		public void onRemoveDevice();
	}
	private OnSettingFragmentListener mOnSettingFragmentListener;
	public void setOnSettingFragmentListener(OnSettingFragmentListener l){
		this.mOnSettingFragmentListener = l;
	}
	
	private static Handler mHandler = new Handler(){};
	private void removeDevice() {
		if (null != getBlueService()) {
			getBlueService().write(MassagerProtocolWriteManager.newInstance().writeForStopMassager());
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mOnSettingFragmentListener != null) {
						mOnSettingFragmentListener.onRemoveDevice();
					}
//					BondedDeviceUtil.save(1, "", getActivity());
//					blueService.closeAll();
				}
			}, 2000);
		}
		updateBleAddress("");
		updateBleInfo(false);
	}
	private void showDialogForRemove(){
		new AlertDialog.Builder(getActivity())
		.setTitle("确定解除绑定设备？")
		.setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				removeDevice();
			}

			
		})
		.setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.show();
	}
}
