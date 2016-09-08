package com.dankira.achat.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.dankira.achat.R;
import com.dankira.achat.sync.AchatSyncaAdapter;
import com.dankira.achat.views.ShoppingListActivity;

/**
 * Created by da on 9/6/2016.
 */
public class AchatWidgetProvider extends AppWidgetProvider
{
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        for (int wid : appWidgetIds)
        {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            Intent intent = new Intent(context, ShoppingListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setPendingIntentTemplate(R.id.shopping_list_widget_lv, pendingIntent);
            views.setRemoteAdapter(R.id.shopping_list_widget_lv, new Intent(context, AchatWidgetRemoteViewsService.class));
            views.setEmptyView(R.id.shopping_list_widget_lv, R.id.shopping_list_empty_text);
            appWidgetManager.updateAppWidget(wid, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
        if (intent.getAction().equals(AchatSyncaAdapter.ACTION_DATA_UPDATED))
        {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.shopping_list_widget_lv);
        }
    }
}
