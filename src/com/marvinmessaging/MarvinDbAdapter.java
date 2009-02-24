package com.marvinmessaging;

import java.io.FileNotFoundException;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MarvinDbAdapter {
    public static final String KEY_ID = "_id";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_MOB_NUM = "mobile_num";
    public static final String KEY_PUB_KEY = "pub_key";
	public static final String KEY_IS_AUTH = "is_auth";
    public static final String KEY_CONVO_TIMEOUT = "convo_timeout";
    public static final String KEY_MSGS_PER_KEY = "msgs_per_key";

    private static final String CONTACTS_TABLE = "contacts";
    private static final String DB_NAME = "marvin_messaging.db";
    private static final int DB_VERSION = 1; //TODO: is this needed?
    private static final String CREATE_CONTACTS_TABLE = "create table " + 
        CONTACTS_TABLE + " (_id integer primary key autoincrement, " +
        KEY_FIRST_NAME     + " text not null, " +
        KEY_LAST_NAME      + " text not null, " +
        KEY_MOB_NUM        + " text not null, " +
        KEY_PUB_KEY        + " text, " + 
		KEY_IS_AUTH		   + " boolean not null default 0, " +
        KEY_CONVO_TIMEOUT  + " long not null default 1440, " +
        KEY_MSGS_PER_KEY   + " int not null default 10);";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context ctx;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
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
    }

    /**
     * Constructor - takes context for opening/creating database
     * 
     * @param ctx the Context within which to work
     */
    public MarvinDbAdapter(Context context) {
        this.ctx = context;
    }

    /**
     * Open the database.  If the database cannot be opened, we will
     * 
     * @return this (self reference, allowing chaining in initialization call)
     * @throws SQLException if db can neither be open or created
     */
    public MarvinDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(ctx);
        mDb = mDbHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    /**
     * Pull a cursor for all contacts
     *
     * @return cursor for contacts
     */
    public Cursor getContacts() {
        return mDb.query(CONTACTS_TABLE, 
                new String[] {KEY_ID, KEY_LAST_NAME, KEY_FIRST_NAME, KEY_MOB_NUM}, 
                null, null, null, null, null);
    }

    /**
     * pull a cursor for a single contact
     *
     * @param id the id of contact we want
     * @return cursor for the contact
     * @throws SQLException if note could not be found
     */
    public Cursor getContact(long id) throws SQLException {
        Cursor cursor = mDb.query(true, CONTACTS_TABLE, new String[] {KEY_ID,
            KEY_FIRST_NAME, KEY_LAST_NAME, KEY_MOB_NUM}, KEY_ID + "=" + id,
            null, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Create a new contact
     *
     * @param fName contacts first name
     * @param lName contacts last name
     * @param num contacts phone number
     * 
     * @return id or -1 if failed
     */
    public long createContact(String fName, String lName, String num) {
        ContentValues values = new ContentValues();
        values.put(KEY_FIRST_NAME, fName);
        values.put(KEY_LAST_NAME, lName);
        values.put(KEY_MOB_NUM, num);

        return mDb.insert(CONTACTS_TABLE, null, values);
    }

    /**
     * Update a contact
     *
     * @param fName contacts first name
     * @param lName contacts last name
     * @param num contacts mobile number
     *
     * @return true if updated, false if failed
     */
    public boolean updateContact(long id, String fName, String lName, String num, boolean auth) {
        ContentValues values = new ContentValues();
        values.put(KEY_FIRST_NAME, fName);
        values.put(KEY_LAST_NAME, lName);
        values.put(KEY_MOB_NUM, num);
		values.put(KEY_IS_AUTH, auth);

        return mDb.update(CONTACTS_TABLE, values, KEY_ID + "=" + id, null) > 0;
    }

	/**
	 * delete a given contact
	 *
	 * @param id the id of contact to delete
	 *
	 * @returns true if contact was deleted, false otherwise
	 */
    public boolean deleteContact(long id) {
        return mDb.delete(CONTACTS_TABLE, KEY_ID + "=" + id, null) > 0;
    }

	public String getFormattedPhone(long id) {
		String num;
		String formattedNum;
        Cursor cursor = mDb.query(true, CONTACTS_TABLE, new String[] {KEY_ID,
            KEY_MOB_NUM}, KEY_ID + "=" + id, null, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
		num = cursor.getString(cursor.getColumnIndex(KEY_MOB_NUM));
		
		if(num.length() == 10) {
			formattedNum = "(" + num.substring(0, 3) + ")" + num.substring(3,6) + "-" + num.substring(6);
		} else {
			formattedNum = num;
		}
		return formattedNum;
	}
}
