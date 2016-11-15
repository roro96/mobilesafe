package com.example.mobile360.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.mobile360.R;
import com.example.mobile360.utils.ConstantValue;
import com.example.mobile360.utils.SpUtil;
import com.example.mobile360.utils.ToastUtil;

public class Setup4Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);

		initUI();
	}

	private void initUI() {
		final CheckBox cb_box = (CheckBox) findViewById(R.id.cb_box);
		boolean open_security = SpUtil.getBoolean(getApplicationContext(),
				ConstantValue.OPEN_SECURITY, false);
		cb_box.setChecked(open_security);
		if (open_security) {
			cb_box.setText("安全设置已开启");
		} else {
			cb_box.setText("安全设置已关闭");
		}
		
		cb_box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SpUtil.putBoolean(getApplicationContext(),
						ConstantValue.OPEN_SECURITY, isChecked);
				if (isChecked) {
					cb_box.setText("安全设置已开启");
				} else {
					cb_box.setText("安全设置已关闭");
				}
			}
		});
	}

	@Override
	protected void showNextPage() {
		boolean open_security = SpUtil.getBoolean(getApplicationContext(),
				ConstantValue.OPEN_SECURITY, false);
		if (open_security) {
			Intent intent = new Intent(getApplicationContext(),
					SetupOverActivity.class);
			startActivity(intent);
			finish();
			SpUtil.putBoolean(getApplicationContext(),
					ConstantValue.SETUP_OVER, true);
			overridePendingTransition(R.anim.anim_in_after,
					R.anim.anim_out_after);
		} else {
			ToastUtil.show(getApplicationContext(), "请开启防盗保护");
		}
	}

	@Override
	protected void showPrePage() {
		Intent intent = new Intent(getApplicationContext(),
				Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.anim_in_pre, R.anim.anim_out_pre);
	}
}
