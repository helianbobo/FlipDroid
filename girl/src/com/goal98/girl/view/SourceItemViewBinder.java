package com.goal98.girl.view;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.goal98.android.WebImageView;
import com.goal98.girl.R;
import com.srz.androidtools.util.DeviceInfo;

public class SourceItemViewBinder implements SimpleAdapter.ViewBinder, SimpleCursorAdapter.ViewBinder {
    private DeviceInfo deviceInfo;

    public SourceItemViewBinder(DeviceInfo deviceInfo){
       this.deviceInfo = deviceInfo;
    }
    public boolean setViewValue(View view, Cursor cursor, int i) {

        boolean binded = true;
        String value = cursor.getString(i);

        binded = bindView(view, binded, value);

        return binded;
    }

    public boolean setViewValue(View view, Object o, String s) {
        boolean binded = true;
        String value = (String) o;

        binded = bindView(view, binded, value);

        return binded;
    }

    private boolean bindView(View view, boolean binded, String value) {
        int viewId = view.getId();

        switch (viewId) {
            case R.id.source_name:
            case R.id.source_desc:
            case R.id.source_type:
            case R.id.source_url:

                TextView noteName = (TextView) view;
                noteName.setText(value);

                break;


            case R.id.source_image:

                if (value != null) {
                    WebImageView sourceImageView = (WebImageView) view;
                    if(deviceInfo.isLargeScreen()){
                        sourceImageView.setDefaultHeight(75);
                        sourceImageView.setDefaultWidth(75);
                    }else if(deviceInfo.isSmallScreen()){
                        sourceImageView.setDefaultHeight(30);
                        sourceImageView.setDefaultWidth(30);
                    }
                    try {
                        if (value != null)
                            sourceImageView.setImageUrl(value);
                        //System.out.println("webImageView" + value);
                        sourceImageView.loadImage();
                    } catch (Exception e) {
                        Log.e(this.getClass().getName(), e.getMessage(), e);
                        binded = false;
                    }
                }

                break;
        }
        return binded;
    }
}
