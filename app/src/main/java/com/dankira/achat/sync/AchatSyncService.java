package com.dankira.achat.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by da on 7/6/2016.
 */
public class AchatSyncService extends Service
{

    private static final Object threadLockObject = new Object();
    private static AchatSyncaAdapter achatSyncaAdapter = null;

    @Override
    public void onCreate()
    {
        synchronized (threadLockObject)
        {
            if (achatSyncaAdapter == null)
                achatSyncaAdapter = new AchatSyncaAdapter(getApplicationContext(), true);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return achatSyncaAdapter.getSyncAdapterBinder();
    }
}
