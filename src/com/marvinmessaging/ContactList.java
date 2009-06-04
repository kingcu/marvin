package com.marvinmessaging;

import android.os.Bundle;
import android.util.Log;
import android.app.ListActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import android.content.DialogInterface;
import android.content.DialogInterface;

public class ContactList extends ListActivity {
    private MarvinDbAdapter mDbAdapter;
    private MarvinApplication mApp;

    protected static final int MSG_CONTACT_ITEM = Menu.FIRST;
    protected static final int EDIT_CONTACT_ITEM = Menu.FIRST + 1;
    protected static final int DELETE_CONTACT_ITEM = Menu.FIRST + 2;
    protected static final int CANCEL_CONTACT_ITEM = Menu.FIRST + 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApp = (MarvinApplication)getApplication();
        mApp.requestPassword(this);

        setContentView(R.layout.contact_list);
        
        mDbAdapter = new MarvinDbAdapter(this);
        mDbAdapter.open();

        populateList();

		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Intent intent = new Intent();
				intent.setClassName("com.marvinmessaging", "com.marvinmessaging.NewMessage");
				intent.putExtra(MarvinDbAdapter.KEY_ID, id);
				startActivity(intent);
			}
		});

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
				menu.add(0, CANCEL_CONTACT_ITEM, 0, "Cancel");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDbAdapter.close();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, 0, 0, "Add New Contact");
        menu.add(0, 1, 1, "Settings");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case 0:
                intent = new Intent();
                intent.setClassName("com.marvinmessaging", "com.marvinmessaging.NewContact");
                startActivity(intent);
                return true;
            case 1:
                intent = new Intent();
                intent.setClassName("com.marvinmessaging", "com.marvinmessaging.ApplicationSettings");
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
		final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
		final long rowId = getListAdapter().getItemId(menuInfo.position);
		Intent intent;

        switch(id) {
            case MSG_CONTACT_ITEM:
				intent = new Intent();
				intent.setClassName("com.marvinmessaging",
						"com.marvinmessaging.NewMessage");
				intent.setAction(Intent.ACTION_VIEW);
				intent.putExtra(MarvinDbAdapter.KEY_ID, rowId);
				startActivity(intent);
                break;
            case EDIT_CONTACT_ITEM:
                intent = new Intent();
                intent.setClassName("com.marvinmessaging", 
                        "com.marvinmessaging.NewContact");
                intent.setAction(Intent.ACTION_EDIT);
				intent.putExtra(MarvinDbAdapter.KEY_ID, rowId);
                startActivity(intent);
                break;
            case DELETE_CONTACT_ITEM:
                new AlertDialog.Builder(this)
                    .setTitle("Are you sure?")
                    .setMessage("This will delete the contact permanently, there is no going back!")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mDbAdapter.deleteContact(rowId);
							populateList();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
                break;
			case CANCEL_CONTACT_ITEM:
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
            
            nameView.setText(CryptoHelper.decryptText(fName) + " " + CryptoHelper.decryptText(lName));
            numView.setText(mDbAdapter.getFormattedPhone(cursor.getInt(cursor.getColumnIndex(MarvinDbAdapter.KEY_ID))));
        }
    }
}
