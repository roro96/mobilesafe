package com.example.mobile360.activity;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mobile360.R;
import com.example.mobile360.engine.SmsBackUp;
import com.example.mobile360.engine.SmsBackUp.CallBack;

public class AToolActivity extends Activity {
	private TextView tv_query_phone_address, tv_sms_backup, tv_commonnumber_query;
	private ProgressBar pb_bar;
	private TextView tv_app_lock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);

		// 电话归属地查询方法
		initPhoneAddress();
		// 短信备份方法
		initSmsBackUp();
		//常用号码查询
		initCommonNumberQuery();
		//程序锁
		initAppLock();
	}

	private void initAppLock() {
		tv_app_lock = (TextView) findViewById(R.id.tv_app_lock);
		tv_app_lock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				startActivity(new Intent(getApplicationContext(), AppLockActivity.class));
			}
		});
	}

	private void initCommonNumberQuery() {
		tv_commonnumber_query = (TextView) findViewById(R.id.tv_commonnumber_query);
		tv_commonnumber_query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), CommonNumberQueryActivity.class));
			}
		});
	}

	private void initSmsBackUp() {
		tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		tv_sms_backup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSmsBackUpDialog();
			}
		});
	}

	protected void showSmsBackUpDialog() {
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setTitle("短信备份");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.show();
		new Thread() {
			@Override
			public void run() {
				String path = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + File.separator + "sms.xml";
				SmsBackUp.backup(getApplicationContext(), path, new CallBack() {
					@Override
					public void setProgress(int index) {
						progressDialog.setProgress(index);
						pb_bar.setProgress(index);
					}

					@Override
					public void setMax(int max) {
						progressDialog.setMax(max);
						pb_bar.setMax(max);
					}
				});

				progressDialog.dismiss();
			}
		}.start();
	}

	private void initPhoneAddress() {
		tv_query_phone_address = (TextView) findViewById(R.id.tv_query_phone_address);
		tv_query_phone_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						QueryAddressActivity.class));
			}
		});
	}
}
