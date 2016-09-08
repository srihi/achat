package com.dankira.achat.api;

import com.google.gson.annotations.Expose;

/**
 * Created by da on 8/24/2016.
 */
public class ShoppingListParam
{
    @Expose
    private String listName;
    @Expose
    private String listDescription;

    public ShoppingListParam(String listName, String listDescription)
    {
        this.listName = listName;
        this.listDescription = listDescription;
    }
}
