package com.dankira.achat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dankira.achat.api.ServiceGenerator;
import com.dankira.achat.api.UserCredentials;
import com.dankira.achat.api.UserProfile;
import com.dankira.achat.api.WebApiEndPointInterface;
import com.dankira.achat.utils.SQLiteHandler;
import com.dankira.achat.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnRegister;
    private EditText txtPassword;
    private EditText txtUserEmail;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    private SQLiteHandler sqliteHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUserEmail = (EditText) findViewById(R.id.txtUserEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btn_linktoReg);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        sqliteHandler = new SQLiteHandler(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.isLoggedIn())
        {
            Intent toMainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(toMainActivityIntent);
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                String userEmail = txtUserEmail.getText().toString().trim();
                String userPassword = txtPassword.getText().toString().trim();

                if (!userEmail.isEmpty() && !userPassword.isEmpty())
                {
                    if (verifyCredentials(userEmail, userPassword))
                    {
                        Intent toMainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(toMainActivityIntent);
                        finish();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_error_message),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent toRegisterActivityIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(toRegisterActivityIntent);
                finish();
            }
        });

    }

    private boolean verifyCredentials(String userEmail, String userPassword)
    {
        WebApiEndPointInterface apiInterface = ServiceGenerator.createService(WebApiEndPointInterface.class);
        Call<UserProfile> apiCall = apiInterface.loginUser(new UserCredentials(userEmail, userPassword));
        apiCall.enqueue(new Callback<UserProfile>()
        {

            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response)
            {
                int statusCode = response.code();
                UserProfile userProfile = response.body();
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t)
            {
                // TODO: 6/30/2016 Log error here, something went wrong obviously.
            }
        });
        return false;
    }
}

