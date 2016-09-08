package com.dankira.achat.api;

import com.google.gson.annotations.Expose;

/**
 * Created by da on 8/24/2016.
 */
public class ShoppingItemParam
{
    @Expose
    private String itemTitle;

    @Expose
    private String itemDescription;

    @Expose
    private int itemQuantity;

    public ShoppingItemParam(String itemTitle, String itemDescription, int itemQuantity)
    {
        this.itemTitle = itemTitle;
        this.itemDescription = itemDescription;
        this.itemQuantity = itemQuantity;
    }
}
