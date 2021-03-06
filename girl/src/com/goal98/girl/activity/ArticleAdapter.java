package com.goal98.girl.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.goal98.girl.R;
import com.goal98.girl.model.Article;
import com.goal98.girl.view.ArticleHolder;
import com.goal98.girl.view.StreamStyledArticleView;
import com.srz.androidtools.autoloadlistview.OnNothingLoaded;
import com.srz.androidtools.autoloadlistview.PaginationLoaderAdapter;
import com.srz.androidtools.autoloadlistview.PaginationLoaderService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: janexie
 * Date: 12-1-26
 * Time: 下午8:05
 * To change this template use File | Settings | File Templates.
 */
public class ArticleAdapter extends PaginationLoaderAdapter {
    protected Context context;
    private boolean loaded;

    public ArticleAdapter(Activity activity, ListView listView, int progressDrawableResourceId, int layoutId, PaginationLoaderService loaderService, List<Article> videoDetailInfos, int nodataview, View.OnClickListener noitemListener, OnNothingLoaded onNothingLoadedListener) {
        super(activity, listView, layoutId, progressDrawableResourceId, videoDetailInfos, nodataview, loaderService, noitemListener,onNothingLoadedListener);
        if(onNothingLoadedListener!=null)
            onNothingLoadedListener.setAdapter(this);
        this.context = activity;
    }

    public ArticleAdapter(Activity activity, ListView listView, int progressDrawableResourceId, int layoutId, PaginationLoaderService loaderService, int nodataview, View.OnClickListener noitemListener,OnNothingLoaded onNothingLoadedListener) {
        this(activity, listView, progressDrawableResourceId, layoutId, loaderService, new ArrayList(), nodataview, noitemListener,onNothingLoadedListener);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        StreamStyledArticleView articleView = (StreamStyledArticleView) view;
        Article article = articleView.getArticle();

        List<Article> articles = new LinkedList<Article>();
        for (int j = 0; j < items.size(); j++) {
            ArticleDetailInfo adi = (ArticleDetailInfo) items.get(j);
            articles.add(adi.getArticle());
        }
        ArticleHolder.getInstance().setArticles(articles);
        ArticleHolder.getInstance().setArticle(article);

        Intent articleLoadedActivityIntent = new Intent(context, ContentPagedActivity.class);
        context.startActivity(articleLoadedActivityIntent);
    }

    @Override
    protected void setNoDataOnClickListener(View nodataitem, View.OnClickListener listener) {
        nodataitem.findViewById(R.id.addmorefeeds).setOnClickListener(listener);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
        loaded = true;
    }


}
