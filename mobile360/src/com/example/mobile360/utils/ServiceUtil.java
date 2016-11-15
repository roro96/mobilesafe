package com.example.mobile360.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {

	public static boolean isRunning(Context ctx, String serviceName){
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = am.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			if (serviceName.equals(runningServiceInfo.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
