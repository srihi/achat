package com.dankira.achat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dankira.achat.sync.AchatAuthenticatorActivity;

public class RegisterActivity extends AppCompatActivity
{

    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        accountType = getIntent().getStringExtra(AchatAuthenticatorActivity.ARG_ACCOUNT_TYPE);
        setContentView(R.layout.activity_register);

        findViewById(R.id.btn_already_member).setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                createAccount();
            }
        });
    }

    private void createAccount()
    {

        // TODO: 7/6/2016 verify the data that is entered in the relevant fields

        // TODO: 7/6/2016 Add code to register user in your web api with an async thread

        new AsyncTask<String, Void, Intent>()
        {

            @Override
            protected Intent doInBackground(String... strings)
            {
                return null;
            }

            @Override
            protected void onPostExecute(Intent intent)
            {
                super.onPostExecute(intent);
            }
        }.execute();


    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
