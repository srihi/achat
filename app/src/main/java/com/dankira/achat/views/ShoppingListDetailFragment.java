package com.dankira.achat.views;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dankira.achat.R;
import com.dankira.achat.adapters.ItemClickSupport;
import com.dankira.achat.adapters.ShoppingItemCursorAdapter;
import com.dankira.achat.models.ShoppingItem;
import com.dankira.achat.provider.AchatDbContracts;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ShoppingListDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int ACHAT_ITEMS_CURSOR_ID = 200;
    private ShoppingItemCursorAdapter shoppingItemCursorAdapter;
    private static final String SHOPPING_LIST_ID_KEY = "shopping_list_id";
    private static final String FRAGMENT_ADD_ITEM_TAG = "fragment_add_item-tag";
    private String shoppingListId;

    private static final String[] SHOPPING_ITEMS_PROJECTION = {
            AchatDbContracts.ShoppingItemTable._ID,
            AchatDbContracts.ShoppingItemTable.ID_ShoppingList,
            AchatDbContracts.ShoppingItemTable.ITEM_NAME,
            AchatDbContracts.ShoppingItemTable.ITEM_DESCRIPTION,
            AchatDbContracts.ShoppingItemTable.ITEM_QUANTITY,
            AchatDbContracts.ShoppingItemTable.ITEM_CHECKED,
            AchatDbContracts.ShoppingItemTable.ITEM_BAR_CODE
    };

    private static final String SHOPPING_ITEM_BY_LIST_WHERE = " where "
            + AchatDbContracts.ShoppingItemTable.ID_ShoppingList
            + " =? ";

    public static ShoppingListDetailFragment newInstance(String shoppingListId)
    {
        ShoppingListDetailFragment instance = new ShoppingListDetailFragment();
        Bundle args = new Bundle();
        args.putString(SHOPPING_LIST_ID_KEY, shoppingListId);

        instance.setArguments(args);
        return instance;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(ACHAT_ITEMS_CURSOR_ID, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_shopping_list_detail, container, false);
        FloatingActionButton scanNewItem  = (FloatingActionButton) rootView.findViewById(R.id.add_new_item);
        scanNewItem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                IntentIntegrator.forSupportFragment(ShoppingListDetailFragment.this).initiateScan();
            }
        });

        Bundle args = getArguments();

        if (args != null)
        {
            shoppingListId = args.getString(SHOPPING_LIST_ID_KEY);
        }

        shoppingItemCursorAdapter = new ShoppingItemCursorAdapter(getActivity(), null);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.shopping_items_recycler_view);
        final ItemClickSupport itemClickSupport = ItemClickSupport.addTo(recyclerView);

        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener()
        {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v)
            {

            }
        });

        itemClickSupport.setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v)
            {
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            if (result.getContents() == null)
            {
                // there is nothing scanned, must have been canceled.
            }
            else
            {
                // TODO: 7/21/2016 This is where we get the code for sharing, then verify and move on.
                addNewListItem(result.getContents());
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void addNewListItem(String itemBarCode)
    {
        // 1. check if the item already exists in the local storage with the same barcode.
        ShoppingItem item = getItemFromPastItemsByBarcode(itemBarCode);

        if (item != null)
        {
            //2. If the item exists then add the item

            // then initiate the barcode scanner again.
            IntentIntegrator.forSupportFragment(ShoppingListDetailFragment.this).initiateScan();
        }
        else
        {
            FragmentManager manager = getFragmentManager();
            Fragment fragment = manager.findFragmentByTag(FRAGMENT_ADD_ITEM_TAG);

            if (fragment != null)
            {
                manager.beginTransaction().remove(fragment).commit();
            }

            AddNewItemDialog addNewItemDialog = AddNewItemDialog.newInstance(itemBarCode);
            addNewItemDialog.show(manager,FRAGMENT_ADD_ITEM_TAG);
        }


        //3. if the item does not exist, then open the add item list with a barcode already filled.
    }

    private ShoppingItem getItemFromPastItemsByBarcode(String itemBarCode)
    {
        return null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        Uri shoppingItemsUri = AchatDbContracts.ShoppingItemTable.CONTENT_URI;

        CursorLoader loader = new CursorLoader(getActivity(), shoppingItemsUri,
                SHOPPING_ITEMS_PROJECTION, SHOPPING_ITEM_BY_LIST_WHERE, new String[]{shoppingListId},
                AchatDbContracts.ShoppingItemTable.DEFAULT_SORT_ORDER);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        shoppingItemCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        shoppingItemCursorAdapter.swapCursor(null);
    }
}
