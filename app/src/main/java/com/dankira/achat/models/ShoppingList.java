package com.dankira.achat.models;

import android.database.Cursor;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by da on 7/7/2016.
 */
public class ShoppingList implements Serializable
{

    private int id;
    private String listTitle;
    private String listDesc;
    private int itemCount;
    private Date createdOn;
    private int shareStatus;

    public ShoppingList()
    {
    }

    public ShoppingList(int id, String listTitle, String listDesc, int itemCount, Date createdOn, int shareStatus)
    {
        this.id = id;
        this.listTitle = listTitle;
        this.listDesc = listDesc;
        this.itemCount = itemCount;
        this.createdOn = createdOn;
        this.shareStatus = shareStatus;
    }

    public static ShoppingList fromCursor(Cursor cursor)
    {
        ShoppingList shoppingList = new ShoppingList();

        // TODO: 7/11/2016 get the shopping list from the cursor here.
        // The cursor is already pointing to the right location of the element when this is called.
        // you only need to cast the column elements and create the right object to return.
        //T element = cursor.getT(cursor.getColumnIndex(ShoppingListDb.SOME_COLUMN_NAME))

        return shoppingList;
    }

    public int getId()
    {
        return id;
    }

    public String getListTitle()
    {
        return listTitle;
    }

    public void setListTitle(String listTitle)
    {
        this.listTitle = listTitle;
    }

    public String getListDesc()
    {
        return listDesc;
    }

    public void setListDesc(String listDesc)
    {
        this.listDesc = listDesc;
    }

    public Date getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn)
    {
        this.createdOn = createdOn;
    }

    public int getShareStatus()
    {
        return shareStatus;
    }

    public void setShareStatus(int shareStatus)
    {
        this.shareStatus = shareStatus;
    }

    public int getItemCount()
    {
        return itemCount;
    }

    public void setItemCount(int itemCount)
    {
        this.itemCount = itemCount;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ShoppingList sl = (ShoppingList) obj;

        if (!Objects.equals(sl.getListTitle(), getListTitle())) return false;
        if (!Objects.equals(sl.getListDesc(), getListDesc())) return false;

        return true;
    }
}
