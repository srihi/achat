package com.dankira.achat.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dankira.achat.R;
import com.dankira.achat.models.ShoppingItem;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingItemCursorAdapter extends CursorRecyclerViewAdapter<ShoppingItemCursorAdapter.ViewHolder>
{
    private Context currentContext;

    public ShoppingItemCursorAdapter(Context context, Cursor cursor)
    {
        super(context, cursor);
        currentContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_shopping_item, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor)
    {
        ShoppingItem item = ShoppingItem.fromCursor(cursor);
        String checkedItem_cd = currentContext.getResources().getString(R.string.cd_is_item_checked);

        viewHolder.isItemChecked.setText(item.getItemTitle());

        if (item.getItemQuantity() > 1)
        {
            String listItemsCountFormat = currentContext.getResources().getString(R.string.list_items_count_formatted);
            String listItemCount = String.format(Locale.US, listItemsCountFormat, item.getItemQuantity());

            viewHolder.shoppingItemQty.setText(listItemCount);
        }
        else
        {
            viewHolder.shoppingItemQty.setVisibility(View.GONE);
        }

        viewHolder.isItemChecked.setChecked(item.isItemChecked());

        viewHolder.isItemChecked.setContentDescription(item.getItemQuantity() + " " + item.getItemTitle() + " " + (item.isItemChecked() ? checkedItem_cd : ""));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.txt_shopping_item_qty)
        public TextView shoppingItemQty;
        @BindView(R.id.ckbox_item_is_checked)
        public CheckBox isItemChecked;

        public ViewHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            ButterKnife.bind(this, itemLayoutView);
        }
    }
}

