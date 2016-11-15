package com.example.mobile360.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mobile360.engine.ProcessInfoProvider;

public class KillProcessReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ProcessInfoProvider.killAll(context);
	}

}
