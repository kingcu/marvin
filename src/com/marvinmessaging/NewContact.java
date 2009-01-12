package com.marvinmessaging;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Button;
import android.content.Intent;
import android.database.Cursor;
import com.marvinmessaging.MarvinDbAdapter;
import com.marvinmessaging.Contact;

public class NewContact extends Activity {
    private MarvinDbAdapter mDbAdapter;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mMobileNum;
    private Long mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbAdapter = new MarvinDbAdapter(this);
        mDbAdapter.open();
        setContentView(R.layout.new_contact);

        mFirstName = (EditText)findViewById(R.id.contact_f_name);
        mLastName = (EditText)findViewById(R.id.contact_l_name);
        mMobileNum = (EditText)findViewById(R.id.contact_m_num);

        mId = (savedInstanceState != null) ? 
            savedInstanceState.getLong(MarvinDbAdapter.KEY_ID) : null;

        if(mId == null) {
            Bundle extras = getIntent().getExtras();
            mId = (extras != null) ? 
                extras.getLong(MarvinDbAdapter.KEY_ID) : null;
        } else {
            populateForm();
        }

        Button submit = (Button)findViewById(R.id.contact_submit_button);
        submit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //tell the activity the result returned to the caller
                setResult(RESULT_OK);
                finish(); //we are done, so onPause will be called;
            }
        });
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
    protected void onResume() {
        super.onResume();
        populateForm();
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
