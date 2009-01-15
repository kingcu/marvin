package com.marvinmessaging;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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

        TextView contacts = (TextView)findViewById(R.id.view_contacts);
        contacts.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.marvinmessaging", "com.marvinmessaging.ContactList");
                startActivity(intent);
            }
        });
    }
}
