package com.goal98.girl.activity;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.goal98.android.WebImageView;
import com.goal98.girl.db.RSSURLDB;
import com.goal98.girl.db.SourceDB;
import com.goal98.girl.model.Article;
import com.goal98.girl.model.Source;
import com.goal98.girl.util.Constants;
import com.goal98.tika.common.TikaConstants;
import com.srz.androidtools.database.EachCursor;
import com.srz.androidtools.util.DeviceInfo;
import com.srz.androidtools.util.ManagedCursor;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-8-11
 * Time: 下午4:42
 * To change this template use File | Settings | File Templates.
 */
public class SourceGridArrayAdapter<T> extends ArrayAdapter<SourceItem> {
    int sourceItemLayoutResource;
    private LayoutInflater inflator;
    private DeviceInfo deviceInfo;
    private boolean selectable;

    public SourceGridArrayAdapter(Context indexActivity, final int sourceItemLayoutResource, SourceDB sourceDB, final DeviceInfo deviceInfo, final RSSURLDB urlDB) {
        super(indexActivity, sourceItemLayoutResource);

        Cursor sourceCursor = sourceDB.findSourceByMultipleType(new String[]{TikaConstants.TYPE_RSS, TikaConstants.TYPE_FEATURED});
//        Cursor sourceCursor = sourceDB.findAll();
        new ManagedCursor(sourceCursor).each(new EachCursor() {
            public void call(Cursor cursor, int index) {
                String sourceType = cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_TYPE));
                String sourceContentUrl = cursor.getString(cursor.getColumnIndex(Source.KEY_CONTENT_URL));
                String sourceName = cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_NAME));
                String sourceImage = cursor.getString(cursor.getColumnIndex(Source.KEY_IMAGE_URL));
                String sourceDesc = cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_DESC));
//                String sourceID = cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_ID));
                String sourceCat = cursor.getString(cursor.getColumnIndex(Source.KEY_CAT));
                long sourceUpdateTime = cursor.getLong(cursor.getColumnIndex(Source.KEY_UPDATE_TIME));

                String from = null;
                if (sourceType.equals(TikaConstants.TYPE_FEATURED)) {
                    from = sourceCat;
                } else {
                    from = sourceContentUrl;
                }
                String firstImage = null;
                List<Article> articles = urlDB.findOneByStatus(RSSURLDB.STATUS_NEW, from, 0, 50);
                if (articles != null && articles.size() > 0) {
                    String image = articles.get(0).getImageUrl().toExternalForm();
                    int i = 1;
                    while (image == null && i < articles.size()) {
                        image = articles.get(i).getImageUrl().toExternalForm();
                        i++;
                    }
                    if (image != null) {
                        firstImage = image;
                    }
                }

                SourceItem item = new SourceItem();
                item.setSourceType(sourceType);
                item.setSourceName(sourceName);
                item.setSourceImage(sourceImage);
                item.setSourceURL(sourceContentUrl);
                item.setSourceDesc(sourceDesc);
//                item.setSourceId(sourceID);
                item.setCategory(sourceCat);
                final Date date = new Date();
                date.setTime(sourceUpdateTime);
                item.setSourceUpdateTime(date);
                item.setFirstImage(firstImage);
                SourceGridArrayAdapter.this.add(item);
            }
        });
        init(sourceItemLayoutResource, deviceInfo);
    }

    private void init(int sourceItemLayoutResource, DeviceInfo deviceInfo) {
        this.notifyDataSetChanged();
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
        ViewGroup sourceItemView;
        final SourceItem item = getItem(position);

        if (convertView == null) {
            sourceItemView = (ViewGroup) inflator.inflate(sourceItemLayoutResource, null);
        } else {
            sourceItemView = (ViewGroup) convertView;
        }

        sourceItemView.removeAllViews();
        WebImageView sourceImageView = new WebImageView(this.getContext(), item.getFirstImage(), this.getContext().getResources().getDrawable(Constants.DEFAULT_PIC), this.getContext().getResources().getDrawable(Constants.DEFAULT_PIC), false, false, ImageView.ScaleType.CENTER_CROP);
        sourceImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (deviceInfo.isLargeScreen()) {
            sourceImageView.setDefaultHeight(200);
            sourceImageView.setDefaultWidth(200);
        } else if (deviceInfo.isSmallScreen()) {
            sourceImageView.setDefaultHeight(120);
            sourceImageView.setDefaultWidth(120);
        }
        try {
            if (item.getFirstImage() != null) {
                System.out.println(item.getFirstImage());
                sourceImageView.setImageUrl(item.getFirstImage());
                sourceImageView.loadImage();
            }
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }
        item.setSourceItemView(sourceItemView);
        sourceItemView.addView(sourceImageView,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, 200));

        return sourceItemView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
