package com.example.videotophoto.model;

import android.net.Uri;

public class Video {
    private long id;
    private final Uri uriData;
    private final String strTitle;
    private final String strDuration;

    public Video(long id, Uri uriData, String strTitle, String strDuration) {
        this.id = id;
        this.uriData = uriData;
        this.strTitle = strTitle;
        this.strDuration = strDuration;
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

    public String getStrDuration() {
        return strDuration;
    }

    public Uri getUriData() {
        return uriData;
    }

}
