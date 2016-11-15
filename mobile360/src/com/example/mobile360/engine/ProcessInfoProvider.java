package com.example.mobile360.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.example.mobile360.R;
import com.example.mobile360.bean.ProcessInfo;

public class ProcessInfoProvider {

	private static BufferedReader bufferedReader;

	public static int getProcessCount(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		return runningAppProcesses.size();
	}

	/**
	 * @param context
	 * @return 可用的内存数
	 */
	public static long getAvailSpace(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}

	public static long getTotalSpace(Context context) {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader("proc/meminfo");
			bufferedReader = new BufferedReader(fileReader);
			String readLine = bufferedReader.readLine();
			char[] charArray = readLine.toCharArray();
			StringBuffer stringBuffer = new StringBuffer();
			for (char c : charArray) {
				if (c >= '0' && c <= '9') {
					stringBuffer.append(c);
				}
			}
			return Long.parseLong(stringBuffer.toString()) * 1024;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileReader != null && bufferedReader != null) {
				try {
					fileReader.close();
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

	public static List<ProcessInfo> getProcessInfos(Context context) {
		List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		for (RunningAppProcessInfo info : runningAppProcesses) {
			ProcessInfo processInfo = new ProcessInfo();
			processInfo.packageName = info.processName;
			android.os.Debug.MemoryInfo[] processMemoryInfo = am
					.getProcessMemoryInfo(new int[] { info.pid });
			android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
			processInfo.memSize = memoryInfo.getTotalPrivateDirty() * 1024;

			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(
						processInfo.packageName, 0);
				processInfo.name = applicationInfo.loadLabel(pm).toString();
				processInfo.icon = applicationInfo.loadIcon(pm);

				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					processInfo.isSystem = true;
				} else {
					processInfo.isSystem = false;
				}
			} catch (NameNotFoundException e) {
				processInfo.name = info.processName;
				processInfo.icon = context.getResources().getDrawable(
						R.drawable.ic_launcher);
				processInfo.isSystem = true;
				e.printStackTrace();
			}
			processInfoList.add(processInfo);
		}
		return processInfoList;
	}

	public static void killProcess(Context context, ProcessInfo processInfo) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(processInfo.packageName);
	}
	
	public static void killAll(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		for (RunningAppProcessInfo info : runningAppProcesses) {
			if (info.processName.equals(context.getPackageName())) {
				continue;
			}
			am.killBackgroundProcesses(info.processName);
		}
	}

}
