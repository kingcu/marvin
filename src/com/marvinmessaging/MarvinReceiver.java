package com.marvinmessaging;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.telephony.gsm.SmsMessage;
import android.widget.Toast;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;

public class MarvinReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		Object messages[] = (Object[])bundle.get("pdus");
		SmsMessage smsMessages[] = new SmsMessage[messages.length];
		for(int i = 0; i < messages.length; i++) {
			smsMessages[i] = SmsMessage.createFromPdu((byte[])messages[i]);

            String body = smsMessages[i].getMessageBody();
            if(body.length() > 3 && body.substring(0, 4).equals("?mm?")) {
                //Toast toast = Toast.makeText(context, "Received SMS: " + body.substring(4), Toast.LENGTH_LONG);
                //toast.show();

                /*
                Intent intent2 = new Intent();
                intent2.putExtra("message", body.substring(4));
                intent2.putExtra("number", smsMessages[i].getOriginatingAddress());
                intent2.setClassName("com.marvinmessaging", "com.marvinmessaging.ReceivedMessageService");
                context.startService(intent2);
                */

                NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                Intent msgIntent = new Intent(context, ViewMessage.class);
                //msgIntent.setClassName("com.marvinmessaging", "com.marvinmessaging.ViewMessage");
                msgIntent.putExtra("message", body.substring(4));
                msgIntent.putExtra("number", smsMessages[i].getOriginatingAddress());

                Notification notification = new Notification(
                        R.drawable.alert_dialog_icon, "New marvin encrypted message...", System.currentTimeMillis());

                CharSequence title = "Marvin Notification";
                CharSequence text = "Start Marvin to decrypt and view this message";
                PendingIntent notificationIntent = PendingIntent.getActivity(context, 0, msgIntent, 0);
                notification.setLatestEventInfo(context, title, text, notificationIntent);
                notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
                nm.notify(1, notification);
            }
		}
	}
}
