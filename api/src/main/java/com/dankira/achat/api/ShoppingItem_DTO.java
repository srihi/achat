package com.dankira.achat.api;

/**
 * Created by da on 6/30/2016.
 */
public class ShoppingItem_DTO
{
    private long id;
    private long shoppingListId;
    private String name;
    private String description;
    private int quantity;
    private int checked;
    private String image;
    private String group;
    private String createdOn;
    private String updatedOn;

    public ShoppingItem_DTO()
    {
    }

    public ShoppingItem_DTO(long id, long shoppingListId, String name, String description,
                            int quantity, int checked, String image, String group, String createdOn,
                            String updatedOn)
    {
        this.id = id;
        this.shoppingListId = shoppingListId;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.checked = checked;
        this.image = image;
        this.group = group;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    public long getShoppingListId()
    {
        return shoppingListId;
    }

    public void setShoppingListId(long shoppingListId)
    {
        this.shoppingListId = shoppingListId;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public int getChecked()
    {
        return checked;
    }

    public void setChecked(int checked)
    {
        this.checked = checked;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    public String getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn(String createdOn)
    {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn()
    {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn)
    {
        this.updatedOn = updatedOn;
    }
}
