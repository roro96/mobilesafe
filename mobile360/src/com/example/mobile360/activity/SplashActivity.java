package com.example.mobile360.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.youmi.android.AdManager;

import org.apache.http.HttpException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobile360.R;
import com.example.mobile360.utils.ConstantValue;
import com.example.mobile360.utils.SpUtil;
import com.example.mobile360.utils.StreamUtil;
import com.example.mobile360.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class SplashActivity extends Activity {

	protected static final String tag = "SplashActivity";

	protected static final int UPDATE_VERSION = 0;

	protected static final int ENTER_HOME = 1;

	protected static final int URL_ERROR = 2;

	protected static final int IO_ERROR = 3;

	protected static final int JSON_ERROR = 4;

	private TextView tv_version_name;
	private int mLocalVersionCode;
	private String mDownloadUrl;
	private String mVersionDes;
	private RelativeLayout rl_root;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_VERSION:
				showDialog();
				break;
			case ENTER_HOME:
				// 进入应用程序主界面,activity跳转过程
				enterHome();
				break;
			case URL_ERROR:
				ToastUtil.show(getApplicationContext(), "url异常");
				enterHome();
				break;
			case IO_ERROR:
				ToastUtil.show(getApplicationContext(), "读取异常");
				enterHome();
				break;
			case JSON_ERROR:
				ToastUtil.show(getApplicationContext(), "json解析异常");
				enterHome();
				break;
			default:
				break;
			}
		};
	};

	private InputStream inputStream;

	private FileOutputStream fileOutputStream;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// 初始化UI
		initUI();
		// 初始化数据
		initData();

		initAnimation();

		initDB();

		AdManager.getInstance(this).init("2f2c31cc7b078bd0",
				"bbb521418b4757dd", true);

		if (SpUtil.getBoolean(getBaseContext(), ConstantValue.HAS_SHORTCUT,
				true)) {
			initShortCut();
		}
	}

	/**
	 * 生成快捷方式
	 */
	private void initShortCut() {
		Intent intent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory
				.decodeResource(getResources(), R.drawable.app_icon));
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");

		Intent shortCutIntent = new Intent("android.intent.action.HOME");
		shortCutIntent.addCategory("android.intent.category.DEFAULT");

		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
		sendBroadcast(intent);

		SpUtil.putBoolean(getApplicationContext(), ConstantValue.HAS_SHORTCUT,
				true);
	}

	private void initDB() {
		initMobileSafeDB("address.db");
		initMobileSafeDB("commonnum.db");
		initMobileSafeDB("antivirus.db");
	}

	private void initMobileSafeDB(String dbName) {
		File file = new File(getFilesDir(), dbName);
		if (file.exists()) {
			return;
		}
		try {
			inputStream = getAssets().open(dbName);
			fileOutputStream = new FileOutputStream(file);
			byte[] bs = new byte[1024];
			int len = -1;
			while ((len = inputStream.read(bs)) != -1) {
				fileOutputStream.write(bs, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null && fileOutputStream != null) {
				try {
					inputStream.close();
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void initAnimation() {
		AlphaAnimation aa = new AlphaAnimation(0, 1);
		aa.setDuration(3000);
		rl_root.startAnimation(aa);
	}

	private void initData() {
		// 应用版本号
		tv_version_name.setText("版本号：" + getVersionName());
		// 获取本地版本号
		mLocalVersionCode = getVersionCode();
		// 检测版本是否需要更新
		if (SpUtil.getBoolean(getApplicationContext(),
				ConstantValue.OPEN_UPDATE, false)) {
			checkVersion();
		} else {
			mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
		}
	}

	/**
	 * 检测版本号
	 */
	private void checkVersion() {
		new Thread(new Runnable() {

			Message msg = Message.obtain();

			@Override
			public void run() {
				try {
					URL url = new URL("http://10.0.2.2:8080/update.json");
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setReadTimeout(2000);
					connection.setConnectTimeout(2000);

					if (connection.getResponseCode() == 200) {
						InputStream is = connection.getInputStream();
						String json = StreamUtil.stream2String(is);
						Log.i(tag, json);

						JSONObject jo = new JSONObject();
						String versionName = jo.getString("versionName");
						String versionCode = jo.getString("versionCode");
						mVersionDes = jo.getString("versionDes");
						mDownloadUrl = jo.getString("downloadUrl");

						// 日志打印
						Log.i(tag, versionName);
						Log.i(tag, mVersionDes);
						Log.i(tag, versionCode);
						Log.i(tag, mDownloadUrl);

						if (mLocalVersionCode < Integer.parseInt(versionCode)) {
							msg.what = UPDATE_VERSION;
						} else {
							msg.what = ENTER_HOME;
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					msg.what = URL_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					msg.what = IO_ERROR;
				} catch (JSONException e) {
					e.printStackTrace();
					msg.what = JSON_ERROR;
				} finally {
					/*
					 * long startTime = System.currentTimeMillis(); long endTime
					 * = System.currentTimeMillis();
					 */
					/*
					 * if ((endTime - startTime) < 4000) {
					 * SystemClock.sleep(4000 - (endTime - startTime)); }
					 */

					mHandler.sendMessageDelayed(msg, 4000);
				}
			}
		}).start();
	}

	protected void showDialog() {
		// 弹出对话框,提示用户更新
		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("版本更新");
		// 设置描述内容
		builder.setMessage(mVersionDes);

		builder.setPositiveButton("立即更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				updateVersion();
			}
		});

		builder.setNegativeButton("稍后再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				enterHome();
			}
		});

		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
				dialog.dismiss();
			}
		});

		builder.show();

	}

	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	// 版本更新
	protected void updateVersion() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.pathSeparator + "mobilesafe.apk";
			HttpUtils httpUtils = new HttpUtils();
			httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {

				@Override
				public void onSuccess(ResponseInfo<File> info) {
					Log.i(tag, "下载成功");
					File file = info.result;
					installApk(file);
				}

				@Override
				public void onStart() {
					Log.i(tag, "刚刚开始下载");
					super.onStart();
				}

				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					Log.i(tag, "下载中........");
					Log.i(tag, "total = " + total);
					Log.i(tag, "current = " + current);
					super.onLoading(total, current, isUploading);
				}

				@Override
				public void onFailure(
						com.lidroid.xutils.exception.HttpException arg0,
						String arg1) {
					enterHome();
				}
			});
		}
	}

	/**
	 * 安装apk
	 * 
	 * @param file
	 */
	protected void installApk(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		enterHome();
		super.onActivityResult(arg0, arg1, arg2);
	}

	private void initUI() {
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
	}

	/**
	 * 获取版本号
	 * 
	 * @return
	 */
	private int getVersionCode() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取版本名称
	 * 
	 * @return
	 */
	private String getVersionName() {
		// 包管理者对象packageManager
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
