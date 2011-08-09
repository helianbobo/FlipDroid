package com.goal98.flipdroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.activity.FlipdroidApplications;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.DeviceInfo;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-6-24
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
public class HeaderView extends LinearLayout {
    private ListView sourceList;
    //    private LinearLayout navigatorFrame;
    private boolean sourceSelectMode;
    private PageActivity pageActivity;
    private LayoutInflater inflater;
    private WeiboPageView pageView;
    private ViewSwitcher viewSwitcher;


    public HeaderView(Context context) {
        super(context);
        this.pageActivity = (PageActivity) context;
        buildHeaderText();
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.pageActivity = (PageActivity) context;
        buildHeaderText();
    }

    public DeviceInfo getDeviceInfoFromApplicationContext(){
        FlipdroidApplications fa = (FlipdroidApplications) this.getContext().getApplicationContext();
        return fa.getDeviceInfo();
    }

    public void setPageView(WeiboPageView pageView) {
        this.pageView = pageView;
    }

    private void buildHeaderText() {
        inflater = LayoutInflater.from(pageActivity);
        sourceList = (ListView) inflater.inflate(R.layout.navigator, null);
        viewSwitcher = (ViewSwitcher) inflater.inflate(R.layout.header, null);

        showTitleBar();

        viewSwitcher.setInAnimation(AnimationUtils.loadAnimation(pageActivity, R.anim.fadein));
        viewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(pageActivity, R.anim.fade));
        TextView headerText = (TextView) viewSwitcher.findViewById(R.id.headerText);
        DeviceInfo deviceInfo = getDeviceInfoFromApplicationContext();
        if (deviceInfo.isLargeScreen()) {
            headerText.setTextSize(24);
        } else if (deviceInfo.isSmallScreen()) {
            headerText.setTextSize(18);
        }
        headerText.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                HeaderView.this.pageActivity.showDialog(PageActivity.NAVIGATION);
            }
        });
        LinearLayout greyLayer = new LinearLayout(this.getContext());
        greyLayer.setBackgroundColor(Constants.LINE_COLOR);
        greyLayer.setPadding(0, 0, 0, 1);

        greyLayer.addView(viewSwitcher, new LayoutParams
                (LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        this.addView(greyLayer, new LayoutParams
                (LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    public void showToolBar() {
        viewSwitcher.setDisplayedChild(1);
    }

    public void showTitleBar() {
        viewSwitcher.setDisplayedChild(0);
    }
}
