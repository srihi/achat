package com.dankira.achat.api;

import com.google.gson.annotations.Expose;

/**
 * Created by da on 8/29/2016.
 */
public class ShareCodeResponse
{
    @Expose
    public String share_code;

    @Expose
    public boolean is_share_successful;

}
