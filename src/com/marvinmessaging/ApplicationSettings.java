package com.marvinmessaging;

import java.util.Arrays;

import android.util.Log;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.database.Cursor;
import android.content.SharedPreferences;

import com.marvinmessaging.MarvinDbAdapter;

public class ApplicationSettings extends Activity {
    private MarvinDbAdapter mDbAdapter;
    private MarvinApplication mApp;
    private Bundle mBundle;
    private EditText mPass;
    private EditText mPassConf;
    private SharedPreferences mSettings;
    private char[] mPassword;
    private final String KEY_PASS = "pass";
    private final String KEY_SALT = "salt";

    private static final String LOG_TAG = "MarvinMessaging";
    private static final String PREF_NAME = "MarvinMessagingPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        final String action = intent.getAction();
        mApp = (MarvinApplication)getApplication();

        mSettings = getSharedPreferences(PREF_NAME, 0);
        mBundle = savedInstanceState;
        mDbAdapter = new MarvinDbAdapter(this);
        mDbAdapter.open();

        if(mApp.unlockPassword == null)
            mApp.requestPassword(this);

        setContentView(R.layout.application_settings);

        Button saveButton = (Button)findViewById(R.id.settings_save_button);
        mPass = (EditText)findViewById(R.id.settings_master_password);
        mPassConf = (EditText)findViewById(R.id.settings_master_password_conf);

        saveButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                if(saveState()) {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        populateForm(); //TODO: maybe do inline
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbAdapter.close();
    }

    private boolean saveState() {
        char[] pass = CryptoHelper.fromCharSeqToChars(mPass.getText());
        char[] passConf = CryptoHelper.fromCharSeqToChars(mPassConf.getText());

        if(!Arrays.equals(pass, passConf)) {
            //hmmm maybe use toast to bitch at the user that their passwords don't match
            Toast toast = Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }

        byte[] generatedSalt = CryptoHelper.generateSalt();
        byte[] generatedHash = {};

        try {
            generatedHash = CryptoHelper.generateHash(pass, generatedSalt);
        } catch (Exception e) {
            Log.d(LOG_TAG, "createKey", e);
        }

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(KEY_PASS, CryptoHelper.toHexString(generatedHash));
        editor.putString(KEY_SALT, CryptoHelper.toHexString(generatedSalt));
        editor.commit();

        return true;
    }

    private void populateForm() {
        String salt = mSettings.getString(KEY_SALT, "");
        String hash = mSettings.getString(KEY_PASS, "");

        if(salt.length() > 0) {
            if(CryptoHelper.checkPassword("password".toCharArray(), hash, salt)) {
                Log.i(LOG_TAG, "KEYS MATCH!");
            } else {
                Log.i(LOG_TAG, "KEYS DONT MATCH FUCK");
            }
        }
    }
}
