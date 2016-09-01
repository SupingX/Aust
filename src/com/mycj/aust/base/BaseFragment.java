package com.mycj.aust.base;

import com.mycj.aust.service.BlueService;
import com.mycj.aust.util.ToastUtil;

import android.support.v4.app.Fragment;


public class BaseFragment extends Fragment {
	public void show(String msg){
	/*     View toastRoot = LayoutInflater.from(getActivity()).inflate(
	                R.layout.common_toast, null);
	        ((TextView) toastRoot.findViewById(R.id.toast_text)).setText(msg);
	        Toast toast = new Toast(getActivity());
	        toast.setGravity(Gravity.CENTER, 0, 0);
	        toast.setDuration(Toast.LENGTH_SHORT);
	        toast.setView(toastRoot);
	        toast.show();*/
		
//		ToastUtil.showCustomToast(getActivity()	, msg);
		ToastUtil.showToast(getActivity(), msg);
//		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}
	
	public BlueService getBlueService(){
		
		BaseApp app = (BaseApp) getActivity().getApplication();
		return app.getXBlueService();
	}
	

}
