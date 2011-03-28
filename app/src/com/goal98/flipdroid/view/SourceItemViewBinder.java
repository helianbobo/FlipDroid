package com.goal98.flipdroid.view;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.goal98.flipdroid.R;

import java.net.URL;

public class SourceItemViewBinder implements SimpleAdapter.ViewBinder, SimpleCursorAdapter.ViewBinder {

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

                TextView noteName = (TextView) view;
                noteName.setText(value);

                break;
            case R.id.source_image:

                if (value != null) {
                    InternetImageView sourceImageView = (InternetImageView) view;
                    try {
                        URL url = new URL(value);
                        sourceImageView.loadImage(url);
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
