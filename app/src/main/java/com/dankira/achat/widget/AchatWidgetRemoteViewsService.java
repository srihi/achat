package com.dankira.achat.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.dankira.achat.R;
import com.dankira.achat.models.ShoppingList;
import com.dankira.achat.provider.AchatDbContracts;
import com.dankira.achat.views.ShoppingListActivity;

/**
 * Created by da on 9/6/2016.
 */
public class AchatWidgetRemoteViewsService extends RemoteViewsService
{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent)
    {
        return new RemoteViewsFactory()
        {
            private Cursor currentCursor = null;
            @Override
            public void onCreate()
            {

            }

            @Override
            public void onDataSetChanged()
            {
                if(currentCursor != null)
                {
                    currentCursor.close();
                }

                final long callerIdentityToken = Binder.clearCallingIdentity();

                currentCursor = getContentResolver().query(AchatDbContracts.ShoppingListTable.CONTENT_URI,
                        null,
                        null,
                        null,
                        AchatDbContracts.ShoppingListTable.LIST_CREATED_ON +" DESC");
                Binder.restoreCallingIdentity(callerIdentityToken);
            }

            @Override
            public void onDestroy()
            {
                if(currentCursor != null)
                {
                    currentCursor.close();
                    currentCursor = null;
                }
            }

            @Override
            public int getCount()
            {
                return currentCursor == null ? 0: currentCursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position)
            {
                if(position == AdapterView.INVALID_POSITION || currentCursor == null || !currentCursor.moveToPosition(position))
                {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_shopping_list_item_template);

                ShoppingList shoppingList = ShoppingList.fromCursor(currentCursor);

                views.setTextViewText(R.id.txt_shopping_list_title, shoppingList.getListTitle());
                String itemCountFormatted = getResources().getQuantityString(R.plurals.items_count_formatted, shoppingList.getItemCount(), shoppingList.getItemCount());
                views.setTextViewText(R.id.txt_shopping_list_item_count, itemCountFormatted);

                //final Intent fillInIntent = new Intent();

                Intent fillInIntent = new Intent();
                fillInIntent.putExtra(ShoppingListActivity.EXTRA_LIST_GUID, shoppingList.getListGuid());
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView()
            {
                return new RemoteViews(getPackageName(), R.layout.widget_layout);
            }

            @Override
            public int getViewTypeCount()
            {
                return 1;
            }

            @Override
            public long getItemId(int i)
            {
                if(currentCursor.moveToPosition(i))
                {
                    return currentCursor.getLong(currentCursor.getColumnIndex(AchatDbContracts.ShoppingListTable._ID));
                }
                return i;
            }

            @Override
            public boolean hasStableIds()
            {
                return true;
            }
        };
    }
}
