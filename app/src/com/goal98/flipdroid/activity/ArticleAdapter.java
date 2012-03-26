package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.view.ArticleHolder;
import com.goal98.flipdroid.view.ContentLoadedView;
import com.goal98.flipdroid.view.PopupWindowManager;
import com.goal98.flipdroid.view.StreamStyledArticleView;

import java.util.ArrayList;
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

    public ArticleAdapter(Activity activity, ListView listView, int progressDrawableResourceId, int layoutId, PaginationLoaderService loaderService, List<Article> videoDetailInfos, int nodataview, View.OnClickListener noitemListener) {
        super(activity, listView, layoutId, progressDrawableResourceId, videoDetailInfos, nodataview, loaderService, noitemListener);
        this.context = activity;
    }

    public ArticleAdapter(Activity activity, ListView listView, int progressDrawableResourceId, int layoutId, PaginationLoaderService loaderService, int nodataview, View.OnClickListener noitemListener) {
        this(activity, listView, progressDrawableResourceId, layoutId, loaderService, new ArrayList(), nodataview, noitemListener);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        StreamStyledArticleView articleView = (StreamStyledArticleView) view;
        Article article = articleView.getArticle();

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
