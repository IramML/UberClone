package com.iramml.uberclone.Model;

public class Notification {
    public String title;
    public String body;

    public Notification(String title, String body) {
        this.body = body;
    }

    public Notification() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
