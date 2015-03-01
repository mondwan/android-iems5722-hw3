package com.iems5722.assignment3;

import java.net.URL;

/**
 * Created by mondwan on 14/2/15.
 */
public class GCMContentStorage {
    // A Class for storing contents from GCM

    protected URL url;

    protected String title;

    protected String description;

    public URL getUrl() {
        return this.url;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    // Constructor
    //
    // @param url string
    // @param title string
    // @param description string
    public GCMContentStorage(URL url, String title, String description) {
        this.title = title;
        this.description = description;
        this.url = url;
    }
}
