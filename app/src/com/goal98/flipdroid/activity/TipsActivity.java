package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.util.Constants;

import java.util.ArrayList;


public class TipsActivity extends Activity {


    private ArrayList<Integer> tipsResourceIdList = new ArrayList<Integer>();
    private ViewPager viewPager;
    private ProgressBar progressBar;
    private Button nextButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tips);

        initTipsList();

        viewPager = (ViewPager) findViewById(R.id.tipsViewPager);
        progressBar = (ProgressBar) findViewById(R.id.tipsProgressBar);
        nextButton = (Button) findViewById(R.id.tipsNextButton);


        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {

                return tipsResourceIdList.size();
            }

            @Override
            public Object instantiateItem(View collection, int position) {
                ImageView imageView = new ImageView(TipsActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setImageResource(tipsResourceIdList.get(position));
                ((ViewPager) collection).addView(imageView, 0);

                return imageView;
            }

            @Override
            public void destroyItem(View collection, int position, Object view) {
                ((ViewPager) collection).removeView((ImageView) view);
            }


            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == ((ImageView) object);
            }

            @Override
            public void finishUpdate(View arg0) {
            }


            @Override
            public void restoreState(Parcelable arg0, ClassLoader arg1) {
            }

            @Override
            public Parcelable saveState() {
                return null;
            }

            @Override
            public void startUpdate(View arg0) {
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                progressBar.setProgress(position + 1);
            }

            public void onPageSelected(int position) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public void onPageScrollStateChanged(int state) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        progressBar.setMax(tipsResourceIdList.size());
        setProgressBarVisibility(true);
        progressBar.setProgress(1);


        nextButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        final boolean handled = viewPager.arrowScroll(View.FOCUS_FORWARD);
                        progressBar.incrementProgressBy(1);
                        if (!handled) {
                            PreferenceManager.getDefaultSharedPreferences(TipsActivity.this).edit().putBoolean(Constants.PREFERENCE_TIPS_READ, true).commit();
                            startActivity(new Intent(TipsActivity.this, IndexActivity.class));
                            finish();
                        }
                        break;
                    default:
                        break;
                }

                return false;
            }
        });


    }

    private void initTipsList() {
        tipsResourceIdList.add(R.drawable.tips_1);
        tipsResourceIdList.add(R.drawable.tips_2);
        tipsResourceIdList.add(R.drawable.tips_3);
        tipsResourceIdList.add(R.drawable.tips_4);
    }
}