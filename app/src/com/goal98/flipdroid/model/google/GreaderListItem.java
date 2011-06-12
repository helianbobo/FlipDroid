package com.goal98.flipdroid.model.google;

public class GreaderListItem {
    private String id;
    private String title;
    private String link;
    private String author;

    public String toString() {
        return id + " " + title + " " + link + " " + author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
