package com.race604.sms.model;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.text.SpannableStringBuilder;

public class Utility {
	
	public static final int MIN_NUMBER_LEN = 5;

	public static final String DESC_SORT_ORDER = "date DESC";
	public static final String ASC_SORT_ORDER = "date ASC";

	public static final String DEFAULT_SORT_ORDER = DESC_SORT_ORDER;

	public static String[] SMS_PROJECTION = new String[] { "_id", "thread_id",
			"address", "person", "date", "protocol", "read", "status", "type",
			"body" };

	public static List<MSInfo> getSms(Context context, Uri uri,
			String selection, String[] selectionArgs, String sortOrder,
			int maxCount) {
		List<MSInfo> smsList = new LinkedList<MSInfo>();

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
				MSInfo msInfo = new MSInfo();
				msInfo.id = cusor.getInt(idCol);
				msInfo.thread_id = cusor.getInt(thread_idCol);
				msInfo.address = cusor.getString(addressCol);
				msInfo.person = cusor.getString(personCol);
				msInfo.date = cusor.getLong(dateCol);
				msInfo.protocol = cusor.getInt(protocolCol);
				msInfo.read = cusor.getInt(readCol);
				msInfo.status = cusor.getInt(statusCol);
				msInfo.type = cusor.getInt(typeCol);
				msInfo.body = cusor.getString(bodyCol);
				smsList.add(msInfo);

				count++;
				if (maxCount > 0 && count >= maxCount) {
					break;
				}
			}
			cusor.close();
		}
		return smsList;
	}

	public static List<MSInfo> getSms(Context context, Uri uri,
			String selection, String[] selectionArgs, String sortOrder) {
		return getSms(context, uri, selection, selectionArgs, sortOrder, 0);
	}

	public static MSInfo getAMSInfo(Context context, Uri uri) {
		List<MSInfo> list = getSms(context, uri, null, null, null, 1);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;

	}

	public static List<MSInfo> getSmsAll(Context context) {
		return getSms(context, Uri.parse(MSInfo.SMS_URI_ALL), null, null,
				DEFAULT_SORT_ORDER);
	}

	public static List<MSInfo> getSmsInbox(Context context) {
		return getSms(context, Uri.parse(MSInfo.SMS_URI_INBOX), null, null,
				DEFAULT_SORT_ORDER);
	}

	public static List<MSInfo> getSmsSend(Context context) {
		return getSms(context, Uri.parse(MSInfo.SMS_URI_SEND), null, null,
				DEFAULT_SORT_ORDER);
	}

	public static List<MSInfo> getSmsDraft(Context context) {
		return getSms(context, Uri.parse(MSInfo.SMS_URI_DRAFT), null, null,
				DEFAULT_SORT_ORDER);
	}

	public static List<MSThread> getSmsThread(Context context) {
		List<MSThread> list = new ArrayList<MSThread>();
		HashMap<Long, Integer> threadIds = new HashMap<Long, Integer>();

		String[] projection = new String[] { "_id", "thread_id", "address",
				"date", "read", "status", "type", "body" };

		Cursor cusor = context.getContentResolver().query(
				Uri.parse(MSInfo.SMS_URI_ALL), projection, null, null,
				DESC_SORT_ORDER);
		int idCol = cusor.getColumnIndex("_id");
		int thread_idCol = cusor.getColumnIndex("thread_id");
		int addressCol = cusor.getColumnIndex("address");
		// int personCol = cusor.getColumnIndex("person");
		int dateCol = cusor.getColumnIndex("date");
		// int protocolCol = cusor.getColumnIndex("protocol");
		int readCol = cusor.getColumnIndex("read");
		int statusCol = cusor.getColumnIndex("status");
		int typeCol = cusor.getColumnIndex("type");
		int bodyCol = cusor.getColumnIndex("body");

		if (cusor.moveToFirst()) {
			do {
				Integer index;
				MSThread thread;
				long thread_id = cusor.getLong(thread_idCol);
				int read = cusor.getInt(readCol);

				index = threadIds.get(thread_id);
				if (index == null) {
					threadIds.put(thread_id, list.size());

					MSInfo msInfo = new MSInfo();
					msInfo.id = cusor.getInt(idCol);
					msInfo.thread_id = thread_id;
					msInfo.address = cusor.getString(addressCol);
					msInfo.date = cusor.getLong(dateCol);
					msInfo.read = read;
					msInfo.status = cusor.getInt(statusCol);
					msInfo.type = cusor.getInt(typeCol);
					msInfo.body = cusor.getString(bodyCol);

					thread = new MSThread();
					thread.count = 0;
					thread.unread = (read == 0);
					thread.latest = msInfo;
					list.add(thread);
				} else {
					thread = list.get(index);
					if (thread.latest.address == null) {
						thread.latest.address = cusor.getString(addressCol);
					}
				}

				thread.count++;
				thread.unread |= (read == 0);
			} while (cusor.moveToNext());
		}
		return list;
	}

	public static List<MSInfo> getMms(Context context, Uri uri,
			String selection, String[] selectionArgs, String sortOrder) {
		return getMms(context, uri, selection, selectionArgs, sortOrder, 0);
	}

	public static List<MSInfo> getMms(Context context, Uri uri,
			String selection, String[] selectionArgs, String sortOrder,
			int maxCount) {
		List<MSInfo> list = new LinkedList<MSInfo>();
		ContentResolver contentResolver = context.getContentResolver();
		final String[] projection = new String[] { "_id", "thread_id", "date",
				"read", "sub", "msg_box"};
		Cursor cursor = contentResolver.query(uri, projection, selection,
				selectionArgs, sortOrder);
		int idCol = cursor.getColumnIndex("_id");
		int tidCol = cursor.getColumnIndex("thread_id");
		int dateCol = cursor.getColumnIndex("date");
		int readCol = cursor.getColumnIndex("read");
		int subjectCol = cursor.getColumnIndex("sub");
		int typeCol = cursor.getColumnIndex("msg_box");

		int count = 0;
		if (cursor.moveToFirst()) {
			do {
				if (maxCount != 0 && count >= maxCount) {
					break;
				}
				MSInfo info = new MSInfo();
				info.id = cursor.getLong(idCol);
				info.thread_id = cursor.getLong(tidCol);
				info.date = cursor.getLong(dateCol) * 1000;
				info.read = cursor.getInt(readCol);
				info.address = getMmsAddress(context, info.id);
				info.body = cursor.getString(subjectCol);
				info.type = cursor.getInt(typeCol);
				info.msType = MSInfo.MSType.MMS;
				try {
					info.body = new String(info.body.getBytes("ISO8859_1"),
							"utf-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}

				list.add(info);

			} while (cursor.moveToNext());
		}

		return list;
	}

	public static List<MSThread> getMmsThread(Context context) {

		List<MSThread> list = new LinkedList<MSThread>();
		HashMap<Long, Integer> threadIds = new HashMap<Long, Integer>();

		ContentResolver contentResolver = context.getContentResolver();
		final String[] projection = new String[] { "_id", "thread_id", "date",
				"read", "sub" };
		
		
		Uri uri = Uri.parse(MSInfo.MMS_URI_ALL);
		Cursor cursor = contentResolver.query(uri, projection, null, null,
				DESC_SORT_ORDER);
		
		int idCol = cursor.getColumnIndex("_id");
		int tidCol = cursor.getColumnIndex("thread_id");
		int dateCol = cursor.getColumnIndex("date");
		int readCol = cursor.getColumnIndex("read");
		int subjectCol = cursor.getColumnIndex("sub");

		Integer index;
		MSThread thread;

		if (cursor.moveToFirst()) {
			long thread_id;
			int read;
			do {
				thread_id = cursor.getLong(tidCol);
				read = cursor.getInt(readCol);

				index = threadIds.get(thread_id);
				if (index == null) {
					MSInfo info = new MSInfo();
					info.id = cursor.getLong(idCol);
					info.thread_id = thread_id;
					info.date = cursor.getLong(dateCol) * 1000;
					info.read = read;
					info.body = cursor.getString(subjectCol);

					try {
						info.body = new String(info.body.getBytes("ISO8859_1"),
								"utf-8");
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}

					info.msType = MSInfo.MSType.MMS;
					threadIds.put(thread_id, list.size());
					thread = new MSThread();
					thread.count = 0;
					thread.unread = (read == 0);
					thread.latest = info;
					list.add(thread);
				} else {
					thread = list.get(index);
					// if (thread.latest.address == null) { // ≤›∏Â√ª”–address
					// thread.latest.address = mms.address;
					// }
				}

				thread.count++;
				thread.unread |= (read == 0);

			} while (cursor.moveToNext());
		}

		return list;
	}

	private static String getMmsAddress(Context context, long id) {
		String add = "";
		final String[] proj = new String[] { "address", "contact_id",
				"charset", "type" };
		final String selection = "type=137"; // "type="+ PduHeaders.FROM,

		Uri.Builder builder = Uri.parse("content://mms").buildUpon();
		builder.appendPath(String.valueOf(id)).appendPath("addr");

		Cursor cur = context.getContentResolver().query(builder.build(), proj,
				selection, null, null);

		if (cur.moveToFirst()) {
			add = cur.getString(0);
		}

		return add;
	}

	public static List<MSThread> getMmsSmsThread(Context context) {
		LinkedList<MSThread> list = new LinkedList<MSThread>();

		List<MSThread> mmsThreads = getMmsThread(context);
		List<MSThread> smsThreads = getSmsThread(context);

		HashMap<Long, Integer> threadIds = new HashMap<Long, Integer>();

		int sPos = 0;
		int mPos = 0;

		int sLen = smsThreads.size();
		int mLen = mmsThreads.size();

		while (sPos < sLen && mPos < mLen) {
			MSThread sms = smsThreads.get(sPos);
			MSThread mms = mmsThreads.get(mPos);
			MSThread toAdd;
			if (sms.latest.date >= mms.latest.date) {
				toAdd = sms;
				sPos++;
			} else {
				toAdd = mms;
				mPos++;
			}

			Integer idx = threadIds.get(toAdd.latest.thread_id);
			if (idx == null) {
				threadIds.put(toAdd.latest.thread_id, list.size());
				if (toAdd.latest.msType == MSInfo.MSType.MMS) {
					toAdd.latest.address = getMmsAddress(context,
							toAdd.latest.id);
				}
				list.add(toAdd);
			} else {
				list.get(idx).count += toAdd.count;
			}
		}

		while (sPos < sLen) {
			MSThread toAdd = smsThreads.get(sPos);
			Integer idx = threadIds.get(toAdd.latest.thread_id);
			if (idx == null) {
				threadIds.put(toAdd.latest.thread_id, list.size());
				list.add(toAdd);
			} else {
				list.get(idx).count += toAdd.count;
			}
			sPos++;
		}

		while (mPos < mLen) {
			MSThread toAdd = mmsThreads.get(mPos);
			Integer idx = threadIds.get(toAdd.latest.thread_id);
			if (idx == null) {
				threadIds.put(toAdd.latest.thread_id, list.size());
				toAdd.latest.address = getMmsAddress(context, toAdd.latest.id);
				list.add(toAdd);
			} else {
				list.get(idx).count += toAdd.count;
			}
			mPos++;
		}

		return list;
	}

	public static List<MSInfo> getMmsSmsByThreadId(Context context,
			long thread_id) {
		List<MSInfo> list = new LinkedList<MSInfo>();

		List<MSInfo> smsList = getSms(context, Uri.parse(MSInfo.SMS_URI_ALL),
				"thread_id = ?", new String[] { String.valueOf(thread_id) },
				Utility.ASC_SORT_ORDER);

		List<MSInfo> mmsList = getMms(context, Uri.parse(MSInfo.MMS_URI_ALL),
				"thread_id = ?", new String[] { String.valueOf(thread_id) },
				Utility.ASC_SORT_ORDER);

		Iterator<MSInfo> smsIterator = smsList.iterator();
		Iterator<MSInfo> mmsIterator = mmsList.iterator();

		MSInfo sms;
		MSInfo mms;
		while (smsIterator.hasNext() && mmsIterator.hasNext()) {
			sms = smsIterator.next();
			mms = mmsIterator.next();
			if (sms.date > mms.date) {
				list.add(sms);
			} else {
				list.add(mms);
			}
		}

		while (smsIterator.hasNext()) {
			sms = smsIterator.next();
			list.add(sms);
		}
		
		while (mmsIterator.hasNext()) {
			mms = mmsIterator.next();
			list.add(mms);
		}

		return list;
	}

	public static List<MSInfo> getSmsAllByThreadId(Context context,
			long thread_id) {
		return getSms(context, Uri.parse(MSInfo.SMS_URI_ALL), "thread_id = ?",
				new String[] { String.valueOf(thread_id) },
				Utility.ASC_SORT_ORDER);
	}

	public static long getThreadIdByPhone(Context context, String phone) {
		List<MSInfo> list = getSms(context, Uri.parse(MSInfo.SMS_URI_ALL),
				"address = ? ", new String[] { phone }, Utility.DESC_SORT_ORDER);
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
		Cursor cursor = contentResolver
				.query(uri, projection, null, null, null);

		int idCol = cursor.getColumnIndex("_id");
		int dateCol = cursor.getColumnIndex("normalized_date");
		int tidCol = cursor.getColumnIndex("tid");
		int readCol = cursor.getColumnIndex("read");
		int addressCol = cursor.getColumnIndex("address");

		if (cursor.moveToFirst()) {
			do {
				MsConversation conv = new MsConversation();
				conv.id = cursor.getLong(idCol);
				conv.date = cursor.getLong(dateCol);
				conv.thread_id = cursor.getLong(tidCol);
				conv.read = cursor.getInt(readCol);
				conv.address = cursor.getString(addressCol);
				list.add(conv);
			} while (cursor.moveToNext());
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
	
	public static boolean isNumber(char a) {
		return (a >= '0' && a <= '9');
	}
	
	public static SpannableStringBuilder generateSpannable(String str) {
		if (str == null ) {
			return null;
		}
		
		SpannableStringBuilder spanBuilder = new SpannableStringBuilder(str);
		int cur = 0;
		int start = -1;
		int numberLen = 0;
		int urlLen = 0;
		for (; cur < str.length(); cur++) {
			if (isNumber(str.charAt(cur)) && urlLen == 0) {
				if (numberLen == 0) {
					start = cur;
				}
				numberLen++;
			} else {
				if (numberLen >= MIN_NUMBER_LEN) {
					//spanBuilder.setSpan(what, start, end, flags)
				}
			}
		}
		
		return null;
	}
 

}
