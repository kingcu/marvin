package com.marvinmessaging;

import android.app.Service;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.database.Cursor;

public class ReceivedMessageService extends Service {
    
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Context context = this.getApplicationContext();

        /*
        MarvinDbAdapter dbAdapter = new MarvinDbAdapter(context);
        dbAdapter.open();

        String message = intent.getStringExtra("message");
        String number = intent.getStringExtra("number");
        String numberHash = CryptoHelper.encryptText(number);
        Cursor cursor = dbAdapter.getContactByNumber(numberHash);
        
        Toast arg = Toast.makeText(context, Integer.toString(cursor.getCount()), Toast.LENGTH_LONG);
        arg.show();
        if(cursor.getCount() > 0) {
            CharSequence key = CryptoHelper.decryptText(cursor.getString(
                        cursor.getColumnIndexOrThrow(MarvinDbAdapter.KEY_PUB_KEY)));
            CryptoHelper.genMessageCiphers(CryptoHelper.fromCharSeqToChars(key));

            CharSequence plaintext = CryptoHelper.decryptMessageText(message);
            Toast toast = Toast.makeText(context, "From: " + number + " -- " + (String)message, Toast.LENGTH_LONG);
            toast.show();
        }

        cursor.close();
        dbAdapter.close();
        */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //Not being used
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
