package com.goal98.girl.util;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 3/20/11
 * Time: 6:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class TikaResponse {
    public String getContent() {
        return content;
    }

    String title;
    String content;

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;

    }

    public void setTitle(String title) {
        this.title = title;
    }
}
