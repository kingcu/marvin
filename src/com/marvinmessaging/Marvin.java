package com.marvinmessaging;

import android.util.Log;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;

public class Marvin extends Activity {
    private Activity mActivity;
    private Bundle mExtras;
    private MarvinApplication mApp;
    private static String KEY_PASS = "pass";
    private static final int PASSWORD_ACTIVITY_ID = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mExtras = getIntent().getExtras();
        mApp = (MarvinApplication)getApplication();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mApp.unlockPassword == null)
            mApp.requestPassword(mActivity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case MarvinApplication.PASSWORD_ACTIVITY_ID:
                if(resultCode == Activity.RESULT_OK) {
                    mActivity.setContentView(R.layout.main);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, 0, 0, "View Contacts");
        menu.add(0, 1, 1, "Settings");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case 0:
                intent = new Intent();
                intent.setClassName("com.marvinmessaging", "com.marvinmessaging.ContactList");
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
}
