package com.example.mobile360.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.mobile360.R;
import com.example.mobile360.service.AddressService;
import com.example.mobile360.service.BlackNumberService;
import com.example.mobile360.service.WatchDogService;
import com.example.mobile360.utils.ConstantValue;
import com.example.mobile360.utils.ServiceUtil;
import com.example.mobile360.utils.SpUtil;
import com.example.mobile360.view.SettingClickView;
import com.example.mobile360.view.SettingItemView;

public class SettingActivity extends Activity {

	private SettingItemView siv_update;
	private SettingItemView siv_address;
	private String[] toastStyles;
	private SettingClickView scv_toast_style;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		initUpdate();
		initAddress();
		initToastStyle();
		initLocation();
		initBlacknumber();
		initAppLock();
	}

	

	private void initAppLock() {
		final SettingItemView siv_app_lock = (SettingItemView) findViewById(R.id.siv_app_lock);
		boolean isRunning = ServiceUtil.isRunning(this, "com.example.mobile360.service.WatchDogService");
		siv_app_lock.setCheck(isRunning);
		
		siv_app_lock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isCheck = siv_app_lock.isCheck();
				siv_app_lock.setCheck(!isCheck);
				if(!isCheck){
					//开启服务
					startService(new Intent(getApplicationContext(), WatchDogService.class));
				}else{
					//关闭服务
					stopService(new Intent(getApplicationContext(), WatchDogService.class));
				}
			}
		});
	}



	private void initBlacknumber() {
		final SettingItemView siv_blacknumber = (SettingItemView) findViewById(R.id.siv_blacknumber);
		boolean isRunning = ServiceUtil.isRunning(getApplicationContext(), "com.example.mobile360.service.BlackNumberService");
		siv_blacknumber.setCheck(isRunning);
		siv_blacknumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isCheck = siv_blacknumber.isCheck();
				siv_blacknumber.setCheck(!isCheck);
				if (!isCheck) {
					startService(new Intent(getApplicationContext(), BlackNumberService.class));
				}else {
					stopService(new Intent(getApplicationContext(), BlackNumberService.class));
				}
			}
		});
	}



	private void initLocation() {
		SettingClickView scv_toast_location = (SettingClickView) findViewById(R.id.scv_toast_location);
		scv_toast_location.setTitle("归属地提示框的位置");
		scv_toast_location.setDes("设置归属地提示框的位置");
		scv_toast_location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						ToastLocationActivity.class));
			}
		});
	}

	private void initToastStyle() {
		scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
		scv_toast_style.setTitle("设置归属地提示框风格");
		toastStyles = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
		int style = SpUtil.getInt(this, ConstantValue.TOAST_STYLE, 0);
		scv_toast_style.setDes(toastStyles[style]);

		scv_toast_style.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showToastStyleDialog();
			}
		});
	}

	protected void showToastStyleDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("归属地提示框风格");
		builder.setIcon(R.drawable.app_icon);
		int style = SpUtil.getInt(getApplicationContext(),
				ConstantValue.TOAST_STYLE, 0);
		builder.setSingleChoiceItems(toastStyles, style,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SpUtil.putInt(getApplicationContext(),
								ConstantValue.TOAST_STYLE, which);
						dialog.dismiss();
						scv_toast_style.setDes(toastStyles[which]);
					}
				});

		builder.setNegativeButton("取消", null);
		builder.show();
	}

	private void initAddress() {
		siv_address = (SettingItemView) findViewById(R.id.siv_address);
		boolean running = ServiceUtil.isRunning(getApplicationContext(),
				"com.example.mobile360.service.AddressService");
		siv_address.setCheck(running);
		siv_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isCheck = siv_address.isCheck();
				siv_address.setCheck(!isCheck);
				if (!isCheck) {
					Intent intent = new Intent(getApplicationContext(),
							AddressService.class);
					startService(intent);
				} else {
					stopService(new Intent(getApplicationContext(),
							AddressService.class));
				}
			}
		});
	}

	/**
	 * 自动更新
	 */
	private void initUpdate() {
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		boolean open_update = SpUtil.getBoolean(getApplicationContext(),
				ConstantValue.OPEN_UPDATE, false);
		siv_update.setCheck(open_update);
		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isCheck = siv_update.isCheck();
				siv_update.setCheck(!isCheck);
				SpUtil.putBoolean(getApplicationContext(),
						ConstantValue.OPEN_UPDATE, !isCheck);
			}
		});
	}
}
