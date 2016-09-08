package com.dankira.achat.models;

import android.database.Cursor;

import com.dankira.achat.provider.AchatDbContracts;

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
    private String listGuid;
    private boolean isItemChecked;

    public ShoppingItem()
    {
        itemQuantity = 1;
    }

    public ShoppingItem(int id, String barCode, String itemTitle, String itemDescription,
                        Date itemAddedOn, int itemQuantity, String listGuid, boolean isItemChecked)
    {
        this.id = id;
        this.barCode = barCode;
        this.itemTitle = itemTitle;
        this.itemDescription = itemDescription;
        this.itemAddedOn = itemAddedOn;
        this.itemQuantity = itemQuantity;
        this.listGuid = listGuid;
        this.isItemChecked = isItemChecked;
    }

    public static ShoppingItem fromCursor(Cursor cursor)
    {

        ShoppingItem shoppingItem = new ShoppingItem();
        shoppingItem.setItemTitle(cursor.getString(cursor.getColumnIndex(AchatDbContracts.ShoppingItemTable.ITEM_NAME)));
        shoppingItem.setItemDescription(cursor.getString(cursor.getColumnIndex(AchatDbContracts.ShoppingItemTable.ITEM_DESCRIPTION)));
        shoppingItem.setItemQuantity(cursor.getInt(cursor.getColumnIndex(AchatDbContracts.ShoppingItemTable.ITEM_QUANTITY)));
        shoppingItem.setListGuid(cursor.getString(cursor.getColumnIndex(AchatDbContracts.ShoppingItemTable.LIST_GUID)));
        shoppingItem.setItemChecked(cursor.getInt(cursor.getColumnIndex(AchatDbContracts.ShoppingItemTable.ITEM_CHECKED)) > 0);

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

    public String getListGuid()
    {
        return listGuid;
    }

    public void setListGuid(String listGuid)
    {
        this.listGuid = listGuid;
    }

    public boolean isItemChecked()
    {
        return isItemChecked;
    }

    public void setItemChecked(boolean itemChecked)
    {
        isItemChecked = itemChecked;
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
        if (!Objects.equals(si.getListGuid(), getListGuid())) return false;

        return true;
    }
}
