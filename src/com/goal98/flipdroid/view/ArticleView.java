package com.goal98.flipdroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableLayout;
import android.widget.TextView;
import com.goal98.flipdroid.model.Article;

public class ArticleView extends TableLayout{

    private Article article;

    private TextView titleView;

    public void setArticle(Article article) {
        this.article = article;
        renderView();
    }

    private void renderView() {
        if(titleView == null){
            titleView = new TextView(getContext());
        }
        titleView.setText(article.getTitle());
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
        addView(titleView);
    }
}
