package com.marvinmessaging;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Date;

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

        mSettings = getSharedPreferences(PREF_NAME, 0);
        mApp = (MarvinApplication)getApplication();
        if(mSettings.contains(KEY_PASS) && mApp.unlockPassword == null)
            mApp.requestPassword(this);

        mBundle = savedInstanceState;
        mDbAdapter = new MarvinDbAdapter(this);
        mDbAdapter.open();

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

        long now = (new Date()).getTime();
        if(mApp.unlockPassword == null || (now - mApp.lastActivity) > 60000)
            mApp.requestPassword(this);
        mApp.lastActivity = (new Date()).getTime();

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
        if(mPass.getText().length() > 0) {
            //OK, so we are changing the password....
            char[] pass = CryptoHelper.fromCharSeqToChars(mPass.getText());
            char[] passConf = CryptoHelper.fromCharSeqToChars(mPassConf.getText());
            int i;

            if(!Arrays.equals(pass, passConf)) {
                Toast toast = Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG);
                toast.show();
                return false;
            }

            //before we change anything, let's get all old contacts and decrypt them.
            //then after changing password, we will go through contacts and update
            //the encrypted fields in the database, with the newly encrypted values
            //using the new key
            Cursor contacts = mDbAdapter.getContacts();
            contacts.moveToFirst(); //might be implicit, not sure
            Hashtable[] tempContacts = new Hashtable[contacts.getCount()];
            for(i = 0; i < contacts.getCount(); i++) {
                tempContacts[i] = new Hashtable(4);
                tempContacts[i].put("id", contacts.getLong(contacts.getColumnIndex(MarvinDbAdapter.KEY_ID)));
                tempContacts[i].put("fname", CryptoHelper.decryptText(
                            contacts.getString(contacts.getColumnIndex(MarvinDbAdapter.KEY_FIRST_NAME))));
                tempContacts[i].put("lname", CryptoHelper.decryptText(
                            contacts.getString(contacts.getColumnIndex(MarvinDbAdapter.KEY_LAST_NAME))));
                tempContacts[i].put("num", CryptoHelper.decryptText(
                            contacts.getString(contacts.getColumnIndex(MarvinDbAdapter.KEY_MOB_NUM))));
                contacts.move(1);
            }
            //close our cursor right off the bat, sine we don't need it anymore
            contacts.close();

            //now we swap our keys
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
            mApp.unlockPassword = pass;

            //recreate our ciphers then re-encrypt our contacts
            //TODO: do a try catch block to rollback this on any error
            CryptoHelper.genStorageCiphers(pass, generatedSalt);
            for(i = 0; i < tempContacts.length; i++) {
                mDbAdapter.updateContact((Long)tempContacts[i].get("id"),
                        CryptoHelper.encryptText((CharSequence)tempContacts[i].get("fname")),
                        CryptoHelper.encryptText((CharSequence)tempContacts[i].get("lname")),
                        CryptoHelper.encryptText((CharSequence)tempContacts[i].get("num")));
            }
            mApp.unlockPassword = pass;
            mApp.lastActivity = (new Date()).getTime();
        }
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
