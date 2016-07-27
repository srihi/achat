package com.dankira.achat.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by da on 6/29/2016.
 */
public class SQLiteHandler extends SQLiteOpenHelper
{

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "achat";
    private static final String CURRENT_USER_TABLE = "current_user";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_CREATED_AT = "created_on";

    public SQLiteHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + CURRENT_USER_TABLE + "( "
                + KEY_ID + " INTEGER PRIVATE KEY, " + KEY_NAME + " TEXT, "
                + KEY_EMAIL + " TEXT UNIQUE, "
                + KEY_CREATED_AT + " TEXT" + " )";
        db.execSQL(CREATE_LOGIN_TABLE);
        Log.d(TAG, "Database tables created for current user.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer)
    {
        db.execSQL("DROP TABLE IF EXISTS " + CURRENT_USER_TABLE);

        onCreate(db);
    }

    public void addUser(String name, String email, String created_on)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_CREATED_AT, created_on);

        long id = db.insert(CURRENT_USER_TABLE, null, values);
        db.close();

        Log.d(TAG, "New user inserted into current user table " + id);
    }

    public HashMap<String, String> getUserDetails()
    {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + CURRENT_USER_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0)
        {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("created_on", cursor.getString(3));
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetched user from Sqlite: " + user.toString());

        return user;
    }

    public void deleteUsers()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(CURRENT_USER_TABLE, null, null);
        db.close();

        Log.d(TAG, "Cleared the current user table");
    }
}
