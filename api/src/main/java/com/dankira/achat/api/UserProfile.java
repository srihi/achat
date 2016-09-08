package com.dankira.achat.api;

import com.google.gson.annotations.Expose;

/**
 * Created by da on 6/29/2016.
 */
public class UserProfile
{
    @Expose
    public String userName;
    @Expose
    public String password;
    @Expose
    public String firstName;
    @Expose
    public String lastName;

    public UserProfile(String email, String password, String firstName, String lastName)
    {
        this.userName = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
