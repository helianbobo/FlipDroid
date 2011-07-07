package com.goal98.flipdroid.model;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 7/2/11
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Tip {
    String text;
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Tip(String tipText, int tipId) {
        this.text = tipText;
        this.id=tipId;

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
