package com.example.mobile360.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.mobile360.R;
import com.example.mobile360.service.LockScreenService;
import com.example.mobile360.utils.ConstantValue;
import com.example.mobile360.utils.ServiceUtil;
import com.example.mobile360.utils.SpUtil;

public class ProcessSettingActivity extends Activity {

	private CheckBox cb_show_system, cb_lock_clear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);

		initSystemShow();
		initLockScreenClear();
	}

	private void initLockScreenClear() {
		cb_lock_clear = (CheckBox) findViewById(R.id.cb_lock_clear);
		boolean isRunning = ServiceUtil.isRunning(getApplicationContext(), "com.example.mobile360.service.LockScreenService");
		if (isRunning) {
			cb_lock_clear.setText("锁屏清理已开启");
		}else{
			cb_lock_clear.setText("锁屏清理已关闭");
		}
		cb_lock_clear.setChecked(isRunning);
		
		cb_lock_clear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_lock_clear.setText("锁屏清理已开启");
					startService(new Intent(getApplicationContext(), LockScreenService.class));
				}else {
					cb_lock_clear.setText("锁屏清理已关闭");
					stopService(new Intent(getApplicationContext(), LockScreenService.class));
				}
			}
		});
	}

	private void initSystemShow() {
		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		boolean showSystem = SpUtil.getBoolean(getApplicationContext(),
				ConstantValue.SHOW_SYSTEM, false);
		cb_show_system.setChecked(showSystem);
		if (showSystem) {
			cb_show_system.setText("显示系统进程");
		} else {
			cb_show_system.setText("隐藏系统进程");
		}

		cb_show_system
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							cb_show_system.setText("显示系统进程");
						} else {
							cb_show_system.setText("隐藏系统进程");
						}
						SpUtil.putBoolean(getApplicationContext(),
								ConstantValue.SHOW_SYSTEM, isChecked);
					}
				});
	}
}
