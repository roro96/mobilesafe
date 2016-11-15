package com.example.mobile360.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.mobile360.R;
import com.example.mobile360.utils.ConstantValue;
import com.example.mobile360.utils.SpUtil;
import com.example.mobile360.utils.ToastUtil;
import com.example.mobile360.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);

		initUI();
	}

	private void initUI() {
		final SettingItemView siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);
		// 1,回显(读取已有的绑定状态,用作显示,sp中是否存储了sim卡的序列号)
		String simNumber = SpUtil.getString(getApplicationContext(),
				ConstantValue.SIM_NUMBER, "");
		// 2,判断是否序列卡号为""
		if (!TextUtils.isEmpty(simNumber)) {
			siv_sim_bound.setCheck(true);
		} else {
			siv_sim_bound.setCheck(false);
		}
		siv_sim_bound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 3,获取原有的状态
				boolean isCheck = siv_sim_bound.isCheck();
				// 4,将原有状态取反
				// 5,状态设置给当前条目
				siv_sim_bound.setCheck(!isCheck);
				if (!isCheck) {
					// 6,存储(序列卡号)
					// 6.1获取sim卡序列号TelephoneManager
					TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					// 6.2获取sim卡的序列卡号
					String simSerialNumber = manager.getSimSerialNumber();
					// 6.3存储
					SpUtil.putString(getApplicationContext(),
							ConstantValue.SIM_NUMBER, simSerialNumber);
				} else {
					// 7,将存储序列卡号的节点,从sp中删除掉
					SpUtil.remove(getApplicationContext(),
							ConstantValue.SIM_NUMBER);
				}
			}
		});
	}

	@Override
	protected void showPrePage() {
		Intent intent = new Intent(getApplicationContext(),
				Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.anim_in_pre, R.anim.anim_out_pre);
	}

	@Override
	protected void showNextPage() {
		String serialNumber = SpUtil.getString(getApplicationContext(),
				ConstantValue.SIM_NUMBER, "");
		if (!TextUtils.isEmpty(serialNumber)) {
			Intent intent = new Intent(getApplicationContext(),
					Setup3Activity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.anim_in_after,
					R.anim.anim_out_after);
		} else {
			ToastUtil.show(getApplicationContext(), "请绑定sim卡");
		}
	}
}
