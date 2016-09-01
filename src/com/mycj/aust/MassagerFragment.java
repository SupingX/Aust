package com.mycj.aust;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.laputa.blue.util.XLog;
import com.mycj.aust.base.BaseApp;
import com.mycj.aust.base.BaseFragment;
import com.mycj.aust.view.ControlView;
import com.mycj.protocol.bean.MycjMassagerInfo;
import com.mycj.protocol.core.MassagerProtocolWriteManager;
/**
 * 	因为班子的力度只有11级
 * 	SeekBar有20级。
 * 	每2级改变1级力度 
 *  修改处 edit_power
 * 
 * @author Administrator
 *
 */
public class MassagerFragment extends BaseFragment{
	private ControlView cvTimeAndTemp;
	private LinearLayout llPatternAnmo;
	private LinearLayout llPatternChuiji;
	private LinearLayout llPatternZhenjiu;
	private LinearLayout llPatternTuina;
	private ImageView ivPatternAnmo;
	private ImageView ivPatternChuiji;
	private ImageView ivPatternZhenjiu;
	private ImageView ivPatternTuina;
	private TextView tvPatternAnmo;
	private TextView tvPatternChuji;
	private TextView tvPatternZhenjiu;
	private TextView tvPatternTuina;
	private MassagerProtocolWriteManager write;
	private int currentPatternViewId = -1;
	private SeekBar sbPower;
	private static Handler mHandler = new Handler(){};
//	private BlueService blueService;
	
	public static final long LOADING_TIME = 1500L;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_massager, container,false);
		initViews(view);
		write = MassagerProtocolWriteManager.newInstance();
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
//		if (getBlueService()!=null) {
//			MycjMassagerInfo massagerInfo = getBlueService().getMassagerInfo();
//			updateUiByMassagerInfo(massagerInfo);
//		}
	}
	
/*	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				blueService = getBlueService();
				if (blueService!=null) {
					MycjMassagerInfo massagerInfo = blueService.getMassagerInfo();
					updateUiByMassagerInfo(massagerInfo);
				}
			}
		}, 1000);
	}*/
	
	public void updateTime(int time){
		cvTimeAndTemp.setTime((int)(time *1.0f / 60));
	}
	
	public void updateTemperature(int temp){
//		XLog.e("MassagerFragment","___________temp : " + temp);
		cvTimeAndTemp.setTemp(temp);
	}
	
	public void updatePower(int power){
		sbPower.setProgress(power);
	}
	
	public void updatePatternFromMassager(int pattern){
		int id = -1;
		switch (pattern) {
		case 1:
			id = R.id.ll_pattern_anmo;
			break;
		case 2:
			id = R.id.ll_pattern_chuiji;
			break;
		case 3:
			id = R.id.ll_pattern_zhenjiu;
			break;
		case 4:
			id = R.id.ll_pattern_tuina;
			break;
		}
		currentPatternViewId = id;
		updatePattern(id);
	}
	
	public void updateUiByMassagerInfo(MycjMassagerInfo info){
		XLog.e("MassagerFragment","updateUiByMassagerInfo() --- info :" + info);
		if (info == null) {
			return;
		}
		int open = info.getOpen();
		if (open ==1 ) {
			updatePatternFromMassager(info.getPattern());
			updateTemperature(info.getTemperature());
			updateTime(info.getLeftTime());
		
		}else{
			updatePatternFromMassager(-1);
			updateTemperature(BaseApp.temp);
			updateTime(BaseApp.time);
			
		}
		
	
		updatePower(info.getPower());
	}
	
	private void writeProtocol(byte[] data){
		if (getBlueService()!= null && getBlueService().ifAllConnected()) {
			getBlueService().write(data);
		}
	}
	
	
	private boolean isMassagerStarting(){
		if (getBlueService()!=null) {
			MycjMassagerInfo massagerInfo = getBlueService().getMassagerInfo();
			return massagerInfo!=null && massagerInfo.getOpen()==1;
		}
		return false;
	}
	
	private void initViews(View view) {
		//按摩时间和温度
		cvTimeAndTemp = (ControlView) view.findViewById(R.id.cv_time_and_temp);
		cvTimeAndTemp.setOnProgressChanged(new ControlView.OnProgressChangedListener() {
			
			@Override
			public void onTimeChanged(int time) {
				if (getBlueService() == null) {
					return;
				}
				if (time == 0) {
					time = 1;
				}
//				show("time : " + time);
				if (isMassagerStarting()) {
					writeProtocol(write.writeForChangeTime(time * 60));
					if (mOnMassagerFragmentListener != null) {
						mOnMassagerFragmentListener.showLoading("设置时间中...");
					}
					//延迟5秒未成功就关闭dialog
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							mOnMassagerFragmentListener.closeLoading();	
						}
					}, LOADING_TIME);
				}
			}
			
			@Override
			public void onTempChanged(int temp) {
				if (temp == 0) {
					temp = 1;
				}
//				show("temp : " + temp);
				if (isMassagerStarting()) {
				writeProtocol(write.writeForChangeTemperature(temp, 0));
				if (mOnMassagerFragmentListener != null) {
					mOnMassagerFragmentListener.showLoading("设置时间中...");
				}
				//延迟5秒未成功就关闭dialog
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mOnMassagerFragmentListener.closeLoading();	
					}
				}, LOADING_TIME);
				}
			}
		});
		
		//按摩力度
		sbPower = (SeekBar) view.findViewById(R.id.sb_power);
		sbPower.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
