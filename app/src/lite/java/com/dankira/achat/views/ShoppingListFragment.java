package com.dankira.achat.views;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.dankira.achat.IDialogSubmitListener;
import com.dankira.achat.IShoppingListSelectedListener;
import com.dankira.achat.R;
import com.dankira.achat.adapters.DividerItemDecoration;
import com.dankira.achat.adapters.ItemClickSupport;
import com.dankira.achat.adapters.ShoppingListCursorAdapter;
import com.dankira.achat.models.ShoppingList;
import com.dankira.achat.provider.AchatDbContracts;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShoppingListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, IDialogSubmitListener, IRefreshListener
{
    public static final String NEW_LIST_BUNDLE_KEY = "new_list_item";
    public static final String EDITED_LIST_BUNDLE_KEY = "edited_list_item";
    private static final int ACHAT_CURSOR_ID = 100;
    private ShoppingListCursorAdapter shoppingListAdapter;
    private IShoppingListSelectedListener selectedListener;

    @BindView(R.id.shopping_list_swipe_refresh)
    private SwipeRefreshLayout shopping_list_swipe_refresh_layout;
    @BindView(R.id.add_first_list_button)
    private AppCompatImageButton addFirstListImageButton;
    @BindView(R.id.shopping_list_rv_container)
    private FrameLayout recyclerViewContainerLayout;
    @BindView(R.id.empty_list_placeholder)
    private FrameLayout emptyListPlaceHolder;
    @BindView(R.id.shopping_list_recycler_view)
    private RecyclerView recyclerView;
    @BindView(R.id.add_new_list)
    private FloatingActionButton addNewList;
    @BindView(R.id.adView)
    private AdView mAdView;

    private Unbinder unbinder;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            selectedListener = (IShoppingListSelectedListener) context;
            ShoppingListActivity parentActivity = (ShoppingListActivity) getActivity();

            if (parentActivity != null)
            {
                parentActivity.setRefreshListener(this);
            }
        }
        catch (ClassCastException exception)
        {
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        getLoaderManager().initLoader(ACHAT_CURSOR_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        addNewList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showAddNewListDialog();
            }
        });
        addFirstListImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showAddNewListDialog();
            }
        });
        shoppingListAdapter = new ShoppingListCursorAdapter(getActivity(), null);

        final ItemClickSupport itemClickSupport = ItemClickSupport.addTo(recyclerView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener()
        {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v)
            {
                Cursor current_cursor = shoppingListAdapter.getCursor();
                ShoppingList sl = new ShoppingList();
                if (current_cursor != null && current_cursor.moveToPosition(position))
                {
                    sl = ShoppingList.fromCursor(current_cursor);
                }
                selectedListener.OnShoppingListSelected(sl.getListGuid(), v);
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

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.shopping_list_divider));
        recyclerView.setAdapter(shoppingListAdapter);

        shopping_list_swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                ShoppingListFragment.this.onRefresh();
            }
        });

        shopping_list_swipe_refresh_layout.setColorSchemeResources(R.color.icon_color_default);

        MobileAds.initialize(getActivity().getApplicationContext(), getResources().getString(R.string.banner_ad_unit_id));

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        onRefresh();

        return rootView;
    }

    private void showAddNewListDialog()
    {
        final MaterialDialog dialog = new MaterialDialog.Builder(ShoppingListFragment.this.getActivity())
                .title(R.string.add_new_list_dialog_title)
                .iconRes(R.drawable.icon_basket)
                .limitIconToDefaultSize()
                .customView(R.layout.fragment_add_new_list_dialog, true)
                .positiveText(R.string.btn_add_list_label)
                .negativeText(R.string.btn_cancel_label)
                .titleGravity(GravityEnum.CENTER)
                .onPositive(new MaterialDialog.SingleButtonCallback()
                {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                    {

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback()
                {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                    {
                        dialog.dismiss();
                    }
                }).build();
        final MDButton positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        final EditText listTitleInput = (EditText) dialog.getCustomView().findViewById(R.id.edit_list_title);
        final EditText listDescInput = (EditText) dialog.getCustomView().findViewById(R.id.edit_list_description);
        listTitleInput.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence title, int start, int count, int after)
            {
                positiveAction.setEnabled(title.toString().trim().length() > 2);
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });
        positiveAction.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ShoppingList list = new ShoppingList();
                list.setListTitle(listTitleInput.getText().toString().trim());
                list.setListDesc(listDescInput.getText().toString().trim());

                ContentValues cv = new ContentValues();
                cv.put(AchatDbContracts.ShoppingListTable.LIST_TITLE, list.getListTitle());
                cv.put(AchatDbContracts.ShoppingListTable.LIST_DESCRIPTION, list.getListDesc());
                // add the passed shopping list item to the database.
                getContext().getContentResolver().insert(AchatDbContracts.ShoppingListTable.CONTENT_URI, cv);

                // TODO: 8/30/2016 Sync immediately

                //reset loader
                getLoaderManager().restartLoader(ACHAT_CURSOR_ID, null, ShoppingListFragment.this);

                dialog.dismiss();

            }
        });
        dialog.show();
        positiveAction.setEnabled(false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        Uri shoppingListUri = AchatDbContracts.ShoppingListTable.CONTENT_URI;
        CursorLoader cursorLoader = new CursorLoader(getActivity(), shoppingListUri, null,
                null, null, AchatDbContracts.ShoppingListTable.DEFAULT_SORT_ORDER);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        shoppingListAdapter.swapCursor(cursor);

        if (cursor == null || cursor.getCount() < 1)
        {
            emptyListPlaceHolder.setVisibility(View.VISIBLE);
            recyclerViewContainerLayout.setVisibility(View.GONE);
        }
        else
        {
            emptyListPlaceHolder.setVisibility(View.GONE);
            recyclerViewContainerLayout.setVisibility(View.VISIBLE);
        }

        shopping_list_swipe_refresh_layout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        shoppingListAdapter.swapCursor(null);
    }

    @Override
    public void OnDialogSubmit(Bundle bundle)
    {
        if (bundle.containsKey(NEW_LIST_BUNDLE_KEY))
        {
            ShoppingList list = (ShoppingList) bundle.getSerializable(NEW_LIST_BUNDLE_KEY);

            if (list != null)
            {
                ContentValues cv = new ContentValues();
                cv.put(AchatDbContracts.ShoppingListTable.LIST_TITLE, list.getListTitle());
                cv.put(AchatDbContracts.ShoppingListTable.LIST_DESCRIPTION, list.getListDesc());
                // add the passed shopping list item to the database.
                getContext().getContentResolver().insert(AchatDbContracts.ShoppingListTable.CONTENT_URI, cv);
                //reset loader
                getLoaderManager().restartLoader(ACHAT_CURSOR_ID, null, this);
            }
        }
        else if (bundle.containsKey(EDITED_LIST_BUNDLE_KEY))
        {
            ShoppingList list = (ShoppingList) bundle.getSerializable(EDITED_LIST_BUNDLE_KEY);

            if (list != null)
            {
                ContentValues cv = new ContentValues();
                cv.put(AchatDbContracts.ShoppingListTable._ID, list.getId());
                cv.put(AchatDbContracts.ShoppingListTable.LIST_TITLE, list.getListTitle());
                cv.put(AchatDbContracts.ShoppingListTable.LIST_DESCRIPTION, list.getListDesc());

                // add the passed shopping list item to the database.
                getContext().getContentResolver().update(AchatDbContracts.ShoppingListTable.CONTENT_URI, cv, null, null);
                //reset loader
                getLoaderManager().restartLoader(ACHAT_CURSOR_ID, null, this);
            }
        }

    }

    @Override
    public void onRefresh()
    {
        new AsyncTask<String, Void, Void>()
        {
            @Override
            protected Void doInBackground(String... strings)
            {
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                ContentResolver.requestSync(null, AchatDbContracts.CONTENT_AUTHORITY, bundle);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                getLoaderManager().restartLoader(ACHAT_CURSOR_ID, null, ShoppingListFragment.this);
            }
        }.execute();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }
}
