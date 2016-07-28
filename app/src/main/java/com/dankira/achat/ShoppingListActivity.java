package com.dankira.achat;

import android.os.Bundle;

public class ShoppingListActivity extends SecuredAppCompatActivityBase
{

    private static final String SL_DETAIL_FRAGMENT_TAG = "SLDETAILSFRAG";
    private boolean isTwoPaneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        if (findViewById(R.id.shopping_list_detail_frame) != null)
        {
            isTwoPaneLayout = true;
            if (savedInstanceState == null)
            {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.shopping_list_detail_frame, new ShoppingListDetailFragment(),
                                SL_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        }
        else
        {
            isTwoPaneLayout = false;
        }
    }
    // TODO: 7/12/2016
    // check if the app is being sed on a tablet with main->detail split screen or on mobile where
    // there is only main

}
