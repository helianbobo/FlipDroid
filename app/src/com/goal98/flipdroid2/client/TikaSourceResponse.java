package com.goal98.flipdroid2.client;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 7/3/11
 * Time: 5:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class TikaSourceResponse {
    private String accountType;
    private String name;
    private String id;
    private String desc;
    private String imageURL;
    private String contentURL;

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    private String cat;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getContentURL() {
        return contentURL;
    }

    public void setContentURL(String contentURL) {
        this.contentURL = contentURL;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
