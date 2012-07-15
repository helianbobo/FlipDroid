package com.goal98.girl.activity;

import android.view.View;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-8-11
 * Time: 下午4:09
 * To change this template use File | Settings | File Templates.
 */
public class SourceItem {
    private String sourceName;
    private String sourceDesc;
    private String sourceImage;
    private String sourceType;
    private String sourceURL;
    private View sourceItemView;
    private Date sourceUpdateTime;
    private String firstImage;

    public String getCategory() {
        return category;
    }

    private String category;

    public Date getSourceUpdateTime() {
        return sourceUpdateTime;
    }

    public void setSourceUpdateTime(Date sourceUpdateTime) {
        this.sourceUpdateTime = sourceUpdateTime;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    private String sourceId;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceDesc() {
        return sourceDesc;
    }

    public void setSourceDesc(String sourceDesc) {
        this.sourceDesc = sourceDesc;
    }

    public String getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(String sourceImage) {
        this.sourceImage = sourceImage;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public void setSourceItemView(View sourceItemView) {
        this.sourceItemView = sourceItemView;
    }

    public View getSourceItemView() {
        return sourceItemView;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBackgroundImage(String firstImage) {
        this.firstImage = firstImage;
    }

    public String getFirstImage() {
        return firstImage;
    }

    public void setFirstImage(String firstImage) {
        this.firstImage = firstImage;
    }
}
