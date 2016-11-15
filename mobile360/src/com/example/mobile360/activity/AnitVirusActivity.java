package com.example.mobile360.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mobile360.R;
import com.example.mobile360.db.dao.VirusDao;
import com.example.mobile360.utils.Md5Util;

public class AnitVirusActivity extends Activity {
	protected static final int SCANING = 0;
	protected static final int SCAN_FINISH = 1;
	private ImageView iv_scanning;
	private TextView tv_name;
	private ProgressBar pb_bar;
	private LinearLayout ll_add_text;
	int index = 0;
	private List<ScanInfo> mVirusScanInfoList;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANING:
				ScanInfo scanInfo = (ScanInfo) msg.obj;
				tv_name.setText(scanInfo.name);
				TextView textView = new TextView(getApplicationContext());
				if (scanInfo.isVirus) {
					textView.setTextColor(Color.RED);
					textView.setText("发现病毒:" + scanInfo.name);
				} else {
					textView.setTextColor(Color.BLACK);
					textView.setText("扫描安全:" + scanInfo.name);
				}
				ll_add_text.addView(textView, 0);
				break;
			case SCAN_FINISH:
				tv_name.setText("扫描完成");
				iv_scanning.clearAnimation();
				// 告知用户卸载包含了病毒的应用
				unInstallVirus();
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anit_virus);

		initUI();
		initAnimation();
		checkVirus();
	}

	protected void unInstallVirus() {
		for (ScanInfo info : mVirusScanInfoList) {
			String packageName = info.packageName;
			Intent intent = new Intent("android.intent.action.DELETE");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:" + packageName));
			startActivity(intent);
		}
	}

	private void checkVirus() {
		new Thread() {
			public void run() {
				List<String> virusList = VirusDao.getVirus();
				PackageManager pm = getPackageManager();
				// 获取所有应用程序签名文件和卸载完了的应用,残余的文件
				List<PackageInfo> installedPackageList = pm
						.getInstalledPackages(PackageManager.GET_SIGNATURES
								+ PackageManager.GET_UNINSTALLED_PACKAGES);

				mVirusScanInfoList = new ArrayList<ScanInfo>();
				// 所有应用的集合
				List<ScanInfo> scanInfoList = new ArrayList<ScanInfo>();

				// 设置进度条的最大值
				pb_bar.setMax(installedPackageList.size());

				for (PackageInfo packageInfo : installedPackageList) {
					ScanInfo scanInfo = new ScanInfo();
					// 获取签名文件的数组
					Signature[] signatures = packageInfo.signatures;
					Signature signature = signatures[0];
					String string = signature.toCharsString();
					String encoder = Md5Util.encoder(string);
					if (virusList.contains(encoder)) {
						scanInfo.isVirus = true;
						mVirusScanInfoList.add(scanInfo);
					} else {
						scanInfo.isVirus = false;
					}
					scanInfo.packageName = packageInfo.packageName;
					scanInfo.name = packageInfo.applicationInfo.loadLabel(pm)
							.toString();
					scanInfoList.add(scanInfo);

					index++;
					pb_bar.setProgress(index);

					SystemClock.sleep(50 + new Random().nextInt(100));

					Message msg = Message.obtain();
					msg.what = SCANING;
					msg.obj = scanInfo;
					mHandler.sendMessage(msg);
				}
				Message msg = Message.obtain();
				msg.what = SCAN_FINISH;
				mHandler.sendMessage(msg);
			};
		}.start();
	}

	class ScanInfo {
		public boolean isVirus;
		public String packageName;
		public String name;
	}

	private void initAnimation() {
		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setDuration(1000);
		ra.setRepeatCount(RotateAnimation.INFINITE);
		ra.setFillAfter(true);
		iv_scanning.startAnimation(ra);
	}

	private void initUI() {
		iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
		tv_name = (TextView) findViewById(R.id.tv_name);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
	}
}
