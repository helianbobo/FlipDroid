package com.goal98.flipdroid.activity;

import android.view.View;
import android.view.ViewManager;
import android.widget.LinearLayout;

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
}
