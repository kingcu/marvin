package com.marvinmessaging;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

public class Marvin extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

	TextView contacts = (TextView)findViewById(R.id.contacts);
	contacts.setOnClickListener(contactsListener);
    }

    private OnClickListener contactsListener = new OnClickListener() {
	public void onClick(View v) {
		Intent data = new Intent();
		data.setAction("Contacts");
		setResult(RESULT_OK, data);
		finish();
	}
    };
}
