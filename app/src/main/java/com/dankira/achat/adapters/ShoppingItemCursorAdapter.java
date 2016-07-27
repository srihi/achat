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
import com.dankira.achat.models.ShoppingItem;

/**
 * Created by da on 7/7/2016.
 */
public class ShoppingItemCursorAdapter extends CursorRecyclerViewAdapter<ShoppingItemCursorAdapter.ViewHolder>
{

    public ShoppingItemCursorAdapter(Context context, Cursor cursor)
    {
        super(context, cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_shopping_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor)
    {

        ShoppingItem item = ShoppingItem.fromCursor(cursor);

        viewHolder.shoppingItemTitle.setText(item.getItemTitle());
        viewHolder.shoppingItemQty.setText(item.getItemQuantity());

    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView shoppingItemImage;
        public TextView shoppingItemTitle;
        public TextView shoppingItemQty;

        public ViewHolder(View itemLayoutView)
        {
            super(itemLayoutView);

            shoppingItemImage = (ImageView) itemLayoutView.findViewById(R.id.img_shopping_item_pic);
            shoppingItemTitle = (TextView) itemLayoutView.findViewById(R.id.txt_shopping_item_title);
            shoppingItemQty = (TextView) itemLayoutView.findViewById(R.id.txt_shopping_item_qty);
        }
    }
}

