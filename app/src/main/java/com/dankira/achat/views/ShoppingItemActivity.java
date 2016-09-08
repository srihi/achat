package com.dankira.achat.views;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.dankira.achat.R;
import com.dankira.achat.models.ShoppingList;
import com.dankira.achat.provider.AchatDbContracts;

public class ShoppingItemActivity extends SecuredAppCompatActivityBase
{
    private String selectedListId = "";
    private static final String[] SHOPPING_LIST_PROJECTION = {
            AchatDbContracts.ShoppingListTable._ID,
            AchatDbContracts.ShoppingListTable.LIST_GUID,
            AchatDbContracts.ShoppingListTable.LIST_TITLE,
            AchatDbContracts.ShoppingListTable.LIST_DESCRIPTION,
            AchatDbContracts.ShoppingListTable.LIST_CREATED_ON,
            AchatDbContracts.ShoppingListTable.LIST_SHARE_STATUS
    };
    private TextView title_bar_text;
    private ShoppingList currentShoppingList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.shopping_item_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {

            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            /*getSupportActionBar().setLogo(R.drawable.icon_achat_app);
            getSupportActionBar().setDisplayUseLogoEnabled(true);*/
        }
        title_bar_text = (TextView)findViewById(R.id.shopping_items_toolbar_title);
        if (savedInstanceState == null)
        {
            selectedListId = getIntent().getStringExtra(ShoppingItemsFragment.SELECTED_LIST_ID);
            ShoppingItemsFragment fragment = ShoppingItemsFragment.newInstance(selectedListId);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_shopping_items, fragment)
                    .commit();

            Cursor shoppingListCursor = getContentResolver().query(
                    AchatDbContracts.ShoppingListTable.CONTENT_URI.buildUpon().appendPath(selectedListId).build(),
                    SHOPPING_LIST_PROJECTION, null, null,
                    AchatDbContracts.ShoppingListTable.DEFAULT_SORT_ORDER
            );
            if (shoppingListCursor != null && shoppingListCursor.moveToFirst())
            {
                currentShoppingList = ShoppingList.fromCursor(shoppingListCursor);
                title_bar_text.setText(currentShoppingList.getListTitle().trim());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_shopping_item, menu);
        return true;
    }

    /*@Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if (currentShoppingList != null && currentShoppingList.getItemCount() < 1) {
            menu.findItem(R.id.action_share).setEnabled(false);
            // You can also use something like:
            // menu.findItem(R.id.example_foobar).setEnabled(false);
        }
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_share:
                Intent shareIntent = new Intent(this, ShareListActivity.class);
                shareIntent.putExtra(ShareListActivity.LIST_GUID_PARAM_KEY, selectedListId);
                startActivity(shareIntent);
                break;

            default:
                super.onOptionsItemSelected(item);
                break;
        }

        return true;
    }
}
