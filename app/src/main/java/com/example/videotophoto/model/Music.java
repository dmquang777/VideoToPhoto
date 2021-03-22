package com.example.videotophoto.model;

import android.net.Uri;

public class Music {
    private long id;
    private final Uri uriData;
    private final String strTitle;
    private final String strDuration;
    private final String strSize;
    private final String strDateAdded;

    public Music(long id, Uri uriData, String strTitle, String strDuration, String strSize, String strModified) {
        this.id = id;
        this.uriData = uriData;
        this.strTitle = strTitle;
        this.strDuration = strDuration;
        this.strSize = strSize;
        this.strDateAdded = strModified;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Uri getUriData() {
        return uriData;
    }

    public String getStrTitle() {
        return strTitle;
    }

    public String getStrDuration() {
        return strDuration;
    }

    public String getStrSize() {
        return strSize;
    }

    public String getStrDateAdded() {
        return strDateAdded;
    }

}
