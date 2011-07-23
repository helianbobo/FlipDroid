package com.goal98.flipdroid.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.text.Html;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.PrettyTimeUtil;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 2/17/11
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContentLoadedView extends ArticleView {
    private float oldDist;

    public ContentLoadedView(Context context, Article article, WeiboPageView pageView) {
        super(context, article, pageView, true);
    }

    protected String getPrefix() {
        return "            ";
    }

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount <= 1)
            return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                //Log.d("MTZ", "oldDist=" + oldDist);

                if (oldDist > 10f) {
                    mode = ZOOM;
                    //Log.d("MTZ", "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    float newDist = spacing(event);
                    //Log.d("MTZ", "newDist=" + newDist);
                    if (newDist > oldDist) {
                        //Log.d("MTZ", "zoomout" + newDist);
                    } else if (newDist < oldDist) {
                        //Log.d("MTZ", "zoomin" + newDist);
                    }
                }
                break;
        }
        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public void buildView() {
        LayoutInflater inflator = LayoutInflater.from(this.getContext());
        LinearLayout layout = (LinearLayout) inflator.inflate(R.layout.enlarged_content, this);

        ScrollView wrapper = (ScrollView) layout.findViewById(R.id.wrapper);
        wrapper.setVerticalScrollBarEnabled(false);

        this.authorView = (TextView) layout.findViewById(R.id.author);
        this.titleView = (TextView) layout.findViewById(R.id.title);
        authorView.setText(article.getAuthor());
        titleView.setText(article.getTitle());
        createDateView = (TextView) layout.findViewById(R.id.createdDate);

        String time = PrettyTimeUtil.getPrettyTime(this.getContext(), article.getCreatedDate());
        createDateView.setText(time);

        this.portraitView = (WebImageView) layout.findViewById(R.id.portrait);
        if (article.getPortraitImageUrl() != null) {
            this.portraitView.setImageUrl(article.getPortraitImageUrl().toString());
            this.portraitView.loadImage();
        }else{
            this.portraitView.setVisibility(View.GONE);
        }

        this.contentView = (TextView) layout.findViewById(R.id.content);
        new ArticleTextViewRender(getPrefix()).renderTextView(contentView, article);
    }

    public void renderBeforeLayout() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
