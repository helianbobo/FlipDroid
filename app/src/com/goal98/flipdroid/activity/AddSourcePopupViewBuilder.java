package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.util.SinaAccountUtil;
import com.goal98.tika.common.TikaConstants;

public class AddSourcePopupViewBuilder {
    private final Activity activity;

    public AddSourcePopupViewBuilder(Activity indexActivity) {
        this.activity = indexActivity;
    }

    public View buildAddSourcePopupView(final Activity activity) {
        final View addSourcePopUp = LayoutInflater.from(activity).inflate(R.layout.add_source, null);
        addSourcePopUp.findViewById(R.id.sina).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (SinaAccountUtil.alreadyBinded(activity)) {
                    Intent intent = new Intent(activity, SinaSourceSelectionActivity.class);
                    intent.putExtra("type", TikaConstants.TYPE_SINA_WEIBO);
                    intent.putExtra("next", SinaSourceSelectionActivity.class.getName());
                    AddSourcePopupViewBuilder.this.activity.startActivity(intent);
                } else {
                    final Intent intent = new Intent(activity, SinaAccountActivity.class);
                    intent.putExtra("PROMPTTEXT", activity.getString(R.string.gotosinaoauth));
                    AddSourcePopupViewBuilder.this.activity.startActivity(intent);
                }
            }
        });

        addSourcePopUp.findViewById(R.id.rss).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(activity, RSSSourceSelectionActivity.class);
                intent.putExtra("type", TikaConstants.TYPE_RSS);
                AddSourcePopupViewBuilder.this.activity.startActivity(intent);
            }
        });

        addSourcePopUp.findViewById(R.id.gr).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AddSourcePopupViewBuilder.this.activity.startActivity(new Intent(activity, GoogleAccountActivity.class));
            }
        });
        return addSourcePopUp;
    }
}