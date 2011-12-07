package com.goal98.flipdroid.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.goal98.flipdroid.R;
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
    private PageActivity pageActivity;
    private LayoutInflater inflater;
    private ViewSwitcher bottomBar;


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
        return DeviceInfo.getInstance(pageActivity);
    }





    private void buildHeaderText() {
        inflater = LayoutInflater.from(pageActivity);
        bottomBar = (ViewSwitcher) inflater.inflate(R.layout.header, null);

        showTitleBar();

        bottomBar.setInAnimation(AnimationUtils.loadAnimation(pageActivity, R.anim.fadeinfast));
        bottomBar.setOutAnimation(AnimationUtils.loadAnimation(pageActivity, R.anim.fadefast));
        TextView headerText = (TextView) bottomBar.findViewById(R.id.headerText);
        DeviceInfo deviceInfo = getDeviceInfoFromApplicationContext();
        headerText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_TITLE);
        headerText.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                HeaderView.this.pageActivity.showDialog(PageActivity.NAVIGATION);
            }
        });
        LinearLayout greyLayer = new LinearLayout(this.getContext());
        greyLayer.setBackgroundColor(Color.parseColor(Constants.SHADOW_LAYER_COLOR));
        greyLayer.setPadding(0, 0, 0, 1);

        greyLayer.addView(bottomBar, new LayoutParams
                (LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        this.addView(greyLayer, new LayoutParams
                (LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    public void showToolBar() {
        bottomBar.setDisplayedChild(1);
    }

    public void showTitleBar() {
        bottomBar.setDisplayedChild(0);
    }

    public void hide() {
        bottomBar.setVisibility(GONE);
    }

    public void show() {
        bottomBar.setVisibility(VISIBLE);
    }
}
