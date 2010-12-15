package com.goal98.flipdroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.goal98.flipdroid.model.Article;

public class ArticleView extends TableLayout{

    private Article article;

    private TextView titleView;
    private TextView contentView;
    private InternetImageView portraitView;

    public void setArticle(Article article) {
        this.article = article;
        renderView();
    }

    private void renderView() {
        if(titleView == null){
            titleView = new TextView(getContext());
        }
        titleView.setText(article.getTitle());

        if(contentView == null){
            contentView = new TextView(getContext());
        }
        contentView.setText(article.getContent());

        if(portraitView == null){
            portraitView = new InternetImageView(getContext(), article.getImageUrl());
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



        TableRow tableRow2 = new TableRow(getContext());
        addView(tableRow2);

        tableRow2.addView(contentView);
    }
}
