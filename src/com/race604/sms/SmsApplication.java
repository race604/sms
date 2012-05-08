package com.race604.sms;

import com.race604.fetion.client.FetionClient;

import android.app.Activity;
import android.app.Application;
import android.util.DisplayMetrics;

public class SmsApplication extends Application {


	private Activity mCurrentActivity = null;
	private static SmsApplication mTheApp;
	private static float dmDensity = 0.0f;
	
	public static final String PREFER = "com.race604.sms";
	public static final String NOTIFICATION_COUNT = "notification_count";
	
	private static FetionClient mFetionClient = null;
	
	public synchronized void setFetionClient(FetionClient client) {
		mFetionClient = client;
	}
	
	public FetionClient getFetionClient() {
		return mFetionClient;
	}
	
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
