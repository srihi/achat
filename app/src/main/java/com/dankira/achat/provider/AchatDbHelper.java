package com.dankira.achat.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AchatDbHelper extends SQLiteOpenHelper
{
    public AchatDbHelper(Context context)
    {
        super(context, AchatDbContracts.DATABASE_NAME, null, AchatDbContracts.DATABBASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        if (sqLiteDatabase.isReadOnly())
        {
            sqLiteDatabase = getWritableDatabase();
        }

        AchatDbContracts.ShoppingListTable.onCreate(sqLiteDatabase);
        AchatDbContracts.ShoppingItemTable.onCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer)
    {
        AchatDbContracts.ShoppingItemTable.onUpgrade(sqLiteDatabase, oldVer, newVer);
        AchatDbContracts.ShoppingListTable.onUpgrade(sqLiteDatabase, oldVer, newVer);
        onCreate(sqLiteDatabase);
    }
}
