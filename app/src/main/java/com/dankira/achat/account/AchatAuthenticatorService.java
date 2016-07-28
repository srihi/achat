package com.dankira.achat.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by da on 7/6/2016.
 */
public class AchatAuthenticatorService extends Service
{
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        AchatAuthenticator authenticator = new AchatAuthenticator(this);
        return authenticator.getIBinder();
    }
}