//				show("power : " + seekBar.getProgress() );
				if (isMassagerStarting()) {
					
					// 修改处5 edit_power * 2
				writeProtocol(write.writeForChangePower(seekBar.getProgress()/2));
				if (mOnMassagerFragmentListener != null) {
					mOnMassagerFragmentListener.showLoading("设置力度中...");
				}
				//延迟5秒未成功就关闭dialog
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mOnMassagerFragmentListener.closeLoading();	
					}
				}, LOADING_TIME);
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					
				}
			}
		});
		
		// 按摩模式
		llPatternAnmo = (LinearLayout) view.findViewById(R.id.ll_pattern_anmo);
		llPatternChuiji = (LinearLayout) view.findViewById(R.id.ll_pattern_chuiji);
		llPatternZhenjiu = (LinearLayout) view.findViewById(R.id.ll_pattern_zhenjiu);
		llPatternTuina = (LinearLayout) view.findViewById(R.id.ll_pattern_tuina);
		ivPatternAnmo = (ImageView) view.findViewById(R.id.iv_pattern_anmo);
		ivPatternChuiji = (ImageView) view.findViewById(R.id.iv_pattern_chuiji);
		ivPatternZhenjiu = (ImageView) view.findViewById(R.id.iv_pattern_zhenjiu);
		ivPatternTuina = (ImageView) view.findViewById(R.id.iv_pattern_tuina);
		tvPatternAnmo = (TextView) view.findViewById(R.id.tv_pattern_anmo);
		tvPatternChuji = (TextView) view.findViewById(R.id.tv_pattern_chuiji);
		tvPatternZhenjiu = (TextView) view.findViewById(R.id.tv_pattern_zhenjiu);
		tvPatternTuina = (TextView) view.findViewById(R.id.tv_pattern_tuina);
		updatePattern(currentPatternViewId);
		View.OnClickListener patternOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (getBlueService()==null) {
					XLog.e("blueService为空");
					return ;
				}
				// 1.得到pattern
				int id = v.getId();
				int pattern = -1;
				switch (id) {
				case R.id.ll_pattern_anmo:
					pattern = 1;
					break;
				case R.id.ll_pattern_chuiji:
					pattern = 2;
					break;
				case R.id.ll_pattern_zhenjiu:
					pattern = 3;
					break;
				case R.id.ll_pattern_tuina:
					pattern = 4;
					break;
				}
				String loading = null;
				// 2.判断是开启?/停止?/切换模式?
				MycjMassagerInfo massagerInfo = getBlueService()!=null?getBlueService().getMassagerInfo():null;
				// 2.1当前massagerInfo为空时,直接开启按摩
				if (massagerInfo == null) {
					
					// 修改处4 edit_power * 2
					int power = sbPower.getProgress()/2 ;
					
					int leftTime = cvTimeAndTemp.getTime() * 60;
					int settingTime = leftTime;
					int temperature = cvTimeAndTemp.getTemp();
					
					int tempUnit= 0;
					int loader=0;
					int hr=0;
					MycjMassagerInfo info = new MycjMassagerInfo(1, pattern, power, leftTime, settingTime, temperature, tempUnit, loader, hr);
					writeProtocol(write.writeForStartMassager(info));
					loading = "正在开启...";
					// 2.2当前massagerInfo不为空时
				}else{
					int currentPattern = massagerInfo.getPattern();
					//2.2.1 模式相同时，则停止按摩
					if (currentPattern == pattern) {
						writeProtocol(write.writeForStopMassager());
						loading = "正在结束...";
					
						BaseApp.temp = massagerInfo.getTemperature();
						BaseApp.time = massagerInfo.getLeftTime();
						Log.e("", "---> temp : " +BaseApp.temp );
						Log.e("", "---> time : " +BaseApp.time );
						
					//2.2.2 模式不同时，切换模式
					}else{
							writeProtocol(write.writeForChangePattern(pattern));
							loading = "正在切换模式...";
					
					}
				}
				
				// 3.更新UI
				// 3.1开启loading
				if (mOnMassagerFragmentListener != null && loading !=null) {
					mOnMassagerFragmentListener.showLoading(loading);
				}
				// 3.2延迟5秒未成功就关闭dialog
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mOnMassagerFragmentListener.closeLoading();	
					}
				}, 2000);
				// 3.3
				// updatePattern(currentPatternViewId);
			}
		};
		
		llPatternAnmo.setOnClickListener(patternOnClickListener);
		llPatternChuiji.setOnClickListener(patternOnClickListener);
		llPatternZhenjiu.setOnClickListener(patternOnClickListener);
		llPatternTuina.setOnClickListener(patternOnClickListener);
		
		
	
	}
	
	public interface OnMassagerFragmentListener {
		public void showLoading(String msg);
		public void closeLoading();
	} 
	private OnMassagerFragmentListener mOnMassagerFragmentListener;
	public void setOnMassagerFragmentListener(OnMassagerFragmentListener l){
		this.mOnMassagerFragmentListener = l;
	} 
	
	
	public void updatePattern(int id){
		XLog.e("MassagerFragment", "_________updatePattern : " + id);
		int colorPatternTextViewNormal = getResources().getColor(R.color.color_menu_normal);
		int colorPatternTextViewSelected = getResources().getColor(R.color.color_text_time);
		switch (id) {
		case R.id.ll_pattern_anmo:
//			show("按摩");
			ivPatternAnmo.setImageResource(R.drawable.ic_pattern_anmo_selected);
			ivPatternChuiji.setImageResource(R.drawable.ic_pattern_chuiji_normal);
			ivPatternZhenjiu.setImageResource(R.drawable.ic_pattern_zhenjiu_normal);
			ivPatternTuina.setImageResource(R.drawable.ic_pattern_tuina_normal);
			tvPatternAnmo.setTextColor(colorPatternTextViewSelected);
			tvPatternChuji.setTextColor(colorPatternTextViewNormal);
			tvPatternZhenjiu.setTextColor(colorPatternTextViewNormal);
			tvPatternTuina.setTextColor(colorPatternTextViewNormal);
			llPatternAnmo.setBackgroundResource(R.drawable.ic_pattern_bg_selected);
			llPatternChuiji.setBackgroundResource(R.drawable.ic_pattern_bg_normal);
			llPatternZhenjiu.setBackgroundResource(R.drawable.ic_pattern_bg_normal);
			llPatternTuina.setBackgroundResource(R.drawable.ic_pattern_bg_normal);
			break;
		case R.id.ll_pattern_chuiji:
//			show("锤击");
			ivPatternAnmo.setImageResource(R.drawable.ic_pattern_anmo_normal);
			ivPatternChuiji.setImageResource(R.drawable.ic_pattern_chuiji_selected);
			ivPatternZhenjiu.setImageResource(R.drawable.ic_pattern_zhenjiu_normal);
			ivPatternTuina.setImageResource(R.drawable.ic_pattern_tuina_normal);
			tvPatternAnmo.setTextColor(colorPatternTextViewNormal);
			tvPatternChuji.setTextColor(colorPatternTextViewSelected);
			tvPatternZhenjiu.setTextColor(colorPatternTextViewNormal);
			tvPatternTuina.setTextColor(colorPatternTextViewNormal);
			llPatternAnmo.setBackgroundResource(R.drawable.ic_pattern_bg_normal);
			llPatternChuiji.setBackgroundResource(R.drawable.ic_pattern_bg_selected);
			llPatternZhenjiu.setBackgroundResource(R.drawable.ic_pattern_bg_normal);
			llPatternTuina.setBackgroundResource(R.drawable.ic_pattern_bg_normal);
			break;
		case R.id.ll_pattern_zhenjiu:
//			show("针灸");
			ivPatternAnmo.setImageResource(R.drawable.ic_pattern_anmo_normal);
			ivPatternChuiji.setImageResource(R.drawable.ic_pattern_chuiji_normal);
			ivPatternZhenjiu.setImageResource(R.drawable.ic_pattern_zhenjiu_selected);
			ivPatternTuina.setImageResource(R.drawable.ic_pattern_tuina_normal);
			tvPatternAnmo.setTextColor(colorPatternTextViewNormal);
			tvPatternChuji.setTextColor(colorPatternTextViewNormal);
			tvPatternZhenjiu.setTextColor(colorPatternTextViewSelected);
			tvPatternTuina.setTextColor(colorPatternTextViewNormal);
			llPatternAnmo.setBackgroundResource(R.drawable.ic_pattern_bg_normal);
			llPatternChuiji.setBackgroundResource(R.drawable.ic_pattern_bg_normal);
			llPatternZhenjiu.setBackgroundResource(R.drawable.ic_pattern_bg_selected);
			llPatternTuina.setBackgroundResource(R.drawable.ic_pattern_bg_normal);
			break;
		case R.id.ll_pattern_tuina:
//			show("推拿");
			ivPatternAnmo.setImageResource(R.drawable.ic_pattern_anmo_normal);
			ivPatternChuiji.setImageResource(R.drawable.ic_pattern_chuiji_normal);
			ivPatternZhenjiu.setImageResource(R.drawable.ic_pattern_zhenjiu_normal);
			ivPatternTuina.setImageResource(R.drawable.ic_pattern_tuina_selected);
			tvPatternAnmo.setTextColor(colorPatternTextViewNormal);
			tvPatternChuji.setTextColor(colorPatternTextViewNormal);
			tvPatternZhenjiu.setTextColor(colorPatternTextViewNormal);
			tvPatternTuina.setTextColor(colorPatternTextViewSelected);
			llPatternAnmo.setBackgroundResource(R.drawable.ic_pattern_bg_normal);
			llPatternChuiji.setBackgroundResource(R.drawable.ic_pattern_bg_normal);
			llPatternZhenjiu.setBackgroundResource(R.drawable.ic_pattern_bg_normal);
			llPatternTuina.setBackgroundResource(R.drawable.ic_pattern_bg_selected);
			break;
		default :
//			show("选择按摩模式直接开始按摩.");
			ivPatternAnmo.setImageResource(R.drawable.ic_pattern_anmo_normal);
			ivPatternChuiji.setImageResource(R.drawable.ic_pattern_chuiji_normal);
			ivPatternZhenjiu.setImageResource(R.drawable.ic_pattern_zhenjiu_normal);
			ivPatternTuina.setImageResource(R.drawable.ic_pattern_tuina_normal);
			tvPatternAnmo.setTextColor(colorPatternTextViewNormal);
			tvPatternChuji.setTextColor(colorPatternTextViewNormal);
			tvPatternZhenjiu.setTextColor(colorPatternTextViewNormal);
			tvPatternTuina.setTextColor(colorPatternTextViewNormal);
			llPatternAnmo.setBackgroundResource(R.drawable.ic_pattern_bg_normal);
			llPatternChuiji.setBackgroundResource(R.drawable.ic_pattern_bg_normal);
			llPatternZhenjiu.setBackgroundResource(R.drawable.ic_pattern_bg_normal);
			llPatternTuina.setBackgroundResource(R.drawable.ic_pattern_bg_normal);
			break;
		}
	}
	
	
}
