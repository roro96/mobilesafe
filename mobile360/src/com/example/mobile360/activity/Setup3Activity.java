package com.example.mobile360.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.mobile360.R;
import com.example.mobile360.utils.ConstantValue;
import com.example.mobile360.utils.SpUtil;
import com.example.mobile360.utils.ToastUtil;

public class Setup3Activity extends BaseSetupActivity {

	private EditText et_phone_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);

		initUI();
	}

	private void initUI() {
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		Button btn_constact = (Button) findViewById(R.id.btn_constact);

		String phone = SpUtil.getString(getApplicationContext(),
				ConstantValue.CONTACT_PHONE, "");
		et_phone_number.setText(phone);

		btn_constact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						ContactListActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			String phone = data.getStringExtra("phone");

			phone = phone.replace("-", "").replace(" ", "").trim();
			et_phone_number.setText(phone);
			SpUtil.putString(getApplicationContext(),
					ConstantValue.CONTACT_PHONE, phone);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void showNextPage() {
		String phone = et_phone_number.getText().toString().trim();
		if (!TextUtils.isEmpty(phone)) {
			Intent intent = new Intent(getApplicationContext(),
					Setup4Activity.class);
			startActivity(intent);
			finish();

			SpUtil.putString(getApplicationContext(),
					ConstantValue.CONTACT_PHONE, phone);
			overridePendingTransition(R.anim.anim_in_after,
					R.anim.anim_out_after);
		} else {
			ToastUtil.show(getApplicationContext(), "请输入电话号码");
		}
	}

	@Override
	protected void showPrePage() {
		Intent intent = new Intent(getApplicationContext(),
				Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.anim_in_pre, R.anim.anim_out_pre);
	}
}
