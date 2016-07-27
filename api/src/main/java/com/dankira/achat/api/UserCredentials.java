package com.dankira.achat.api;

/**
 * Created by da on 6/30/2016.
 */
public class UserCredentials
{
    private String _userEmail;
    private String _password;

    public UserCredentials(String userEmail, String password)
    {
        this._userEmail = userEmail;
        this._password = password;
    }

    public String get_userEmail()
    {
        return _userEmail;
    }

    public String get_password()
    {
        return _password;
    }

}
