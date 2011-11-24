package com.goal98;


import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageViewActivity extends Activity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageview);

        imageView = (ImageView) findViewById(R.id.imageView);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){

            case MotionEvent.ACTION_UP:

                final TextView textView = (TextView) ImageViewActivity.this.findViewById(R.id.info);
                textView.setText(imageView.getWidth() + " " + imageView.getHeight());

                break;
            default:
                break;
        }

        return true;
    }
}
