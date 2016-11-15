package com.example.mobile360.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.example.mobile360.db.dao.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public class BlackNumberService extends Service {
	private BlackNumberDao mDao;
	private InnerSmsReceiver mInnerSmsReceiver;
	private MyPhoneStateListener mPhoneStateListener;
	private MyContentObserver mContentObserver;
	private TelephonyManager mTM;

	@Override
	public void onCreate() {
		mDao = BlackNumberDao.getInstance(getApplicationContext());

		// 拦截短信
		IntentFilter intentFilter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(1000);

		mInnerSmsReceiver = new InnerSmsReceiver();
		registerReceiver(mInnerSmsReceiver, intentFilter);

		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}

	class MyPhoneStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:

				break;
			case TelephonyManager.CALL_STATE_RINGING:
				endCall(incomingNumber);
				break;

			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	class InnerSmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
				String originatingAddress = sms.getOriginatingAddress();
				String messageBody = sms.getMessageBody();
				int mode = mDao.getMode(originatingAddress);
				if (mode == 1 || mode == 3) {
					abortBroadcast();
				}
			}
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void endCall(String phone) {
		int mode = mDao.getMode(phone);
		if (mode == 2 || mode == 3) {
			try {
				Class<?> clazz = Class.forName("android.os.ServiceManager");
				Method method = clazz.getMethod("getService", String.class);
				IBinder iBinder = (IBinder) method.invoke(null,
						Context.TELEPHONY_SERVICE);
				ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
				iTelephony.endCall();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mContentObserver = new MyContentObserver(
					new Handler(), phone);
		}

	}

	class MyContentObserver extends ContentObserver {

		private String phone;

		public MyContentObserver(Handler handler, String phone) {
			super(handler);
			this.phone = phone;
		}

		@Override
		public void onChange(boolean selfChange) {
			getContentResolver().delete(Uri.parse("content://call_log/calls"),
					"number= ?", new String[] { phone });
			super.onChange(selfChange);
		}

	}

	@Override
	public void onDestroy() {
		if (mInnerSmsReceiver != null) {
			unregisterReceiver(mInnerSmsReceiver);
		}
		
		if (mContentObserver != null) {
			getContentResolver().unregisterContentObserver(mContentObserver);
		}
		
		if (mPhoneStateListener != null) {
			mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		super.onDestroy();
	}
}
