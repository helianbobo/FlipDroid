package com.goal98.flipdroid.view;

import android.content.Context;
import android.graphics.Color;
import android.text.AndroidCharacter;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.util.Constants;

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
    private boolean updating;
    private PageActivity activity;

    public void setLatestUpdateDate(Date latestUpdateDate) {
        this.latestUpdateDate = latestUpdateDate;
    }

    public PageIndexView(Context context) {
        super(context);

    }

    public PageIndexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (PageActivity) context;
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
        latest.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
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
        latestUpdate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        this.addView(latestUpdate, params);

        if (hasUpdate) {
            TextView update = new TextView(this.getContext());
            update.setText("Reload");
            update.setTextColor(Color.parseColor(Constants.COLOR_RED));
            update.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

            update.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View view, MotionEvent motionEvent) {

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            activity.reload();
                            break;
                        default:
                            break;
                    }

                    return false;
                }

            });
            this.addView(update, params);
        } else if (updating) {
            LinearLayout progressBar = (LinearLayout) LayoutInflater.from(this.getContext()).inflate(R.layout.progressbar, null);
            progressBar.findViewById(R.id.loading).setVisibility(GONE);
            this.addView(progressBar, params);
        }
    }

    public void setHasUpdate(boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
        this.updating = false;
        System.out.println("has update" + hasUpdate);
        updateView();
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
        System.out.println("updating view");
        updateView();
    }

    public boolean isHasUpdate() {
        return hasUpdate;
    }

    public boolean isUpdating() {
        return updating;
    }

    public void hide() {
        this.setVisibility(GONE);
    }

    public void show() {
        this.setVisibility(VISIBLE);
    }
}
