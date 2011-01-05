package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
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

        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
//                imageView.setLayoutParams(new GridView.LayoutParams(120, 120));
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(image_array[i]);

            return imageView;
        }
    }
}