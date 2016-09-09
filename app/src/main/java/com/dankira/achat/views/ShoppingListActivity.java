package com.dankira.achat.views;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dankira.achat.IDialogSubmitListener;
import com.dankira.achat.IShoppingListSelectedListener;
import com.dankira.achat.R;
import com.dankira.achat.account.AccountGeneral;
import com.dankira.achat.utils.CryptoUtils;
import com.dankira.achat.utils.PicassoCircleTransform;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingListActivity extends SecuredAppCompatActivityBase
        implements IShoppingListSelectedListener, IDialogSubmitListener
{
    private static final String SL_DETAIL_FRAGMENT_TAG = "SLDETAILSFRAG";
    private static final String SL_FRAGMENT_TAG = "SL_FRAGMENT_TAG";
    private boolean isTwoPaneLayout;
    private IRefreshListener refreshListener;
    private static final String GRAVATAR_BASE_URL = "https://www.gravatar.com/avatar/";
    public static final String EXTRA_LIST_GUID = "intent_extra_list_guid";

    @BindView(R.id.main_activity_drawer_layout)
    private DrawerLayout navDrawerLayout;
    @BindView(R.id.navigation_view)
    private NavigationView navigationView;
    @BindView(R.id.shopping_list_toolbar)
    private Toolbar toolbar;
    @BindView(R.id.shopping_list_detail_frame)
    private FrameLayout flShoppingListContainerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        ButterKnife.bind(this);

        ShoppingListFragment slf = new ShoppingListFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.shopping_list_frame, slf, SL_FRAGMENT_TAG)
                .commit();

        View navHeaderView = navigationView.getHeaderView(0);
        if (navHeaderView != null)
        {
            TextView userName = (TextView) navHeaderView.findViewById(R.id.nav_drawer_user_email);
            Account[] accountsList = AccountManager.get(this).getAccounts();
            if (accountsList.length > 0)
            {
                String name = accountsList[0].name;
                userName.setText(name);

                final ImageView avatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_avatar_image);
                Picasso.with(this)
                        .load(GRAVATAR_BASE_URL + CryptoUtils.MD5(name))
                        .transform(new PicassoCircleTransform())
                        .placeholder(R.drawable.icon_account_circle)
                        .error(R.drawable.icon_account_circle)
                        .into(avatar);
            }

            ImageButton closeDrawerButton = (ImageButton) navHeaderView.findViewById(R.id.nav_drawer_close_button);

            if (closeDrawerButton != null)
            {
                closeDrawerButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (navDrawerLayout.isDrawerOpen(GravityCompat.START))
                            navDrawerLayout.closeDrawers();
                    }
                });
            }
        }


        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_drawer);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (flShoppingListContainerLayout != null)
        {
            isTwoPaneLayout = true;
            if (savedInstanceState == null)
            {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.shopping_list_detail_frame, new ShoppingItemsFragment(),
                                SL_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        }
        else
        {
            isTwoPaneLayout = false;
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                item.setChecked(true);

                switch (item.getItemId())
                {
                    case R.id.nav_drawer_home:
                        // go to the home intent
                        break;
                    case R.id.nav_drawer_add_shared_list:
                        Intent acceptShareIntent = new Intent(ShoppingListActivity.this, AcceptShareActivity.class);
                        startActivity(acceptShareIntent);
                        // open shared list
                        break;
                    case R.id.nav_drawer_help:
                        // open help on the internet
                        break;
                    case R.id.nav_drawer_manage_shared_list:
                        // open the manage share list interface
                        break;
                    case R.id.nav_drawer_settings:
                        // open the settings page
                        break;
                    case R.id.nav_logout:
                        final Account[] accountsList = AccountManager.get(ShoppingListActivity.this).getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
                        if (accountsList.length > 0)
                        {
                            new MaterialDialog.Builder(ShoppingListActivity.this)
                                    .title(R.string.logout_alert_dialog_title)
                                    .content(R.string.logout_alert_dialog_message)
                                    .positiveText(R.string.logout_button_label)
                                    .negativeText(R.string.btn_cancel_label)
                                    .onPositive(new MaterialDialog.SingleButtonCallback()
                                    {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                                        {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                                            {
                                                AccountManager.get(ShoppingListActivity.this).removeAccount(accountsList[0],
                                                        ShoppingListActivity.this,
                                                        null,
                                                        null);
                                                recreate();
                                            }
                                            else
                                            {
                                                AccountManager.get(ShoppingListActivity.this).removeAccount(accountsList[0], null, null);
                                            }
                                        }
                                    })
                                    .show();
                        }
                        break;
                    default:
                        break;
                }

                navDrawerLayout.closeDrawers();
                return true;
            }
        });

    }

    @Override
    public void OnShoppingListSelected(String list_guid, View view)
    {
        if (isTwoPaneLayout)
        {
            ShoppingItemsFragment df = ShoppingItemsFragment.newInstance(list_guid);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.shopping_list_detail_frame, df, SL_DETAIL_FRAGMENT_TAG)
                    .commit();
        }
        else
        {
            Intent listItemIntent = new Intent(this, ShoppingItemActivity.class);
            listItemIntent.putExtra(ShoppingItemsFragment.SELECTED_LIST_ID, list_guid);

            ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    new Pair<View, String>(view, getString(R.string.shopping_items_transition_name)));
            ActivityCompat.startActivity(this, listItemIntent, activityOptions.toBundle());
        }
    }

    @Override
    public void OnDialogSubmit(Bundle bundle)
    {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_shopping_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // open the navigation drawer
                navDrawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.action_refresh:
                if (refreshListener != null)
                {
                    refreshListener.onRefresh();
                }
                break;

            default:
                break;
        }

        return true;
    }

    public void setRefreshListener(IRefreshListener refreshListener)
    {
        this.refreshListener = refreshListener;
    }
}
