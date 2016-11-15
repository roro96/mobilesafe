package com.example.mobile360.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.mobile360.R;
import com.example.mobile360.bean.AppInfo;
import com.example.mobile360.engine.AppInfoProvider;
import com.example.mobile360.utils.ToastUtil;

public class AppManagerActivity extends Activity implements OnClickListener {

	private TextView tv_memory;
	private TextView tv_sd_memory;
	private ListView lv_app_list;
	private TextView tv_des;
	private List<AppInfo> mSystemList;
	private List<AppInfo> mCustomerList;
	private MyAdapter mAdapter;
	private AppInfo mAppInfo;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			mAdapter = new MyAdapter();
			lv_app_list.setAdapter(mAdapter);

			if (tv_des != null && mCustomerList != null) {
				tv_des.setText("用户应用(" + mCustomerList.size() + ")");
			}
		};
	};
	private PopupWindow mPopupWindow;

	class MyAdapter extends BaseAdapter {

		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == mCustomerList.size() + 1) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public int getCount() {
			return mSystemList.size() + mCustomerList.size() + 2;
		}

		@Override
		public AppInfo getItem(int position) {
			if (position == 0 || position == mCustomerList.size() + 1) {
				return null;
			} else {
				if (position < mCustomerList.size() + 1) {
					return mCustomerList.get(position - 1);
				} else {
					return mSystemList.get(position - mCustomerList.size() - 2);
				}
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			if (type == 0) {
				ViewTitleHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.listview_app_item_title, null);
					holder = new ViewTitleHolder();
					holder.tv_title = (TextView) convertView
							.findViewById(R.id.tv_title);
					convertView.setTag(holder);
				} else {
					holder = (ViewTitleHolder) convertView.getTag();
				}
				if (position == 0) {
					holder.tv_title.setText("用户应用(" + mCustomerList.size()
							+ ")");
				} else {
					holder.tv_title.setText("系统应用(" + mSystemList.size() + ")");
				}
				return convertView;
			} else {
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.listview_app_item, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_icon);
					holder.tv_name = (TextView) convertView
							.findViewById(R.id.tv_name);
					holder.tv_path = (TextView) convertView
							.findViewById(R.id.tv_path);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
				holder.tv_name.setText(getItem(position).name);
				if (getItem(position).isSdCard) {
					holder.tv_path.setText("sd卡应用");
				} else {
					holder.tv_path.setText("手机应用");
				}
				return convertView;
			}
		}

	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_path;
	}

	static class ViewTitleHolder {
		TextView tv_title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);

		initUI();
		initData();
	}

	private void initData() {
		initTitle();
		initList();
	}

	private void initList() {
		lv_app_list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (mCustomerList != null && mSystemList != null) {
					if (firstVisibleItem >= mCustomerList.size() + 1) {
						tv_des.setText("系统应用（" + mSystemList.size() + "）");
					} else {
						tv_des.setText("用户应用（" + mCustomerList.size() + "）");
					}
				}
			}
		});

		lv_app_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0 || position == mCustomerList.size() + 1) {
					return;
				} else {
					if (position < mCustomerList.size() + 1) {
						mAppInfo = mCustomerList.get(position - 1);
					} else {
						mAppInfo = mSystemList.get(position
								- mCustomerList.size() -2);
					}
					showPopupWindow(view);
				}
			}
		});
	}

	protected void showPopupWindow(View view) {
		View popupView = View.inflate(getApplicationContext(),
				R.layout.popupwindow_layout, null);
		TextView tv_uninstall = (TextView) popupView
				.findViewById(R.id.tv_uninstall);
		TextView tv_start = (TextView) popupView.findViewById(R.id.tv_start);
		TextView tv_share = (TextView) popupView.findViewById(R.id.tv_share);

		tv_uninstall.setOnClickListener(this);
		tv_start.setOnClickListener(this);
		tv_share.setOnClickListener(this);

		AlphaAnimation aa = new AlphaAnimation(0, 1);
		aa.setDuration(500);
		aa.setFillAfter(true);

		ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(500);
		sa.setFillAfter(true);

		AnimationSet set = new AnimationSet(true);
		set.addAnimation(aa);
		set.addAnimation(sa);

		mPopupWindow = new PopupWindow(popupView,
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable());
		mPopupWindow.showAsDropDown(view, 150, -view.getHeight());

		popupView.startAnimation(set);
	}

	/**
	 * ,获取磁盘、sd卡可用大小
	 */
	private void initTitle() {
		// 1,获取磁盘(内存,区分于手机运行内存)可用大小,磁盘路径
		String path = Environment.getDataDirectory().getAbsolutePath();
		// 2,获取sd卡可用大小,sd卡路径
		String sdPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		// 3,获取以上两个路径下文件夹的可用大小
		String memoryAvailSpace = Formatter.formatFileSize(
				getApplicationContext(), getAvailSpace(path));
		String sdMemoryAvailSpace = Formatter.formatFileSize(
				getApplicationContext(), getAvailSpace(sdPath));

		tv_memory.setText("内部存储可用：" + memoryAvailSpace);
		tv_sd_memory.setText("sd卡可用:" + sdMemoryAvailSpace);
	}

	@SuppressWarnings("deprecation")
	private long getAvailSpace(String path) {
		StatFs statFs = new StatFs(path);
		long count = statFs.getAvailableBlocks();
		long size = statFs.getBlockSize();
		return count * size;
	}

	private void initUI() {
		tv_memory = (TextView) findViewById(R.id.tv_memory);
		tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);
		lv_app_list = (ListView) findViewById(R.id.lv_app_list);
		tv_des = (TextView) findViewById(R.id.tv_des);
	}

	@Override
	protected void onResume() {
		getData();
		super.onResume();
	}

	private void getData() {
		new Thread() {
			public void run() {
				List<AppInfo> appInfoList = AppInfoProvider
						.getAppInfoList(getApplicationContext());
				mSystemList = new ArrayList<AppInfo>();
				mCustomerList = new ArrayList<AppInfo>();
				for (AppInfo appInfo : appInfoList) {
					if (appInfo.isSystem) {
						mSystemList.add(appInfo);
					} else {
						mCustomerList.add(appInfo);
					}
				}
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_uninstall:
			if (mAppInfo.isSystem) {
				ToastUtil.show(getApplicationContext(), "系统应用不可卸载");
			} else {
				Intent intent = new Intent("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:" + mAppInfo.getPackageName()));
				startActivity(intent);
			}
			break;
		case R.id.tv_start:
			PackageManager pm = getPackageManager();
			Intent launchIntentForPackage = pm
					.getLaunchIntentForPackage(mAppInfo.getPackageName());
			if (launchIntentForPackage != null) {
				startActivity(launchIntentForPackage);
			} else {
				ToastUtil.show(getApplicationContext(), "此应用不能被开启");
			}
			break;
		case R.id.tv_share:
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT,
					"分享一个应用,应用名称为" + mAppInfo.getName());
			intent.setType("text/plain");
			startActivity(intent);
			break;
		}
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}
}
