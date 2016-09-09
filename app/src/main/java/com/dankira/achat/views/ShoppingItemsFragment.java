package com.dankira.achat.views;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.dankira.achat.IDialogSubmitListener;
import com.dankira.achat.R;
import com.dankira.achat.adapters.DividerItemDecoration;
import com.dankira.achat.adapters.ItemClickSupport;
import com.dankira.achat.adapters.ShoppingItemCursorAdapter;
import com.dankira.achat.models.ShoppingItem;
import com.dankira.achat.provider.AchatDbContracts;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShoppingItemsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, IDialogSubmitListener
{

    public static final String NEW_ITEM_BUNDLE_KEY = "new_item_bundle";
    public static final String EDIT_ITEM_BUNDLE_KEY = "edit_bundle_item_key";
    public static final String SELECTED_LIST_ID = "SELECTED_LIST_ID";
    private static final int ACHAT_ITEMS_CURSOR_ID = 200;
    private static final String SHOPPING_LIST_ID_KEY = "shopping_list_id";
    private static final String FRAGMENT_ADD_ITEM_TAG = "fragment_add_item-tag";
    private static final String[] SHOPPING_ITEMS_PROJECTION = {
            AchatDbContracts.ShoppingItemTable._ID,
            AchatDbContracts.ShoppingItemTable.LIST_GUID,
            AchatDbContracts.ShoppingItemTable.ITEM_NAME,
            AchatDbContracts.ShoppingItemTable.ITEM_DESCRIPTION,
            AchatDbContracts.ShoppingItemTable.ITEM_QUANTITY,
            AchatDbContracts.ShoppingItemTable.ITEM_CHECKED,
            AchatDbContracts.ShoppingItemTable.ITEM_BAR_CODE
    };
    private static final String SHOPPING_ITEM_BY_LIST_WHERE = AchatDbContracts.ShoppingItemTable.LIST_GUID
            + " =? ";
    public static final String DEFAULT_ITEM_QTY = "1";
    private String shoppingListId;
    private ShoppingItemCursorAdapter shoppingItemCursorAdapter;

    @BindView(R.id.add_new_item)
    private FloatingActionButton addNewItem;
    @BindView(R.id.shopping_items_rv_container)
    private FrameLayout itemsRecyclerViewContainer;
    @BindView(R.id.empty_items_placeholder)
    private LinearLayout emptyItemsContainer;
    @BindView(R.id.shopping_items_swipe_refresh)
    private SwipeRefreshLayout shopping_items_swipe_refresh_layout;
    @BindView(R.id.shopping_items_recycler_view)
    private RecyclerView recyclerView;

    private Unbinder unbinder;

    public static ShoppingItemsFragment newInstance(String shoppingListId)
    {
        ShoppingItemsFragment instance = new ShoppingItemsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_shopping_items, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        Bundle args = getArguments();

        if (args != null)
        {
            shoppingListId = args.getString(SHOPPING_LIST_ID_KEY);
        }

        addNewItem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showNewItemDialog();
            }
        });

        shoppingItemCursorAdapter = new ShoppingItemCursorAdapter(getActivity(), null);

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

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.shopping_item_divider));
        recyclerView.setAdapter(shoppingItemCursorAdapter);

        shopping_items_swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                onReloadItems();
            }
        });
        shopping_items_swipe_refresh_layout.setColorSchemeResources(R.color.icon_color_default);
        return rootView;
    }

    private void showNewItemDialog()
    {
        final MaterialDialog dialog = new MaterialDialog.Builder(ShoppingItemsFragment.this.getActivity())
                .title(R.string.add_new_item_dialog_title)
                .iconRes(R.drawable.icon_cart_add)
                .limitIconToDefaultSize()
                .customView(R.layout.fragment_add_new_item, true)
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
        final EditText itemTitleInput = (EditText) dialog.getCustomView().findViewById(R.id.edit_item_title);
        final EditText itemDescInput = (EditText) dialog.getCustomView().findViewById(R.id.edit_item_desc);
        final ImageButton scanBarCode = (ImageButton) dialog.getCustomView().findViewById(R.id.btn_scan_item_barcode);
        final ImageButton qtyPlusOneButton = (ImageButton) dialog.getCustomView().findViewById(R.id.btn_item_qty_plus_one);
        final TextView itemQtyText = (TextView) dialog.getCustomView().findViewById(R.id.new_item_qty);
        final TextView itemBarCode = (TextView) dialog.getCustomView().findViewById(R.id.txt_item_barcode);
        itemQtyText.setText(DEFAULT_ITEM_QTY);
        itemTitleInput.addTextChangedListener(new TextWatcher()
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
                ShoppingItem item = new ShoppingItem();
                item.setItemTitle(itemTitleInput.getText().toString().trim());
                item.setItemDescription(itemDescInput.getText().toString().trim());
                item.setItemQuantity(Integer.parseInt(itemQtyText.getText().toString().trim()));
                item.setBarCode(itemBarCode.getText().toString().trim());

                ContentValues cv = new ContentValues();
                cv.put(AchatDbContracts.ShoppingListTable.LIST_TITLE, item.getItemTitle());
                cv.put(AchatDbContracts.ShoppingListTable.LIST_DESCRIPTION, item.getItemDescription());
                // add the passed shopping list item to the database.
                getContext().getContentResolver().insert(AchatDbContracts.ShoppingListTable.CONTENT_URI, cv);

                // TODO: 8/30/2016 Sync immediately

                //reset loader
                getLoaderManager().restartLoader(ACHAT_ITEMS_CURSOR_ID, null, ShoppingItemsFragment.this);

                dialog.dismiss();

            }
        });
        scanBarCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(ShoppingItemsFragment.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.setPrompt(getResources().getString(R.string.bar_code_scanner_prompt));
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();

            }
        });
        qtyPlusOneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int qty = Integer.parseInt(itemQtyText.getText().toString().trim());
                itemQtyText.setText(String.format(Locale.US, "%s", Integer.toString(qty + 1)));
            }
        });
        dialog.show();
        positiveAction.setEnabled(false);
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

    private void addNewListItem(String itemBarCodeScanned)
    {
        // 1. check if the item already exists in the local storage with the same barcode.
        ShoppingItem item = getItemFromPastItemsByBarcode(itemBarCodeScanned);

        if (item != null)
        {
            //2. If the item exists then add the item
            // then initiate the barcode scanner again.
            // IntentIntegrator.forSupportFragment(ShoppingItemsFragment.this).initiateScan();
        }
        else
        {
            //itemBarCode.setText(itemBarCodeScanned);
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
        if (shoppingListId != null)
        {
            CursorLoader loader = new CursorLoader(getActivity(), shoppingItemsUri,
                    SHOPPING_ITEMS_PROJECTION, SHOPPING_ITEM_BY_LIST_WHERE, new String[]{shoppingListId},
                    AchatDbContracts.ShoppingItemTable.DEFAULT_SORT_ORDER);

            return loader;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        shoppingItemCursorAdapter.swapCursor(cursor);
        if (cursor == null || cursor.getCount() < 1)
        {
            emptyItemsContainer.setVisibility(View.VISIBLE);
            itemsRecyclerViewContainer.setVisibility(View.GONE);
        }
        else
        {

            emptyItemsContainer.setVisibility(View.GONE);
            itemsRecyclerViewContainer.setVisibility(View.VISIBLE);
        }

        shopping_items_swipe_refresh_layout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        shoppingItemCursorAdapter.swapCursor(null);
    }

    @Override
    public void OnDialogSubmit(Bundle bundle)
    {
        if (bundle.containsKey(NEW_ITEM_BUNDLE_KEY))
        {
            ShoppingItem item = (ShoppingItem) bundle.getSerializable(NEW_ITEM_BUNDLE_KEY);

            if (item != null)
            {
                ContentValues cv = new ContentValues();

                cv.put(AchatDbContracts.ShoppingItemTable.ITEM_NAME, item.getItemTitle());
                cv.put(AchatDbContracts.ShoppingItemTable.ITEM_DESCRIPTION, item.getItemDescription());
                cv.put(AchatDbContracts.ShoppingItemTable.ITEM_BAR_CODE, item.getBarCode());
                cv.put(AchatDbContracts.ShoppingItemTable.ITEM_QUANTITY, item.getItemQuantity());
                // add the passed shopping list item to the database.
                getContext().getContentResolver().insert(AchatDbContracts.ShoppingItemTable.CONTENT_URI, cv);
                //reset loader
                getLoaderManager().restartLoader(ACHAT_ITEMS_CURSOR_ID, null, this);
            }
        }
        else if (bundle.containsKey(EDIT_ITEM_BUNDLE_KEY))
        {
            ShoppingItem editedItem = (ShoppingItem) bundle.getSerializable(EDIT_ITEM_BUNDLE_KEY);

            if (editedItem != null)
            {
                ContentValues cv = new ContentValues();
                cv.put(AchatDbContracts.ShoppingItemTable._ID, editedItem.getId());
                cv.put(AchatDbContracts.ShoppingItemTable.ITEM_NAME, editedItem.getItemTitle());
                cv.put(AchatDbContracts.ShoppingItemTable.ITEM_DESCRIPTION, editedItem.getItemDescription());
                cv.put(AchatDbContracts.ShoppingItemTable.ITEM_BAR_CODE, editedItem.getBarCode());
                cv.put(AchatDbContracts.ShoppingItemTable.ITEM_QUANTITY, editedItem.getItemQuantity());
                // add the passed shopping list item to the database.
                getContext().getContentResolver().update(AchatDbContracts.ShoppingItemTable.CONTENT_URI, cv, null, null);
                //reset loader
                getLoaderManager().restartLoader(ACHAT_ITEMS_CURSOR_ID, null, this);
            }
        }

    }

    public void onReloadItems()
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
            protected void onPostExecute(Void v)
            {
                if (getLoaderManager() != null)
                    getLoaderManager().restartLoader(ACHAT_ITEMS_CURSOR_ID, null, ShoppingItemsFragment.this);
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
