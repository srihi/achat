package com.dankira.achat.api;

import com.google.gson.annotations.Expose;

/**
 * Created by da on 8/18/2016.
 */
public class RegistrationResponse
{
    @Expose
    public boolean isRegistrationSuccessful;
    @Expose
    public String registrationErrorMessage;
    @Expose
    public String authToken;

    public RegistrationResponse()
    {
    }

    public boolean isRegistrationSuccessful()
    {
        return isRegistrationSuccessful;
    }

    public String getRegistrationErrorMessage()
    {
        return registrationErrorMessage;
    }

    public String getAuthToken()
    {
        return authToken;
    }
}
