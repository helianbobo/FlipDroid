package com.goal98.flipdroid.model;

import java.net.URL;

public class Article {

    private String title;
    private URL portraitImageUrl;
    private String author;
    private String status;
    private String content;
    private URL imageUrl;
    private int weight = -1;

    public int getWeight() {
        if (weight == -1){
            weight = calculateWeight();
        }
        return weight;
    }

    private int calculateWeight(){
        int result = 0;
        boolean containUrl = status != null && status.contains("http://");
        if(containUrl)
            result++;
        if (imageUrl != null)
            result++;
        if(content != null)
            result++;
        return result;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public URL getPortraitImageUrl() {
        return portraitImageUrl;
    }

    public void setPortraitImageUrl(URL portraitImageUrl) {
        this.portraitImageUrl = portraitImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URL imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
