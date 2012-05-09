package com.race604.sms;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.race604.fetion.client.FetionClient;
import com.race604.fetion.client.ImageVerifyEvent;
import com.race604.fetion.client.LoginStateEvent;
import com.race604.fetion.client.NotifyEvent;
import com.race604.fetion.client.NotifyEventListener;
import com.race604.fetion.data.LoginState;
import com.race604.fetion.data.Presence;
import com.race604.fetion.data.VerifyImage;

/**
 * @author Wu Jing wujing@jike.com
 * @version Create at：2012-5-8 下午3:15:54
 * 
 **/
public class SettingsActivity extends SherlockActivity implements OnClickListener, NotifyEventListener {

	private EditText mAccountEt;
	private EditText mPasswordEt;
	private CheckBox mHiddenCb;
	private TableRow mImgVerifyTr;
	private ImageView mVerifyImgIv;
	private EditText mVerifyCodeEt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings_layout);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.option).toUpperCase());
		
		Button loginBtn = (Button) findViewById(R.id.btn_login);
		loginBtn.setOnClickListener(this);
		
		mAccountEt = (EditText) findViewById(R.id.et_fetion_account);
		mPasswordEt = (EditText) findViewById(R.id.et_fetion_pass);
		mHiddenCb = (CheckBox) findViewById(R.id.cb_fetion_presence);
		
		mImgVerifyTr = (TableRow) findViewById(R.id.tr_verify);
		mVerifyImgIv = (ImageView) findViewById(R.id.iv_verify_img);
		mVerifyCodeEt = (EditText) findViewById(R.id.et_verify_code);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home: { // home button
			finish();
			break;
		}

		}
		return true;
	}
	
	private void loginFetion() {
		String account = mAccountEt.getText().toString();
		String pass = mPasswordEt.getText().toString();
		
		if (account == null || account.length() == 0
				|| pass == null || pass.length() == 0) {
			return;
		}
		
		FetionClient fetionClient;
		fetionClient = new FetionClient(account, pass);
		fetionClient.setNotifyEventListener(this);
		SmsApplication.get().setFetionClient(fetionClient);
		fetionClient.login(mHiddenCb.isChecked() ? Presence.HIDEN : Presence.ONLINE);

	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_login:
			loginFetion();
			break;
		}
		
	}

	@Override
	public void fireEvent(NotifyEvent event) {
		switch(event.getEventType()){
		case IMAGE_VERIFY: {
			ImageVerifyEvent e10 = (ImageVerifyEvent) event;
			imageVerify(e10.getVerifyImage(), e10.getVerifyReason(), e10.getVerifyTips(), e10);
			break;
		}
		case LOGIN_STATE:
			LoginStateEvent e7 = (LoginStateEvent) event;
			loginStateChanged(e7.getLoginState());
			break;
			
		}
		
	}
	
	/**
	 * 处理验证码
     * @param verifyImage		验证图片
     * @param verifyReason		验证原因
     * @param verifyTips		验证提示
     */
	private void imageVerify(VerifyImage verifyImage, String verifyReason,
            String verifyTips, ImageVerifyEvent event)
    {
		mImgVerifyTr.setVisibility(View.VISIBLE);
		byte[] imgData = verifyImage.getImageData();
		Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
		mVerifyImgIv.setImageBitmap(bitmap);
    }
	
	/**
	 * 登陆状态发生了改变
	 * @param state
	 */
	private void loginStateChanged(LoginState state)
	{
	}
}
