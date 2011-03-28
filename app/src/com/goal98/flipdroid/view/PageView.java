package com.goal98.flipdroid.view;


import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.Article;

import java.util.*;

public class PageView extends LinearLayout {


    private Page page;
    //private int articlePerRow = 2;
    private LinearLayout layout; //this layout is supposed to be dynamic, depending on the Articles on this page
    private LinearLayout headerLayout;


    public void setPage(Page page) {
        this.page = page;
        this.layout.removeAllViews();
        this.viewList.clear();
        setArticleList();
    }

    private void setArticleList() {
        if (page != null) {
            List<Article> articleList = page.getArticleViewList();
            Log.d("number of article", articleList.size() + "");
            if (articleList != null && !articleList.isEmpty()) {

                for (int i = 0; i < articleList.size(); i++) {
                    Article article = articleList.get(i);

                    addArticleView(article);
                }
            }
        }
    }

    public LinearLayout addArticleView(Article article) {
        ArticleWithoutURLView withoutURLView = new ArticleWithoutURLView(PageView.this.getContext(), article);

        withoutURLView.preload();
        LinearLayout articleWrapper = new LinearLayout(this.getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8,0,8,0);
        articleWrapper.addView(withoutURLView, layoutParams);
        layout.addView(articleWrapper,new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        viewList.add(articleWrapper);
        return articleWrapper;
    }

    private void fillPartOnLayout(int index, int viewid, List<Article> articleList) {
        Article article = articleList.get(index);
        ArticleView articleView = new ArticleWithoutURLView(this.getContext(), article);
        LinearLayout articleWrapper = (LinearLayout) layout.findViewById(viewid);
        articleWrapper.removeAllViews();


    }

    public List<LinearLayout> getViewList() {
        return viewList;
    }
    
    public void centerAll(){
        for (int i = 0; i < viewList.size(); i++) {
            LinearLayout linearLayout =  viewList.get(i);
            linearLayout.setGravity(Gravity.CENTER);
        }
    }

    private List<LinearLayout> viewList = new ArrayList<LinearLayout>();

    public PageView(Context context) {
        super(context);
        setDynamicLayout(this.getContext());
    }

    private void setDynamicLayout(Context context) {
        this.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(context);
        this.layout = (LinearLayout) inflater.inflate(R.layout.l3, null);
        headerLayout = (LinearLayout) inflater.inflate(R.layout.header, null);
        TextView headerText = (TextView) headerLayout.findViewById(R.id.headerText);
        headerText.setText(R.string.sinaweoboheader);

        this.setOrientation(VERTICAL);

        this.addView(headerLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT, 93));
        this.addView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT, 7));
    }

    public void enlarge(ArticleView articleView) {
        final Animation fadeOutAni = AnimationUtils.loadAnimation(this.getContext(), R.anim.fade);

        for (int i = 0; i < viewList.size(); i++) {
            final LinearLayout articleViewWrapper = viewList.get(i);
            if (articleViewWrapper.indexOfChild(articleView) == -1) {
               articleViewWrapper.startAnimation(fadeOutAni);
            }
        }

        PageView.this.layout.removeAllViews();
        LinearLayout enlargedViewWrapper = new LinearLayout(this.getContext());
        enlargedViewWrapper.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        ((LinearLayout) articleView.getParent()).removeAllViews();
        enlargedViewWrapper.setPadding(8, 0, 8, 0);

        enlargedViewWrapper.addView(articleView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        this.layout.addView(enlargedViewWrapper, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        articleView.enlarge();
    }

    public void removeArticleView(ArticleView articleView) {
        for (int i = 0; i < viewList.size(); i++) {
            LinearLayout linearLayout =  viewList.get(i);
            if(linearLayout.indexOfChild(articleView) != -1){
                layout.removeView(linearLayout);
                viewList.remove(i);
                return ;
            }
        }
    }

    public LinearLayout getLayout() {
        return layout;
    }
}
