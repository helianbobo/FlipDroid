package com.goal98.flipdroid.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.util.TikaClient;
import com.goal98.flipdroid.util.TikaClientException;
import com.goal98.flipdroid.util.TikaResponse;
import weibo4j.http.HttpClient;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 2/17/11
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArticleWithoutURLView extends ArticleView {
    public ArticleWithoutURLView(Context context) {
        super(context);
    }

    public ArticleWithoutURLView(Context context, Article article) {
        super(context, article);
    }

    protected String getPrefix() {
        return "    \"";
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (article.hasLink()) {
            String url = article.extractURL();
            if (url != null)
                url = url.trim();
            TikaClient tc = new TikaClient();
            TikaResponse response = null;
            try {
                response = tc.extract(url);
            } catch (TikaClientException e) {
                return true;
            }
            article.setContent(response.getContent());
            article.setTitle(response.getTitle());
            ArticleView loadedArticleView = new ArticleWithURLView(this.getContext(), article);
            this.removeAllViews();
            this.addView(loadedArticleView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        }
        return true;
    }

    protected void buildView() {

        this.setBackgroundColor(0xffF7F7F7);
        this.setPadding(8, 0, 8, 0);
        LinearLayout titleLL = new LinearLayout(this.getContext());
        titleLL.setOrientation(HORIZONTAL);
        titleLL.setBaselineAligned(false);

        contentView.setText(getPrefix() + article.getStatus());
        contentView.setMaxLines(6);
        contentView.setEllipsisMore("");
        contentView.setTextSize(17);
        //contentView.setLayoutParams(new LayoutParams(0, LayoutParams.FILL_PARENT, 1));
        //contentView.setTypeface(Typeface.SERIF);
        titleLL.addView(contentView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        LinearLayout noneTitle = new LinearLayout(this.getContext());
        noneTitle.setOrientation(VERTICAL);
        noneTitle.setBaselineAligned(true);
        noneTitle.setGravity(Gravity.BOTTOM);

        LinearLayout publisherView = new LinearLayout(this.getContext());

//        TextView sharedByView = new TextView(this.getContext());
//        sharedByView.setText("Shared by");
//        sharedByView.setPadding(2, 2, 5, 2);
//        sharedByView.setTextSize(14);
//        sharedByView.setTextColor(0xffAAAAAA);
//        publisherView.addView(sharedByView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));

        publisherView.addView(portraitView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
        authorView.setTextSize(16);
        authorView.setTypeface(Typeface.DEFAULT_BOLD);
        publisherView.addView(super.authorView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
        publisherView.addView(super.createDateView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));


        this.setOrientation(VERTICAL);

        noneTitle.addView(publisherView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        this.setGravity(Gravity.CENTER);
        this.addView(titleLL, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        this.addView(noneTitle, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        this.setLayoutParams(new LayoutParams(0, LayoutParams.FILL_PARENT, 1));
        this.setBaselineAligned(false);
    }
}
