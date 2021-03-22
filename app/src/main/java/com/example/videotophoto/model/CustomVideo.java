package com.example.videotophoto.model;

import android.net.Uri;

public class CustomVideo {
    private long id;
    private Uri uriData;
    private String strTitle;
    private final String strDuration;
    private final String strDateAdded;
    private final String strSize;

    public CustomVideo(long id, Uri uriData, String strTitle, String strDuration, String strDateAdded, String strSize) {
        this.id = id;
        this.uriData = uriData;
        this.strTitle = strTitle;
        this.strDuration = strDuration;
        this.strDateAdded = strDateAdded;
        this.strSize = strSize;
    }

    public String getStrSize() {
        return strSize;
    }

    public String getStrDateAdded() {
        return strDateAdded;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStrTitle() {
        return strTitle;
    }

    public void setStrTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public String getStrDuration() {
        return strDuration;
    }

    public Uri getUriData() {
        return uriData;
    }

    public void setUriData(Uri uriData) {
        this.uriData = uriData;
    }
}
