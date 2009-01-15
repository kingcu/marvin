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
import com.marvinmessaging.Contact;

public class NewContact extends Activity {
    private static final int CREATE_STATE = 0;
    private static final int EDIT_STATE = 1;
    private MarvinDbAdapter mDbAdapter;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mMobileNum;
    private TextView mTitleText;
    private Button mSubmitButton;
    private Long mId;
    private int mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        final String action = intent.getAction();

        mDbAdapter = new MarvinDbAdapter(this);
        mDbAdapter.open();
        setContentView(R.layout.new_contact);

        mFirstName = (EditText)findViewById(R.id.contact_f_name);
        mLastName = (EditText)findViewById(R.id.contact_l_name);
        mMobileNum = (EditText)findViewById(R.id.contact_m_num);
        mTitleText = (TextView)findViewById(R.id.contact_form_title);

        mId = (savedInstanceState != null) ? 
            savedInstanceState.getLong(MarvinDbAdapter.KEY_ID) : null;

        if(Intent.ACTION_EDIT.equals(action)) {
            mState = EDIT_STATE;
        } else if(Intent.ACTION_INSERT.equals(action)) {
            mState = CREATE_STATE;
        }

        mSubmitButton = (Button)findViewById(R.id.contact_submit_button);
        mSubmitButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //tell the activity the result returned to the caller
                setResult(RESULT_OK);
                finish(); //we are done, so onPause will be called;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mState == EDIT_STATE) { //we are editing a contact
            mTitleText.setText(getText(R.string.contact_form_title_edit));
            mSubmitButton.setText(getText(R.string.contact_form_button_edit));
            setTitle(getText(R.string.contact_form_title_edit));
            populateForm();
        } else { //entering new contact
            mTitleText.setText(getText(R.string.contact_form_title_insert));
            mSubmitButton.setText(getText(R.string.contact_form_button_insert));
            setTitle(getText(R.string.contact_form_title_insert));

            /*
            Bundle extras = getIntent().getExtras();
            mId = (extras != null) ? 
                extras.getLong(MarvinDbAdapter.KEY_ID) : null;
            */
        }
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
        String fname = mFirstName.getText().toString();
        String lname = mLastName.getText().toString();
        String num = mMobileNum.getText().toString();

        if(mId == null) { //new entry, create a new contact
            long id = mDbAdapter.createContact(fname, lname, num);
            if(id>0) {
                mId = id;
            }
        } else { //we are updating an existing contact
            mDbAdapter.updateContact(mId, fname, lname, num);
        }
    }

    private void populateForm() {
        if(mId != null) {
            Cursor contact = mDbAdapter.getContact(mId);
            startManagingCursor(contact);
            mFirstName.setText(contact.getString(contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_FIRST_NAME)));
            mLastName.setText(contact.getString(contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_LAST_NAME)));
            mMobileNum.setText(contact.getString(contact.getColumnIndexOrThrow(MarvinDbAdapter.KEY_MOB_NUM)));
        }
    }
}
