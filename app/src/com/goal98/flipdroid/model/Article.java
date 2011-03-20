package com.goal98.flipdroid.model;

import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Article {

    private String title;
    private URL portraitImageUrl;
    private String author;
    private String status;
    private String content;
    private URL imageUrl;

    static Pattern urlPattern = Pattern.compile(
            "http://([\\w-]+\\.)+[\\w-]+(/[\\w\\- ./?%&=]*)?",
            Pattern.CASE_INSENSITIVE);


    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    private int weight = -1;
    private Date createdDate;

    public boolean hasLink() {
        if(status == null)
            return false;
        Matcher mat = urlPattern.matcher(status);
        boolean result = mat.find();
        mat.reset();
        return result;
    }

    public String extractURL(){
         Matcher mat = urlPattern.matcher(status);
         mat.find();
         return mat.group();
    }

    public int getWeight() {
        if (weight == -1) {
            weight = calculateWeight();
        }
        return weight;
    }

    private int calculateWeight() {
        int result = 0;
        boolean containUrl = hasLink();
        if (containUrl)
            result++;
        if (imageUrl != null)
            result++;
        if (content != null)
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
