package com.dankira.achat.api;

/**
 * Created by da on 7/20/2016.
 */
public class ShareStatus
{
    private boolean shareSucceeded;
    private String shareCode;

    public ShareStatus()
    {
    }

    public ShareStatus(boolean shareSucceeded, String shareCode)
    {
        this.shareSucceeded = shareSucceeded;
        this.shareCode = shareCode;
    }

    public boolean isShareSucceeded()
    {
        return shareSucceeded;
    }

    public void setShareSucceeded(boolean shareSucceeded)
    {
        this.shareSucceeded = shareSucceeded;
    }

    public String getShareCode()
    {
        return shareCode;
    }

    public void setShareCode(String shareCode)
    {
        this.shareCode = shareCode;
    }
}
