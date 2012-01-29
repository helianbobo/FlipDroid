package com.goal98.flipdroid.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/16/11
 * Time: 11:21 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ItemView extends LinearLayout {
    public ItemView(Context context) {
        super(context);
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract void render(DetailInfo di);
}
