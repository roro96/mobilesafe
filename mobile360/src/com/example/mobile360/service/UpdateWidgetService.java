package com.example.mobile360.service;

import java.util.Timer;
import java.util.TimerTask;

import com.example.mobile360.R;
import com.example.mobile360.engine.ProcessInfoProvider;
import com.example.mobile360.receiver.MyAppWidgetProvider;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {
	protected static final String tag = "UpdateWidgetService";
	private InnerReceiver mInnerReceiver;
	private Timer mTimer;

	@Override
	public void onCreate() {
		// 管理进程总数和可用内存数更新(定时器)
		startTimer();

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);

		mInnerReceiver = new InnerReceiver();
		registerReceiver(mInnerReceiver, filter);

		super.onCreate();
	}

	class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context ctx, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				startTimer();
			} else {
				cancelTimerTask();
			}
		}
	}

	private void startTimer() {
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				updateAppWeight();
				Log.i(tag, "5秒一次的定时任务现在正在运行..........");
			}
		}, 0, 500);
	}

	protected void updateAppWeight() {
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(getApplicationContext());
		RemoteViews remoteViews = new RemoteViews(getPackageName(),
				R.layout.process_widget);
		remoteViews.setTextViewText(R.id.tv_process_count, "进程总数:"
				+ ProcessInfoProvider.getProcessCount(this));
		String strAvailSpace = Formatter.formatFileSize(
				getApplicationContext(),
				ProcessInfoProvider.getAvailSpace(getApplicationContext()));
		remoteViews.setTextViewText(R.id.tv_process_memory, "可用内存:"
				+ strAvailSpace);

		//点击窗体小部件,进入应用
		Intent intent = new Intent("android.intent.action.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);

		// 通过延期意图发送广播,在广播接受者中杀死进程,匹配规则看action
		Intent broadCastintent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
		PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, broadCastintent, PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.btn_clear,broadcast);

		ComponentName componentName = new ComponentName(this,
				MyAppWidgetProvider.class);
		appWidgetManager.updateAppWidget(componentName, remoteViews);
	}

	public void cancelTimerTask() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onDestroy() {
		if (mInnerReceiver != null) {
			unregisterReceiver(mInnerReceiver);
		}
		cancelTimerTask();
		super.onDestroy();
	}
}
