package com.goal98.flipdroid2.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid2.R;
import com.goal98.flipdroid2.model.Article;
import com.goal98.flipdroid2.util.Constants;
import com.goal98.flipdroid2.util.PrettyTimeUtil;

import java.util.concurrent.*;


/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 2/17/11
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class WeiboArticleView extends ExpandableArticleView {
    public WeiboArticleView(Context context, Article article, ThumbnailViewContainer pageViewContainer, boolean placedAtBottom, ExecutorService executor) {
        super(context, article, pageViewContainer, placedAtBottom, executor);
        setWillNotDraw(false);
    }

    public void setText() {
        contentView.setText(article.getStatus());
    }

    protected String getPrefix() {
        return Constants.WITHURLPREFIX;
    }

    protected void reloadOriginalView() {
        setDisplayedChild(0);
        getChildAt(0).setVisibility(INVISIBLE);
        fadeInAni.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                getChildAt(0).setVisibility(VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {

            }
        });
        WeiboArticleView.this.contentView.startAnimation(fadeInAni);
    }

    protected ExecutorService executor;

    public void buildView() {


        LayoutInflater inflater = LayoutInflater.from(this.getContext());


        inflater.inflate(R.layout.weibo_article_view, this,true);
        setDisplayedChild(0);


        this.titleView = (TextView) findViewById(R.id.title);
        this.authorView = (TextView) findViewById(R.id.author);
        this.createDateView = (TextView) findViewById(R.id.createDate);
        this.contentViewWrapper = (LinearLayout) findViewById(R.id.contentll);

//        buildImageAndContent();
        this.portraitView = (WebImageView) findViewById(R.id.portrait2);

        authorView.setText(article.getAuthor());
        String localeStr = this.getContext().getString(R.string.locale);
        createDateView.setText(PrettyTimeUtil.getPrettyTime(localeStr, article.getCreatedDate()));

        if (article.getPortraitImageUrl() != null)
            portraitView.setImageUrl(article.getPortraitImageUrl().toString());

        addOnClickListener(contentViewWrapper);

    }

    public void renderBeforeLayout() {
        if (handler == null)
            handler = new Handler();

        portraitView.loadImage();
        new Thread(new Runnable() {
            public void run() {
                try {
                    isLoading = true;
                    handler.post(new Runnable() {
                        public void run() {
                            buildImageAndContent();
                        }

                    });
                } finally {
                    isLoading = false;
                }

            }
        }).start();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        Rect r = new Rect();
        this.getDrawingRect(r);
        canvas.drawColor(0xFF000000);
        paint.setColor(0xFF434343);
        canvas.drawLine(r.left, r.top + 1, r.right, r.top + 1, paint);
        int color = 46;

        for (int i = 0; i < (this.getHeight() / 2); i++) {
            paint.setARGB(255, color, color, color);
            canvas.drawRect(r.left, r.top + i + 1, r.right, r.top + i + 2, paint);
            color--;
        }
    }
}