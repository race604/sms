package com.race604.sms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.race604.sms.model.ContactInfo;
import com.race604.sms.model.MSInfo;
import com.race604.sms.model.Utility;

public class SmsReceiver extends BroadcastReceiver {

	public static final int NOTIFY_NEW_SMS_RECEIVED = 1;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		MSInfo[] smsInfos = null;
		// String message = "";
		if (bundle != null) {
			// 接收短信
			Object[] pdus = (Object[]) bundle.get("pdus");
			smsInfos = new MSInfo[pdus.length];
			Uri last = null;
			for (int i = 0; i < smsInfos.length; i++) {
				SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus[i]);
				smsInfos[i] = Utility.parseSmsMessage(msg);
				last = Utility.saveReceivedSms(context,
						msg.getOriginatingAddress(), msg.getMessageBody());
				// message += smsInfos[i].address + ": " + smsInfos[i].body +
				// "\n";
			}
			// ---display the new SMS message---
			// Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			MSInfo sms = Utility.getAMSInfo(context, last);
			long thread_id = sms.thread_id;

			Context appContext = SmsApplication.get();
			NotificationManager notificationManager = (NotificationManager) appContext
					.getSystemService(Context.NOTIFICATION_SERVICE);

			int icon = R.drawable.ic_notify_new;
			ContactInfo contact = Utility.getCantactByPhone(appContext,
					sms.address);
			String from = contact.displayName == null ? sms.address
					: contact.displayName;
			CharSequence tickerText = from + ": " + sms.body;
			long when = System.currentTimeMillis();
			Notification notification = new Notification(icon, tickerText, when);

			CharSequence contentTitle = from;
//			Intent notificationIntent = new Intent[2];
//			notificationIntent[0] = new Intent(appContext,
//					com.race604.sms.MainActivity.class);
//			notificationIntent[0].setAction(Intent.ACTION_MAIN);
//			notificationIntent[0].addCategory(Intent.CATEGORY_LAUNCHER);
//			notificationIntent[0].addCategory(Intent.CATEGORY_LAUNCHER);
//			notificationIntent[0].addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			notificationIntent[0].addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			
			Intent notificationIntent = new Intent(appContext, ThreadActivity.class);
			notificationIntent.putExtra("id", thread_id);

			PendingIntent contentIntent = PendingIntent.getActivity(
					appContext, 0, notificationIntent,
					PendingIntent.FLAG_ONE_SHOT);

			notification.setLatestEventInfo(context, contentTitle, sms.body,
					contentIntent);

			// LED light
			notification.defaults |= Notification.DEFAULT_ALL;
			notification.ledARGB = 0xff00ff00;
			notification.ledOnMS = 200;
			notification.ledOffMS = 250;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			SharedPreferences preference = appContext.getSharedPreferences(
					SmsApplication.PREFER, 0);
			int current_count = preference.getInt(
					SmsApplication.NOTIFICATION_COUNT, 0) + smsInfos.length;
			notification.number = current_count;

			notificationManager.notify(NOTIFY_NEW_SMS_RECEIVED, notification);

			Editor editor = preference.edit();
			editor.putInt(SmsApplication.NOTIFICATION_COUNT, current_count);
			editor.commit();
			// TextView textView = new TextView(context);
			// textView.setText(sms.body);
			// WindowManager.LayoutParams params = new
			// WindowManager.LayoutParams(
			// WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
			// WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
			// PixelFormat.TRANSLUCENT);
			// params.gravity = Gravity.RIGHT | Gravity.TOP;
			// params.setTitle("Load Average");
			// WindowManager wm = (WindowManager) SmsApplication.get()
			// .getSystemService(Service.WINDOW_SERVICE);
			// wm.addView(textView, params);

			// Activity curActivity = SmsApplication.get().getCurrentActivity();
			// if (curActivity != null && curActivity instanceof ThreadActivity)
			// {
			// ThreadActivity threadAcitivity = (ThreadActivity)curActivity;
			// if (thread_id == threadAcitivity.getThreadId()) {
			// threadAcitivity.addSmsInfo(smsInfos[smsInfos.length-1]);
			// } else {
			// threadAcitivity.showThread(thread_id);
			// }
			// }

			// else {
			// Intent threadItent = new Intent(SmsApplication.get(),
			// ThreadActivity.class);
			// threadItent.putExtra("id", thread_id);
			// threadItent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// context.startActivity(threadItent);
			// }

			// 禁止其他短信接收软件
			abortBroadcast();
		}

	}
}
