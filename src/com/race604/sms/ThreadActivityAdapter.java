package com.race604.sms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.text.SpannableStringBuilder;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.race604.sms.model.MSInfo;
import com.race604.sms.model.Utility;

public class ThreadActivityAdapter  extends ArrayAdapter<MSInfo>{


	private List<MSInfo> mList;
	private Activity mContext;
	private LayoutInflater mInflater;
	private String mName;
	
	private int mReadCount = 0;
	
	public ThreadActivityAdapter(Activity context, List<MSInfo> smsList) {
		super(context, R.layout.thread_item, smsList);
		if (smsList == null) {
			mList = new ArrayList<MSInfo>();
		} else {
			mList = smsList;
		}
		mContext = context;
		mInflater = mContext.getLayoutInflater();
	}
	
	public void setContactName(String name) {
		mName = name;
	}
	
	public String getContactName() {
		return mName;
	}
	
	@Override
	public void add(MSInfo object) {
		mList.add(object);
	}

	@Override
	public void addAll(Collection<? extends MSInfo> collection) {
		mList.addAll(collection);
	}

	@Override
	public void clear() {
		super.clear();
		mList.clear();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public MSInfo getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rawView = convertView;
		if (rawView == null) {
			rawView = mInflater.inflate(R.layout.thread_item, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = (TextView) rawView.findViewById(R.id.name_tv);
			viewHolder.time = (TextView) rawView.findViewById(R.id.time_tv);
			viewHolder.sms = (TextView) rawView.findViewById(R.id.sms_tv);
			rawView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rawView.getTag();
		MSInfo sms = mList.get(position);
		if (sms.type == 1)  { // 接收到的短信
			holder.name.setText(mName);
		} else { // 自己发送的短信
			holder.name.setText(R.string.me);
		}
		Time time = new Time();
		time.set(sms.date);
		holder.time.setText(time.format("%Y-%m-%d %H:%M"));
		
		String body = sms.body;
		if (body == null) {
			body = mContext.getString(R.string.null_content);
		}
		holder.sms.setText(body);
		
		if (sms.read == 0) {
			sms.read = 1;
			mReadCount++;
			Utility.setSmsAsRead(mContext, sms.id);
		}
		
		return rawView;
	}
	
	public int getReadCount() {
		return mReadCount;
	}
	
	private static class ViewHolder {
		public TextView name;
		public TextView time;
		public TextView sms;
	}
	
}
