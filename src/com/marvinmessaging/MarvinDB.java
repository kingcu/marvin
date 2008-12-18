package com.marvinmessaging;

import java.io.FileNotFoundException;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MarvinDB {
    private static final String CREATE_CONTACTS_TABLE = "create table contacts (_id integer primary key autoincrement, " +
        "first_name text not null, last_name text not null, mobile_num int not null, public_key text, " + 
        "convo_timeout long not null, msgs_per_convo int not null);";
    private static final String CONTACTS_TABLE = "contacts";
    private static final String DB_NAME = "marvin_messaging.db";
    private static final int DB_VERSION = 1;


    //our database creation, if necessary, stuff
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_CONTACTS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //some call to Log??
            db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE);
            onCreate(db);
        }
    }


}
