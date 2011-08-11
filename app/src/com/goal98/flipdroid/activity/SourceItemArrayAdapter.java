package com.goal98.flipdroid.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.google.RssParser;
import com.goal98.flipdroid.util.DeviceInfo;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-8-11
 * Time: 下午4:42
 * To change this template use File | Settings | File Templates.
 */
public class SourceItemArrayAdapter<T> extends ArrayAdapter<SourceItem> {
    int sourceItemLayoutResource;
    private LayoutInflater inflator;
    private DeviceInfo deviceInfo;

    public SourceItemArrayAdapter(Context indexActivity, int sourceItemLayoutResource, List<SourceItem> items, DeviceInfo deviceInfo) {
        super(indexActivity, sourceItemLayoutResource, items);
        this.sourceItemLayoutResource = sourceItemLayoutResource;
        inflator = LayoutInflater.from(this.getContext());
        this.deviceInfo = deviceInfo;
    }

    public int getCount() {
        return super.getCount();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public SourceItem getItem(int i) {
        return super.getItem(i);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public long getItemId(int i) {
        return super.getItemId(i);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LinearLayout sourceItemView;
        SourceItem item = getItem(position);

        if (convertView == null) {
            sourceItemView = (LinearLayout) inflator.inflate(sourceItemLayoutResource, null);
        } else {
            sourceItemView = (LinearLayout) convertView;
        }

        TextView sourceNameTV = (TextView) sourceItemView.findViewById(R.id.source_name);
        TextView sourceTypeTV = (TextView) sourceItemView.findViewById(R.id.source_type);
        TextView sourceDescTV = (TextView) sourceItemView.findViewById(R.id.source_desc);
        TextView sourceURLTV = (TextView) sourceItemView.findViewById(R.id.source_url);
        WebImageView sourceImageView = (WebImageView) sourceItemView.findViewById(R.id.source_image);

        if (deviceInfo.isLargeScreen()) {
            sourceImageView.setDefaultHeight(75);
            sourceImageView.setDefaultWidth(75);
        } else if (deviceInfo.isSmallScreen()) {
            sourceImageView.setDefaultHeight(30);
            sourceImageView.setDefaultWidth(30);
        }
        try {
            if (item.getSourceImage() != null)
                sourceImageView.setImageUrl(item.getSourceImage());
            sourceImageView.loadImage();
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }
        item.setSourceItemView(sourceItemView);
        sourceNameTV.setText(item.getSourceName());
        sourceTypeTV.setText(item.getSourceType());
        sourceDescTV.setText(item.getSourceDesc());
        sourceURLTV.setText(item.getSourceURL());

        return sourceItemView;
    }
}
