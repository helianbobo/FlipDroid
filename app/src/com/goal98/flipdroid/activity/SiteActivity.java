package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.SinaAccountUtil;


public class SiteActivity extends Activity {


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.site);

        GridView g = (GridView) findViewById(R.id.siteGrid);
        g.setAdapter(new SiteAdapter(this));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public class SiteAdapter extends BaseAdapter {

        private Context mContext;

        private String[] site_type_array = {Constants.TYPE_SINA_WEIBO, Constants.TYPE_RSS, Constants.TYPE_GOOGLE_READER, Constants.TYPE_FLIPDROID,Constants.TYPE_BAIDUSEARCH};
        private int[] image_array = {R.drawable.sina, R.drawable.rss, R.drawable.greader, R.drawable.icon_large,R.drawable.baidu};

        public SiteAdapter(Context context) {
            this.mContext = context;
        }

        public int getCount() {
            return site_type_array.length;
        }

        public Object getItem(int i) {
            return site_type_array[i];
        }

        public long getItemId(int i) {
            return i;
        }

        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            ImageButton imageButton;
            if (convertView == null) {
                imageButton = new ImageButton(mContext);
                imageButton.setAdjustViewBounds(true);
                imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                imageButton = (ImageButton) convertView;
            }

            imageButton.setImageResource(image_array[i]);

            imageButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    String type = (String) getItem(i);

                    if (Constants.TYPE_SINA_WEIBO.equals(type)) {
                        if (SinaAccountUtil.alreadyBinded(SiteActivity.this)) {
                            Intent intent = new Intent(SiteActivity.this, SinaSourceSelectionActivity.class);
                            intent.putExtra("type", type);
                            intent.putExtra("next", SinaSourceSelectionActivity.class.getName());
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                        } else {
                            final Intent intent = new Intent(SiteActivity.this, SinaAccountActivity.class);
                            intent.putExtra("PROMPTTEXT",SiteActivity.this.getString(R.string.gotosinaoauth));
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                        }
                    }
                    if (Constants.TYPE_GOOGLE_READER.equals(type)) {
                        startActivity(new Intent(SiteActivity.this, GoogleAccountActivity.class));
                        overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                        finish();
                    }
                    if (Constants.TYPE_RSS.equals(type)) {
                        Intent intent = new Intent(SiteActivity.this, RSSSourceSelectionActivity.class);
                        intent.putExtra("type", type);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                        finish();
                    }
                    if (Constants.TYPE_FLIPDROID.equals(type)) {
                        Intent intent = new Intent(SiteActivity.this, FlipdroidSourceActivity.class);
                        intent.putExtra("type", type);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                        finish();
                    }
                    
                    if (Constants.TYPE_BAIDUSEARCH.equals(type)) {
                        Intent intent = new Intent(SiteActivity.this, BaiduSearchSourceActivity.class);
                        intent.putExtra("type", type);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                        finish();
                    }

//                        if(Constants.TYPE_TENCENT_WEIBO.equals(type)){
//                            //TODO: Tencent Login
//                        } if(Constants.TYPE_TWITTER.equals(type)){
//                            //TODO: Twitter Login
//                        }


                }

            }

            );
            return imageButton;
        }


    }
}