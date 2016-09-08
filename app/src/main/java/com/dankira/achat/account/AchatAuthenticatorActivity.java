package com.dankira.achat.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dankira.achat.R;
import com.dankira.achat.api.LoginResponse;
import com.dankira.achat.api.ServiceGenerator;
import com.dankira.achat.api.UserCredentials;
import com.dankira.achat.api.WebApiEndPointInterface;
import com.dankira.achat.views.RegisterActivity;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Response;

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
    private Button btnLinkToRegistration;
    private TextView txtUserEmail;

    private CoordinatorLayout coordinatorLayout;
    private static final String LOG_TAG = AchatAuthenticatorActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.login_activity_coord_layout);
        txtUserEmail = (TextView) findViewById(R.id.txtUserEmail);
        accountManager = AccountManager.get(getBaseContext());
        signInButton = (Button) findViewById(R.id.btnLogin);
        signInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                signInButton.setEnabled(false);
                submit();
            }
        });

        btnLinkToRegistration = (Button) findViewById(R.id.btn_linktoReg);
        btnLinkToRegistration.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                Intent signUpIntent = new Intent(getBaseContext(), RegisterActivity.class);
                signUpIntent.putExtras(getIntent().getExtras());
                startActivityForResult(signUpIntent, REQ_SIGN_UP);
            }
        });

        Intent originIntent = getIntent();
        if (originIntent != null)
        {
            String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
            authTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);

            if (authTokenType != null)
            {
                txtUserEmail.setText(accountName);
            }
        }
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
        setResult(RESULT_OK, intent);
        finish();
    }

    private void submit()
    {
        EditText txtUserEmailField = (EditText) findViewById(R.id.txtUserEmail);
        EditText txtPasswordField = (EditText) findViewById(R.id.txtPassword);

        final String userEmail = txtUserEmailField.getText().toString().trim();
        final String userPassword = txtPasswordField.getText().toString().trim();
        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        if (!isValidEmail(userEmail))
        {
            txtUserEmailField.setError(getResources().getString(R.string.invalid_email_error_text));
            signInButton.setEnabled(true);
            return;
        }

        if (!isValidPassword(userPassword))
        {
            txtPasswordField.setError(getResources().getString(R.string.invalid_password_error_text));
            signInButton.setEnabled(true);
            return;
        }

        new AsyncTask<String, Void, Intent>()
        {
            @Override
            protected Intent doInBackground(String... params)
            {
                Intent data = new Intent();
                try
                {
                    String authToken = verifyCredentials(userEmail, userPassword);

                    if (authToken != null && !TextUtils.isEmpty(authToken))
                    {
                        data.putExtra(AccountManager.KEY_ACCOUNT_NAME, userEmail);
                        data.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                        data.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
                        data.putExtra(PARAM_USER_PASS, userPassword);
                    }
                    else
                    {
                        Log.i(LOG_TAG, "Unable to authenticate user.");
                        data.putExtra(KEY_ERROR_MESSAGE, "Cannot authenticate user.");
                    }
                }
                catch (Exception e)
                {
                    Log.e(LOG_TAG, " An exception has occurred while authenticating user with API. Message: " + e.getMessage());
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
                    Log.e(LOG_TAG, "An error has occurred while authenticating user with API. Message: " + intent.getStringExtra(KEY_ERROR_MESSAGE));
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, intent.getStringExtra(KEY_ERROR_MESSAGE),
                            Snackbar.LENGTH_LONG);
                    snackbar.show();
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
        WebApiEndPointInterface apiInterface = ServiceGenerator.createService(WebApiEndPointInterface.class,
                AccountGeneral.API_KEY);
        Call<LoginResponse> apiCall = apiInterface.loginUser(new UserCredentials(userEmail, userPassword));

        String authToken = "";

        try
        {
            Response<LoginResponse> response = apiCall.execute();
            LoginResponse loginResponse = response.body();
            authToken = loginResponse.access_token;
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, "An exception occurred while authenticating user. Message: " + e.getMessage());
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

    private boolean isValidEmail(String email)
    {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass)
    {
        if (pass != null && pass.length() > 0)
        {
            return true;
        }
        else
        {
            Log.e(LOG_TAG, "The password is empty.");
            return false;
        }

    }
}
