package com.dankira.achat.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dankira.achat.R;
import com.dankira.achat.models.ShoppingList;

/**
 * Created by skyfishjy on 10/31/14.
 */
public class ShoppingListCursorAdapter extends CursorRecyclerViewAdapter<ShoppingListCursorAdapter.ViewHolder>
{

    public ShoppingListCursorAdapter(Context context, Cursor cursor)
    {
        super(context, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_shopping_list, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor)
    {
        ShoppingList list = ShoppingList.fromCursor(cursor);

        viewHolder.shoppingListItemCount.setText(list.getItemCount());
        viewHolder.shoppingListTitle.setText(list.getListTitle());
        //viewHolder.shoppingListIcon.setImageResource();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView shoppingListIcon;
        public TextView shoppingListTitle;
        public TextView shoppingListItemCount;

        public ViewHolder(View view)
        {
            super(view);
            shoppingListIcon = (ImageView) view.findViewById(R.id.img_shopping_list_icon);
            shoppingListTitle = (TextView) view.findViewById(R.id.txt_shopping_list_title);
            shoppingListItemCount = (TextView) view.findViewById(R.id.txt_shopping_list_item_count);
        }
    }
}