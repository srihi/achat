package com.dankira.achat.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dankira.achat.R;
import com.dankira.achat.models.ShoppingList;
import com.dankira.achat.provider.AchatDbContracts;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by skyfishjy on 10/31/14.
 */
public class ShoppingListCursorAdapter extends CursorRecyclerViewAdapter<ShoppingListCursorAdapter.ViewHolder>
{
    private Context currentContext;
    private static final int VIEW_HOLDER_TYPE_1 = 100;
    private static final int VIEW_HOLDER_TYPE_2 = 200;

    public ShoppingListCursorAdapter(Context context, Cursor cursor)
    {
        super(context, cursor);
        currentContext = context;
    }

    @Override
    public int getItemViewType(int position)
    {
        Cursor currentCursor = getCursor();

        if (currentCursor != null && currentCursor.moveToPosition(position))
        {
            boolean isShared = currentCursor.getInt(currentCursor.getColumnIndex(AchatDbContracts.ShoppingListTable.LIST_SHARE_STATUS)) == 1;
            if (isShared)
                return VIEW_HOLDER_TYPE_1;
        }

        return VIEW_HOLDER_TYPE_2;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // return a new view holder for shared lists.
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_shopping_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor)
    {
        // bind depending on the Viewholder type.
        ShoppingList list = ShoppingList.fromCursor(cursor);
        int itemCount = list.getItemCount();
        String itemsCountFormatted = currentContext.getResources().getQuantityString(R.plurals.items_count_formatted, itemCount, itemCount);
        viewHolder.shoppingListItemCount.setText(itemsCountFormatted);
        viewHolder.shoppingListTitle.setText(list.getListTitle());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.txt_shopping_list_title)
        public TextView shoppingListTitle;
        @BindView(R.id.txt_shopping_list_item_count)
        public TextView shoppingListItemCount;

        public ViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}