package com.race604.sms.model;

public class MsConversation {
	public static final String MMS_SMS_THREAD_URI = "content://mms-sms/conversations/";
	public static final String MMS_THREAD_URI = "content://mms/threads/";
	public static final String SMS_THREAD_URI = "content://sms/threads/";
	
	// [tid, normalized_date, body, person, sub, subject, retr_st, type, date, 
	//  ct_cls, sub_cs, _id, read, ct_l, tr_id, st, msg_box, thread_id, 
	//  reply_path_present, m_cls, read_status, ct_t, status, retr_txt_cs, 
	//  d_rpt, error_code, m_id, m_type, v, exp, pri, service_center, address, 
	//  rr, rpt_a, resp_txt, locked, resp_st, m_size]
	
	public long id; // 最新的短信的_id;可以通过content://sms/id or content://mms/id来获取短信或者彩信
	public long date; // normalized_date
	public long thread_id; // tid
	public String body; // 短信的内容；对于彩信，如果彩信有text part，body则为null
	public int read; // 0未读，1已读
	public String address; // address
}
