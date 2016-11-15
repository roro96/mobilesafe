package com.example.mobile360.activity;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mobile360.R;
import com.example.mobile360.utils.ConstantValue;
import com.example.mobile360.utils.Md5Util;
import com.example.mobile360.utils.SpUtil;
import com.example.mobile360.utils.ToastUtil;

public class HomeActivity extends Activity {

	private GridView gv_home;
	private String[] mTitleStrs;
	private int[] mDrawableIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		initUI();
		initData();
		
		// 实例化广告条
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);

		// 获取要嵌入广告条的布局
		LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout);

		// 将广告条加入到布局中
		adLayout.addView(adView);
	}

	private void initData() {
		// 准备数据(文字(9组),图片(9张))
		mTitleStrs = new String[] { "手机防盗", "通信卫士", "软件管理", "进程管理", "流量监控",
				"手机杀毒", "缓存清理", "高级工具", "设置中心" };
		mDrawableIds = new int[] { R.drawable.home_safe,
				R.drawable.home_callmsgsafe, R.drawable.home_apps,
				R.drawable.home_taskmanager, R.drawable.home_netmanager,
				R.drawable.home_trojan, R.drawable.home_sysoptimize,
				R.drawable.home_tools, R.drawable.home_settings };

		gv_home.setAdapter(new MyAdapter());

		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					showDialog();
					break;
				case 1:
					startActivity(new Intent(getApplicationContext(),
							BlackNumberActivity.class));
					break;
				case 2:
					startActivity(new Intent(getApplicationContext(),
							AppManagerActivity.class));
					break;
				case 3:
					startActivity(new Intent(getApplicationContext(),
							ProcessManagerActivity.class));
					break;
				case 4:
					startActivity(new Intent(getApplicationContext(),
							TrafficActivity.class));
					break;
				case 5:
					startActivity(new Intent(getApplicationContext(),
							AnitVirusActivity.class));
					break;
				case 6:
					startActivity(new Intent(getApplicationContext(),
							CacheClearActivity.class));
					break;
				case 7:
					startActivity(new Intent(getApplicationContext(),
							AToolActivity.class));
					break;
				case 8:
					startActivity(new Intent(getApplicationContext(),
							SettingActivity.class));
					break;
				default:
					break;
				}
			}
		});
	}

	protected void showDialog() {
		String pwd = SpUtil.getString(getApplicationContext(),
				ConstantValue.MOBILE_SAFE_PWD, "");
		if (TextUtils.isEmpty(pwd)) {
			// 初始设置密码对话框
			showSetPwdDialog();
		} else {
			// 确认密码对话框
			showConfirmPwdDialog();
		}
	}

	/**
	 * 确认密码对话框
	 */
	private void showConfirmPwdDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		final View view = View.inflate(this, R.layout.dialog_confirm_pwd, null);
		// 对话框低版本展示样式,兼容性的处理
		dialog.setView(view, 0, 0, 0, 0);// 设置对话框样式的时候,不需要内边距
		dialog.show();

		Button btn_submit = (Button) view.findViewById(R.id.btn_submit);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				EditText et_confirm_pwd = (EditText) view
						.findViewById(R.id.et_confirm_pwd);

				String confirmPwd = et_confirm_pwd.getText().toString().trim();

				if (!TextUtils.isEmpty(confirmPwd)) {
					String pwd = SpUtil.getString(getApplicationContext(),
							ConstantValue.MOBILE_SAFE_PWD, "");
					if (pwd.equals(Md5Util.encoder(confirmPwd))) {
						Intent intent = new Intent(getApplicationContext(),
								SetupOverActivity.class);
						startActivity(intent);
						dialog.dismiss();
						SpUtil.putString(getApplicationContext(),
								ConstantValue.MOBILE_SAFE_PWD, pwd);
					} else {
						ToastUtil.show(getApplicationContext(), "密码错误，请重新输入");
					}
				} else {
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}
			}
		});

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 设置密码对话框
	 */
	private void showSetPwdDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		final View view = View.inflate(this, R.layout.dialog_set_pwd, null);
		// 对话框低版本展示样式,兼容性的处理
		dialog.setView(view, 0, 0, 0, 0);// 设置对话框样式的时候,不需要内边距
		dialog.show();

		Button btn_submit = (Button) view.findViewById(R.id.btn_submit);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText et_set_pwd = (EditText) view
						.findViewById(R.id.et_set_pwd);
				EditText et_confirm_pwd = (EditText) view
						.findViewById(R.id.et_confirm_pwd);

				String setPwd = et_set_pwd.getText().toString().trim();
				String confirmPwd = et_confirm_pwd.getText().toString().trim();

				if (!TextUtils.isEmpty(confirmPwd)
						&& !TextUtils.isEmpty(setPwd)) {
					if (confirmPwd.equals(setPwd)) {
						Intent intent = new Intent(getApplicationContext(),
								SetupOverActivity.class);
						startActivity(intent);
						dialog.dismiss();
						SpUtil.putString(getApplicationContext(),
								ConstantValue.MOBILE_SAFE_PWD,
								Md5Util.encoder(setPwd));
					} else {
						ToastUtil.show(getApplicationContext(), "密码错误，请重新输入");
					}
				} else {
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}
			}
		});

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mTitleStrs.length;
		}

		@Override
		public Object getItem(int position) {
			return mTitleStrs[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView != null) {
				view = convertView;
			} else {
				view = View.inflate(getApplicationContext(), R.layout.gv_item,
						null);
			}
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);

			iv_item.setBackgroundResource(mDrawableIds[position]);
			tv_item.setText(mTitleStrs[position]);

			return view;
		}
	}

	private void initUI() {
		gv_home = (GridView) findViewById(R.id.gv_home);
	}
}
