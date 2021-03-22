package com.example.videotophoto.model;

import android.net.Uri;

public class Image {
    private long id;
    private final Uri uriData;

    public Image(long id, Uri uriData) {
        this.id = id;
        this.uriData = uriData;
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
}
