package com.goal98.flipdroid2.client;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/20/11
 * Time: 6:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class TikaExtractResponse {
    private Date createDate;

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    private String sourceURL;

    public String getContent() {
        return content;
    }
    String author;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    String title;

    public List<String> getImages() {
        return images;
    }

    String content;
    List<String> images;

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public boolean hasContent(){
        String content = getContent();
        return content != null && content.trim().length() != 0;
    }
}
