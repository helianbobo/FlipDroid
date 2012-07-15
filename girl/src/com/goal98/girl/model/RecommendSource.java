package com.goal98.girl.model;

import java.util.Date;

public class RecommendSource {

    public static final String TABLE_NAME = "recommand_source";
    public static final String KEY_UPDATE_TIME = "update_on";
    public static final String KEY_BODY = "body";
    public static final String KEY_TYPE = "type";
    private String body;


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;
    private Date updateTime;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
