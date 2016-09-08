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
import android.widget.ImageView;
import android.widget.TextView;

import com.dankira.achat.R;
import com.dankira.achat.ShareCodeHelper;
import com.dankira.achat.account.AccountGeneral;
import com.dankira.achat.api.ServiceGenerator;
import com.dankira.achat.api.ShareCodeResponse;
import com.dankira.achat.api.WebApiEndPointInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class ShareListActivity extends SecuredAppCompatActivityBase
{

    public static final String LIST_GUID_PARAM_KEY = "list_guid_param";
    public static final String SHARE_CODE_PARAM_KEY = "share_code_param";
    public static final String KEY_ERROR_MESSAGE = "error_message_param";
    private static final String LOG_TAG = ShareListActivity.class.getSimpleName();
    private ImageView imgQRCode;
    private TextView txtShareCode;
    private Button btnNewShareCode;
    private CoordinatorLayout coordLayout;
    private AccountManager accountManager;
    private String listGuid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);

        accountManager = AccountManager.get(this);
        imgQRCode = (ImageView) findViewById(R.id.img_share_qr_code);
        txtShareCode = (TextView) findViewById(R.id.txt_share_code);
        btnNewShareCode = (Button) findViewById(R.id.btn_new_share_code);
        coordLayout = (CoordinatorLayout) findViewById(R.id.share_list_activity_coord_layout);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            listGuid = extras.getString(LIST_GUID_PARAM_KEY);
        }
        else
        {
            Snackbar snackbar = Snackbar.make(coordLayout,
                    getResources().getString(R.string.error_msg_no_list_selected), Snackbar.LENGTH_LONG);
            snackbar.show();
            finish();
        }

        if (savedInstanceState == null)
        {
            setShareCard();
        }

        btnNewShareCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setShareCard();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_share_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setShareCard()
    {
        btnNewShareCode.setEnabled(false);

        new AsyncTask<String, Void, Intent>()
        {
            @Override
            protected Intent doInBackground(String... strings)
            {
                Account account = accountManager.getAccountsByType(getResources().getString(R.string.account_type))[0];
                String authToken = "";
                Intent data = new Intent();

                try
                {
                    authToken = accountManager.blockingGetAuthToken(account, AccountGeneral.AUTHTOKEN_TYYPE_FULL_ACCESS, true);
                }
                catch (OperationCanceledException | IOException | AuthenticatorException e)
                {
                    data.putExtra(KEY_ERROR_MESSAGE, "Unable to authenticate the current user.");
                }

                if (!TextUtils.isEmpty(authToken) && !TextUtils.isEmpty(listGuid))
                {
                    WebApiEndPointInterface api = ServiceGenerator.createService(WebApiEndPointInterface.class,
                            AccountGeneral.API_KEY);

                    Call<ShareCodeResponse> share_code_api_call = api.initShare(authToken, listGuid);
                    try
                    {
                        Response<ShareCodeResponse> shareCodeResponse = share_code_api_call.execute();
                        ShareCodeResponse shareCode = shareCodeResponse.body();

                        if (shareCode != null && shareCode.is_share_successful && !TextUtils.isEmpty(shareCode.share_code))
                        {
                            data.putExtra(SHARE_CODE_PARAM_KEY, shareCode.share_code);
                        }
                        else
                        {
                            data.putExtra(KEY_ERROR_MESSAGE, getResources().getString(R.string.error_msg_server_returned_empty_share_code));
                        }

                    }
                    catch (Exception e)
                    {
                        Log.e(LOG_TAG, e.getMessage());
                        data.putExtra(KEY_ERROR_MESSAGE, "An error occurred getting share card from the server.");
                    }
                }

                return data;
            }

            @Override
            protected void onPostExecute(Intent intent)
            {
                btnNewShareCode.setEnabled(true);

                if (intent.hasExtra(KEY_ERROR_MESSAGE))
                {
                    Snackbar snackbar = Snackbar.make(coordLayout, intent.getStringExtra(KEY_ERROR_MESSAGE), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else
                {
                    String code = intent.getStringExtra(SHARE_CODE_PARAM_KEY);
                    if (!TextUtils.isEmpty(code))
                    {
                        txtShareCode.setText(code);
                        int dipFactor = getResources().getDisplayMetrics().densityDpi;
                        imgQRCode.setImageBitmap(ShareCodeHelper.generateShareCodeQR(code, dipFactor));
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
