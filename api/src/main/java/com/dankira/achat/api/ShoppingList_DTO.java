package com.dankira.achat.api;

/**
 * Created by da on 6/29/2016.
 */
public class ShoppingList_DTO
{
    private long id;
    private String name;
    private String description;
    private String createdOn;

    public ShoppingList_DTO()
    {
    }

    public ShoppingList_DTO(long id, String name, String description, String createdOn)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdOn = createdOn;
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

    public String getCreatedOn()
    {
        return createdOn;
    }

    public void setCreatedOn(String createdOn)
    {
        this.createdOn = createdOn;
    }

}
