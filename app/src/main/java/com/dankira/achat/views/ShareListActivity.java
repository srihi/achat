package com.dankira.achat.views;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dankira.achat.R;
import com.dankira.achat.ShareCodeHelper;
import com.dankira.achat.api.ServiceGenerator;
import com.dankira.achat.api.WebApiEndPointInterface;
import com.dankira.achat.account.AccountGeneral;

import java.io.IOException;

import retrofit2.Call;

public class ShareListActivity extends SecuredAppCompatActivityBase
{

    private ImageView imgQRCode;
    private TextView txtShareCode;
    private Button btnNewShareCode;
    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);
        accountManager = AccountManager.get(this);
        imgQRCode = (ImageView) findViewById(R.id.img_share_qr_code);
        txtShareCode = (TextView) findViewById(R.id.txt_share_code);
        btnNewShareCode = (Button) findViewById(R.id.btn_new_share_code);

        setShareCard();

        btnNewShareCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setShareCard();
            }
        });

    }

    private void setShareCard()
    {
        Account account = accountManager.getAccountsByType(getResources().getString(R.string.account_type))[0];
        String authToken = "";
        String shareCode;

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

            Call<String> share_code_api_call = api.getShareCode();
            try
            {
                shareCode = share_code_api_call.execute().body();

                if (!TextUtils.isEmpty(shareCode))
                {
                    txtShareCode.setText(shareCode);
                    imgQRCode.setImageBitmap(ShareCodeHelper.generateShareCodeQR(shareCode));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }
}
