package com.dankira.achat.views;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.dankira.achat.R;
import com.dankira.achat.account.AccountGeneral;
import com.dankira.achat.api.ServiceGenerator;
import com.dankira.achat.api.ShareStatus;
import com.dankira.achat.api.WebApiEndPointInterface;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;

import retrofit2.Call;

public class AcceptShareActivity extends SecuredAppCompatActivityBase
{
    private static final String LOG_TAG = AcceptShareActivity.class.getSimpleName();
    public static final String KEY_ERROR_MESSAGE = "error_message_param";
    private static final String KEY_SHARE_ACCEPT_SUCCEEDED = "share_accept_succeeded_param";
    private ImageButton btnScanQRCode;
    private EditText editShareCode;
    private Button btnSubmitShareCode;
    private CoordinatorLayout coordinatorLayout;
    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_share);
        accountManager = AccountManager.get(this);

        btnScanQRCode = (ImageButton) findViewById(R.id.btn_scan_qr_code);
        btnSubmitShareCode = (Button) findViewById(R.id.btn_submit_share_code);
        editShareCode = (EditText) findViewById(R.id.edit_share_code);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.accept_share_coord_layout);

        btnScanQRCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                IntentIntegrator integrator = new IntentIntegrator(AcceptShareActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt(getResources().getString(R.string.bar_code_scanner_prompt));
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });

        btnSubmitShareCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String shareCode = editShareCode.getText().toString().trim();
                if (!TextUtils.isEmpty(shareCode))
                {
                    completeListShare(shareCode);
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.accept_share_activity_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            if (result.getContents() == null)
            {
                Log.i(LOG_TAG, "the scanning operation returned with nothing scanned. Nothing to do here...");
            }
            else
            {
                completeListShare(result.getContents());
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void completeListShare(final String shareCode)
    {
        new AsyncTask<String, Void, Intent>()
        {
            @Override
            protected Intent doInBackground(String... strings)
            {
                Account account = accountManager.getAccountsByType(getResources().getString(R.string.account_type))[0];
                String authToken;
                Intent data = new Intent();

                try
                {
                    authToken = accountManager.blockingGetAuthToken(account, AccountGeneral.AUTHTOKEN_TYYPE_FULL_ACCESS, true);
                }
                catch (OperationCanceledException | IOException | AuthenticatorException e)
                {
                    Log.e(LOG_TAG, e.getMessage());
                    data.putExtra(KEY_ERROR_MESSAGE, getResources().getString(R.string.error_msg_authentication_error));
                    return data;
                }

                if (!TextUtils.isEmpty(authToken))
                {
                    WebApiEndPointInterface api = ServiceGenerator.createService(WebApiEndPointInterface.class,
                            AccountGeneral.API_KEY);
                    Call<ShareStatus> share_code_api_call = api.completeShare(authToken, shareCode);

                    try
                    {
                        ShareStatus verified = share_code_api_call.execute().body();

                        if (verified!=null && verified.isShareSucceeded())
                        {
                            return new Intent().putExtra(KEY_SHARE_ACCEPT_SUCCEEDED, true);
                        }
                        else
                        {
                            Log.e(LOG_TAG,"Server returned null.");
                            data.putExtra(KEY_ERROR_MESSAGE, getResources().getString(R.string.error_msg_server_returned_error));
                        }
                    }
                    catch (IOException e)
                    {
                        Log.e(LOG_TAG, e.getMessage());
                        data.putExtra(KEY_ERROR_MESSAGE, getResources().getString(R.string.error_msg_server_returned_error));
                        return data;
                    }
                }
                else
                {
                    Log.e(LOG_TAG, "Auth token for the current user is empty. This shows some programming error. User is invalidated at this point.");
                    data.putExtra(KEY_ERROR_MESSAGE, getResources().getString(R.string.error_msg_authentication_error));
                    return data;
                }

                return data;
            }

            @Override
            protected void onPostExecute(Intent intent)
            {
                if (intent.hasExtra(KEY_ERROR_MESSAGE))
                {
                    Snackbar snackbar = Snackbar.make(coordinatorLayout,
                            intent.getStringExtra(KEY_ERROR_MESSAGE),
                            Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else
                {
                    if (intent.hasExtra(KEY_SHARE_ACCEPT_SUCCEEDED) && intent.getBooleanExtra(KEY_SHARE_ACCEPT_SUCCEEDED, false))
                    {
                        Intent shoppingListIntent = new Intent(AcceptShareActivity.this, ShoppingListActivity.class);
                        startActivity(shoppingListIntent);
                        finish();
                    }
                    else
                    {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                getResources().getString(R.string.error_msg_server_returned_error),
                                Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }

            }
        }.execute();


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }

        return true;
    }
}
