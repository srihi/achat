package com.dankira.achat.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.dankira.achat.api.ServiceGenerator;
import com.dankira.achat.api.UserCredentials;
import com.dankira.achat.api.UserProfile;
import com.dankira.achat.api.WebApiEndPointInterface;

import java.io.IOException;

import retrofit2.Call;

public class AchatAuthenticator extends AbstractAccountAuthenticator
{

    private static final String LOG_TAG = AchatAuthenticator.class.getSimpleName();
    private final Context currentContext;

    public AchatAuthenticator(Context context)
    {
        super(context);
        this.currentContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                             String authTokenType, String[] requiredFeatures, Bundle options)
            throws NetworkErrorException
    {
        final Intent intent = new Intent(currentContext, AchatAuthenticatorActivity.class);
        intent.putExtra(AchatAuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AchatAuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AchatAuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features)
            throws NetworkErrorException
    {
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException
    {
        final AccountManager accountManager = AccountManager.get(currentContext);

        String authToken = accountManager.peekAuthToken(account, authTokenType);

        if (TextUtils.isEmpty(authToken))
        {
            final String password = accountManager.getPassword(account);
            if (password != null)
            {
                WebApiEndPointInterface apiInterface = ServiceGenerator.createService(WebApiEndPointInterface.class);
                Call<UserProfile> apiCall = apiInterface.loginUser(new UserCredentials(account.name, password));

                try
                {
                    UserProfile userProfile = apiCall.execute().body();
                    authToken =  userProfile.getAuthToken();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }

        if(!TextUtils.isEmpty(authToken))
        {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);

            return result;
        }

        final Intent loginIntent = new Intent(currentContext, AchatAuthenticatorActivity.class);
        loginIntent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        loginIntent.putExtra(AchatAuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
        loginIntent.putExtra(AchatAuthenticatorActivity.ARG_ACCOUNT_NAME, account.name);
        loginIntent.putExtra(AchatAuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, loginIntent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle bundle)
            throws NetworkErrorException
    {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType)
    {
        return authTokenType + " (Label)";
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                    Account account, String s, Bundle bundle)
            throws NetworkErrorException
    {
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s)
    {
        return null;
    }

}
