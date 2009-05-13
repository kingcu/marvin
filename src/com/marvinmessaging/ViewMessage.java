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
import android.content.SharedPreferences;

public class ViewMessage extends Activity {

    private MarvinApplication mApp;
    private MarvinDbAdapter mDbAdapter;
    private TextView mMessageNumText;
    private TextView mMessageBodyText;
    private TextView mMessageNameText;
    private Button mMessageReplyButton;
    private String mCiphertext;
    private CharSequence mPlaintext;
    private String mNumber;
    private CharSequence mName;
    private Long mContactId;

    private static final String PREF_NAME = "MarvinMessagingPreferences";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences mSettings = getSharedPreferences(PREF_NAME, 0);
        mApp = (MarvinApplication)getApplication();

        mApp.requestPassword(this);
        setContentView(R.layout.view_message);

        mMessageReplyButton = (Button)findViewById(R.id.view_message_reply_button);
        mMessageReplyButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                Intent intent = new Intent();
                intent.setClassName("com.marvinmessaging", "com.marvinmessaging.NewMessage");
				intent.putExtra(MarvinDbAdapter.KEY_ID, mContactId);
                startActivity(intent);
            }
        }); 
    }

    @Override protected void onResume() {
        super.onResume();
        mApp.lastActivity = (new Date()).getTime();
    }

    @Override protected void onPause() {
        super.onPause();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case MarvinApplication.PASSWORD_ACTIVITY_ID:
                if(resultCode == Activity.RESULT_OK) {
                    populateForm();
                }   
                break;
        }   
    } 

    private void populateForm() {
        mMessageNumText = (TextView)findViewById(R.id.view_message_num);
        mMessageBodyText = (TextView)findViewById(R.id.view_message_body);
        mMessageNameText = (TextView)findViewById(R.id.view_message_name);

        Intent intent = getIntent();
        mCiphertext = intent.getStringExtra("message");
        mNumber = intent.getStringExtra("number");
		MarvinDbAdapter dbAdapter = new MarvinDbAdapter(this);
		dbAdapter.open();
        Cursor contact = dbAdapter.getContactByNumber(mNumber);
        if(contact.getCount() > 0) {
            mContactId = contact.getLong(contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_ID));
            CharSequence key = CryptoHelper.decryptText(contact.getString(
                contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_PUB_KEY)));
            mName = CryptoHelper.decryptText(contact.getString(
                        contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_FIRST_NAME))) + " " +
                        CryptoHelper.decryptText(contact.getString(
                        contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_LAST_NAME)));
            CryptoHelper.genMessageCiphers(CryptoHelper.fromCharSeqToChars(key));
            mPlaintext = CryptoHelper.decryptMessageText(mCiphertext);

            mMessageNumText.setText(mNumber);
            mMessageNameText.setText(mName);
            mMessageBodyText.setText(mPlaintext);
        } else {
            //do something else
        }
        contact.close();
        dbAdapter.close();
    }
}
