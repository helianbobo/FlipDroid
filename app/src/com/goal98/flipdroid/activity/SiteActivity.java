package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.util.Constants;


public class SiteActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.site);

        GridView g = (GridView) findViewById(R.id.siteGrid);
        g.setAdapter(new ButtonAdapter(this));

    }

    public class ButtonAdapter extends BaseAdapter {

        private Context mContext;

        private String[] site_type_array = {Constants.TYPE_SINA_WEIBO, Constants.TYPE_TWITTER, Constants.TYPE_TENCENT_WEIBO};
        private int[] image_array = {R.drawable.sina, R.drawable.twitter, R.drawable.tencent};

        public ButtonAdapter(Context context) {
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
                    Intent intent = new Intent(SiteActivity.this, SourceActivity.class);
                    intent.putExtra("type", (String) getItem(i));
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                }

            });


            return imageButton;
        }
    }
}