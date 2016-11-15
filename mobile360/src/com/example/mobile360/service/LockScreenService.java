package com.example.mobile360.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.example.mobile360.engine.ProcessInfoProvider;

public class LockScreenService extends Service {
	
	private InnerReceiver innerReceiver;

	@Override
	public void onCreate() {
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, filter);
		super.onCreate();
	}

	class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			ProcessInfoProvider.killAll(context);
		}
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		if (innerReceiver != null) {
			unregisterReceiver(innerReceiver);
		}
		super.onDestroy();
	}

}
