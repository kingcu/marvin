package com.marvinmessaging;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import android.database.Cursor;
import com.marvinmessaging.MarvinDbAdapter;

public class NewMessage extends Activity {
	private EditText mMsgBody;
	private TextView mName;
	private TextView mNum;
	private Button mSubmitButton;
	private MarvinDbAdapter mDbAdapter;
	private Long mId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent = getIntent();
		final String action = intent.getAction();

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

		mMsgBody = (EditText)findViewById(R.id.new_message_body);
		mName = (TextView)findViewById(R.id.new_message_name);
		mNum = (TextView)findViewById(R.id.new_message_num);

		populateForm();

		mSubmitButton = (Button)findViewById(R.id.new_message_submit_button);
		mSubmitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendMessage();
				setResult(RESULT_OK);
				finish();
			}
		});
	}

	//TODO: implement decryption stuff here (for saved instance states)
	@Override
	protected void onResume() {
		super.onResume();
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
	}

	private void populateForm() {
		if(mId != null) {
			Cursor contact = mDbAdapter.getContact(mId);
			startManagingCursor(contact);
			String fname = contact.getString(contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_FIRST_NAME));
			String lname = contact.getString(contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_LAST_NAME));
			String num = contact.getString(contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_MOB_NUM));
			
			mName.setText(fname + " " + lname);
			mNum.setText(num);
		}
	}
}
