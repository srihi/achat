package com.dankira.achat.models;

import android.database.Cursor;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by da on 7/7/2016.
 */
public class ShoppingItem implements Serializable
{
    private int id;
    private String barCode;
    private String itemTitle;
    private String itemDescription;
    private Date itemAddedOn;
    private int itemQuantity;
    private int shoppingListId;

    public ShoppingItem()
    {
    }

    public ShoppingItem(int id, String barCode, String itemTitle, String itemDescription,
                        Date itemAddedOn, int itemQuantity, int shoppingListId)
    {
        this.id = id;
        this.barCode = barCode;
        this.itemTitle = itemTitle;
        this.itemDescription = itemDescription;
        this.itemAddedOn = itemAddedOn;
        this.itemQuantity = itemQuantity;
        this.shoppingListId = shoppingListId;
    }

    public static ShoppingItem fromCursor(Cursor cursor)
    {

        ShoppingItem shoppingItem = new ShoppingItem();
        // TODO: 7/11/2016 get the shopping item from the cursor here.
        // The cursor is already pointing to the right location of the element when this is called.
        // you only need to cast the column elements and create the right object to return.
        //T element = cursor.getT(cursor.getColumnIndex(ShoppingListDb.SOME_COLUMN_NAME))
        return shoppingItem;
    }

    public int getId()
    {
        return id;
    }

    public String getBarCode()
    {
        return barCode;
    }

    public void setBarCode(String barCode)
    {
        this.barCode = barCode;
    }

    public String getItemTitle()
    {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle)
    {
        this.itemTitle = itemTitle;
    }

    public String getItemDescription()
    {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription)
    {
        this.itemDescription = itemDescription;
    }

    public Date getItemAddedOn()
    {
        return itemAddedOn;
    }

    public void setItemAddedOn(Date itemAddedOn)
    {
        this.itemAddedOn = itemAddedOn;
    }

    public int getItemQuantity()
    {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity)
    {
        this.itemQuantity = itemQuantity;
    }

    public int getShoppingListId()
    {
        return shoppingListId;
    }

    public void setShoppingListId(int shoppingListId)
    {
        this.shoppingListId = shoppingListId;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ShoppingItem si = (ShoppingItem) obj;

        if (!Objects.equals(si.getItemTitle(), getItemTitle())) return false;
        if (!Objects.equals(si.getItemDescription(), getItemDescription())) return false;
        if (!Objects.equals(si.getBarCode(), getBarCode())) return false;
        if (!Objects.equals(si.getShoppingListId(), getShoppingListId())) return false;

        return true;
    }
}
