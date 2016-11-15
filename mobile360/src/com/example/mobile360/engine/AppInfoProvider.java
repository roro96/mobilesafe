package com.example.mobile360.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.mobile360.bean.AppInfo;

public class AppInfoProvider {

	public static List<AppInfo> getAppInfoList(Context context) {
		// 包的管理者对象
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
		List<AppInfo> appInfoList = new ArrayList<AppInfo>();

		for (PackageInfo packageInfo : packageInfoList) {
			AppInfo appInfo = new AppInfo();
			appInfo.packageName = packageInfo.packageName;
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			appInfo.icon = applicationInfo.loadIcon(pm);
			appInfo.name = applicationInfo.loadLabel(pm).toString();

			if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				appInfo.isSystem = true;
			} else {
				appInfo.isSystem = false;
			}

			if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
				appInfo.isSdCard = true;
			} else {
				appInfo.isSdCard = false;
			}
			appInfoList.add(appInfo);
		}
		return appInfoList;
	}
}
