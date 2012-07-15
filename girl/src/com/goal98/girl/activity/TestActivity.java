package com.goal98.girl.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.goal98.girl.R;
import com.google.ads.*;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 12-7-14
 * Time: 下午9:31
 * To change this template use File | Settings | File Templates.
 */
public class TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        LinearLayout ll = (LinearLayout) this.findViewById(R.id.sb);

        AdView adView = new AdView(this, AdSize.BANNER, "a150011ead7ed10");
        adView.setVisibility(View.VISIBLE);

        AdRequest adRequest = new AdRequest();
        adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
        adRequest.addTestDevice("E2CDD7A5D609F85B1545FE23D70E22FB");

        adView.loadAd(adRequest);

        ll.addView(adView);
    }
}
