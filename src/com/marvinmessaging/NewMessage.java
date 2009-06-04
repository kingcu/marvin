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
import android.telephony.gsm.SmsManager;

public class NewMessage extends Activity {
	private TextView mMsgBody;
	private TextView mName;
	private TextView mNum;
	private TextView mAuthenticated;
	private Button mSubmitButton;
	private MarvinDbAdapter mDbAdapter;
	private Long mId;
    private MarvinApplication mApp;
    private CharSequence mNumber;
    private char[] mSecret;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent = getIntent();
		final String action = intent.getAction();

        mApp = (MarvinApplication)getApplication();
		mDbAdapter = new MarvinDbAdapter(this);
		mDbAdapter.open();

		//TODO: IMPLEMENT ENCRYPTION FOR SAVED INSTANCE STATES!
		//this is where we have to make a decision...android has these curtousy
		//methods so when an activity gets interrupted (incoming phone call for example)
		//what they were doing gets tucked away while the call is handled...is it insecure
		//to allow marvin things to do this?  I think it can be done as long as when we
		//save our instance state we encrypt everything...when onResume gets called, we
		//check our session is still open and if so, decrypt the stored stuff...yeah i
		//think that's what we will do.
		mId = (savedInstanceState != null) ?
			savedInstanceState.getLong(MarvinDbAdapter.KEY_ID) : null;
		if(mId == null) {
			Bundle extras = getIntent().getExtras();
			mId = extras != null ? extras.getLong(MarvinDbAdapter.KEY_ID) : null;
		}

		setContentView(R.layout.new_message);

		mMsgBody = (TextView)findViewById(R.id.new_message_body);
		mName = (TextView)findViewById(R.id.new_message_name);
		mNum = (TextView)findViewById(R.id.new_message_num);
		mAuthenticated = (TextView)findViewById(R.id.new_message_authenticated);

		mSubmitButton = (Button)findViewById(R.id.new_message_submit_button);
		mSubmitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendMessage();
				setResult(RESULT_OK);
				finish();
			}
		});

		populateForm();
	}

	//TODO: implement decryption stuff here (for saved instance states)
	@Override
	protected void onResume() {
		super.onResume();
        mApp.requestPassword(this);
        mApp.lastActivity = (new Date()).getTime();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(MarvinDbAdapter.KEY_ID, mId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbAdapter.close();
	}

	private void saveState() {
		//TODO: this is where we would encrypt our shit and store it somewhere...
	}

	private void sendMessage() {
		//TODO: encrypt message, send to contact number!
        CryptoHelper.genMessageCiphers(mSecret);
        String cipherText = CryptoHelper.encryptMessageText(mMsgBody.getText());
        SmsManager sm = SmsManager.getDefault();
        //TODO: will casting to string be problematic with the whole immutable thing?
        sm.sendTextMessage((String)mNumber, null, "?mm?" + cipherText, null, null);
	}

	private void populateForm() {
		if(mId != null) {
			Cursor contact = mDbAdapter.getContact(mId);
			startManagingCursor(contact);
            if(contact.getCount() > 0) {
                CharSequence fname = CryptoHelper.decryptText(
                        contact.getString(contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_FIRST_NAME)));
                CharSequence lname = CryptoHelper.decryptText(
                        contact.getString(contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_LAST_NAME)));
                String num = contact.getString(contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_MOB_NUM));
                CharSequence secret = CryptoHelper.decryptText(
                        contact.getString(contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_PUB_KEY)));


                mName.setText(fname + " " + lname);
                mNum.setText(mDbAdapter.getFormattedPhone(mId));
                mAuthenticated.setText("Authenticated :)");

                //TODO: this is insecure!  get rid of string inbetween.
                mNumber = num;
                mSecret = CryptoHelper.fromCharSeqToChars(secret);
            }
            contact.close();
        }
	}
}
