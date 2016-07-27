package com.dankira.achat;

import android.app.Application;

/**
 * Created by da on 6/30/2016.
 */
public class AppController extends Application
{

    private static AppController _instance;

    public static synchronized AppController getInstance()
    {
        return _instance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        _instance = this;
    }
}
