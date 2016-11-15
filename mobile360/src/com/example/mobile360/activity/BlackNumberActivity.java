package com.example.mobile360.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.mobile360.R;
import com.example.mobile360.bean.BlackNumberInfo;
import com.example.mobile360.db.dao.BlackNumberDao;
import com.example.mobile360.utils.ToastUtil;

public class BlackNumberActivity extends Activity {
	private Button btn_add;
	private ListView lv_blacknumber;
	private List<BlackNumberInfo> list;
	private BlackNumberDao dao;
	private int mode = 1;
	private MyAdapter mAdapter;
	private boolean mIsLoad = false;
	private int mCount;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (mAdapter == null) {
				mAdapter = new MyAdapter();
				lv_blacknumber.setAdapter(mAdapter);
			}else {
				mAdapter.notifyDataSetChanged();
			}
		};
	};

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.listview_blacknumber_item, null);
				viewHolder = new ViewHolder();
				viewHolder.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_phone);
				viewHolder.tv_mode = (TextView) convertView
						.findViewById(R.id.tv_mode);
				viewHolder.iv_delete = (ImageView) convertView
						.findViewById(R.id.iv_delete);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dao.delete(list.get(position).phone);
					list.remove(position);
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}
				}
			});
			viewHolder.tv_phone.setText(list.get(position).phone);
			int mode = Integer.parseInt(list.get(position).mode);
			switch (mode) {
			case 1:
				viewHolder.tv_mode.setText("拦截短信");
				break;
			case 2:
				viewHolder.tv_mode.setText("拦截电话");
				break;
			case 3:
				viewHolder.tv_mode.setText("拦截所有");
				break;
			default:
				break;
			}
			return convertView;
		}

	}

	static class ViewHolder {
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber);

		initUI();
		initData();
	}

	private void initData() {
		new Thread() {
			public void run() {
				dao = BlackNumberDao.getInstance(getApplicationContext());
				list = dao.find(0);
				mCount = dao.getCount();
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initUI() {
		btn_add = (Button) findViewById(R.id.btn_add);
		lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);

		btn_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		lv_blacknumber.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (list != null) {
					if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
							&& lv_blacknumber.getLastVisiblePosition() >= list
									.size() - 1 && !mIsLoad) {
						if (mCount > list.size()) {
							new Thread() {
								public void run() {
									dao = BlackNumberDao
											.getInstance(getApplicationContext());
									List<BlackNumberInfo> find = dao.find(list
											.size());
									list.addAll(find);
									mHandler.sendEmptyMessage(0);
								};
							}.start();
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	protected void showDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(getApplicationContext(),
				R.layout.dialog_add_blacknumber, null);
		dialog.setView(view, 0, 0, 0, 0);
		final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);

		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_sms:
					mode = 1;
					break;
				case R.id.rb_phone:
					mode = 2;
					break;
				case R.id.rb_all:
					mode = 3;
					break;
				default:
					break;
				}
			}
		});
		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phone = et_phone.getText().toString();
				if (!TextUtils.isEmpty(phone)) {
					dao.insert(phone, mode + "");
					BlackNumberInfo info = new BlackNumberInfo();
					info.phone = phone;
					info.mode = mode + "";
					list.add(0, info);
					if (mAdapter != null) {
						mAdapter.notifyDataSetChanged();
					}
					dialog.dismiss();
				} else {
					ToastUtil.show(getApplicationContext(), "请输入拦截号码");
				}
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
}
