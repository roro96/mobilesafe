package com.example.mobile360.receiver;

import com.example.mobile360.service.UpdateWidgetService;
import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MyAppWidgetProvider extends AppWidgetProvider {

	private static final String tag = "MyAppWidgetProvider";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(tag, "onReceive.............................");
		super.onReceive(context, intent);
	}

	@Override
	public void onEnabled(Context context) {
		Log.i(tag, "onEnabled 创建第一个窗体小部件调用方法");
		context.startService(new Intent(context, UpdateWidgetService.class));
		super.onEnabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.i(tag, "onUpdate 创建多一个窗体小部件调用方法");
		context.startService(new Intent(context, UpdateWidgetService.class));
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@SuppressLint("NewApi")
	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		Log.i(tag, "onAppWidgetOptionsChanged 创建多一个窗体小部件调用方法");
		context.startService(new Intent(context, UpdateWidgetService.class));
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.i(tag, "onDeleted 删除一个窗体小部件调用方法");
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		Log.i(tag, "onDisabled 删除最后一个窗体小部件调用方法");
		context.stopService(new Intent(context, UpdateWidgetService.class));
		super.onDisabled(context);
	}

}
