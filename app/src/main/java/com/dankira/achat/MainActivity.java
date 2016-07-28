package com.dankira.achat;

import android.os.Bundle;

public class MainActivity extends SecuredAppCompatActivityBase
{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
