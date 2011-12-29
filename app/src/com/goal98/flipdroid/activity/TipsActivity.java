package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.util.Constants;
import com.mobclick.android.MobclickAgent;

import java.util.ArrayList;


public class TipsActivity extends Activity {


    private ArrayList<Integer> tipsResourceIdList = new ArrayList<Integer>();
    private ViewPager viewPager;
    private ProgressBar progressBar;
    private Button nextButton;
    private String[] tipsTextArray;

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
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                LinearLayout linearLayout = new LinearLayout(TipsActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setVerticalGravity(Gravity.BOTTOM);
                
                ImageView imageView = new ImageView(TipsActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setImageResource(tipsResourceIdList.get(position));

                
                TextView tipsText = new TextView(TipsActivity.this);
                tipsText.setText(tipsTextArray[position]);
                tipsText.setPadding(15, 5, 15, 5);
                tipsText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                tipsText.setTextColor(Constants.COLOR_LINK_TEXT);

                linearLayout.addView(imageView, params);
                linearLayout.addView(tipsText, params);

                ((ViewPager) collection).addView(linearLayout, 0);
                return linearLayout;
            }

            @Override
            public void destroyItem(View collection, int position, Object view) {
                ((ViewPager) collection).removeView((LinearLayout) view);
            }


            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
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
                if(progressBar.getProgress() == progressBar.getMax()){
                    nextButton.setText(R.string.button_finish);
                }else {
                    nextButton.setText(R.string.next);
                }
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
        tipsResourceIdList.add(R.drawable.tips_4);
        tipsResourceIdList.add(R.drawable.tips_7);

        tipsTextArray = getResources().getStringArray(R.array.tips);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}