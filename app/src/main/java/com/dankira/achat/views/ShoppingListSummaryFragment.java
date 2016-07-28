package com.dankira.achat.views;

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

import com.dankira.achat.OnDialogSubmitListener;
import com.dankira.achat.R;
import com.dankira.achat.adapters.ItemClickSupport;
import com.dankira.achat.adapters.ShoppingListCursorAdapter;
import com.dankira.achat.provider.AchatDbContracts;

public class ShoppingListSummaryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        OnDialogSubmitListener
{
    private static final String FRAGMENT_ADD_LIST_TAG = "fragment_add_new_list";

    private static final String[] SHOPPING_LIST_PROJECTION = {
            AchatDbContracts.ShoppingListTable._ID,
            AchatDbContracts.ShoppingListTable.LIST_TITLE,
            AchatDbContracts.ShoppingListTable.LIST_DESCRIPTION,
            AchatDbContracts.ShoppingListTable.LIST_CREATED_ON,
            AchatDbContracts.ShoppingListTable.LIST_SHARE_STATUS
    };

    private static final int ACHAT_CURSOR_ID = 100;
    private ShoppingListCursorAdapter shoppingListAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(ACHAT_CURSOR_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_shopping_list_summary, container, false);
        FloatingActionButton addNewList = (FloatingActionButton) rootView.findViewById(R.id.add_new_list);
        addNewList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FragmentManager manager = getFragmentManager();
                Fragment fragment = manager.findFragmentByTag(FRAGMENT_ADD_LIST_TAG);

                if (fragment != null)
                {
                    manager.beginTransaction().remove(fragment).commit();
                }

                AddNewListDialog addNewListDialog = new AddNewListDialog();
                addNewListDialog.show(manager, FRAGMENT_ADD_LIST_TAG);
            }
        });

        shoppingListAdapter = new ShoppingListCursorAdapter(getActivity(), null);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.shopping_list_recycler_view);
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

        recyclerView.setAdapter(shoppingListAdapter);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        Uri shoppingListUri = AchatDbContracts.ShoppingListTable.CONTENT_URI;

        return new CursorLoader(getActivity(), shoppingListUri,SHOPPING_LIST_PROJECTION,
                null, null, AchatDbContracts.ShoppingListTable.DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        shoppingListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        shoppingListAdapter.swapCursor(null);
    }

    @Override
    public void OnDialogSubmit(Bundle bundle)
    {
    }
}
