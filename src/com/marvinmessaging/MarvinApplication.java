package com.marvinmessaging;

import java.util.Date;
import android.util.Log;
import android.app.Application;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.Intent;
import android.content.SharedPreferences;

public class MarvinApplication extends Application {
    public static final String SETTINGS_KEY = "MarvinMessagingPreferences";
    public static final String SETTINGS_KEY_PASS = "pass";
    public static final String SETTINGS_KEY_SALT = "salt";
    public static final String LOG_TAG = "MarvinMessaging";
    public static final int PASSWORD_ACTIVITY_ID = 0;

    public char[] unlockPassword;
    public long lastActivity = 0;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void requestPassword(Activity activity) {
        long now = (new Date()).getTime();
        SharedPreferences settings = activity.getSharedPreferences(SETTINGS_KEY, 0);

        if(settings.getString(SETTINGS_KEY_PASS, "").length() > 0 && (unlockPassword == null  || (now - lastActivity) > 60000)) {
            Log.i("MARVIN", "requestPassword starting activity");
            Intent intent = new Intent();
            intent.setClassName("com.marvinmessaging", "com.marvinmessaging.PasswordPrompt");
            activity.startActivityForResult(intent, PASSWORD_ACTIVITY_ID);
        }
    }
}
