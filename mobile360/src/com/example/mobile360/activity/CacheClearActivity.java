package com.example.mobile360.activity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mobile360.R;

public class CacheClearActivity extends Activity {

	protected static final int CHECK_CACHE_APP = 1;
	protected static final int CHECK_FINISH = 2;
	protected static final int UPDATE_CACHE_APP = 3;
	protected static final int CLEAR_CACHE = 4;
	private Button bt_clear;
	private ProgressBar pb_bar;
	private TextView tv_name;
	private LinearLayout ll_add_text;
	int mIndex = 0;
	private String name;
	private PackageManager mPm;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHECK_CACHE_APP:
				tv_name.setText((CharSequence) msg.obj);
				break;
			case CHECK_FINISH:
				tv_name.setText("扫描完成");
				break;
			case UPDATE_CACHE_APP:
				// 在线性布局中添加有缓存应用条目
				View view = View.inflate(getApplicationContext(),
						R.layout.linearlayout_cache_item, null);
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_item_name = (TextView) view
						.findViewById(R.id.tv_name);
				TextView tv_memory_info = (TextView) view
						.findViewById(R.id.tv_memory_info);
				ImageView iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);

				final CacheInfo cacheInfo = (CacheInfo) msg.obj;
				iv_icon.setBackgroundDrawable(cacheInfo.icon);
				tv_item_name.setText(cacheInfo.name);
				tv_memory_info.setText(Formatter.formatFileSize(
						getApplicationContext(), cacheInfo.cacheSize));
				
				ll_add_text.addView(view, 0);
				
				iv_delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.setData(Uri.parse("package:"+cacheInfo.packagename));
						startActivity(intent);
					}
				});
				break;
			case CLEAR_CACHE:
				// 从线性布局中移除所有的条目
				ll_add_text.removeAllViews();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_clear);
		initUI();
		initData();
	}

	private void initData() {
		new Thread() {
			public void run() {
				mPm = getPackageManager();
				List<PackageInfo> installedPackages = mPm
						.getInstalledPackages(0);
				pb_bar.setMax(installedPackages.size());
				for (PackageInfo packageInfo : installedPackages) {
					String packageName = packageInfo.packageName;
					getPackageCache(packageName);

					SystemClock.sleep(100 + new Random().nextInt(50));

					mIndex++;
					pb_bar.setProgress(mIndex);

					Message msg = Message.obtain();
					msg.what = CHECK_CACHE_APP;
					try {
						name = mPm.getApplicationInfo(packageName, 0)
								.loadLabel(mPm).toString();
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					msg.obj = name;
					mHandler.sendMessage(msg);
				}
				Message msg = Message.obtain();
				msg.what = CHECK_FINISH;
				mHandler.sendMessage(msg);
			};
		}.start();
	}

	protected void getPackageCache(String packageName) {
		IPackageStatsObserver.Stub stub = new IPackageStatsObserver.Stub() {

			private CacheInfo cacheInfo;

			@Override
			public void onGetStatsCompleted(PackageStats pStats,
					boolean succeeded) throws RemoteException {
				long cacheSize = pStats.cacheSize;
				if (cacheSize > 0) {
					Message msg = Message.obtain();
					msg.what = UPDATE_CACHE_APP;
					try {
						cacheInfo = new CacheInfo();
						cacheInfo.cacheSize = cacheSize;
						cacheInfo.packagename = pStats.packageName;
						cacheInfo.icon = mPm.getApplicationInfo(
								pStats.packageName, 0).loadIcon(mPm);
						cacheInfo.name = mPm
								.getApplicationInfo(pStats.packageName, 0)
								.loadLabel(mPm).toString();
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					msg.obj = cacheInfo;
					mHandler.sendMessage(msg);
				}
			}
		};
		try {
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			Method method = clazz.getMethod("getPackageSizeInfo", String.class,
					IPackageStatsObserver.class);
			method.invoke(mPm, packageName, stub);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class CacheInfo {
		public String name;
		public Drawable icon;
		public String packagename;
		public long cacheSize;
	}

	private void initUI() {
		bt_clear = (Button) findViewById(R.id.bt_clear);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		tv_name = (TextView) findViewById(R.id.tv_name);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);

		bt_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					Class<?> clazz = Class
							.forName("android.content.pm.PackageManager");
					Method method = clazz.getMethod("freeStorageAndNotify",
							long.class, IPackageDataObserver.class);
					method.invoke(mPm, Long.MAX_VALUE,
							new IPackageDataObserver.Stub() {

								@Override
								public void onRemoveCompleted(
										String packageName, boolean succeeded)
										throws RemoteException {
									Message msg = Message.obtain();
									msg.what = CLEAR_CACHE;
									mHandler.sendMessage(msg);
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
