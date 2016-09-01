package com.mycj.aust;

import java.util.ArrayList;
import java.util.List;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class DeviceAdapter extends BaseAdapter{
	private List<BluetoothDevice> list = new ArrayList<BluetoothDevice>();;
	private Context context;
	private String weizhi="未知";

	public DeviceAdapter(List<BluetoothDevice> devices,Context context) {
		super();
		this.list = devices;
		this.context = context;
		
	}
	
	public Context getContext(){
		return this.context;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
			vh = new ViewHolder();
			vh.tvName = (TextView) convertView.findViewById(R.id.tv_name);
			vh.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		
		BluetoothDevice device = list.get(position);
		if (device != null) {
			String name = device !=null ? device.getName() : "";
			if (name == null || name.equals("")) {
				name = weizhi;
			}
			
//			if (name.equals(AbstractXplBluetoothService.BLE_NAME) ||
//					name.equals(AbstractXplBluetoothService.BLE_NAME_NEW) 	
//					) {
//				name = "Bratin";
//			}
			
			vh.tvName.setText(name);
			vh.tvAddress.setText(device.getAddress());
		}
		return convertView;
	}

	class ViewHolder {
		TextView tvName;
		TextView tvAddress;
	}

}
