package com.dankira.achat.api;

import com.google.gson.annotations.Expose;

/**
 * Created by da on 8/23/2016.
 */
public class LoginResponse
{
    @Expose
    public boolean isLoginSuccessful;

    @Expose
    public String access_token;

    public LoginResponse()
    {
    }

    public boolean isLoginSuccessful()
    {
        return isLoginSuccessful;
    }

    public String getAccess_token()
    {
        return access_token;
    }
}
