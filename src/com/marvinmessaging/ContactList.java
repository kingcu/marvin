package com.marvinmessaging;

import android.os.Bundle;
import android.app.ListActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class ContactList extends ListActivity {
    private MarvinDbAdapter mDbAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);
        
        mDbAdapter = new MarvinDbAdapter(this);
        mDbAdapter.open();

        TextView newContact = (TextView)findViewById(R.id.new_contact);
        newContact.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.marvinmessaging", "com.marvinmessaging.NewContact");
                startActivity(intent);
            }
        });
        populateList();
    }

    private void populateList() {
        Cursor c = mDbAdapter.getContacts();
        startManagingCursor(c); //let android handle cursor life

        ContactsCursorAdapter contacts = new ContactsCursorAdapter(this, c);
        setListAdapter(contacts);
    }

    private class ContactsCursorAdapter extends CursorAdapter {
        private Context mContext;
        private Cursor mCursor;

        public ContactsCursorAdapter(Activity context, Cursor c) {
            super(context, c);
            this.mContext = context;
            this.mCursor = c;
        }

        
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = View.inflate(context, R.layout.contact_row, null);
            bindView(view, context, cursor);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView nameView = (TextView)view.findViewById(R.id.row_name);
            TextView numView = (TextView)view.findViewById(R.id.row_phone_number);
            String lName = cursor.getString(cursor.getColumnIndex(MarvinDbAdapter.KEY_LAST_NAME));
            String fName = cursor.getString(cursor.getColumnIndex(MarvinDbAdapter.KEY_FIRST_NAME));
            String num = cursor.getString(cursor.getColumnIndex(MarvinDbAdapter.KEY_MOB_NUM));

            nameView.setText(lName + ", " + fName);
            numView.setText(num);
        }
    }
}
