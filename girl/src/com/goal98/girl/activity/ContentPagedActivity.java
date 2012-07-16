package com.goal98.girl.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.goal98.android.WebImageView;
import com.goal98.girl.R;
import com.goal98.girl.client.OAuth;
import com.goal98.girl.db.RSSURLDB;
import com.goal98.girl.exception.NoSinaAccountBindedException;
import com.goal98.girl.model.Article;
import com.goal98.girl.model.ContentPagerAdapter;
import com.goal98.girl.util.AlarmSender;
import com.goal98.girl.util.Constants;
import com.goal98.girl.util.GestureUtil;
import com.goal98.girl.util.SinaAccountUtil;
import com.goal98.girl.view.ArticleHolder;
import com.goal98.girl.view.ContentPagerView;
import com.goal98.girl.view.PageIndexView;
import com.goal98.tika.common.TikaConstants;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.mobclick.android.MobclickAgent;
import com.srz.androidtools.util.DeviceInfo;
import weibo4j.WeiboException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-2-7
 * Time: 下午10:01
 * To change this template use File | Settings | File Templates.
 */
public class ContentPagedActivity extends SherlockActivity {
    private LayoutInflater inflater;
    private SinaWeiboHelper sinaWeiboHelper;
    private Article article;
    private Handler hander = new Handler();
    private MenuItem favoriteItem;
    private RelativeLayout body;
    private PageIndexView pageIndexView;
    private boolean inShare;
    private int mTouchSlop;
    private ContentPagerView loadedArticleView;
    private ContentPagerAdapter mPagerAdapter;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);

        final ViewConfiguration configuration = ViewConfiguration.get(this);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        GestureUtil.minDelta = mTouchSlop;

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.contentloaded);

        if (AdMobActivity.AdMobMemoryLeakWorkAroundActivity == null) {
            Log.i("CHAT", "starting the AdMobActivity");
            AdMobActivity.startAdMobActivity(this);
        }





        pageIndexView = (PageIndexView) findViewById(R.id.pageIndex);

        body = (RelativeLayout) findViewById(R.id.body);

        inflater = LayoutInflater.from(this);
        sinaWeiboHelper = new SinaWeiboHelper(this);
        article = ArticleHolder.getInstance().get();
        if (article == null)
            finish();
        getSupportActionBar().setTitle(article.getAuthor());

        loadedArticleView = new ContentPagerView(this, article);
        loadedArticleView.setId(R.id.close); //mock
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        loadedArticleView.setPadding(0, 10, 0, 0);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.topbar);
        layoutParams.addRule(RelativeLayout.ABOVE, R.id.pageIndex);

        body.addView(loadedArticleView, layoutParams);

        RelativeLayout.LayoutParams adParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        if (adView == null) {
            adView = new AdView(AdMobActivity.AdMobMemoryLeakWorkAroundActivity, AdSize.BANNER, "a150011ead7ed10");
            // Create an ad request.
            AdRequest adRequest = new AdRequest();
//            adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
//            adRequest.addTestDevice("E2CDD7A5D609F85B1545FE23D70E22FB");
            // Start loading the ad in the background.
            adView.loadAd(adRequest);
        }


        body.addView(adView, adParam);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DeviceInfo deviceInfo = DeviceInfo.getInstance(this);


        mPagerAdapter = new ContentPagerAdapter(article, deviceInfo, this);
        loadedArticleView.setAdapter(mPagerAdapter);

        loadedArticleView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int exitSignal = 0;

            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if (i == 0 || mPagerAdapter.getCount() - 1 == i) {
                    if (v == 0)
                        if (exitSignal <= 3)
                            exitSignal++;
                        else {
                            if (i == 0)
                                ContentPagedActivity.this.overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                            else {
                                ContentPagedActivity.this.overridePendingTransition(android.R.anim.slide_out_right, R.anim.fade);
                                if (ArticleHolder.getInstance().getNextArticle() == null) {
                                    ContentPagedActivity.this.finish();
                                    return;
                                }
                                loadedArticleView.setOnPageChangeListener(null);
                                Intent articleLoadedActivityIntent = new Intent(ContentPagedActivity.this.getApplication(), ContentPagedActivity.class);
                                articleLoadedActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ContentPagedActivity.this.getApplication().startActivity(articleLoadedActivityIntent);
                            }
                            ContentPagedActivity.this.finish();
                        }

                } else {
                    exitSignal = 0;
                }
            }

            @Override
            public void onPageSelected(int i) {
                pageIndexView.setDot(mPagerAdapter.getCount(), i + 1);
//                if (i >= 2) {
                if (i + 1 < mPagerAdapter.getCount()) {
                    List<WebImageView> images = mPagerAdapter.getItem(i);
                    for (int j = 0; j < images.size(); j++) {
                        WebImageView webImageView = images.get(j);
                        webImageView.loadImage();
                    }

                    images = mPagerAdapter.getItem(i + 1);
                    for (int j = 0; j < images.size(); j++) {
                        WebImageView webImageView = images.get(j);
                        webImageView.loadImage();
                    }
                }
//                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        pageIndexView.setDot(mPagerAdapter.getCount(), 1);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
        System.out.println("released");
        mPagerAdapter.release();

        adView.destroy();

        unbindReferences(this, R.id.body);
    }

    private void setWordCountIndicator(TextView wordCount, int current) {
        if (current > 140) {
            wordCount.setTextColor(Color.parseColor(Constants.COLOR_RED));
        } else {
            wordCount.setTextColor(Color.BLACK);
        }

        wordCount.setText(current + "/140");
    }

    public static final int PROMPT_OAUTH = 1;
    public static final int NAVIGATION = 2;
    public static final int PROMPT_INPROGRESS = 3;

    public Dialog dialog;
    public Dialog promptDialog;

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = null;
        switch (id) {
            case PROMPT_INPROGRESS:
                ProgressDialog dialogProgress = new ProgressDialog(this);
                dialogProgress.setIcon(R.drawable.icon);
                dialogProgress.setMessage(this.getString(R.string.inprogress));
                dialogProgress.setCancelable(false);
                this.promptDialog = dialogProgress;
                this.dialog = dialogProgress;
                break;
            case PROMPT_OAUTH:
                builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.commentneedsinaoauth)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FlipdroidApplications application = (FlipdroidApplications) getApplication();
                                final OAuth oauth = new OAuth();
                                application.setOauth(oauth);
                                if (ContentPagedActivity.this.promptDialog != null)
                                    ContentPagedActivity.this.promptDialog.dismiss();

                                showDialog(PROMPT_INPROGRESS);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final boolean result = oauth.RequestAccessToken(ContentPagedActivity.this, "girl://SinaAccountSaver", null);
                                        hander.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!result) {
                                                    new AlarmSender(ContentPagedActivity.this.getApplicationContext()).sendInstantMessage(R.string.networkerror);
                                                }
                                            }
                                        });

                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                this.dialog = builder.create();

                break;

            default:
                this.dialog = null;
        }
        if (dialog != null) {
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialogInterface) {
                    dialog = null;
                }
            });
        }
        if (promptDialog != null) {
            promptDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public void onDismiss(DialogInterface dialogInterface) {
                    promptDialog = null;
                }
            });
        }
        return this.dialog;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Used to put dark icons on light action bar
        boolean isLight = StreamActivity.THEME == R.style.Theme_Sherlock_Light;

        MenuItem shareItem = menu.add(getString(R.string.share));
        shareItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        shareItem.setIcon(R.drawable.ic_share_topbar);
        favoriteItem = menu.add(getString(R.string.favorite));
        favoriteItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        favoriteItem.setIcon(article.isFavorite() ? R.drawable.ic_favorite_on : R.drawable.ic_favorite);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.favorite))) {
            article.setIsFavorite(!article.isFavorite());
            RSSURLDB rssurldb = new RSSURLDB(ContentPagedActivity.this);
            rssurldb.updateArticle(article);
            item.setIcon(article.isFavorite() ? R.drawable.ic_favorite_on : R.drawable.ic_favorite);
        } else if (item.getTitle().equals(this.getString(R.string.share))) {
            {

                if (!SinaAccountUtil.alreadyBinded(ContentPagedActivity.this)) {
                    showDialog(ContentPagedActivity.PROMPT_OAUTH);
                    return true;
                }
                if (inShare)
                    return true;

                final LinearLayout commentShadowLayer = new LinearLayout(ContentPagedActivity.this);
                commentShadowLayer.setBackgroundColor(Color.parseColor(Constants.SHADOW_LAYER_COLOR));
                commentShadowLayer.setPadding(14, 20, 14, 20);
                LinearLayout commentPad = (LinearLayout) inflater.inflate(R.layout.comment_pad, null);
                WebImageView sourceImage = (WebImageView) commentPad.findViewById(R.id.source_image);
                TextView sourceName = (TextView) commentPad.findViewById(R.id.source_name);
                Button closeBtn = (Button) commentPad.findViewById(R.id.close);
                ImageButton sendBtn = (ImageButton) commentPad.findViewById(R.id.send);

                //closeBtn
                closeBtn.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_UP:
                                body.removeView(commentShadowLayer);
                                inShare = false;
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

                sendBtn.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_UP:
                                if (commentEditText.getText().length() > 140) {
                                    wordCount.setText(R.string.toolong);
                                    return true;
                                }

                                MobclickAgent.onEvent(ContentPagedActivity.this, "ShareViaSinaWeibo");

                                Thread t = new Thread(new Runnable() {
                                    public void run() {
                                        if (article.getSourceType().equals(TikaConstants.TYPE_SINA_WEIBO)) {
                                            try {
                                                sinaWeiboHelper.comment(commentEditText.getText().toString(), article);
                                            } catch (WeiboException e) {
                                                e.printStackTrace();
                                            } catch (NoSinaAccountBindedException e) {
                                                ContentPagedActivity.this.startActivity(new Intent(ContentPagedActivity.this, SinaAccountActivity.class));
                                            }
                                        } else {
                                            try {
                                                sinaWeiboHelper.forward(commentEditText.getText().toString(), article);

                                            } catch (WeiboException e) {
                                                e.printStackTrace();
                                            } catch (NoSinaAccountBindedException e) {
                                                ContentPagedActivity.this.startActivity(new Intent(ContentPagedActivity.this, SinaAccountActivity.class));
                                            }
                                        }
                                        body.post(new Runnable() {
                                            public void run() {
                                                Toast.makeText(ContentPagedActivity.this, R.string.share_success, 2000).show();
                                                ContentPagedActivity.this.dismissDialog(PageActivity.PROMPT_INPROGRESS);
                                            }
                                        });
                                    }
                                });
                                t.start();
                                ContentPagedActivity.this.showDialog(PageActivity.PROMPT_INPROGRESS);
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
                String prefixPart = "";
                String templateText = "";
                if (article.getTitle() != null && article.getTitle().length() != 0) {
                    prefixPart = "[" + article.getTitle() + "]";
                    templateText = "| " + prefixPart + " " + paragraph1 + " " + (article.getSourceURL() == null ? "" : article.getSourceURL());
                }

                commentEditText.setText(templateText);
                commentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    public void onFocusChange(View view, boolean b) {
                        commentEditText.requestFocus();
                        commentEditText.setSelection(0);
                        commentEditText.setFocusable(true);
                    }
                });

                int count = templateText.length();
                setWordCountIndicator(wordCount, count);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
                commentShadowLayer.addView(commentPad, params);
                inShare = true;
                body.addView(commentShadowLayer, params);
            }
        } else {
            this.finish();
        }

        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (promptDialog != null)
            promptDialog.dismiss();
    }

    /**
     * Removes the reference to the activity from every view in a view hierarchy (listeners, images etc.).
     * This method should be called in the onDestroy() method of each activity
     *
     * @param viewID normally the id of the root layout
     *               <p/>
     *               see http://code.google.com/p/android/issues/detail?id=8488
     */
    public static void unbindReferences(Activity activity, int viewID) {
        try {
            View view = activity.findViewById(viewID);
            if (view != null) {
                unbindViewReferences(view);
                if (view instanceof ViewGroup) unbindViewGroupReferences((ViewGroup) view);
            }
            System.gc();
        } catch (Throwable e) {
            // whatever exception is thrown just ignore it because a crash is always worse than this method not doing what it's supposed to do
        }
    }

    private static void unbindViewGroupReferences(ViewGroup viewGroup) {
        int nrOfChildren = viewGroup.getChildCount();
        for (int i = 0; i < nrOfChildren; i++) {
            View view = viewGroup.getChildAt(i);
            unbindViewReferences(view);
            if (view instanceof ViewGroup) unbindViewGroupReferences((ViewGroup) view);
        }
        try {
            viewGroup.removeAllViews();
        } catch (Throwable mayHappen) {
            // AdapterViews, ListViews and potentially other ViewGroups don't support the removeAllViews operation
        }
    }

    private static void unbindViewReferences(View view) {
        // set all listeners to null (not every view and not every API level supports the methods)
        try {
            view.setOnClickListener(null);
        } catch (Throwable mayHappen) {
        }
        ;
        try {
            view.setOnCreateContextMenuListener(null);
        } catch (Throwable mayHappen) {
        }
        ;
        try {
            view.setOnFocusChangeListener(null);
        } catch (Throwable mayHappen) {
        }
        ;
        try {
            view.setOnKeyListener(null);
        } catch (Throwable mayHappen) {
        }
        ;
        try {
            view.setOnLongClickListener(null);
        } catch (Throwable mayHappen) {
        }
        ;
        try {
            view.setOnClickListener(null);
        } catch (Throwable mayHappen) {
        }
        ;

        // set background to null
        Drawable d = view.getBackground();
        if (d != null) d.setCallback(null);
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            d = imageView.getDrawable();
            if (d != null) d.setCallback(null);
            imageView.setImageDrawable(null);
            imageView.setBackgroundDrawable(null);
        }

        // destroy webview
        if (view instanceof WebView) {
            ((WebView) view).destroyDrawingCache();
            ((WebView) view).destroy();
        }
    }
}






