package com.dankira.achat.api;

import com.google.gson.annotations.Expose;

/**
 * Created by da on 8/23/2016.
 */
public class UserProfileResponse
{
    @Expose
    private boolean isProfileFound;
    @Expose
    private String userName;
    @Expose
    private String firstName;
    @Expose
    private String lastName;

    public UserProfileResponse()
    {
    }

    public boolean isProfileFound()
    {
        return isProfileFound;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }
}
