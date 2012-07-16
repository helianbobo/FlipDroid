package com.goal98.girl.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import com.goal98.girl.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 12-7-16
 * Time: 下午11:07
 * To change this template use File | Settings | File Templates.
 */
public class GrilPTR extends PullToRefreshListView {
    public GrilPTR(Context context) {
        super(context);
    }

    public GrilPTR(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ListView createAdapterView(Context context, AttributeSet attrs) {
        ListView lv = new ListView(context, attrs);
        // Set it to this so it can be used in ListActivity/ListFragment
        lv.setVerticalFadingEdgeEnabled(true);
        lv.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.listbggirl));
//        lv.setPadding(10,0,10,0);
        lv.setId(android.R.id.list);
        return lv;
    }
}
