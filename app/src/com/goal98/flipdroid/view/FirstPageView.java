package com.goal98.flipdroid.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.model.PageViewSlidingWindows;
import com.goal98.flipdroid.model.SlidingWindows;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/31/11
 * Time: 10:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class FirstPageView extends WeiboPageView {
    private LinearLayout frame;
    SlidingWindows windows;

    public FirstPageView(PageActivity context, PageViewSlidingWindows windows) {
        super(context);

    }

    protected void setDynamicLayout(Context context) {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        this.frame = (LinearLayout) inflater.inflate(R.layout.first_page_view, null);
        this.addView(frame, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    public void showLoading() {
//        LayoutInflater inflater = LayoutInflater.from(WeiboPageView.this.getContext());
//        LinearLayout loadingView = (LinearLayout) inflater.inflate(R.layout.loading, null);
//        this.addView(loadingView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
    }

    public boolean isFirstPage() {
        return true;  //To change body of created methods use File | Settings | File Templates.
    }
}