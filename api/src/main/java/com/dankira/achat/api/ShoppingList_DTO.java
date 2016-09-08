package com.dankira.achat.api;

/**
 * Created by da on 6/29/2016.
 */
public class ShoppingList_DTO
{
    private long id;
    private String list_guid;
    private String user_guid;
    private String list_title;
    private String list_description;
    private String created_on;
    private int item_count;
    private boolean share_status;

    public ShoppingList_DTO()
    {
    }

    public ShoppingList_DTO(long id, String list_guid, String user_guid, String list_title,
                            String list_description, String created_on, int item_count, boolean share_status)
    {
        this.id = id;
        this.list_guid = list_guid;
        this.user_guid = user_guid;
        this.list_title = list_title;
        this.list_description = list_description;
        this.created_on = created_on;
        this.item_count = item_count;
        this.share_status = share_status;
    }

    public String getList_guid()
    {
        return list_guid;
    }

    public String getUser_guid()
    {
        return user_guid;
    }

    public boolean getShare_Status()
    {
        return share_status;
    }

    public void setList_guid(String list_guid)
    {
        this.list_guid = list_guid;
    }

    public void setUser_guid(String user_guid)
    {
        this.user_guid = user_guid;
    }

    public void setItem_count(int item_count)
    {
        this.item_count = item_count;
    }

    public void setShare_status(boolean share_status)
    {
        this.share_status = share_status;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getList_title()
    {
        return list_title;
    }

    public void setList_title(String list_title)
    {
        this.list_title = list_title;
    }

    public String getList_description()
    {
        return list_description;
    }

    public void setList_description(String list_description)
    {
        this.list_description = list_description;
    }

    public String getCreated_on()
    {
        return created_on;
    }

    public void setCreated_on(String created_on)
    {
        this.created_on = created_on;
    }

    public int getItem_count()
    {
        return item_count;
    }

    public boolean isShare_status()
    {
        return share_status;
    }
}
