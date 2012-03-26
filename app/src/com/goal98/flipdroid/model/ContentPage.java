package com.goal98.flipdroid.model;

import android.view.View;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-3-25
 * Time: 下午7:32
 * To change this template use File | Settings | File Templates.
 */
public class ContentPage {
    private float maxHeightInPixel;
    private View view;

    public float getTotalHeight() {
        return totalHeight;
    }

    public void setTotalHeight(float totalHeight) {
        this.totalHeight = totalHeight;
    }

    private float totalHeight = 0.0f;

    public ContentPage(float maxHeightInPixel) {
        this.maxHeightInPixel = maxHeightInPixel;
    }

    public void setView(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public float overFlowIfPut(float height) {
        totalHeight += height;
        if (totalHeight > maxHeightInPixel)
            return totalHeight-maxHeightInPixel;

        return -1;
    }
}
