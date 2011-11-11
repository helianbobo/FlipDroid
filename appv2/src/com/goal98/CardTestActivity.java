package com.goal98;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class CardTestActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cardtest);

        CardScrollView cardScrollView = (CardScrollView)findViewById(R.id.cardview);

        int childCount = cardScrollView.getChildCount();
        Log.i("CardTestActivity", "childCount:" + childCount);

    }
}