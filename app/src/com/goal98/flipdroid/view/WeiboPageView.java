package com.goal98.flipdroid.view;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.activity.IndexActivity;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.activity.SinaAccountActivity;
import com.goal98.flipdroid.activity.WeiPaiWebViewClient;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.exception.NoSinaAccountBindedException;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.DeviceInfo;
import weibo4j.WeiboException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class WeiboPageView extends FrameLayout {
    private Page page;

    protected LinearLayout frame;
    public LayoutInflater inflater;
    protected ExecutorService executor;

    public LinearLayout getContentLayout() {
        return contentLayout;
    }

    protected LinearLayout contentLayout; //this layout is supposed to be dynamic, depending on the Articles on this smartPage


    protected LinearLayout enlargedViewWrapper;
    //    protected ScrollView wrapper;
    protected String sourceName;
    protected String sourceImageURL;
    protected PageActivity pageActivity;
    private LinearLayout wrapperll;
    private LinearLayout loadingView;
    private LinearLayout commentShadowLayer;

    public WebImageView headerImage;


    private boolean rendered;

    public boolean isRendered() {
        return rendered;
    }


    protected boolean loadingNext;

    public void setPage(Page page) {
        this.page = page;

        this.contentLayout.removeAllViews();
        List<Article> articleList = this.page.getArticleViewList();
        //System.out.println("articleList:" + articleList.size());

        int heightSum = 0 ;
        for (int i = 0; i < articleList.size(); i++) {
            heightSum+=articleList.get(i).getHeight();
        }

        for (int i = 0; i < articleList.size(); i++) {
            Article article = articleList.get(i);
            boolean isLastArticle = i == articleList.size() - 1;
            article.setHeight((int) ((article.getHeight()* DeviceInfo.displayHeight)/(float)heightSum));
            addArticleView(article, isLastArticle);
        }
    }

    public List<ArticleView> getWeiboViews() {
        return weiboViews;
    }

    public List<LinearLayout> getWrapperViews() {
        return wrapperViews;
    }

    protected List<ArticleView> weiboViews = new ArrayList<ArticleView>();
    protected List<LinearLayout> wrapperViews = new ArrayList<LinearLayout>();

    public LinearLayout addArticleView(Article article, boolean last) {
        WeiboArticleView withoutURLArticleView = new WeiboArticleView(WeiboPageView.this.getContext(), article, this, last, executor);
        weiboViews.add(withoutURLArticleView);


        LinearLayout articleWrapper = new LinearLayout(this.getContext());
        wrapperViews.add(articleWrapper);
        articleWrapper.setBackgroundColor(0xffDDDDDD);//分割线颜色
        articleWrapper.setGravity(Gravity.TOP);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        articleWrapper.addView(withoutURLArticleView, layoutParams);

        LayoutParams wrapperLayoutParam = null;
//        if (last)
            wrapperLayoutParam = new LayoutParams(DeviceInfo.width, article.getHeight());
//        else {
//            wrapperLayoutParam = new LayoutParams(DeviceInfo.width, LayoutParams.WRAP_CONTENT);
//        }

        if(last)//分割线
            articleWrapper.setPadding(0, 0, 0, 0);
        else
            articleWrapper.setPadding(0, 0, 0, 1);

        contentLayout.addView(articleWrapper, wrapperLayoutParam);
        return articleWrapper;
    }

    public WeiboPageView(PageActivity pageActivity) {
        super(pageActivity);
        this.pageActivity = pageActivity;
        this.sourceName = pageActivity.getSourceName();
        this.sourceImageURL = pageActivity.getSourceImageURL();
        this.executor = pageActivity.getExecutor();
        setDynamicLayout(pageActivity);
    }


    protected void setDynamicLayout(Context context) {
        this.removeAllViews();

        inflater = LayoutInflater.from(context);

        this.frame = (LinearLayout) inflater.inflate(R.layout.pageview, null);
        this.contentLayout = (LinearLayout) frame.findViewById(R.id.content);
        this.addView(this.frame, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }


    public void enlarge(final ArticleView articleView, final ExpandableArticleView weiboArticleView) {
        pageActivity.setEnlargedMode(true);
        final Animation fadeinArticle = AnimationUtils.loadAnimation(this.getContext(), R.anim.fadein);
        final Animation fadeinBoard = AnimationUtils.loadAnimation(this.getContext(), R.anim.fadein);
        fadeinArticle.setDuration(1000);
        fadeinBoard.setDuration(700);
        final LayoutInflater inflater = LayoutInflater.from(WeiboPageView.this.getContext());
        if (enlargedViewWrapper == null) {

            enlargedViewWrapper = (LinearLayout) inflater.inflate(R.layout.enlarged, null);

            wrapperll = (LinearLayout) enlargedViewWrapper.findViewById(R.id.wrapperll);

            LinearLayout shadowlayer = (LinearLayout) enlargedViewWrapper.findViewById(R.id.shadowlayer);
            shadowlayer.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (!weiboArticleView.isLoading)
                        closeEnlargedView(weiboArticleView);
                }
            });

        }
        enlargedViewWrapper.setVisibility(VISIBLE);
        wrapperll.removeAllViews();
        wrapperll.addView(articleView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        articleView.setVisibility(INVISIBLE);
        WeiboPageView.this.removeView(enlargedViewWrapper);
        WeiboPageView.this.addView(enlargedViewWrapper);
        ImageView closeButton = (ImageView) enlargedViewWrapper.findViewById(R.id.close);

        closeButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                closeEnlargedView(weiboArticleView);
            }
        });

        ImageView retweetButton = (ImageView) enlargedViewWrapper.findViewById(R.id.retweet);

        retweetButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if(!pageActivity.sinaAlreadyBinded()){
                    pageActivity.showDialog(PageActivity.PROMPT_OAUTH);
                    return;
                }

                commentShadowLayer = new LinearLayout(WeiboPageView.this.getContext());
                commentShadowLayer.setBackgroundColor(Color.parseColor("#00000000"));
                commentShadowLayer.setPadding(14, 20, 14, 20);
                LinearLayout commandPad = (LinearLayout) inflater.inflate(R.layout.comment_pad, null);
                WebImageView sourceImage = (WebImageView) commandPad.findViewById(R.id.source_image);
                TextView sourceName = (TextView) commandPad.findViewById(R.id.source_name);
                ImageView closeBtn = (ImageView) commandPad.findViewById(R.id.close);
                ImageView sendBtn = (ImageView) commandPad.findViewById(R.id.send);
                closeBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        WeiboPageView.this.removeView(commentShadowLayer);
                    }
                });

                TextView status = (TextView) commandPad.findViewById(R.id.status);
                final EditText commentEditText = (EditText) commandPad.findViewById(R.id.comment);
                commentEditText.addTextChangedListener(new TextWatcher() {
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }

                    public void afterTextChanged(Editable s) {

                    }
                });

                sendBtn.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (articleView.getArticle().getSourceType().equals(Constants.TYPE_SINA_WEIBO)) {
                            try {
                                pageActivity.comment(commentEditText.getText().toString(), articleView.getArticle().getStatusId());
                            } catch (WeiboException e) {
                                e.printStackTrace();
                            } catch (NoSinaAccountBindedException e) {
                                pageActivity.startActivity(new Intent(pageActivity, SinaAccountActivity.class));
                            }
                        } else {
                            try {
                                pageActivity.forward(commentEditText.getText().toString(), articleView.getArticle().extractURL());
                            } catch (WeiboException e) {
                                e.printStackTrace();
                            } catch (NoSinaAccountBindedException e) {
                                pageActivity.startActivity(new Intent(pageActivity, SinaAccountActivity.class));
                            }
                        }

                        WeiboPageView.this.removeView(commentShadowLayer);
                    }
                });
                if (articleView.getArticle().getPortraitImageUrl() != null) {
                    sourceImage.setImageUrl(articleView.getArticle().getPortraitImageUrl().toExternalForm());
                    sourceImage.loadImage();
                }

                sourceName.setText(articleView.getArticle().getAuthor());
                status.setText(articleView.getArticle().getStatus());

                commentShadowLayer.addView(commandPad, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
                WeiboPageView.this.addView(commentShadowLayer);
            }
        });
        final LinearLayout content = (LinearLayout) enlargedViewWrapper.findViewById(R.id.content);
        final LinearLayout contentWrapper = (LinearLayout) enlargedViewWrapper.findViewById(R.id.contentWrapper);

        content.setVisibility(INVISIBLE);

        fadeinBoard.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                content.setVisibility(VISIBLE);
                articleView.startAnimation(fadeinArticle);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });


        fadeinArticle.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {

                articleView.setVisibility(VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });

        contentWrapper.startAnimation(fadeinBoard);

    }



    private void closeEnlargedView(ExpandableArticleView weiboArticleView) {
        if (commentShadowLayer != null)
            this.removeView(commentShadowLayer);
        weiboArticleView.getContentView().setVisibility(VISIBLE);
        weiboArticleView.enlargedView = enlargedViewWrapper;
        final Animation fadeout = AnimationUtils.loadAnimation(pageActivity, R.anim.fade);
        fadeout.setDuration(500);
        fadeout.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                enlargedViewWrapper.setVisibility(INVISIBLE);
                WeiboPageView.this.removeView(enlargedViewWrapper);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
        enlargedViewWrapper.startAnimation(fadeout);
        pageActivity.setEnlargedMode(false);
    }

    public void showLoading() {
        if (!loadingNext) {
            LayoutInflater inflater = LayoutInflater.from(WeiboPageView.this.getContext());
            loadingView = (LinearLayout) inflater.inflate(R.layout.loading, null);
            this.addView(loadingView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
            this.loadingNext = true;
        }
    }

    public void removeLoadingIfNecessary() {
        if (loadingView != null) {
            loadingNext = false;

            this.removeView(loadingView);
        }
    }

    public void setLoadingNext(boolean loadingNext) {
        this.loadingNext = loadingNext;
    }

    public boolean isLastPage() {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    public boolean isFirstPage() {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    public void renderBeforeLayout() {
        rendered = true;
        if (headerImage != null)
            headerImage.loadImage();
    }

}
