package com.marvinmessaging;

import java.io.FileNotFoundException;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MarvinDB extends SQLiteOpenHelper {
    private static final String CONTACTS_TABLE = "contacts";
    private static final String DB_NAME = "marvin_messaging.db";
    private static final int DB_VERSION = 1; //TODO: is this needed?
    private static final String CREATE_CONTACTS_TABLE = "create table " + 
        CONTACTS_TABLE + " (_id integer primary key autoincrement, " +
        Contact.KEY_FIRST_NAME     + " text not null, " +
        Contact.KEY_LAST_NAME      + " text not null, " +
        Contact.KEY_MOB_NUM        + " int not null, " +
        Contact.KEY_PUB_KEY        + " text, " + 
        Contact.KEY_CONVO_TIMEOUT  + " long not null, " +
        Contact.KEY_MSGS_PER_CONVO + " int not null);";

    MarvinDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase marvindb) {
        marvindb.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase marvindb, int oldVersion, int newVersion) {
        //some call to Log??
        marvindb.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE);
        onCreate(marvindb);
    }

    public Cursor getContacts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(CONTACTS_TABLE, 
                new String[] {Contact.KEY_ID, Contact.KEY_LAST_NAME, Contact.KEY_FIRST_NAME}, 
                null, null, null, null, null);
    }

    /*
    public Cursor query(String query) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.execSQL(query);
    }

    public void insert(String query) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(query);
    }

    public void delete(String query) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(query);
    }

    public void update(String query) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(query);
    }
    */
}
