package com.goal98.flipdroid.view;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.activity.PageActivity;
import com.goal98.flipdroid.activity.SinaAccountActivity;
import com.goal98.flipdroid.activity.SiteActivity;
import com.goal98.flipdroid.exception.NoSinaAccountBindedException;
import com.goal98.flipdroid.model.Article;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.DeviceInfo;
import com.goal98.flipdroid.util.StopWatch;
import com.goal98.tika.common.TikaConstants;
import weibo4j.WeiboException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class WeiboPageView extends FrameLayout {
    private Page page;

    protected LinearLayout frame;
    protected ExecutorService executor;
    private Animation fadeinArticle;
    private Animation fadeinBoard;
    private LinearLayout content;
    private LayoutInflater inflater;
    protected DeviceInfo deviceInfo;
    private ExpandableArticleView clickedArticleView;

    public LinearLayout getContentLayout() {
        return contentLayout;
    }

    protected LinearLayout contentLayout; //this layout is supposed to be dynamic, depending on the Articles on this smartPage

    protected WeakReference<LinearLayout> enlargedViewWrapperWr;
    //    protected LinearLayout enlargedViewWrapper;
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
        List<Article> articleList = this.page.getArticleList();
        //System.out.println("articleList:" + articleList.size());

        int heightSum = 0;
        for (int i = 0; i < articleList.size(); i++) {
            heightSum += articleList.get(i).getHeight();
        }

        for (int i = 0; i < articleList.size(); i++) {
            Article article = articleList.get(i);
            boolean isLastArticle = i == articleList.size() - 1;
            article.setHeight((int) ((article.getHeight() * deviceInfo.getDisplayHeight()) / (float) heightSum));
            addArticleView(article, isLastArticle);
        }
    }

    public DeviceInfo getDeviceInfoFromApplicationContext() {
        return DeviceInfo.getInstance((Activity) this.getContext());
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
        articleWrapper.setBackgroundColor(Constants.LINE_COLOR);//分割线颜色
        articleWrapper.setGravity(Gravity.TOP);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        articleWrapper.addView(withoutURLArticleView, layoutParams);

        LayoutParams wrapperLayoutParam = new LayoutParams(deviceInfo.getWidth(), article.getHeight());

        if (last)//分割线
            articleWrapper.setPadding(0, 0, 0, 0);
        else
            articleWrapper.setPadding(0, 0, 0, 1);

        contentLayout.addView(articleWrapper, wrapperLayoutParam);
        return articleWrapper;
    }

    public WeiboPageView(final PageActivity pageActivity) {
        super(pageActivity);
        this.pageActivity = pageActivity;
        this.sourceName = pageActivity.getSourceName();
        this.sourceImageURL = pageActivity.getSourceImageURL();
        this.executor = pageActivity.getExecutor();
        this.deviceInfo = this.getDeviceInfoFromApplicationContext();
        setDynamicLayout(pageActivity);

        fadeinArticle = AnimationUtils.loadAnimation(this.getContext(), R.anim.fadein);
        fadeinBoard = AnimationUtils.loadAnimation(this.getContext(), R.anim.fadein);

        fadeinArticle.setDuration(400);
        fadeinBoard.setDuration(450);

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
                WeiboPageView.this.pageActivity.setEnlargedMode(true);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
    }


    protected void setDynamicLayout(Context context) {
        this.removeAllViews();

        inflater = LayoutInflater.from(context);

        this.frame = (LinearLayout) inflater.inflate(R.layout.pageview, null);
        this.contentLayout = (LinearLayout) frame.findViewById(R.id.content);
        this.addView(this.frame, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    private ArticleView articleView;

    public void enlarge(final ArticleView articleView, final ExpandableArticleView weiboArticleView) {
        this.articleView = articleView;
        this.clickedArticleView = weiboArticleView;
        inflater = LayoutInflater.from(WeiboPageView.this.getContext());
        if (enlargedViewWrapperWr == null || enlargedViewWrapperWr.get() == null) {
            enlargedViewWrapperWr = new WeakReference(inflater.inflate(R.layout.enlarged, null));

            wrapperll = (LinearLayout) (enlargedViewWrapperWr.get().findViewById(R.id.wrapperll));

            LinearLayout shadowLayer = (LinearLayout) enlargedViewWrapperWr.get().findViewById(R.id.shadowlayer);
            shadowLayer.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (!weiboArticleView.isLoading)
                        closeEnlargedView(weiboArticleView);
                }
            });

        }
        enlargedViewWrapperWr.get().setVisibility(VISIBLE);
        wrapperll.removeAllViews();
        wrapperll.addView(articleView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        articleView.setVisibility(INVISIBLE);
        WeiboPageView.this.removeView(enlargedViewWrapperWr.get());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(deviceInfo.getWidth(), deviceInfo.getHeight());
        WeiboPageView.this.addView(enlargedViewWrapperWr.get(), params);
        setupToolBar(articleView, weiboArticleView, pageActivity.getBottomBar());
        pageActivity.hideIndexView();
//        pageActivity.getContainer().setLayoutParams();

//        pageActivity.getBottomBar().setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        content = (LinearLayout) enlargedViewWrapperWr.get().findViewById(R.id.content);
        articleView.startAnimation(fadeinArticle);

    }

    private void setupToolBar(final ArticleView articleView, final ExpandableArticleView weiboArticleView, HeaderView header) {
        Button closeButton = (Button) header.findViewById(R.id.close);

        closeButton.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        closeEnlargedView(weiboArticleView);
                        break;
                    default:
                        break;
                }

                return false;
            }

        });

        ImageView retweetButton = (ImageView) header.findViewById(R.id.retweet);
        retweetButton.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (!pageActivity.sinaAlreadyBinded()) {
                            pageActivity.showDialog(PageActivity.PROMPT_OAUTH);
                            break;
                        }
                        pageActivity.hideBottomBar();
                        commentShadowLayer = new LinearLayout(WeiboPageView.this.getContext());
                        commentShadowLayer.setBackgroundColor(Color.parseColor(Constants.SHADOW_LAYER_COLOR));
                        commentShadowLayer.setPadding(14, 20, 14, 20);
                        LinearLayout commentPad = (LinearLayout) inflater.inflate(R.layout.comment_pad, null);
                        WebImageView sourceImage = (WebImageView) commentPad.findViewById(R.id.source_image);
                        TextView sourceName = (TextView) commentPad.findViewById(R.id.source_name);
                        Button closeBtn = (Button) commentPad.findViewById(R.id.close);
                        ImageButton sendBtn = (ImageButton) commentPad.findViewById(R.id.send);

                        closeBtn.setOnTouchListener(new View.OnTouchListener() {

                            public boolean onTouch(View view, MotionEvent motionEvent) {

                                switch (motionEvent.getAction()) {
                                    case MotionEvent.ACTION_UP:
                                        WeiboPageView.this.removeView(commentShadowLayer);
                                        pageActivity.showBottomBar();
                                        break;
                                    default:
                                        break;
                                }

                                return false;
                            }

                        });


                        TextView status = (TextView) commentPad.findViewById(R.id.status);
                        final TextView wordCount = (TextView) commentPad.findViewById(R.id.wordCount);
                        final EditText commentEditText = (EditText) commentPad.findViewById(R.id.comment);
                        commentEditText.addTextChangedListener(new TextWatcher() {
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            public void beforeTextChanged(CharSequence s, int start, int count,
                                                          int after) {
                            }

                            public void afterTextChanged(Editable s) {
                                setWordCountIndicator(wordCount, s.length());
                            }
                        });

                        final Article article = articleView.getArticle();
                        sendBtn.setOnTouchListener(new View.OnTouchListener() {

                            public boolean onTouch(View view, MotionEvent motionEvent) {

                                switch (motionEvent.getAction()) {
                                    case MotionEvent.ACTION_UP:
                                        if(commentEditText.getText().length() > 140){
                                            wordCount.setText(R.string.toolong);
                                            return true;
                                        }
                                        Thread t = new Thread(new Runnable() {
                                            public void run() {
                                                if (article.getSourceType().equals(TikaConstants.TYPE_SINA_WEIBO)) {
                                                    try {
                                                        pageActivity.comment(commentEditText.getText().toString(), article);
                                                    } catch (WeiboException e) {
                                                        e.printStackTrace();
                                                    } catch (NoSinaAccountBindedException e) {
                                                        pageActivity.startActivity(new Intent(pageActivity, SinaAccountActivity.class));
                                                    }
                                                } else {
                                                    try {
                                                        pageActivity.forward(commentEditText.getText().toString(), article);

                                                    } catch (WeiboException e) {
                                                        e.printStackTrace();
                                                    } catch (NoSinaAccountBindedException e) {
                                                        pageActivity.startActivity(new Intent(pageActivity, SinaAccountActivity.class));
                                                    }
                                                }
                                                WeiboPageView.this.post(new Runnable() {
                                                    public void run() {
                                                        Toast.makeText(pageActivity, R.string.share_success, 2000).show();
                                                        WeiboPageView.this.removeView(commentShadowLayer);
                                                        pageActivity.dismissDialog(PageActivity.PROMPT_INPROGRESS);
                                                        pageActivity.showBottomBar();
                                                    }
                                                });
                                            }
                                        });
                                        t.start();
                                        pageActivity.showDialog(PageActivity.PROMPT_INPROGRESS);
                                        break;
                                    default:
                                        break;
                                }

                                return false;
                            }

                        });

                        if (article.getPortraitImageUrl() != null) {
                            sourceImage.setImageUrl(article.getPortraitImageUrl().toExternalForm());
                            sourceImage.loadImage();
                        }

                        sourceName.setText(article.getAuthor());
                        status.setText(article.getStatus());

                        String paragraph1 = article.getPreviewParagraph();
                        if (paragraph1.length() > 40) {
                            paragraph1 = paragraph1.substring(0, 40);
                        }
