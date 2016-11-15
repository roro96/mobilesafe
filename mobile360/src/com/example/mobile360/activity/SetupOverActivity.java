package com.example.mobile360.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.mobile360.R;
import com.example.mobile360.utils.ConstantValue;
import com.example.mobile360.utils.SpUtil;

public class SetupOverActivity extends Activity {

	private TextView tv_phone;
	private TextView tv_reset_setup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean setup_over = SpUtil.getBoolean(getApplicationContext(), ConstantValue.SETUP_OVER, false);
		if (setup_over) {
			setContentView(R.layout.activity_setup_over);
			initUI();
		}else {
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			finish();
		}
	}

	private void initUI() {
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		String phone = SpUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
		tv_phone.setText(phone);
		
		tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);
		tv_reset_setup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	
}
