package com.dankira.achat.api;

/**
 * Created by da on 6/29/2016.
 */
public class UserProfile
{
    private String authToken;
    private String userName;

    public UserProfile()
    {
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }
}
