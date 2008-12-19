package com.marvinmessaging;

import android.os.Bundle;
import android.app.Activity;
import com.marvinmessaging.MarvinDB;

public class NewContact extends Activity {
    private MarvinDB marvinDB;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.new_contact);

        marvinDB = new MarvinDB(this);
    }
}
