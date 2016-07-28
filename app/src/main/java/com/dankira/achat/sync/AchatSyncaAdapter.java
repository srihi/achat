package com.dankira.achat.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.dankira.achat.account.AccountGeneral;
import com.dankira.achat.api.ServiceGenerator;
import com.dankira.achat.api.ShoppingItem_DTO;
import com.dankira.achat.api.ShoppingList_DTO;
import com.dankira.achat.api.WebApiEndPointInterface;
import com.dankira.achat.provider.AchatDbContracts;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import retrofit2.Call;

public class AchatSyncaAdapter extends AbstractThreadedSyncAdapter
{

    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEX_TIME = SYNC_INTERVAL / 3;
    private static final String[] NOTIFY_ITEM_ADDED_PROJECTION = new String[]{
            AchatDbContracts.ShoppingItemTable.ITEM_NAME,
            AchatDbContracts.ShoppingItemTable.ITEM_QUANTITY,
            AchatDbContracts.ShoppingItemTable.ITEM_IMAGE
    };

    private static final int INDEX_ITEM_NAME = 0;
    private static final int INDEX_ITEM_QUANTITY = 1;
    private static final int INDEX_ITEM_IMAGE = 2;
    private final String TAG = AchatSyncaAdapter.class.getSimpleName();
    private final AccountManager accountManager;
    private Context currentContext;

    public AchatSyncaAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
        currentContext = context;
        accountManager = AccountManager.get(context);
    }

    public AchatSyncaAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs)
    {
        super(context, autoInitialize, allowParallelSyncs);
        currentContext = context;
        accountManager = AccountManager.get(context);
    }

    public static void syncImmediately(Context context)
    {
        AccountManager am = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        //ContentResolver.requestSync(am.getAccountsByType());
    }

    public static void initializeSyncAdapter(Context context)
    {
        //force the sync adapter to authenticate
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              ContentProviderClient contentProviderClient, SyncResult syncResult)
    {

        Log.v(TAG, "Started sync...");
        try
        {
            String authToken = accountManager.blockingGetAuthToken(account, AccountGeneral.AUTHTOKEN_TYYPE_FULL_ACCESS, true);
            WebApiEndPointInterface api = ServiceGenerator.createService(WebApiEndPointInterface.class, authToken);
            Call<List<ShoppingList_DTO>> call_shopping_lists = api.getShoppingLists();
            List<ShoppingList_DTO> shoppingList = null;

            try
            {
                shoppingList = call_shopping_lists.execute().body();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (shoppingList == null) return;

            Vector<ContentValues> cvVector = new Vector<ContentValues>(shoppingList.size());
            for (ShoppingList_DTO listITem : shoppingList)
            {
                ContentValues shoppingListValues = new ContentValues();
                shoppingListValues.put(AchatDbContracts.ShoppingListTable.LIST_TITLE, listITem.getName());
                shoppingListValues.put(AchatDbContracts.ShoppingListTable.LIST_DESCRIPTION, listITem.getDescription());
                shoppingListValues.put(AchatDbContracts.ShoppingListTable.LIST_CREATED_ON, listITem.getCreatedOn());

                cvVector.add(shoppingListValues);
            }
            if (cvVector.size() > 0)
            {
                getContext().getContentResolver().bulkInsert(AchatDbContracts.ShoppingListTable.CONTENT_URI,
                        cvVector.toArray(new ContentValues[cvVector.size()]));
            }

            Call<List<ShoppingItem_DTO>> call_all_items = api.getAllShoppingItems();
            List<ShoppingItem_DTO> shoppingItems = null;
            try
            {
                shoppingItems = call_all_items.execute().body();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            Vector<ContentValues> itemsContentValuesVector = new Vector<ContentValues>(shoppingItems.size());

            for (ShoppingItem_DTO item : shoppingItems)
            {
                ContentValues shoppingItemContentValue = new ContentValues();
                shoppingItemContentValue.put(AchatDbContracts.ShoppingItemTable.ID_ShoppingList, item.getShoppingListId());
                shoppingItemContentValue.put(AchatDbContracts.ShoppingItemTable.ITEM_NAME, item.getName());
                shoppingItemContentValue.put(AchatDbContracts.ShoppingItemTable.ITEM_DESCRIPTION, item.getDescription());
                shoppingItemContentValue.put(AchatDbContracts.ShoppingItemTable.ITEM_GROUP, item.getGroup());
                shoppingItemContentValue.put(AchatDbContracts.ShoppingItemTable.ITEM_QUANTITY, item.getQuantity());
                shoppingItemContentValue.put(AchatDbContracts.ShoppingItemTable.ITEM_CHECKED, item.getChecked());
                shoppingItemContentValue.put(AchatDbContracts.ShoppingItemTable.ITEM_IMAGE, item.getImage());
                shoppingItemContentValue.put(AchatDbContracts.ShoppingItemTable.ITEM_CREATED_ON, item.getCreatedOn());

                itemsContentValuesVector.add(shoppingItemContentValue);
            }
            if (itemsContentValuesVector.size() > 0)
            {
                getContext().getContentResolver().bulkInsert(AchatDbContracts.ShoppingItemTable.CONTENT_URI,
                        itemsContentValuesVector.toArray(new ContentValues[itemsContentValuesVector.size()]));
            }


        }
        catch (OperationCanceledException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            syncResult.stats.numIoExceptions++;
            e.printStackTrace();
        }
        catch (AuthenticatorException e)
        {
            syncResult.stats.numAuthExceptions++;
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
