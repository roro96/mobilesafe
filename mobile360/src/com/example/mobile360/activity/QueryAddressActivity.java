package com.example.mobile360.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mobile360.R;
import com.example.mobile360.db.dao.AddressDao;

public class QueryAddressActivity extends Activity {
	private EditText et_phone;
	private Button btn_query;
	private TextView tv_query_result;
	private String mAddress;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			tv_query_result.setText(mAddress);
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_address);
		
		initUI();
	}

	private void initUI() {
		et_phone = (EditText) findViewById(R.id.et_phone);
		btn_query = (Button) findViewById(R.id.btn_query);
		tv_query_result = (TextView) findViewById(R.id.tv_query_result);
		
		btn_query.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String phone = et_phone.getText().toString();
				if (!TextUtils.isEmpty(phone)) {
					query(phone);
				}else {
					Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
					et_phone.startAnimation(shake);
					
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(2000);
					vibrator.vibrate(new long[]{2000,2000}, -1);
				}
			}
		});
		
		et_phone.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String phone = et_phone.getText().toString();
				query(phone);
			}
		});
	}
	
	

	protected void query(final String phone) {
		new Thread(){
			public void run() {
				mAddress = AddressDao.getAddress(phone);
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}
}
