package com.race604.sms.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;

public class Utility {

	public static final String DESC_SORT_ORDER = "date DESC";
	public static final String ASC_SORT_ORDER = "date ASC";

	public static final String DEFAULT_SORT_ORDER = DESC_SORT_ORDER;

	public static String[] SMS_PROJECTION = new String[] { "_id", "thread_id",
			"address", "person", "date", "protocol", "read", "status", "type",
			"body" };

	public static List<MSInfo> getMSInfo(Context context, Uri uri,
			String selection, String[] selectionArgs, String sortOrder,
			int maxCount) {
		List<MSInfo> smsList = new ArrayList<MSInfo>();

		Cursor cusor = context.getContentResolver().query(uri, SMS_PROJECTION,
				selection, selectionArgs, sortOrder);
		int idCol = cusor.getColumnIndex("_id");
		int thread_idCol = cusor.getColumnIndex("thread_id");
		int addressCol = cusor.getColumnIndex("address");
		int personCol = cusor.getColumnIndex("person");
		int dateCol = cusor.getColumnIndex("date");
		int protocolCol = cusor.getColumnIndex("protocol");
		int readCol = cusor.getColumnIndex("read");
		int statusCol = cusor.getColumnIndex("status");
		int typeCol = cusor.getColumnIndex("type");
		int bodyCol = cusor.getColumnIndex("body");

		int count = 0;
		if (cusor != null) {
			while (cusor.moveToNext()) {
				MSInfo MSInfo = new MSInfo();
				MSInfo.id = cusor.getInt(idCol);
				MSInfo.thread_id = cusor.getInt(thread_idCol);
				MSInfo.address = cusor.getString(addressCol);
				MSInfo.person = cusor.getString(personCol);
				MSInfo.date = cusor.getLong(dateCol);
				MSInfo.protocol = cusor.getInt(protocolCol);
				MSInfo.read = cusor.getInt(readCol);
				MSInfo.status = cusor.getInt(statusCol);
				MSInfo.type = cusor.getInt(typeCol);
				MSInfo.body = cusor.getString(bodyCol);
				smsList.add(MSInfo);

				count++;
				if (maxCount > 0 && count >= maxCount) {
					break;
				}
			}
			cusor.close();
		}
		return smsList;
	}

	public static List<MSInfo> getMSInfo(Context context, Uri uri,
			String selection, String[] selectionArgs, String sortOrder) {
		return getMSInfo(context, uri, selection, selectionArgs, sortOrder, 0);
	}

	public static MSInfo getAMSInfo(Context context, Uri uri) {
		List<MSInfo> list = getMSInfo(context, uri, null, null, null, 1);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;

	}

	public static List<MSInfo> getSmsAll(Context context) {
		return getMSInfo(context, Uri.parse(MSInfo.SMS_URI_ALL), null, null,
				DEFAULT_SORT_ORDER);
	}

	public static List<MSInfo> getSmsInbox(Context context) {
		return getMSInfo(context, Uri.parse(MSInfo.SMS_URI_INBOX), null,
				null, DEFAULT_SORT_ORDER);
	}

	public static List<MSInfo> getSmsSend(Context context) {
		return getMSInfo(context, Uri.parse(MSInfo.SMS_URI_SEND), null, null,
				DEFAULT_SORT_ORDER);
	}

	public static List<MSInfo> getSmsDraft(Context context) {
		return getMSInfo(context, Uri.parse(MSInfo.SMS_URI_DRAFT), null,
				null, DEFAULT_SORT_ORDER);
	}

	public static List<MSThread> getThreadALL(Context context) {
		List<MSThread> list = new ArrayList<MSThread>();
		List<MSInfo> smsList = getSmsAll(context);
		HashMap<Long, Integer> threadIds = new HashMap<Long, Integer>();
		Integer index;
		MSThread thread;
		for (MSInfo sms : smsList) {

			index = threadIds.get(sms.thread_id);
			if (index == null) {
				threadIds.put(sms.thread_id, list.size());
				thread = new MSThread();
				thread.count = 0;
				thread.unread = (sms.read == 0);
				thread.latest = sms;
				list.add(thread);
			} else {
				thread = list.get(index);
				if (thread.latest.address == null) {
					thread.latest.address = sms.address;
				}
			}

			thread.count++;
			thread.unread |= (sms.read == 0);
		}
		return list;
	}
	
	public static List<MSThread> getMmsSmsThread(Context context) {
		List<MSThread> list = new ArrayList<MSThread>();
		
		ContentResolver contentResolver = context.getContentResolver();
		final String[] projection = new String[] { "*" };
		Uri uri = Uri.parse(MSInfo.MMS_URI_ALL);
		Cursor cursor = contentResolver.query(uri, projection, null, null, null);
		String[] cols = cursor.getColumnNames();
		Log.d("mms", cols.toString());
		int idCol = cursor.getColumnIndex("_id");
		int dateCol = cursor.getColumnIndex("thread_id");
		int tidCol = cursor.getColumnIndex("date");
		int readCol = cursor.getColumnIndex("read");
		int addressCol = cursor.getColumnIndex("msg_box");
		
		if(cursor.moveToFirst()) {
			do {
				MSThread conv = new MSThread();

			}while(cursor.moveToNext());
		}
		
		return list;
	}
	
	public static List<MSInfo> getSmsAllByThreadId(Context context,
			long thread_id) {
		return getMSInfo(context, Uri.parse(MSInfo.SMS_URI_ALL),
				"thread_id = ?", new String[] { String.valueOf(thread_id) },
				Utility.ASC_SORT_ORDER);
	}

