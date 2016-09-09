package com.dankira.achat.views;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dankira.achat.R;
import com.dankira.achat.account.AccountGeneral;
import com.dankira.achat.account.AchatAuthenticatorActivity;
import com.dankira.achat.api.RegistrationResponse;
import com.dankira.achat.api.ServiceGenerator;
import com.dankira.achat.api.UserProfile;
import com.dankira.achat.api.WebApiEndPointInterface;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class RegisterActivity extends AppCompatActivity
{

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public static final String KEY_AUTH_TOKEN = "REGISTRATION TOKEN";
    private String accountType;

    @BindView(R.id.btn_register)
    private Button btnRegister;
    @BindView(R.id.edit_register_user_email)
    private EditText txtUserEmailField;
    @BindView(R.id.edit_register_user_password)
    private EditText txtPasswordField;
    @BindView(R.id.edit_register_user_repeatPassword)
    private EditText txtRepeatPasswordField;
    @BindView(R.id.edit_register_user_first_name)
    private EditText txtFirstNameField;
    @BindView(R.id.edit_register_user_lastName)
    private EditText txtLastNameField;
    @BindView(R.id.btn_already_member)
    private Button btnAlreadyMember;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        accountType = getIntent().getStringExtra(AchatAuthenticatorActivity.ARG_ACCOUNT_TYPE);
        btnAlreadyMember.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener()
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
        final String userEmail = txtUserEmailField.getText().toString().trim();
        final String userPassword = txtPasswordField.getText().toString().trim();
        final String userRepeatPassword = txtRepeatPasswordField.getText().toString().trim();
        final String userFirstName = txtFirstNameField.getText().toString().trim();
        final String userLastName = txtLastNameField.getText().toString().trim();


        if (!isValidEmail(userEmail))
        {
            txtUserEmailField.setError(getResources().getString(R.string.invalid_email_error_text));
            btnRegister.setEnabled(true);
            return;
        }

        if (!isValidPassword(userPassword))
        {
            txtPasswordField.setError(getResources().getString(R.string.invalid_password_error_text));
            btnRegister.setEnabled(true);
            return;
        }

        if (!userPassword.equals(userRepeatPassword))
        {
            txtRepeatPasswordField.setError(getResources().getString(R.string.passwords_do_not_match));
            btnRegister.setEnabled(true);
            return;
        }

        new AsyncTask<String, Void, Intent>()
        {
            @Override
            protected Intent doInBackground(String... strings)
            {
                Intent data = new Intent();
                try
                {
                    WebApiEndPointInterface apiInterface = ServiceGenerator.createService(WebApiEndPointInterface.class,
                            AccountGeneral.API_KEY);
                    Call<RegistrationResponse> apiCall = apiInterface.registerUser(new UserProfile(userEmail, userPassword, userFirstName, userLastName));

                    try
                    {
                        RegistrationResponse response = apiCall.execute().body();

                        if (response != null)
                        {
                            if (response.isRegistrationSuccessful())
                            {
                                data.putExtra(KEY_AUTH_TOKEN, response.getAuthToken());
                            }
                            else
                            {
                                data.putExtra(KEY_ERROR_MESSAGE, response.getRegistrationErrorMessage());
                            }
                        }
                        else
                        {
                            data.putExtra(KEY_ERROR_MESSAGE, "Registration failed.");
                        }
                    }
                    catch (IOException e)
                    {
                        data.putExtra(KEY_ERROR_MESSAGE, "Registration failed.");
                    }
                }
                catch (Exception e)
                {
                    data.putExtra(KEY_ERROR_MESSAGE, "Registration failed.");
                }

                return data;
            }

            @Override
            protected void onPostExecute(Intent intent)
            {
                btnRegister.setEnabled(true);
                if (intent.hasExtra(KEY_ERROR_MESSAGE))
                {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE),
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Success...!",
                            Toast.LENGTH_SHORT).show();
                    //// TODO: 8/18/2016 redirect to the login screen
                }
            }
        }.execute();


    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private boolean isValidEmail(String email)
    {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass)
    {
        return pass != null && pass.length() > 6;
    }
}
