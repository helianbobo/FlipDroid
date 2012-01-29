package com.goal98.flipdroid.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.model.SlidingWindows;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/31/11
 * Time: 10:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class LastPageViewContainer extends ThumbnailViewContainer {
    private LinearLayout frame;
    SlidingWindows windows;

    public LastPageViewContainer(PageActivity context) {
        super(context);

    }

    protected void setDynamicLayout(Context context) {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        this.frame = (LinearLayout) inflater.inflate(R.layout.last_page_view, null);
        this.addView(frame, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    public boolean isLastPage() {
        return true;  //To change body of created methods use File | Settings | File Templates.
    }

    public void releaseResource() {

    }
}
