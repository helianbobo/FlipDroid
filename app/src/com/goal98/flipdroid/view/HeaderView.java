package com.goal98.flipdroid.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
public class HeaderView extends ViewSwitcher {
    private PageActivity pageActivity;
    private LayoutInflater inflater;
    private View bottomBar;
    private Button updateButton;


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

    public DeviceInfo getDeviceInfoFromApplicationContext() {
        return DeviceInfo.getInstance(pageActivity);
    }

    public void showUpdate() {
        updateButton.setVisibility(VISIBLE);
    }

    public void hideUpdate() {
        updateButton.setVisibility(INVISIBLE);
    }


    private void buildHeaderText() {
        inflater = LayoutInflater.from(pageActivity);
        bottomBar =  inflater.inflate(R.layout.header, this,true);

        showTitleBar();

        this.setInAnimation(AnimationUtils.loadAnimation(pageActivity, R.anim.toolbar_slide_in_from_left));
        this.setOutAnimation(AnimationUtils.loadAnimation(pageActivity, R.anim.toolbar_slide_out_to_right));
        TextView headerText = (TextView) bottomBar.findViewById(R.id.headerText);
        updateButton = (Button) bottomBar.findViewById(R.id.update);
        updateButton.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        pageActivity.reload();
                        break;
                    default:
                        break;
                }

                return false;
            }
        });
        headerText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Constants.TEXT_SIZE_TITLE);
        headerText.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                HeaderView.this.pageActivity.showDialog(PageActivity.NAVIGATION);
            }
        });
//        LinearLayout greyLayer = new LinearLayout(this.getContext());
//        greyLayer.setBackgroundColor(Color.parseColor(Constants.SHADOW_LAYER_COLOR));
//        greyLayer.setPadding(0, 0, 0, 1);
//
//        greyLayer.addView(bottomBar, new LayoutParams
//                (LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//
//        this.addView(greyLayer, new LayoutParams
//                (LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    public void showToolBar() {
        setDisplayedChild(1);
    }

    public void showTitleBar() {
        setDisplayedChild(0);
    }

    public void hide() {
        bottomBar.setVisibility(GONE);
    }

    public void show() {
        bottomBar.setVisibility(VISIBLE);
    }


}
