package com.dankira.achat.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.dankira.achat.account.AccountGeneral;
import com.dankira.achat.api.ServiceGenerator;
import com.dankira.achat.api.ShoppingItem_DTO;
import com.dankira.achat.api.ShoppingList_DTO;
import com.dankira.achat.api.WebApiEndPointInterface;
import com.dankira.achat.models.ShoppingList;
import com.dankira.achat.provider.AchatDbContracts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Response;

public class AchatSyncaAdapter extends AbstractThreadedSyncAdapter
{
    private static final String LOG_TAG = AchatSyncaAdapter.class.getSimpleName();

    public static final String ACTION_DATA_UPDATED = "com.dankira.achat.action.ACTION_DATA_UPDATED";

    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEX_TIME = SYNC_INTERVAL / 3;
    private static final String[] NOTIFY_ITEM_ADDED_PROJECTION = new String[]{
            AchatDbContracts.ShoppingItemTable.ITEM_NAME,
            AchatDbContracts.ShoppingItemTable.ITEM_QUANTITY
    };
    private static final String[] SHOPPING_LIST_PROJECTION = {
            AchatDbContracts.ShoppingListTable._ID,
            AchatDbContracts.ShoppingListTable.LIST_GUID,
            AchatDbContracts.ShoppingListTable.LIST_TITLE,
            AchatDbContracts.ShoppingListTable.LIST_DESCRIPTION,
            AchatDbContracts.ShoppingListTable.LIST_CREATED_ON,
            AchatDbContracts.ShoppingListTable.LIST_SHARE_STATUS
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

    public static void initializeSyncAdapter(Context context)
    {
        //force the sync adapter to authenticate
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              ContentProviderClient contentProviderClient, SyncResult syncResult)
    {
        Log.v(TAG, "Started sync...");

        List<ShoppingList_DTO> allShoppingListFromServer = new ArrayList<>();
        List<ShoppingList> allShoppingListFromLocal = new ArrayList<>();

        try
        {
            String authToken = accountManager.blockingGetAuthToken(account, AccountGeneral.AUTHTOKEN_TYYPE_FULL_ACCESS, true);
            WebApiEndPointInterface api = ServiceGenerator.createService(WebApiEndPointInterface.class, AccountGeneral.API_KEY);

            Call<ArrayList<ShoppingList_DTO>> call_shopping_lists = api.getShoppingLists(authToken);

            try
            {
                Response<ArrayList<ShoppingList_DTO>> response = call_shopping_lists.execute();
                allShoppingListFromServer = response.body();

            }
            catch (IOException e)
            {
                Log.e(LOG_TAG, "An exception occurred while fetching shopping lists from server. Exception: "+e.getMessage());
            }

            if (allShoppingListFromServer == null) return;

            Cursor cursor = getContext().getContentResolver().query(AchatDbContracts.ShoppingListTable.CONTENT_URI,
                    SHOPPING_LIST_PROJECTION, null, null, AchatDbContracts.ShoppingListTable.DEFAULT_SORT_ORDER);

            if (cursor != null && cursor.moveToFirst())
            {
                do
                {
                    ShoppingList sl = ShoppingList.fromCursor(cursor);
                    allShoppingListFromLocal.add(sl);
                }
                while (cursor.moveToNext());
            }

            // TODO: 9/6/2016 this is where we implement the sync algorith based on the comparison of the two lists.

            Vector<ContentValues> cvVector = new Vector<ContentValues>(allShoppingListFromServer.size());

            for (ShoppingList_DTO listITem : allShoppingListFromServer)
            {
                ContentValues shoppingListValues = new ContentValues();

                shoppingListValues.put(AchatDbContracts.ShoppingListTable.LIST_TITLE, listITem.getList_title());
                shoppingListValues.put(AchatDbContracts.ShoppingListTable.LIST_DESCRIPTION, listITem.getList_description());
                shoppingListValues.put(AchatDbContracts.ShoppingListTable.LIST_GUID, listITem.getList_guid());
                shoppingListValues.put(AchatDbContracts.ShoppingListTable.LIST_SHARE_STATUS, listITem.getShare_Status() ? 1 : 0);
                shoppingListValues.put(AchatDbContracts.ShoppingListTable.LIST_CREATED_ON, listITem.getCreated_on());

                cvVector.add(shoppingListValues);
            }

            if (cvVector.size() > 0)
            {
                getContext().getContentResolver().bulkInsert(AchatDbContracts.ShoppingListTable.CONTENT_URI,
                        cvVector.toArray(new ContentValues[cvVector.size()]));
            }

            Call<ArrayList<ShoppingItem_DTO>> call_all_items = api.getAllShoppingItems(authToken);
            List<ShoppingItem_DTO> shoppingItems = null;

            try
            {
                shoppingItems = call_all_items.execute().body();
            }
            catch (Exception e)
            {
                Log.e(LOG_TAG, "An exception occurred while fetching shopping items from server. Exception: "+e.getMessage());
            }

            Vector<ContentValues> itemsContentValuesVector = new Vector<ContentValues>(shoppingItems.size());

            for (ShoppingItem_DTO item : shoppingItems)
            {
                ContentValues shoppingItemContentValue = new ContentValues();

                shoppingItemContentValue.put(AchatDbContracts.ShoppingItemTable.LIST_GUID, item.getList_guid());
                shoppingItemContentValue.put(AchatDbContracts.ShoppingItemTable.ITEM_NAME, item.getItem_title());
                shoppingItemContentValue.put(AchatDbContracts.ShoppingItemTable.ITEM_DESCRIPTION, item.getItem_description());
                shoppingItemContentValue.put(AchatDbContracts.ShoppingItemTable.ITEM_GROUP, item.getItem_group());
                shoppingItemContentValue.put(AchatDbContracts.ShoppingItemTable.ITEM_QUANTITY, item.getItem_quantity());
                shoppingItemContentValue.put(AchatDbContracts.ShoppingItemTable.ITEM_CHECKED, item.isItem_checked() ? 1 : 0);
                shoppingItemContentValue.put(AchatDbContracts.ShoppingItemTable.ITEM_BAR_CODE, item.getItem_bar_code());
                shoppingItemContentValue.put(AchatDbContracts.ShoppingItemTable.ITEM_CREATED_ON, item.getItem_created_on());

                itemsContentValuesVector.add(shoppingItemContentValue);
            }

            if (itemsContentValuesVector.size() > 0)
            {
                getContext().getContentResolver().bulkInsert(AchatDbContracts.ShoppingItemTable.CONTENT_URI,
                        itemsContentValuesVector.toArray(new ContentValues[itemsContentValuesVector.size()]));
            }

            Context context = getContext();
            // Setting the package ensures that only components in our app will receive the broadcast
            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED).setPackage(context.getPackageName());
            context.sendBroadcast(dataUpdatedIntent);

        }
        catch (OperationCanceledException e)
        {
            Log.e(LOG_TAG, "OperationCanceledException occurred while syncing. Exception: "+e.getMessage());
        }
        catch (IOException e)
        {
            syncResult.stats.numIoExceptions++;
            Log.e(LOG_TAG, "IOException occurred while syncing. Exception: "+e.getMessage());
        }
        catch (AuthenticatorException e)
        {
            syncResult.stats.numAuthExceptions++;
            Log.e(LOG_TAG, "AuthenticatorException occurred while syncing. Exception: "+e.getMessage());
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, "An exception occurred while syncing. Exception: "+e.getMessage());
        }
    }
}
