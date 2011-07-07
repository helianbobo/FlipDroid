package com.goal98.flipdroid.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;

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


    public SourceExpandableListAdapter(Context context, List<? extends Map<String, ?>> groupData, int groupLayout, String[] groupFrom, int[] groupTo, List<? extends List<? extends Map<String, ?>>> childData, int childLayout, String[] childFrom, int[] childTo) {
        super(context, groupData, groupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
        mChildData = (List<? extends List<? extends Map<String, String>>>) childData;
        mChildFrom = childFrom;
        mChildTo = childTo;
    }

    public SourceExpandableListAdapter(Context context, List<? extends Map<String, ?>> groupData, int expandedGroupLayout, int collapsedGroupLayout, String[] groupFrom, int[] groupTo, List<? extends List<? extends Map<String, ?>>> childData, int childLayout, String[] childFrom, int[] childTo) {
        super(context, groupData, expandedGroupLayout, collapsedGroupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
    }

    public SourceExpandableListAdapter(Context context, List<? extends Map<String, ?>> groupData, int expandedGroupLayout, int collapsedGroupLayout, String[] groupFrom, int[] groupTo, List<? extends List<? extends Map<String, ?>>> childData, int childLayout, int lastChildLayout, String[] childFrom, int[] childTo) {
        super(context, groupData, expandedGroupLayout, collapsedGroupLayout, groupFrom, groupTo, childData, childLayout, lastChildLayout, childFrom, childTo);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        View v;
        if (convertView == null) {
            v = newChildView(isLastChild, parent);
        } else {
            v = convertView;
        }
//        SourceItemViewBinder sourceItemViewBinder = new SourceItemViewBinder();
//        sourceItemViewBinder.setViewValue();
        bindView(v, mChildData.get(groupPosition).get(childPosition), mChildFrom, mChildTo, groupPosition, childPosition);
        return v;
    }

    private void bindView(View v, Map<String, String> stringMap, String[] mChildFrom, int[] mChildTo, int groupPosition, int childPosition) {
        WebImageView image = (WebImageView) v.findViewById(R.id.source_image);
        TextView sourceName = (TextView) v.findViewById(R.id.source_name);
        TextView sourceDesc = (TextView) v.findViewById(R.id.source_desc);
        TextView sourceType = (TextView) v.findViewById(R.id.source_type);

        image.setImageUrl(stringMap.get(mChildFrom[2]));
        image.loadImage();
        sourceName.setText(stringMap.get(mChildFrom[0]));
        sourceDesc.setText(stringMap.get(mChildFrom[1]));
        sourceType.setText(stringMap.get(mChildFrom[3]));
    }
}
