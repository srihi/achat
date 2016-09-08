package com.dankira.achat.api;

import com.google.gson.annotations.Expose;

/**
 * Created by da on 6/30/2016.
 */
public class UserCredentials
{
    @Expose
    public String userEmail;
    @Expose
    public String password;

    public UserCredentials(String userEmail, String password)
    {
        this.userEmail = userEmail;
        this.password = password;
    }
}
