package com.example.mobile360.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.mobile360.R;
import com.example.mobile360.db.dao.AddressDao;
import com.example.mobile360.utils.ConstantValue;
import com.example.mobile360.utils.SpUtil;

public class AddressService extends Service {

	private TelephonyManager mTM;
	private MyPhoneStateListener myPhoneStateListener;
	private WindowManager mWM;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private View mViewToast;
	private TextView tv_toast;
	private String mAddress;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			tv_toast.setText(mAddress);
		};
	};
	private int mScreenWidth;
	private int mScreenHeight;
	private WindowManager.LayoutParams params;
	private InnerOutCallReceiver innerOutCallReceiver;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		
		innerOutCallReceiver = new InnerOutCallReceiver();
		registerReceiver(innerOutCallReceiver, intentFilter);
		
		super.onCreate();
	}

	class InnerOutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String phone = getResultData();
			showToast(phone);
		}
		
	}
	
	class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				showToast(incomingNumber);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (mWM != null && mViewToast != null) {
					mWM.removeView(mViewToast);
				}
				break;
			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void showToast(String incomingNumber) {
		params = mParams;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.setTitle("Toast");
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		params.gravity = Gravity.LEFT + Gravity.TOP;

		mViewToast = View.inflate(getApplicationContext(), R.layout.toast_view,
				null);
		tv_toast = (TextView) mViewToast.findViewById(R.id.tv_toast);

		tv_toast.setOnTouchListener(new OnTouchListener() {

			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					int dx = endX - startX;
					int dy = endY - startY;

					params.x = params.x + dx;
					params.y = params.y + dy;

					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y = 0;
					}
					if (params.x > mScreenWidth - mViewToast.getWidth()) {
						params.x = mScreenWidth - mViewToast.getWidth();
					}
					if (params.y > mScreenHeight - mViewToast.getHeight() - 22) {
						params.y = mScreenHeight - mViewToast.getHeight() - 22;
					}

					mWM.updateViewLayout(mViewToast, params);

					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_X, params.x);
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_Y, params.y);
					break;
				default:
					break;
				}
				return true;
			}
		});

		int[] mDrawableIds = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		int toastDrawable = SpUtil.getInt(getApplicationContext(),
				ConstantValue.TOAST_STYLE, 0);
		tv_toast.setBackgroundResource(mDrawableIds[toastDrawable]);

		params.x = SpUtil.getInt(getApplicationContext(),
				ConstantValue.LOCATION_X, 0);
		params.y = SpUtil.getInt(getApplicationContext(),
				ConstantValue.LOCATION_Y, 0);

		mWM.addView(mViewToast, params);

		query(incomingNumber);
	}

	private void query(final String incomingNumber) {
		new Thread() {
			public void run() {
				mAddress = AddressDao.getAddress(incomingNumber);
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	@Override
	public void onDestroy() {
		if (mTM != null && myPhoneStateListener != null) {
			mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		if (innerOutCallReceiver != null) {
			unregisterReceiver(innerOutCallReceiver);
		}
		super.onDestroy();
	}
}
