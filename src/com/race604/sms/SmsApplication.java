package com.race604.sms;

import android.app.Activity;
import android.app.Application;
import android.util.DisplayMetrics;

public class SmsApplication extends Application {


	private Activity mCurrentActivity = null;
	private static SmsApplication mTheApp;
	private static float dmDensity = 0.0f;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mTheApp = SmsApplication.this;

	}
	
	public static SmsApplication get() {
		return mTheApp;
	}

	public void setCurrentActivity(Activity activity) {
		this.mCurrentActivity = activity;
	}
	
	public Activity getCurrentActivity() {
		return this.mCurrentActivity;
	}
	
	public float getDensityDpi() {
		if (dmDensity <= 0.0) {
			DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
			dmDensity = dm.density;
		}
		return dmDensity;
	}
}