	public static long getThreadIdByPhone(Context context, String phone) {
		List<MSInfo> list = getMSInfo(context,
				Uri.parse(MSInfo.SMS_URI_ALL), "address = ? ",
				new String[] { phone }, Utility.DESC_SORT_ORDER);
		if (list.size() <= 0) {
			return -1;
		} else {
			return list.get(0).thread_id;
		}
	}

	public static String getCleanPhone(String phone) {
		String num = phone;
		int len = phone.length();
		if (len > 11) {
			num = phone.substring(len - 11);
		}
		return num;
	}

	public static ContactInfo getCantactByPhone(Context context, String phone) {
		if (phone == null) {
			return null;
		}

		String num = getCleanPhone(phone);

		ContactInfo contact = new ContactInfo();
		contact.displayName = num;
		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID };

		ContentResolver cr = context.getContentResolver();
		Cursor pCur = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
				new String[] { num }, null);
		if (pCur == null) {
			return null;
		}
		if (pCur.moveToFirst()) {
			contact.displayName = pCur
					.getString(pCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			contact.contactId = pCur
					.getLong(pCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
			pCur.close();
		}
		return contact;
	}

	public static Uri saveSentSms(Context context, String address, String body) {
		ContentValues values = new ContentValues();
		values.put("address", address);
		values.put("body", body);
		values.put("status", MSInfo.STATUS_NONE);
		return context.getContentResolver().insert(
				Uri.parse(MSInfo.SMS_URI_SEND), values);
	}

	public static int setSmsAsRead(Context context, Uri uri) {
		ContentValues values = new ContentValues();
		values.put("read", 1);
		return context.getContentResolver().update(uri, values, null, null);
	}

	public static int setSmsAsRead(Context context, long id) {
		ContentValues values = new ContentValues();
		values.put("read", 1);
		return context.getContentResolver().update(
				Uri.parse(MSInfo.SMS_URI_INBOX), values, "_id = ?",
				new String[] { String.valueOf(id) });
	}

	public static Uri saveReceivedSms(Context context, String address,
			String body) {
		ContentValues values = new ContentValues();
		values.put("address", address);
		values.put("body", body);
		values.put("status", MSInfo.STATUS_NONE);
		return context.getContentResolver().insert(
				Uri.parse(MSInfo.SMS_URI_INBOX), values);
	}

	public static int updateSmsStatus(Context context, Uri uri, int status) {
		ContentValues values = new ContentValues();
		values.put("status", status);
		return context.getContentResolver().update(uri, values, null, null);
	}

	public static MSInfo parseSmsMessage(SmsMessage message) {
		MSInfo sms = new MSInfo();
		sms.address = message.getOriginatingAddress();
		sms.body = message.getMessageBody();
		return sms;
	}

	public static List<MsConversation> getMmsSmsConversations(Context context) {
		List<MsConversation> list = new ArrayList<MsConversation>();

		ContentResolver contentResolver = context.getContentResolver();
		final String[] projection = new String[] { "*" };
		Uri uri = Uri.parse(MsConversation.MMS_THREAD_URI);
		Cursor cursor = contentResolver.query(uri, projection, null, null, null);

		int idCol = cursor.getColumnIndex("_id");
		int dateCol = cursor.getColumnIndex("normalized_date");
		int tidCol = cursor.getColumnIndex("tid");
		int readCol = cursor.getColumnIndex("read");
		int addressCol = cursor.getColumnIndex("address");
		
		if(cursor.moveToFirst()) {
			do {
				MsConversation conv = new MsConversation();
				conv.id = cursor.getLong(idCol);
				conv.date = cursor.getLong(dateCol);
				conv.thread_id = cursor.getLong(tidCol);
				conv.read = cursor.getInt(readCol);
				conv.address = cursor.getString(addressCol);
				list.add(conv);
			}while(cursor.moveToNext());
		}
		
		return list;
	}

	public static List<MSInfo> getMmsInfo(Context context, Uri uri,
			String selection, String[] selectionArgs, String sortOrder,
			int maxCount) {
		List<MSInfo> smsList = new ArrayList<MSInfo>();

		Cursor cusor = context.getContentResolver().query(uri, SMS_PROJECTION,
				selection, selectionArgs, sortOrder);
		int idCol = cusor.getColumnIndex("_id");
		int thread_idCol = cusor.getColumnIndex("thread_id");
		int addressCol = cusor.getColumnIndex("address");
		int personCol = cusor.getColumnIndex("person");
		int dateCol = cusor.getColumnIndex("date");
		int protocolCol = cusor.getColumnIndex("protocol");
		int readCol = cusor.getColumnIndex("read");
		int statusCol = cusor.getColumnIndex("status");
		int typeCol = cusor.getColumnIndex("type");
		int bodyCol = cusor.getColumnIndex("body");

		int count = 0;
		if (cusor != null) {
			while (cusor.moveToNext()) {
				MSInfo MSInfo = new MSInfo();
				MSInfo.id = cusor.getInt(idCol);
				MSInfo.thread_id = cusor.getInt(thread_idCol);
				MSInfo.address = cusor.getString(addressCol);
				MSInfo.person = cusor.getString(personCol);
				MSInfo.date = cusor.getLong(dateCol);
				MSInfo.protocol = cusor.getInt(protocolCol);
				MSInfo.read = cusor.getInt(readCol);
				MSInfo.status = cusor.getInt(statusCol);
				MSInfo.type = cusor.getInt(typeCol);
				MSInfo.body = cusor.getString(bodyCol);
				smsList.add(MSInfo);

				count++;
				if (maxCount > 0 && count >= maxCount) {
					break;
				}
			}
			cusor.close();
		}
		return smsList;
	}
	
}
