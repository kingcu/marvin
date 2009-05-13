package com.marvinmessaging;

import java.util.Date;
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
import com.marvinmessaging.MarvinDbAdapter;

public class NewContact extends Activity {
    private static final int CREATE_STATE = 0;
    private static final int EDIT_STATE = 1;
    private MarvinDbAdapter mDbAdapter;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mMobileNum;
    private EditText mKey;
    private Button mSubmitButton;
    private Long mId;
    private int mState;
	private Bundle mBundle;
    private MarvinApplication mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        final String action = intent.getAction();

        mApp = (MarvinApplication)getApplication();
		mBundle = savedInstanceState;
        mDbAdapter = new MarvinDbAdapter(this);
        mDbAdapter.open();
        setContentView(R.layout.new_contact);

        mFirstName = (EditText)findViewById(R.id.contact_f_name);
        mLastName = (EditText)findViewById(R.id.contact_l_name);
        mMobileNum = (EditText)findViewById(R.id.contact_m_num);
        mKey = (EditText)findViewById(R.id.contact_key);
        mSubmitButton = (Button)findViewById(R.id.contact_submit_button);
        mId = (savedInstanceState != null) ? 
            savedInstanceState.getLong(MarvinDbAdapter.KEY_ID) : null;

		if(mId == null) {
			Bundle extras = getIntent().getExtras();
			mId = extras != null ? extras.getLong(MarvinDbAdapter.KEY_ID) : null;
		}

        if(Intent.ACTION_EDIT.equals(action)) {
            mState = EDIT_STATE;
        } else if(Intent.ACTION_INSERT.equals(action)) {
            mState = CREATE_STATE;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        long now = (new Date()).getTime();
        if(mApp.unlockPassword == null || (now - mApp.lastActivity) > 60000)
            mApp.requestPassword(this);

        if(mState == EDIT_STATE) { //we are editing a contact
            mSubmitButton.setText(getText(R.string.contact_form_button_edit));
            setTitle(getText(R.string.contact_form_title_edit));
			if(mBundle != null) {
				mFirstName.setText(mBundle.getCharSequence(MarvinDbAdapter.KEY_FIRST_NAME));
				mLastName.setText(mBundle.getCharSequence(MarvinDbAdapter.KEY_LAST_NAME));
				mMobileNum.setText(mBundle.getCharSequence(MarvinDbAdapter.KEY_MOB_NUM));
			} else {
				populateForm();
			}
        } else { //entering new contact
            mSubmitButton.setText(getText(R.string.contact_form_button_insert));
            setTitle(getText(R.string.contact_form_title_insert));
        }

        mSubmitButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //tell the activity the result returned to the caller
                setResult(RESULT_OK);
				saveState();
                finish(); //we are done, so onPause will be called;
            }
        });
        mApp.lastActivity = (new Date()).getTime();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
		CharSequence fname = mFirstName.getText().toString();
		String lname = mLastName.getText().toString();
		String num = mMobileNum.getText().toString();
        outState.putLong(MarvinDbAdapter.KEY_ID, mId);
		outState.putCharSequence(MarvinDbAdapter.KEY_FIRST_NAME, mFirstName.getText());
		outState.putCharSequence(MarvinDbAdapter.KEY_LAST_NAME, mLastName.getText());
		outState.putCharSequence(MarvinDbAdapter.KEY_MOB_NUM, mMobileNum.getText());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbAdapter.close();
    }

    private void saveState() {
        char[] cfname = CryptoHelper.fromCharSeqToChars(mFirstName.getText());
        char[] clname = CryptoHelper.fromCharSeqToChars(mLastName.getText());
        char[] cnum = CryptoHelper.fromCharSeqToChars(mMobileNum.getText());
        char[] ckey = CryptoHelper.fromCharSeqToChars(mKey.getText());
        String fname = CryptoHelper.encryptText(cfname);
        String lname = CryptoHelper.encryptText(clname);
        String num = CryptoHelper.encryptText(cnum);
        String key = CryptoHelper.encryptText(ckey);

        Log.i("marvin", fname);
        if(mId == null) { //new entry, create a new contact
			long id = mDbAdapter.createContact(fname, lname, num, key);
			if(id>0) {
				mId = id;
			}
        } else { //we are updating an existing contact
            mDbAdapter.updateContact(mId, fname, lname, num, key);
        }
    }

    private void populateForm() {
        if(mId != null) {
            Cursor contact = mDbAdapter.getContact(mId);

            mFirstName.setText(CryptoHelper.decryptText(
                        contact.getString(contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_FIRST_NAME))));
            mLastName.setText(CryptoHelper.decryptText(
                        contact.getString(contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_LAST_NAME))));
            mMobileNum.setText(CryptoHelper.decryptText(
                        contact.getString(contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_MOB_NUM))));

            contact.close();
        }
    }
}
