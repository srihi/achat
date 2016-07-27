package com.dankira.achat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager
{
    private static final String SHARED_PREF_NAME = "achat_login";
    private static final String SHARED_PREF_KEY_ISLOGGEDIN = "isLoggedIn";
    private static String TAG = SessionManager.class.getSimpleName();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context _context;
    int SHARED_PREF_MODE = 0;

    public SessionManager(Context context)
    {
        this._context = context;
        preferences = _context.getSharedPreferences(SHARED_PREF_NAME, SHARED_PREF_MODE);
    }

    public boolean isLoggedIn()
    {
        return preferences.getBoolean(SHARED_PREF_KEY_ISLOGGEDIN, false);
    }

    public void setLogin(boolean isLoggedIn)
    {
        editor = preferences.edit();
        editor.putBoolean(SHARED_PREF_KEY_ISLOGGEDIN, isLoggedIn);
        editor.apply();

        Log.d(TAG, "user login status modified.");
    }
}
