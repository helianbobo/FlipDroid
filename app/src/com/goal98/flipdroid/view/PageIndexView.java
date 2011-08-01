package com.goal98.flipdroid.view;

import android.content.Context;
import android.graphics.Color;
import android.text.AndroidCharacter;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.flipdroid.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 7/2/11
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class PageIndexView extends LinearLayout {
    private Date latestUpdateDate = new Date();
    private int total;
    private int current;
    private boolean hasUpdate;

    public void setLatestUpdateDate(Date latestUpdateDate) {
        this.latestUpdateDate = latestUpdateDate;
    }

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
        this.total = total;
        this.current = current;
        if (this.total >= 15) {
            this.current = (int) ((current / (float) total) * 15);
            this.total = 15;
        }
        updateView();
    }

    private void updateView() {
        this.removeAllViews();
        TextView latest = new TextView(this.getContext());
        latest.setText(R.string.latest);
        latest.setTextColor(Color.parseColor("#CCCCCC"));
        latest.setTextSize(14);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 0, 5, 2);

        this.addView(latest, params);
        for (int i = 0; i < this.total; i++) {
            ImageView greyDot = new ImageView(this.getContext());
            if (i + 1 == current)
                greyDot.setBackgroundResource(R.drawable.dotblue);
            else
                greyDot.setBackgroundResource(R.drawable.dot);
            greyDot.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            LayoutParams paramsDot = new LayoutParams(12, 12);
            paramsDot.setMargins(2, 0, 2, 0);
            this.addView(greyDot, paramsDot);
        }

        TextView latestUpdate = new TextView(this.getContext());
        latestUpdate.setText(new SimpleDateFormat("MMM dd").format(latestUpdateDate));
        latestUpdate.setTextColor(Color.parseColor("#CCCCCC"));
        latestUpdate.setTextSize(14);
        this.addView(latestUpdate, params);

        if (hasUpdate) {
            TextView update = new TextView(this.getContext());
            update.setText("New!!!");
            update.setTextColor(Color.parseColor("#CCCCCC"));
            update.setTextSize(14);
            this.addView(update, params);
        }
    }

    public void setHasUpdate(boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
        System.out.println("updating view");
        updateView();
    }
}
