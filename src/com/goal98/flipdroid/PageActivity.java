package com.goal98.flipdroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

public class PageActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        View container = findViewById(R.id.testPage);
        TextView textView = (TextView)findViewById(R.id.article1);

        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                textView.setTextSize(1);
                container.setAnimation(new AlphaAnimation(0, 1));
                break;
            default:
        }


        return true;
    }
}
