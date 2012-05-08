package com.race604.sms;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.race604.fetion.client.FetionClient;
import com.race604.fetion.data.Presence;

/**
 * @author Wu Jing wujing@jike.com
 * @version Create at：2012-5-8 下午3:15:54
 * 
 **/
public class SettingsActivity extends SherlockActivity implements OnClickListener {

	private EditText mAccountEt;
	private EditText mPasswordEt;
	private CheckBox mHiddenCb;
	
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
}
