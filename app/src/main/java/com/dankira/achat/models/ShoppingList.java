package com.dankira.achat.models;

import android.database.Cursor;

import com.dankira.achat.provider.AchatDbContracts;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ShoppingList implements Serializable {

    private int id;
    private String listGuid;
    private String listTitle;
    private String listDesc;
    private int itemCount;
    private Date createdOn;
    private int shareStatus;

    public ShoppingList() {
    }

    public ShoppingList(int id, String listGuid, String listTitle, String listDesc, int itemCount, Date createdOn, int shareStatus) {
        this.id = id;
        this.listGuid = listGuid;
        this.listTitle = listTitle;
        this.listDesc = listDesc;
        this.itemCount = itemCount;
        this.createdOn = createdOn;
        this.shareStatus = shareStatus;
    }

    public static ShoppingList fromCursor(Cursor cursor) {
        ShoppingList shoppingList = new ShoppingList();

        shoppingList.setListTitle(cursor.getString(cursor.getColumnIndex(AchatDbContracts.ShoppingListTable.LIST_TITLE)));
        shoppingList.setListDesc(cursor.getString(cursor.getColumnIndex(AchatDbContracts.ShoppingListTable.LIST_DESCRIPTION)));
        if (cursor.getColumnIndex("item_count") > 0) {
            shoppingList.setItemCount(cursor.getInt(cursor.getColumnIndex("item_count")));
        }
        shoppingList.setListGuid(cursor.getString(cursor.getColumnIndex(AchatDbContracts.ShoppingListTable.LIST_GUID)));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dateCreated = format.parse(cursor.getString(cursor.getColumnIndex(AchatDbContracts.ShoppingListTable.LIST_CREATED_ON)));
            shoppingList.setCreatedOn(dateCreated);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        shoppingList.setShareStatus(cursor.getInt(cursor.getColumnIndex(AchatDbContracts.ShoppingListTable.LIST_SHARE_STATUS)));



        // TODO: 7/11/2016 get the shopping list from the cursor here.
        // The cursor is already pointing to the right location of the element when this is called.
        // you only need to cast the column elements and create the right object to return.
        //T element = cursor.getT(cursor.getColumnIndex(ShoppingListDb.SOME_COLUMN_NAME))

        return shoppingList;
    }

    public int getId() {
        return id;
    }

    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    public String getListDesc() {
        return listDesc;
    }

    public void setListDesc(String listDesc) {
        this.listDesc = listDesc;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public int getShareStatus() {
        return shareStatus;
    }

    public void setShareStatus(int shareStatus) {
        this.shareStatus = shareStatus;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public String getListGuid() {
        return listGuid;
    }

    public void setListGuid(String listGuid) {
        this.listGuid = listGuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ShoppingList sl = (ShoppingList) obj;

        if (!Objects.equals(sl.getListTitle(), getListTitle())) return false;
        return Objects.equals(sl.getListDesc(), getListDesc());

    }
}
