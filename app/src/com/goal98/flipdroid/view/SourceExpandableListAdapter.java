package com.goal98.flipdroid.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.SourceDB;
import com.srz.androidtools.util.AlarmSender;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-7-6
 * Time: 下午3:40
 * To change this template use File | Settings | File Templates.
 */
public class SourceExpandableListAdapter extends SimpleExpandableListAdapter {
    private List<? extends List<? extends Map<String, String>>> mChildData;
    private String[] mChildFrom;
    private int[] mChildTo;
    private SourceDB sourceDB;
    private Context context;

    public SourceExpandableListAdapter(Context context, List<? extends Map<String, ?>> groupData, int groupLayout, String[] groupFrom, int[] groupTo, List<? extends List<? extends Map<String, ?>>> childData, int childLayout, String[] childFrom, int[] childTo, SourceDB sourceDB) {
        super(context, groupData, groupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
        mChildData = (List<? extends List<? extends Map<String, String>>>) childData;
        mChildFrom = childFrom;
        mChildTo = childTo;
        this.sourceDB = sourceDB;
        this.context = context;
    }

    public SourceExpandableListAdapter(Context context, List<? extends Map<String, ?>> groupData, int expandedGroupLayout, int collapsedGroupLayout, String[] groupFrom, int[] groupTo, List<? extends List<? extends Map<String, ?>>> childData, int childLayout, String[] childFrom, int[] childTo) {
        super(context, groupData, expandedGroupLayout, collapsedGroupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
    }

    public SourceExpandableListAdapter(Context context, List<? extends Map<String, ?>> groupData, int expandedGroupLayout, int collapsedGroupLayout, String[] groupFrom, int[] groupTo, List<? extends List<? extends Map<String, ?>>> childData, int childLayout, int lastChildLayout, String[] childFrom, int[] childTo) {
        super(context, groupData, expandedGroupLayout, collapsedGroupLayout, groupFrom, groupTo, childData, childLayout, lastChildLayout, childFrom, childTo);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return super.getGroupView(groupPosition, isExpanded, convertView, parent);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = newChildView(isLastChild, parent);
        } else {
            v = convertView;
        }

        bindView(v, mChildData.get(groupPosition).get(childPosition), mChildFrom, mChildTo, groupPosition, childPosition);
        return v;
    }

    private void bindView(final View v, Map<String, String> stringMap, String[] mChildFrom, int[] mChildTo, final int groupPosition, final int childPosition) {

        final WebImageView image = (WebImageView) v.findViewById(R.id.source_image);
        final TextView sourceName = (TextView) v.findViewById(R.id.source_name);
        final TextView sourceDesc = (TextView) v.findViewById(R.id.source_desc);
        final TextView sourceType = (TextView) v.findViewById(R.id.source_type);
        final ImageView ticker = (ImageView) v.findViewById(R.id.ticker);
        final ImageView tickerremove = (ImageView) v.findViewById(R.id.tickerremove);

        final String sn = stringMap.get(mChildFrom[0]);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> sourceMap = (Map<String, String>) getChild(groupPosition, childPosition);

                if (sourceDB.findSourceByName(sn).getCount() == 0) {
                    sourceDB.insert(sourceMap);
                } else {
                    sourceDB.removeSourceByName(sn);
                }
                notifyDataSetChanged();

                AlarmSender.sendInstantMessage(R.string.added, SourceExpandableListAdapter.this.context);
            }
        });

        if (sourceDB.findSourceByName(sn).getCount() == 0) {
            ticker.setVisibility(View.VISIBLE);
            tickerremove.setVisibility(View.GONE);
        } else {
            ticker.setVisibility(View.GONE);
            tickerremove.setVisibility(View.VISIBLE);
        }
        image.setInAnimation(null);
        image.setImageUrl(stringMap.get(mChildFrom[2]));
        image.loadImage();
        sourceName.setText(sn);
        sourceDesc.setText(stringMap.get(mChildFrom[1]));
        sourceType.setText(stringMap.get(mChildFrom[3]));
    }
}
