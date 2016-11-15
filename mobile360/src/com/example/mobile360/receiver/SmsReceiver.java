package com.example.mobile360.receiver;

import com.example.mobile360.R;
import com.example.mobile360.service.LocationService;
import com.example.mobile360.utils.ConstantValue;
import com.example.mobile360.utils.SpUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean open_security = SpUtil.getBoolean(context,
				ConstantValue.OPEN_SECURITY, false);
		if (open_security) {
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				SmsMessage smsMessage = SmsMessage
						.createFromPdu((byte[]) object);
				String address = smsMessage.getOriginatingAddress();
				String messageBody = smsMessage.getMessageBody();
				if (messageBody.contains("#*alarm*#")) {
					MediaPlayer mediaPlayer = MediaPlayer.create(context,
							R.raw.smap);
					mediaPlayer.setLooping(true);
					mediaPlayer.start();
				}
				if (messageBody.contains("#*location*#")) {
					Intent intent2 = new Intent(context, LocationService.class);
					context.startService(intent2);
				}
				if (messageBody.contains("#*lockscrenn*#")) {

				}
				if (messageBody.contains("#*wipedate*#")) {

				}
			}
		}
	}

}
