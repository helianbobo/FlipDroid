package com.goal98.flipdroid.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.multiscreen.MultiScreenSupport;
import com.goal98.flipdroid.util.DeviceInfo;

/**
 * Created by IntelliJ IDEA.
 * User: lsha6086
 * Date: 4/1/11
 * Time: 12:28 PM
 * To change this template use FileType | Settings | FileType Templates.
 */
public class TopBar extends LinearLayout {
    private Context context;
    private LinearLayout topbarLL;

    private void init(AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        topbarLL = (LinearLayout) inflater.inflate(R.layout.topbar, this);
        DeviceInfo deviceInfo = DeviceInfo.getInstance((Activity) context);
        LinearLayout titleBar = (LinearLayout)topbarLL.findViewById(R.id.titleBar);
        titleBar.getLayoutParams().height = MultiScreenSupport.getInstance(deviceInfo).getTopbarHeight();
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.topbar);
        String leftButtonLabel = null;
        if(array.hasValue(R.styleable.topbar_leftButtonLabel))
            leftButtonLabel = array.getString(R.styleable.topbar_leftButtonLabel);

        Drawable leftButtonImage = null;
        if(array.hasValue(R.styleable.topbar_leftButtonImage))
            leftButtonImage = array.getDrawable(R.styleable.topbar_leftButtonImage);


        String rightButtonLabel = null;
        if(array.hasValue(R.styleable.topbar_rightButtonLabel))
            rightButtonLabel = array.getString(R.styleable.topbar_rightButtonLabel);

         Drawable rightButtonImage = null;
        if(array.hasValue(R.styleable.topbar_rightButtonImage))
            rightButtonImage = array.getDrawable(R.styleable.topbar_rightButtonImage);

        String title = null;
        if(array.hasValue(R.styleable.topbar_title))
            title = array.getString(R.styleable.topbar_title);

        ImageButton leftButton = (ImageButton)topbarLL.findViewById(R.id.leftButton);
        ImageButton rightButton = (ImageButton)topbarLL.findViewById(R.id.rightButton);
        TextView titleView = (TextView)topbarLL.findViewById(R.id.topBarTitle);

        if(leftButtonImage != null)
            leftButton.setBackgroundDrawable(leftButtonImage);

        if((leftButtonLabel == null || leftButtonLabel.length() == 0) && leftButtonImage==null)
            leftButton.setVisibility(INVISIBLE);

        if(rightButtonImage != null)
            rightButton.setBackgroundDrawable(rightButtonImage);
        if((rightButtonLabel == null || rightButtonLabel.length() == 0) && rightButtonImage==null)
            rightButton.setVisibility(INVISIBLE);


        if(title!= null && title.length() != 0)
            titleView.setText(title);
        else
            titleView.setVisibility(INVISIBLE);

        array.recycle();
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }


}
