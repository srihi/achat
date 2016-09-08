package com.dankira.achat.api;

/**
 * Created by da on 6/30/2016.
 */
public class ShoppingItem_DTO
{
    private long id;
    private String list_guid;
    private String item_title;
    private String item_description;
    private int item_quantity;
    private boolean item_checked;
    private String item_group;
    private String item_created_on;
    private String item_updated_on;
    private String item_bar_code;

    public ShoppingItem_DTO()
    {
    }

    public ShoppingItem_DTO(long id, String shoppingListId, String name, String item_description,
                            int item_quantity, boolean item_checked, String item_group, String item_created_on,
                            String item_updated_on, String item_bar_code)
    {
        this.id = id;
        this.list_guid = shoppingListId;
        this.item_title = name;
        this.item_description = item_description;
        this.item_quantity = item_quantity;
        this.item_checked = item_checked;
        this.item_group = item_group;
        this.item_created_on = item_created_on;
        this.item_updated_on = item_updated_on;
        this.item_bar_code = item_bar_code;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public void setList_guid(String list_guid)
    {
        this.list_guid = list_guid;
    }

    public void setItem_title(String item_title)
    {
        this.item_title = item_title;
    }

    public void setItem_description(String item_description)
    {
        this.item_description = item_description;
    }

    public void setItem_quantity(int item_quantity)
    {
        this.item_quantity = item_quantity;
    }

    public void setItem_checked(boolean item_checked)
    {
        this.item_checked = item_checked;
    }

    public void setItem_group(String item_group)
    {
        this.item_group = item_group;
    }

    public void setItem_created_on(String item_created_on)
    {
        this.item_created_on = item_created_on;
    }

    public void setItem_updated_on(String item_updated_on)
    {
        this.item_updated_on = item_updated_on;
    }

    public void setItem_bar_code(String item_bar_code)
    {
        this.item_bar_code = item_bar_code;
    }

    public long getId()
    {
        return id;
    }

    public String getList_guid()
    {
        return list_guid;
    }

    public String getItem_title()
    {
        return item_title;
    }

    public String getItem_description()
    {
        return item_description;
    }

    public int getItem_quantity()
    {
        return item_quantity;
    }

    public boolean isItem_checked()
    {
        return item_checked;
    }

    public String getItem_group()
    {
        return item_group;
    }

    public String getItem_created_on()
    {
        return item_created_on;
    }

    public String getItem_updated_on()
    {
        return item_updated_on;
    }

    public String getItem_bar_code()
    {
        return item_bar_code;
    }
}
