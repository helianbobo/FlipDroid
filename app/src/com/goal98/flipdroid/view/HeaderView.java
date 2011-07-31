package com.goal98.flipdroid.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.activity.IndexActivity;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.util.DeviceInfo;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-6-24
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */
public class HeaderView extends LinearLayout {
    private ListView sourceList;
    //    private LinearLayout navigatorFrame;
    private boolean sourceSelectMode;
    private PageActivity pageActivity;
    private LayoutInflater inflater;
    private WeiboPageView pageView;

    public HeaderView(Context context) {
        super(context);
        this.pageActivity = (PageActivity) context;
        buildHeaderText();
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.pageActivity = (PageActivity) context;
        buildHeaderText();
    }

    public void setPageView(WeiboPageView pageView) {
        this.pageView = pageView;
    }

    public void setSourceSelectMode(boolean sourceSelectMode) {
        this.sourceSelectMode = sourceSelectMode;
    }

//    private void closeSourceSelection() {
//        setSourceSelectMode(false);
//        navigatorFrame.setVisibility(GONE);
//    }

    public boolean isSourceSelectMode() {
        return sourceSelectMode;
    }

    private void buildHeaderText() {


//        navigatorFrame = new LinearLayout(pageActivity);
//        navigatorFrame.setPadding(0, IndexActivity.statusBarHeight + 4, 0, 0);
//        navigatorFrame.setVisibility(GONE);

//        LinearLayout navigatorShadow = new LinearLayout(pageActivity);
//
//        navigatorShadow.setPadding((int) (DeviceInfo.width * 0.1), 0, (int) (DeviceInfo.width * 0.1), 0);
//        navigatorShadow.setBackgroundColor(Color.parseColor("#77000000"));
//
//        navigatorShadow.setOnClickListener(new OnClickListener() {
//            public void onClick(View view) {
//
//                closeSourceSelection();
//            }
//        });
        inflater = LayoutInflater.from(pageActivity);
        sourceList = (ListView) inflater.inflate(R.layout.navigator, null);
        LinearLayout frameLayout = (LinearLayout) inflater.inflate(R.layout.header, null);

//        final LinearLayout navigator = new LinearLayout(pageActivity);
//        navigator.setOrientation(LinearLayout.VERTICAL);
//        navigator.setBackgroundResource(R.drawable.roundcorner);
//        navigator.setGravity(Gravity.CENTER);
//
//        navigator.addView(sourceList);
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int)
//                (DeviceInfo.width * 0.8), (int) (DeviceInfo.height * 0.5));
//        layoutParams.gravity = Gravity.CENTER;
//        navigatorShadow.addView(navigator, layoutParams);

        TextView headerText = (TextView) frameLayout.findViewById(R.id.headerText);
        boolean largeScreen = false;
        if (DeviceInfo.height == 800) {
            largeScreen = true;
        }
        if (largeScreen) {
            headerText.setTextSize(22);
        }
        headerText.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                //System.out.println("sourceSelectMode:"+sourceSelectMode);
                HeaderView.this.pageActivity.showDialog(PageActivity.NAVIGATION);
//                if (sourceSelectMode) {
//                    closeSourceSelection();
//                    return;
//                }
//                setSourceSelectMode(true);
//                navigatorFrame.setVisibility(VISIBLE);
//
//                pageView.addView(navigatorFrame);
//                SourceDB sourceDB = new SourceDB(pageActivity.getApplicationContext());
//
//                Cursor sourceCursor = sourceDB.findAll();
//                pageActivity.startManagingCursor(sourceCursor);

//                SimpleCursorAdapter adapter = new SimpleCursorAdapter(pageActivity, R.layout.source_selection_item, sourceCursor,
//                        new String[]{Source.KEY_SOURCE_NAME, Source.KEY_SOURCE_DESC, Source.KEY_IMAGE_URL},
//                        new int[]{R.id.source_name, R.id.source_desc, R.id.source_image});
//                adapter.setViewBinder(new SourceItemViewBinder());
//                sourceList.setOnItemClickListener(new ListView.OnItemClickListener() {
//
//                    public void onItemClick(AdapterView<?> l, View view, int i, long id) {
//
//                        Intent intent = new Intent(pageActivity, PageActivity.class);
//                        Cursor cursor = (Cursor) l.getItemAtPosition(i);
//                        intent.putExtra("type", cursor.getString(cursor.getColumnIndex(Source.KEY_ACCOUNT_TYPE)));
//                        intent.putExtra("sourceId", cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_ID)));
//                        intent.putExtra("sourceImage", cursor.getString(cursor.getColumnIndex(Source.KEY_IMAGE_URL)));
//                        intent.putExtra("sourceName", cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_NAME)));
//                        intent.putExtra("contentUrl", cursor.getString(cursor.getColumnIndex(Source.KEY_CONTENT_URL)));
//                        cursor.close();
//                        pageActivity.startActivity(intent);
//                        pageActivity.overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
//                        pageActivity.finish();
//                    }
//                });
//                sourceList.setAdapter(adapter);
            }
        });

//        navigatorFrame.addView(navigatorShadow, new LayoutParams
//                (LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        this.addView(frameLayout);
    }
}
