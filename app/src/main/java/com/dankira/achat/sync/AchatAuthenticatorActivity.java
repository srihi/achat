package com.dankira.achat.sync;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dankira.achat.R;
import com.dankira.achat.RegisterActivity;
import com.dankira.achat.api.ServiceGenerator;
import com.dankira.achat.api.UserCredentials;
import com.dankira.achat.api.UserProfile;
import com.dankira.achat.api.WebApiEndPointInterface;

import java.io.IOException;

import retrofit2.Call;

public class AchatAuthenticatorActivity extends AccountAuthenticatorActivity
{

    public static final String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public static final String ARG_AUTH_TYPE = "AUTH_TYPE";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_NEW_ACCOUNT";
    public static final String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public static final String PARAM_USER_PASS = "USER_PASS";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    private static final int REQ_SIGN_UP = 1;

    private AccountManager accountManager;
    private String authTokenType;

    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        accountManager = AccountManager.get(getBaseContext());

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        authTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (authTokenType != null)
        {
            ((TextView) findViewById(R.id.txtUserEmail)).setText(accountName);
        }

        signInButton = (Button)findViewById(R.id.btnLogin);

        signInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                submit();
                signInButton.setEnabled(false);

            }
        });

        findViewById(R.id.btn_linktoReg).setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                Intent signUpIntent = new Intent(getBaseContext(), RegisterActivity.class);
                signUpIntent.putExtras(getIntent().getExtras());
                startActivityForResult(signUpIntent, REQ_SIGN_UP);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQ_SIGN_UP && resultCode == RESULT_OK)
        {
            finishLogin(data);
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        setAccountAuthenticatorResult(null);
        setResult(RESULT_OK,intent);
        finish();
    }

    private void submit()
    {
        final String userEmail = ((TextView) findViewById(R.id.txtUserEmail)).getText().toString().trim();
        final String userPassword = ((TextView) findViewById(R.id.txtPassword)).getText().toString().trim();
        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        new AsyncTask<String, Void, Intent>()
        {
            @Override
            protected Intent doInBackground(String... params)
            {
                Intent data = new Intent();
                try
                {
                    String authToken = verifyCredentials(userEmail, userPassword);

                    if (authToken!= null && !TextUtils.isEmpty(authToken))
                    {
                        data.putExtra(AccountManager.KEY_ACCOUNT_NAME, userEmail);
                        data.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                        data.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
                        data.putExtra(PARAM_USER_PASS, userPassword);
                    }

                    data.putExtra(KEY_ERROR_MESSAGE, "Cannot authenticate user.");
                }
                catch (Exception e)
                {
                    data.putExtra(KEY_ERROR_MESSAGE, e.getMessage());
                }

                return data;
            }

            @Override
            protected void onPostExecute(Intent intent)
            {
                signInButton.setEnabled(true);
                if (intent.hasExtra(KEY_ERROR_MESSAGE))
                {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE),
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    finishLogin(intent);
                }
            }
        }.execute();
    }

    private String verifyCredentials(final String userEmail, final String userPassword)
    {

        WebApiEndPointInterface apiInterface = ServiceGenerator.createService(WebApiEndPointInterface.class);
        Call<UserProfile> apiCall = apiInterface.loginUser(new UserCredentials(userEmail, userPassword));

        String authToken = "";

        try
        {
            UserProfile userProfile = apiCall.execute().body();
            authToken =  userProfile.getAuthToken();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return authToken;
    }

    private void finishLogin(Intent intent)
    {
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        final Account account = new Account(accountName, accountType);

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false))
        {
            String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authTokenType = this.authTokenType;

            accountManager.addAccountExplicitly(account, accountPassword, null);
            accountManager.setAuthToken(account, authTokenType, authToken);
        }
        else
        {
            accountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }
}