//                        commentEditText.setFocusable(true);
//                        commentEditText.requestFocus(1000);
//                        commentEditText.setFocusableInTouchMode(true);
                        final String templateText = "| [" + article.getTitle() + "] " + paragraph1 + " " + article.getSourceURL();
                        commentEditText.setText(templateText);
                        commentEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
                            public void onFocusChange(View view, boolean b) {
                                commentEditText.requestFocus();
                                commentEditText.setSelection(0);
                                commentEditText.setFocusable(true);
                            }
                        });


                        int count = templateText.length();
                        setWordCountIndicator(wordCount, count);

                        commentShadowLayer.addView(commentPad, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
                        WeiboPageView.this.addView(commentShadowLayer);


                        break;
                    default:
                        break;
                }

                return false;
            }

        });


    }

    private void setWordCountIndicator(TextView wordCount, int current) {
        if(current > 140){
            wordCount.setTextColor(Color.parseColor(Constants.COLOR_RED));
        }else{
            wordCount.setTextColor(Color.BLACK);
        }

        wordCount.setText(current + "/140");
    }


    private void closeEnlargedView(ExpandableArticleView weiboArticleView) {
        if (commentShadowLayer != null)
            this.removeView(commentShadowLayer);

        weiboArticleView.getContentView().setVisibility(VISIBLE);
        weiboArticleView.enlargedView = new WeakReference(enlargedViewWrapperWr.get());
        final Animation fadeout = AnimationUtils.loadAnimation(pageActivity, R.anim.fade);
        fadeout.setDuration(150);
        fadeout.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                enlargedViewWrapperWr.get().setVisibility(INVISIBLE);
                WeiboPageView.this.removeView(enlargedViewWrapperWr.get());
                //TODO TEST
                pageActivity.setEnlargedMode(false);
            }

            public void onAnimationRepeat(Animation animation) {

            }
        });
        enlargedViewWrapperWr.get().startAnimation(fadeout);
        pageActivity.showIndexView();
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

    public void releaseResource() {
        for (int i = 0; i < page.getArticleList().size(); i++) {
            Article article = page.getArticleList().get(i);
            System.out.println("release image...");
            article.setImageBitmap(null);
        }
    }

    public void closeEnlargedView() {
        if (this.clickedArticleView != null) {
            this.closeEnlargedView(clickedArticleView);
        }
        pageActivity.showBottomBar();
    }
}
