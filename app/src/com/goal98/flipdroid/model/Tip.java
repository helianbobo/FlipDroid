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

    public Tip(String tipText) {
        this.text = tipText;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
