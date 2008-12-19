package com.marvinmessaging;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import com.marvinmessaging.Contact;

public class ContactList extends ListActivity {
    private MarvinDB marvinDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);
        
        marvinDB = new MarvinDB(this);
        TextView newContact = (TextView)findViewById(R.id.new_contact);
        newContact.setOnClickListener(newContactListener);
        populateList();
    }

    private void populateList() {
        Cursor c = marvinDB.getContacts();

        startManagingCursor(c); //let android handle cursor life

        String[] from = new String[] {Contact.KEY_LAST_NAME, Contact.KEY_FIRST_NAME};
        int[] to = new int[] { R.id.contact1 };

        SimpleCursorAdapter contacts = 
            new SimpleCursorAdapter(this, R.layout.contact_row, c, from, to);
        setListAdapter(contacts);
    }

    private OnClickListener newContactListener = new OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClassName("com.marvinmessaging", "com.marvinmessaging.NewContact");
            startActivity(intent);
        }
    };
}
