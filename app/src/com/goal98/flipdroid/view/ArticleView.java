package com.goal98.flipdroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.Article;

public class ArticleView extends TableLayout {

    private Article article;

    private TextView titleView;
    private TextView contentView;
    private InternetImageView portraitView;

    public void setArticle(Article article) {
        this.article = article;
        renderView();
    }

    private void renderView() {
        if (titleView == null) {
            titleView = new TextView(getContext());
            titleView.setPadding(2, 2, 2, 2);
            titleView.setTextSize(20);
        }
        titleView.setText(article.getTitle());

        if (contentView == null) {
            contentView = new TextView(getContext());
            contentView.setPadding(5, 5, 5, 5);
            contentView.setSingleLine(false);
            contentView.setWidth(1);
            contentView.setMaxLines(4);
        }
        contentView.setText(article.getContent());

        if (portraitView == null) {
            portraitView = new InternetImageView(getContext(), article.getImageUrl(), 2);
            portraitView.setImageResource(R.drawable.portrait_small);
            portraitView.setPadding(2, 2 ,2 ,2);
        }

    }

    public ArticleView(Context context) {
        super(context);
    }

    public ArticleView(Context context, Article article) {
        super(context);
        setArticle(article);

        buildView();
    }


    public ArticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        buildView();
    }

    private void buildView() {
        TableRow tableRow1 = new TableRow(getContext());
        addView(tableRow1);

        tableRow1.addView(portraitView);
        tableRow1.addView(titleView);

        addView(contentView);
    }
}
