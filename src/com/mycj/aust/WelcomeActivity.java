package com.mycj.aust;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		TextView tvVersion = (TextView) findViewById(R.id.tv_version);
		tvVersion.setText(getVersion());
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
				finish();
			}
		}, 2000);
	}
	
	  public String getVersion() {
	      try {
	         PackageManager manager = this.getPackageManager();
	          PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	          String version = info.versionName;
	         return "版本v" + version;
	     } catch (Exception e) {
	         e.printStackTrace();
	     }
	      return "未知";
	 }
}
