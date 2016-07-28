package com.dankira.achat.views;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dankira.achat.R;
import com.dankira.achat.api.ServiceGenerator;
import com.dankira.achat.api.ShareStatus;
import com.dankira.achat.api.WebApiEndPointInterface;
import com.dankira.achat.account.AccountGeneral;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;

import retrofit2.Call;

public class AcceptShareActivity extends SecuredAppCompatActivityBase
{
    Button btnScanQRCode;
    EditText editShareCode;
    Button btnSubmitShareCode;

    AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_share);
        accountManager = AccountManager.get(this);

        btnScanQRCode = (Button) findViewById(R.id.btn_scan_qr_code);
        btnSubmitShareCode = (Button) findViewById(R.id.btn_submit_share_code);
        editShareCode = (EditText) findViewById(R.id.edit_share_code);

        btnScanQRCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new IntentIntegrator(AcceptShareActivity.this).initiateScan();
            }
        });

        btnSubmitShareCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            if (result.getContents() == null)
            {
                // there is nothing scanned, must have been canceled.
            }
            else
            {
                // TODO: 7/21/2016 This is where we get the code for sharing, then verify and move on.
                completeListShare(result.getContents());
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void completeListShare(String shareCode)
    {
        Account account = accountManager.getAccountsByType(getResources().getString(R.string.account_type))[0];
        String authToken = "";

        try
        {
            authToken = accountManager.blockingGetAuthToken(account, AccountGeneral.AUTHTOKEN_TYYPE_FULL_ACCESS, true);
        }
        catch (OperationCanceledException | IOException | AuthenticatorException e)
        {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(authToken))
        {
            WebApiEndPointInterface api = ServiceGenerator.createService(WebApiEndPointInterface.class, authToken);
            Call<ShareStatus> share_code_api_call = api.verifyShare(shareCode);

            try
            {
                ShareStatus verified = share_code_api_call.execute().body();

                if (verified.isShareSucceeded())
                {
                    Intent intent = new Intent(this, ShoppingListActivity.class);
                    startActivity(intent);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
