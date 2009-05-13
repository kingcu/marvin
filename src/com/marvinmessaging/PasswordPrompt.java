package com.marvinmessaging;

import android.util.Log;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;

public class PasswordPrompt extends Activity {
    private EditText mPasswordField;
    private char[] mPassword;
    private String mHash;
    private String mSalt;
    private MarvinApplication mApp;
    private SharedPreferences mSettings;
    private final String KEY_PASS = "pass";
    private final String KEY_SALT = "salt";
    private static final String PREF_NAME = "MarvinMessagingPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity thisActivity = this;
        mApp = (MarvinApplication)getApplication();

        mSettings = getSharedPreferences(PREF_NAME, 0);
        mHash = mSettings.getString(KEY_PASS, "");
        mSalt = mSettings.getString(KEY_SALT, "");

        setContentView(R.layout.password_prompt);

        Button authButton = (Button)findViewById(R.id.auth_button);
        mPasswordField = (EditText)findViewById(R.id.auth_password_field);

        authButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mPassword = CryptoHelper.fromCharSeqToChars(mPasswordField.getText());

                if(CryptoHelper.checkPassword(mPassword, mHash, mSalt)) {
                    byte[] salt = CryptoHelper.hexStringToBytes(mSalt);
                    mApp.unlockPassword = mPassword;
                    CryptoHelper.genStorageCiphers(mPassword, salt);
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Toast toast = Toast.makeText(thisActivity, "Invalid password!!!?!?!?!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
