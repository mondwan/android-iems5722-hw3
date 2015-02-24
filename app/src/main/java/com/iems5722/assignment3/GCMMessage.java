package com.iems5722.assignment3;

import java.net.URL;

/**
 * Created by mondwan on 14/2/15.
 */
public class GCMMessage {
    // A Class for storing the contents of a GCMMessage

    protected URL url;

    protected String title;

    protected String description;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    // Constructor
    //
    // @param url string
    // @param title string
    // @param description string
    public GCMMessage(URL url, String title, String description) {
        this.title = title;
        this.description = description;
        this.url = url;
    }
}
