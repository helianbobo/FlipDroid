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


public class SiteActivity extends Activity {

    private AccountDB accountDB;

    private SharedPreferences preferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountDB = new AccountDB(this);

        setContentView(R.layout.site);

        GridView g = (GridView) findViewById(R.id.siteGrid);
        g.setAdapter(new ButtonAdapter(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
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

                    String type = (String) getItem(i);
                    if (accountDB.hasAccount(type) && preferences.getString("sina_account", null) != null) {
                        Intent intent = new Intent(SiteActivity.this, SourceSelectionActivity.class);
                        intent.putExtra("type", type);
                        intent.putExtra("next", SourceSelectionActivity.class.getName());
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                    } else {
                        if (Constants.TYPE_SINA_WEIBO.equals(type)) {
                            startActivity(new Intent(SiteActivity.this, SinaAccountActivity.class));
                            overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                        } else if(Constants.TYPE_TENCENT_WEIBO.equals(type)){
                            //TODO: Tencent Login
                        } else if(Constants.TYPE_TWITTER.equals(type)){
                            //TODO: Twitter Login
                        }
                    }


                }

            });


            return imageButton;
        }
    }
}