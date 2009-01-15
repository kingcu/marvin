package com.marvinmessaging;

import android.os.Bundle;
import android.util.Log;
import android.app.ListActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.view.View;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;

public class ContactList extends ListActivity {
    private MarvinDbAdapter mDbAdapter;

    protected static final int MSG_CONTACT_ITEM = Menu.FIRST;
    protected static final int EDIT_CONTACT_ITEM = Menu.FIRST + 1;
    protected static final int DELETE_CONTACT_ITEM = Menu.FIRST + 2;

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

        getListView().setOnCreateContextMenuListener(
                new OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, 
                    ContextMenuInfo menuInfo) {
                /*
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.layout.contact_list_contextmenu, menu);
                */
                menu.setHeaderTitle("Actions:");
                menu.add(0, MSG_CONTACT_ITEM, 0, "Message Contact");
                menu.add(0, EDIT_CONTACT_ITEM, 0, "Edit Contact");
                menu.add(0, DELETE_CONTACT_ITEM, 0, "Delete Contact");
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case MSG_CONTACT_ITEM:
                break;
            case EDIT_CONTACT_ITEM:
                Intent intent = new Intent();
                intent.setClassName("com.marvinmessaging", 
                        "com.marvinmessaging.NewContact");
                intent.setAction(Intent.ACTION_EDIT);
                startActivity(intent);
                break;
            case DELETE_CONTACT_ITEM:
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true; //return true if we are doing any of our actions
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
