package com.dankira.achat;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dankira.achat.sync.AccountGeneral;

/**
 * Created by da on 7/28/2016.
 */
public class SecuredAppCompatActivityBase extends AppCompatActivity
{
    private AccountManager accountManager;
    private AppCompatActivity currentActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        currentActivity = this;
        accountManager = AccountManager.get(this);
        getTokenForAccountCreateIfNeeded(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYYPE_FULL_ACCESS);
    }

    private AccountManagerFuture<Bundle> getTokenForAccountCreateIfNeeded(String accountType, String authTokenType)
    {
        //This method may be called from any thread, but the returned AccountManagerFuture must not be used on the main thread.
        final AccountManagerFuture<Bundle> future = accountManager.getAuthTokenByFeatures(accountType, authTokenType, null, this, null, null,
                new AccountManagerCallback<Bundle>()
                {
                    @Override
                    public void run(AccountManagerFuture<Bundle> futureBundle)
                    {
                        if(futureBundle.isCancelled())
                        {
                            currentActivity.finish();
                            return;
                        }
                        Bundle resultBundle;
                        try
                        {
                            resultBundle = futureBundle.getResult();
                            final String authToken = resultBundle.getString(AccountManager.KEY_AUTHTOKEN);

                            //Log.d(LOG_TAG, ((authToken != null) ? "SUCCESS!\ntoken: " + authToken : "FAIL"));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            //Log.e(LOG_TAG, e.getStackTrace().toString());
                        }
                    }
                }
                , null);
        return future;
    }
}
