package com.goal98.flipdroid.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.activity.IndexActivity;
import com.goal98.flipdroid.model.Article;
//import com.goal98.flipdroid.util.TikaClient;
//import com.goal98.flipdroid.util.TikaClientException;
//import com.goal98.flipdroid.util.TikaResponse;
import com.goal98.flipdroid.util.*;

import java.util.concurrent.*;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 2/17/11
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArticleWithoutURLView extends ArticleView {
    private LinearLayout progressBar;

    private boolean contentExtracted;
    private boolean clicked;

    public ArticleWithoutURLView(Context context, Article article) {
        super(context, article);
    }


    protected String getPrefix() {
        return Constants.WITHURLPREFIX;
    }

    Handler handler = new Handler();

    final Animation fadeOutAni = AnimationUtils.loadAnimation(this.getContext(), R.anim.fade);
    final Animation fadeInAni = AnimationUtils.loadAnimation(this.getContext(), R.anim.fadein);


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (article.hasLink() && !clicked && event.getAction() == MotionEvent.ACTION_UP) {
            clicked = true;
            fadeOutAni.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    ArticleWithoutURLView.this.removeAllViews();
                    final int height = ArticleWithoutURLView.this.getHeight();
                    final int width = ArticleWithoutURLView.this.getWidth();
                    ArticleWithoutURLView.this.addView(progressBar,new LinearLayout.LayoutParams(width, height));
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                final ArticleView loadedArticleView = new ArticleWithURLView(ArticleWithoutURLView.this.getContext(), future.get());
                                handler.post(new Runnable() {
                                    public void run() {
                                        ArticleWithoutURLView.this.removeView(progressBar);
                                        if (loadedArticleView != null) {
                                            LinearLayout parent = (LinearLayout) ArticleWithoutURLView.this.getParent();
                                            parent.removeAllViews();
                                            parent.addView(loadedArticleView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
                                            loadedArticleView.startAnimation(fadeInAni);
                                        }
                                    }
                                });
                                contentExtracted = true;
                            } catch (InterruptedException e) {
                                handler.post(new Runnable() {
                                    public void run() {
                                        AlarmSender.sendInstantMessage(R.string.tikatimeout, ArticleWithoutURLView.this.getContext());
                                        reloadOriginalView();
                                    }
                                });
                            } catch (ExecutionException e) {
                                handler.post(new Runnable() {
                                    public void run() {
                                        AlarmSender.sendInstantMessage(R.string.tikaservererror, ArticleWithoutURLView.this.getContext());
                                        reloadOriginalView();
                                    }
                                });
                            } catch (Exception e) {
                                handler.post(new Runnable() {
                                    public void run() {
                                        AlarmSender.sendInstantMessage(R.string.unknownerror, ArticleWithoutURLView.this.getContext());
                                        reloadOriginalView();
                                    }
                                });
                            }
                        }
                    }).start();

                }

                public void onAnimationRepeat(Animation animation) {
                }
            });
            ArticleWithoutURLView.this.startAnimation(fadeOutAni);
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN)//don't swallow action down event,or PageActivity won't handle it
            return true;

        return false;
    }

    private void reloadOriginalView() {
        this.removeView(progressBar);
        View originalView = new ArticleWithoutURLView(this.getContext(), article);
        this.addView(originalView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        originalView.startAnimation(fadeInAni);
    }

    private Future<Article> future;

    private ExecutorService executor;

    protected void buildView() {
        executor = Executors.newFixedThreadPool(1);
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        progressBar = (LinearLayout) inflater.inflate(R.layout.progressbar, null);

        LinearLayout titleLL = new LinearLayout(this.getContext());
        titleLL.setOrientation(HORIZONTAL);

        contentView.setText(getPrefix() + article.getStatus());
        //contentView.setMaxLines(15);
        contentView.setEllipsisMore("");
        contentView.setTextSize(17);

        titleLL.addView(contentView, new LinearLayout.LayoutParams(IndexActivity.maxWidth, LinearLayout.LayoutParams.FILL_PARENT));

        LinearLayout noneTitle = new LinearLayout(this.getContext());
        noneTitle.setOrientation(VERTICAL);
        noneTitle.setGravity(Gravity.BOTTOM);

        LinearLayout publisherView = new LinearLayout(this.getContext());

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
    }



    public void preload() {
        if (article.hasLink()) {
            future = executor.submit(new Callable() {
                public Object call() throws Exception {

                    String url = article.extractURL();
                     Log.d("cache system","preloading " + url);
                    TikaClient tc = new TikaClient();
                    TikaResponse response = tc.extract(url);
                    Log.d("cache system","preloading " + url + " done");
                    article.setContent(response.getContent());
                    article.setTitle(response.getTitle());
                    return article;
                }
            });
        }
    }


}