package com.goal98.flipdroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.goal98.flipdroid.R;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 7/2/11
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class PageIndexView extends LinearLayout {
    public PageIndexView(Context context) {
        super(context);

    }

    public PageIndexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER);
//        setDot(8, 4);
    }

    public void setDot(int total, int current) {
        this.removeAllViews();
        if(total>=15){

            current = (int) ((current/(float)total)*15);
            total = 15;
        }
        for (int i = 0; i < total; i++) {
            ImageView greyDot = new ImageView(this.getContext());
            if(i+1==current)
               greyDot.setBackgroundResource(R.drawable.dotblue);
            else
                greyDot.setBackgroundResource(R.drawable.dot);
            greyDot.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            LayoutParams params = new LayoutParams(12, 12);
            params.setMargins(2,0,2,0);
            this.addView(greyDot, params);
        }
    }
}
