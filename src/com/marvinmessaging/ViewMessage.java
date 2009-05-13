package com.marvinmessaging;

import java.util.Date;
import java.lang.Long;

import android.util.Log;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import android.database.Cursor;

public class ViewMessage extends Activity {

    private MarvinApplication mApp;
    private MarvinDbAdapter mDbAdapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApp = (MarvinApplication)getApplication();

        long now = (new Date()).getTime();
        if(mApp.unlockPassword == null || (now - mApp.lastActivity) > 60000)
            mApp.requestPassword(this);
        mApp.lastActivity = (new Date()).getTime();

		mDbAdapter = new MarvinDbAdapter(this);
		mDbAdapter.open();
        
        Intent intent = getIntent();
        String ciphertext = intent.getStringExtra("message");
        String number = intent.getStringExtra("number");

        Log.i("marvin", number);

        setContentView(R.layout.view_message);
        mDbAdapter.close();
    }

    @Override protected void onResume() {
        super.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
    }
}
