package com.example.mobile360.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.example.mobile360.utils.ConstantValue;
import com.example.mobile360.utils.SpUtil;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String simSerialNumber = manager.getSimSerialNumber() + "xxxx";
		String sim_number = SpUtil.getString(context, ConstantValue.SIM_NUMBER,
				"");
		if (!sim_number.equals(simSerialNumber)) {
			SmsManager smsManager = SmsManager.getDefault();
			String phone = SpUtil.getString(context, ConstantValue.CONTACT_PHONE, "");
			smsManager.sendTextMessage(phone, null, "sim change!!!", null,
					null);
		}
	}

}
