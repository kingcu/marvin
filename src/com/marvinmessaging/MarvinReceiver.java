package com.marvinmessaging;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.telephony.gsm.SmsMessage;
import android.widget.Toast;
import android.os.Bundle;
import android.content.Context;

public class MarvinReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		Object messages[] = (Object[])bundle.get("pdus");
		SmsMessage smsMessages[] = new SmsMessage[messages.length];
		for(int i = 0; i < messages.length; i++) {
			smsMessages[i] = SmsMessage.createFromPdu((byte[])messages[i]);
		}

		Toast toast = Toast.makeText(context, "Received SMS: " + smsMessages[0].getMessageBody(), Toast.LENGTH_LONG);
		toast.show();
	}
}
