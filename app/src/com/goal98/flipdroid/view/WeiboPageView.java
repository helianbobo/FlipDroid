package com.goal98.flipdroid.view;


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
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.exception.NoSinaAccountBindedException;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.DeviceInfo;
import weibo4j.WeiboException;

import java.util.ArrayList;
import java.util.List;

public class WeiboPageView extends FrameLayout {
    private Page page;

    protected LinearLayout frame;
    public LayoutInflater inflater;

    public LinearLayout getContentLayout() {
        return contentLayout;
    }

    protected LinearLayout contentLayout; //this layout is supposed to be dynamic, depending on the Articles on this smartPage
    protected LinearLayout headerLayout;

    protected LinearLayout enlargedViewWrapper;
    //    protected ScrollView wrapper;
    protected String sourceName;
    protected String sourceImageURL;
    protected PageActivity pageActivity;
    private LinearLayout wrapperll;
    private LinearLayout loadingView;

    private ListView sourceList;
    private LinearLayout navigatorFrame;
    private LinearLayout commentShadowLayer;
    public WebImageView headerImage;
    private boolean sourceSelectMode;

    private boolean rendered;

    public boolean isRendered() {
        return rendered;
    }

    public boolean isSourceSelectMode() {

        return sourceSelectMode;
    }



    protected boolean loadingNext;

    public void setPage(Page page) {
        this.page = page;

        this.contentLayout.removeAllViews();
        List<Article> articleList = this.page.getArticleViewList();
        System.out.println("articleList:" + articleList.size());

        for (int i = 0; i < articleList.size(); i++) {
            Article article = articleList.get(i);
            boolean isLastArticle = i == articleList.size() - 1;
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

        WeiboArticleView withoutURLArticleView = new WeiboArticleView(WeiboPageView.this.getContext(), article, this);
        weiboViews.add(withoutURLArticleView);


        LinearLayout articleWrapper = new LinearLayout(this.getContext());
        wrapperViews.add(articleWrapper);
        articleWrapper.setBackgroundColor(0xffDDDDDD);
        articleWrapper.setGravity(Gravity.CENTER);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        articleWrapper.addView(withoutURLArticleView, layoutParams);

        LayoutParams wrapperLayoutParam = null;
        if (last)
            wrapperLayoutParam = new LayoutParams(DeviceInfo.width, LayoutParams.MATCH_PARENT);
        else {
            wrapperLayoutParam = new LayoutParams(DeviceInfo.width, LayoutParams.WRAP_CONTENT);
        }
        wrapperLayoutParam.setMargins(0, 0, 0, 1);
        contentLayout.addView(articleWrapper, wrapperLayoutParam);
        return articleWrapper;
    }

    public WeiboPageView(PageActivity pageActivity) {
        super(pageActivity);
        this.pageActivity = pageActivity;
        this.sourceName = pageActivity.getSourceName();
        this.sourceImageURL = pageActivity.getSourceImage();

        setDynamicLayout(pageActivity);
    }

    public void setSourceSelectMode(boolean sourceSelectMode) {
        this.sourceSelectMode = sourceSelectMode;
    }

    protected void setDynamicLayout(Context context) {
        this.removeAllViews();

        inflater = LayoutInflater.from(context);

        this.frame = (LinearLayout) inflater.inflate(R.layout.pageview, null);
        this.contentLayout = (LinearLayout) frame.findViewById(R.id.content);
//        this.headerLayout = (LinearLayout) frame.findViewById(R.id.header);
        this.addView(this.frame, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        //buildHeaderText();

//        WeiboPageView.this.addView(navigatorFrame, new LinearLayout.LayoutParams
//                (LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
    }

    private void buildHeaderText() {
        navigatorFrame = new LinearLayout(WeiboPageView.this.getContext());
        navigatorFrame.setPadding(0, IndexActivity.statusBarHeight + 4, 0, 0);
        navigatorFrame.setVisibility(GONE);

        LinearLayout navigatorShadow = new LinearLayout(WeiboPageView.this.getContext());

        navigatorShadow.setPadding((int) (DeviceInfo.width * 0.1), 0, (int) (DeviceInfo.width * 0.1), 0);
        navigatorShadow.setBackgroundColor(Color.parseColor("#77000000"));

        navigatorShadow.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                closeSourceSelection();
            }
        });

        sourceList = (ListView) inflater.inflate(R.layout.navigator, null);

        final LinearLayout navigator = new LinearLayout(WeiboPageView.this.getContext());
        navigator.setOrientation(LinearLayout.VERTICAL);
        navigator.setBackgroundResource(R.drawable.roundcorner);
        navigator.setGravity(Gravity.CENTER);

        navigator.addView(sourceList);
        LayoutParams layoutParams = new LayoutParams((int)
                (DeviceInfo.width * 0.8), (int) (DeviceInfo.height * 0.5));
        layoutParams.gravity = Gravity.CENTER;
        navigatorShadow.addView(navigator, layoutParams);


        TextView headerText = (TextView) headerLayout.findViewById(R.id.headerText);

        headerText.setText(this.sourceName);
        headerText.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (sourceSelectMode) {
                    closeSourceSelection();
                    return;
                }
                setSourceSelectMode(true);
                navigatorFrame.setVisibility(VISIBLE);

                SourceDB sourceDB = new SourceDB(pageActivity.getApplicationContext());

                Cursor sourceCursor = sourceDB.findAll();
                pageActivity.startManagingCursor(sourceCursor);

                SimpleCursorAdapter adapter = new SimpleCursorAdapter(pageActivity, R.layout.source_selection_item, sourceCursor,
                        new String[]{Source.KEY_SOURCE_NAME, Source.KEY_SOURCE_DESC, Source.KEY_IMAGE_URL},
                        new int[]{R.id.source_name, R.id.source_desc, R.id.source_image});
                adapter.setViewBinder(new SourceItemViewBinder());
                sourceList.setOnItemClickListener(new ListView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> l, View view, int i, long id) {
                        closeSourceSelection();

                        Intent intent = new Intent(pageActivity, PageActivity.class);
                        Cursor cursor = (Cursor) l.getItemAtPosition(i);
                        intent.putExtra("type", cursor.getString(cursor.getColumnIndex(Source.KEY_ACCOUNT_TYPE)));
                        intent.putExtra("sourceId", cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_ID)));
                        intent.putExtra("sourceImage", cursor.getString(cursor.getColumnIndex(Source.KEY_IMAGE_URL)));
                        intent.putExtra("sourceName", cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_NAME)));
                        intent.putExtra("contentUrl", cursor.getString(cursor.getColumnIndex(Source.KEY_CONTENT_URL)));
                        cursor.close();
                        pageActivity.startActivity(intent);
                        pageActivity.overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                        pageActivity.finish();
                    }
                });
                sourceList.setAdapter(adapter);
            }
        });

        headerImage = (WebImageView) headerLayout.findViewById(R.id.headerImage);
        if (pageActivity.getSourceImage() != null)
            headerImage.setImageUrl(pageActivity.getSourceImage());


        navigatorFrame.addView(navigatorShadow, new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
    }

    private void closeSourceSelection() {
        setSourceSelectMode(false);
        navigatorFrame.setVisibility(GONE);
    }

    public void enlarge(final ArticleView articleView, final WeiboArticleView weiboArticleView) {
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

    private void closeEnlargedView(WeiboArticleView weiboArticleView) {
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
