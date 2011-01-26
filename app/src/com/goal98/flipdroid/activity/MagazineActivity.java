package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.util.GestureUtil;

/**
 * Created by IntelliJ IDEA.
 * User: lsha6086
 * Date: 1/26/11
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class MagazineActivity extends Activity {
    private boolean goingToSleep;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            nextLayout = extras.getInt("layout");
            index = extras.getInt("index");
        }
        else{
            index = 0;
            nextLayout =  layoutChain[index];
        }
        setContentView(nextLayout);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (event.getHistorySize() > 0 && !goingToSleep) {
                    if (GestureUtil.flipRight(event))
                        goToNextActivity(true);
                    else
                        goToNextActivity(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                boolean emulator = true;
                if (!goingToSleep && emulator) {
                    goToNextActivity(true);
                }
                break;

        }
        return true;
    }

    int nextLayout;
    int[] layoutChain = new int[]{R.layout.l5, R.layout.l4, R.layout.l1,R.layout.l2,R.layout.l3};
    int index=0;

    private void goToNextActivity(boolean next) {
        Intent intent = new Intent(this, MagazineActivity.class);
        if(next)
            nextLayout = getNextLayout();
        else
            nextLayout = getPreviousLayout();

        intent.putExtra("layout", nextLayout);
        intent.putExtra("index", index);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
        goingToSleep = true;
        finish();
    }

    private int getNextLayout() {
        if(++index >= layoutChain.length){
            index =0;
        }
        return layoutChain[index];
    }

    private int getPreviousLayout() {
        if(--index <= -1){
            index = layoutChain.length-1;
        }
        return layoutChain[index];
    }
}