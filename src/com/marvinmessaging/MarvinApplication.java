package com.marvinmessaging;

import android.app.Application;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.Intent;

public class MarvinApplication extends Application {
    public char[] unlockPassword;
    public long lastActivity = 0;

    public static final int PASSWORD_ACTIVITY_ID = 0;

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
        Intent intent = new Intent();
        intent.setClassName("com.marvinmessaging", "com.marvinmessaging.PasswordPrompt");
        activity.startActivityForResult(intent, PASSWORD_ACTIVITY_ID);
    }
}
