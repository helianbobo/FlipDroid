package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.anim.AnimationFactory;
import com.goal98.flipdroid.client.OAuth;
import com.goal98.flipdroid.client.WeiboExt;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.exception.LastWindowException;
import com.goal98.flipdroid.exception.NoMoreStatusException;
import com.goal98.flipdroid.exception.NoSinaAccountBindedException;
import com.goal98.flipdroid.model.*;
import com.goal98.flipdroid.model.google.GoogleReaderArticleSource;
import com.goal98.flipdroid.model.rss.RSSArticleSource;
import com.goal98.flipdroid.model.sina.SinaArticleSource;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.GestureUtil;
import com.goal98.flipdroid.view.*;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import java.util.concurrent.Semaphore;

public class PageActivity extends Activity implements com.goal98.flipdroid.model.Window.OnLoadListener {

    static final private int CONFIG_ID = Menu.FIRST;

    private boolean flipStarted = false;
    private boolean mToggleIndeterminate = false;
    private boolean forward = false;

    private ViewGroup container;
    private WeiboPageView current;
    private WeiboPageView next;
    private WeiboPageView previous;

    private ContentRepo repo;
    private SharedPreferences preferences;

    private WeiboPagingStrategy weiboPagingStrategy;

    private String deviceId;
    private int currentPageIndex = -1;
//    private Page currentSmartPage;


    private AlarmSender alarmSender;

    private AccountDB accountDB;
    private String accountType;
    private String sourceId;
    private String sourceImageURL;
    private String sourceName;
    private String contentUrl;
    //    private int browseMode;
    private PageViewSlidingWindows slidingWindows;
    private WeiboPageViewFactory pageViewFactory;
    private ArticleSource source;
    private Weibo weibo;
    //    private int animationMode;
    private LinearLayout shadow;
    private LinearLayout shadow2;
    private LinearLayout.LayoutParams shadowParams;
    private FrameLayout.LayoutParams pageViewLayoutParamsFront;
    public com.goal98.flipdroid.model.Window preparingWindow;
    private boolean previousDirection;
    private boolean prepareFail;
    private FrameLayout.LayoutParams pageViewLayoutParamsBack;
    private HeaderView header;
    private TextView headerText;
    private WebImageView webImageView;
    private int lastFlipDirection = ACTION_HORIZONTAL;
    private int browseMode;
    public static int ACTION_HORIZONTAL = 1;
    public static int ACTION_VERTICAL = 0;
//    private TextView pageInfo;
    private PageIndexView pageIndexView;

    public String getSourceImageURL() {
        return sourceImageURL;
    }

    public String getSourceName() {
        return sourceName;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        accountDB = new AccountDB(this);
        alarmSender = new AlarmSender(this);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);

        TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tManager.getDeviceId();

        accountType = (String) getIntent().getExtras().get("type");
        sourceId = (String) getIntent().getExtras().get("sourceId");
        sourceImageURL = (String) getIntent().getExtras().get("sourceImage");
        sourceName = (String) getIntent().getExtras().get("sourceName");
        contentUrl = (String) getIntent().getExtras().get("contentUrl");

        System.out.println("sourceImageURL:" + sourceImageURL);
        setContentView(R.layout.main);
        container = (ViewGroup) findViewById(R.id.pageContainer);
        pageIndexView = (PageIndexView) findViewById(R.id.pageIndex);
        header = (HeaderView) findViewById(R.id.header);
//        pageInfo = (TextView)header.findViewById(R.id.pageInfo);
        headerText = (TextView) findViewById(R.id.headerText);
        webImageView = (WebImageView) findViewById(R.id.headerImage);

        PagingStrategy pagingStrategy = null;
        pageViewFactory = new WeiboPageViewFactory();

        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        this.browseMode = getBrowseMode();
//        this.animationMode = getAnimationMode();
        refreshingSemaphore = new Semaphore(1, true);
        if (accountType.equals(Constants.TYPE_SINA_WEIBO)) {

            if (isWeiboMode())
                pagingStrategy = new WeiboPagingStrategy(this);
            else
                pagingStrategy = new FixedPagingStrategy(this, 2);

            pagingStrategy.setNoMoreArticleListener(new NoMoreArticleListener() {
                public void onNoMoreArticle() throws NoMoreStatusException {
                    Log.d("cache system", "no more articles, refreshing repo");
                    repo.refresh(repo.getRefreshingToken());
                }
            });

            repo = new ContentRepo(pagingStrategy, refreshingSemaphore);

            String userId = preferences.getString("sina_account", null);
            Cursor cursor = accountDB.findByTypeAndUsername(accountType, userId);
            cursor.moveToFirst();
            String token = cursor.getString(cursor.getColumnIndex(Account.KEY_PASSWORD));
            String tokenSecret = cursor.getString(cursor.getColumnIndex(Account.KEY_PASSWORD_SECRET));
            cursor.close();

            ArticleFilter filter = null;
            if (isWeiboMode())
                filter = new NullArticleFilter();
            else
                filter = new ContainsLinkFilter(new NullArticleFilter());

            source = new SinaArticleSource(true, token, tokenSecret, sourceId, filter);

        } else if (accountType.equals(Constants.TYPE_RSS)) {
            pagingStrategy = new FixedPagingStrategy(this, 2);
            pagingStrategy.setNoMoreArticleListener(new NoMoreArticleListener() {
                public void onNoMoreArticle() throws NoMoreStatusException {
                    throw new NoMoreStatusException();
                }
            });

            repo = new ContentRepo(pagingStrategy, refreshingSemaphore);
            source = new RSSArticleSource(contentUrl, sourceName, sourceImageURL);
        } else if (accountType.equals(Constants.TYPE_GOOGLE_READER)) {

            String sid = preferences.getString(GoogleAccountActivity.GOOGLE_ACCOUNT_SID, "");
            String auth = preferences.getString(GoogleAccountActivity.GOOGLE_ACCOUNT_AUTH, "");

            pagingStrategy = new FixedPagingStrategy(this, 2);
            pagingStrategy.setNoMoreArticleListener(new NoMoreArticleListener() {
                public void onNoMoreArticle() throws NoMoreStatusException {
                    throw new NoMoreStatusException();
                }
            });
            repo = new ContentRepo(pagingStrategy, refreshingSemaphore);
            source = new GoogleReaderArticleSource(sid, auth);
        }


        repo.setArticleSource(source);

        headerText.setText(sourceName);
        if (sourceImageURL != null) {
            webImageView.setImageUrl(sourceImageURL);
            webImageView.loadImage();
        }

        slidingWindows = new PageViewSlidingWindows(20, repo, pageViewFactory, 3);
        current = pageViewFactory.createFirstPage();

        shadow = new LinearLayout(PageActivity.this);
        shadow.setBackgroundColor(Color.parseColor("#10999999"));

        shadow2 = new LinearLayout(PageActivity.this);
        shadow2.setBackgroundColor(Color.parseColor("#FFDDDDDD"));

        shadowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        pageViewLayoutParamsFront = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
        pageViewLayoutParamsBack = pageViewLayoutParamsFront;
        flipPage(true);
    }

    private int getAnimationMode() {
        String key = getString(R.string.key_animation_mode_preference);
        return Integer.parseInt(preferences.getString(key, "0"));
    }

    private Semaphore refreshingSemaphore;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accountDB.close();
    }

    boolean isWeiboMode() {
        if(source==null){
            return browseMode == 0;
        }
        if (source.getForceMagzine())
            return false;
        return browseMode == 0;
    }

    boolean isFlipHorizonal() {
        return getAnimationMode() == 0;
    }

    public void onWindowLoaded(com.goal98.flipdroid.model.Window window) {
        if (forward)
            next = window.get();
        else
            previous = window.get();
        Log.d("SLIDING", "on windows loaded");
        handler.post(new Runnable() {
            public void run() {
                showAnimation();
            }
        });
    }

    public void onWindowSkipped(com.goal98.flipdroid.model.Window pageViewWindow) {
        handler.post(new Runnable() {

            public void run() {
                slideToNextPage();
            }
        });

    }

    public void comment(String comment, long statusId) throws WeiboException, NoSinaAccountBindedException {
        String userId = preferences.getString("sina_account", null);
        if (userId == null)
            throw new NoSinaAccountBindedException();

        if (weibo == null) {
            initSinaWeibo(userId);
        }
        weibo.updateStatus(comment, statusId);
    }

    public void forward(String comment, String url) throws WeiboException, NoSinaAccountBindedException {
        String userId = preferences.getString("sina_account", null);
        if (userId == null)
            throw new NoSinaAccountBindedException();

        if (weibo == null) {
            initSinaWeibo(userId);
        }
        weibo.updateStatus(comment + " " + url);
    }

    private void initSinaWeibo(String userId) {
        weibo = new WeiboExt();
        Cursor cursor = accountDB.findByTypeAndUsername(Constants.TYPE_SINA_WEIBO, userId);
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex(Account.KEY_PASSWORD));
        cursor.close();

        System.setProperty("weibo4j.oauth.consumerKey", Constants.CONSUMER_KEY);
        System.setProperty("weibo4j.oauth.consumerSecret", Constants.CONSUMER_SECRET);

        Weibo.CONSUMER_KEY = Constants.CONSUMER_KEY;
        Weibo.CONSUMER_SECRET = Constants.CONSUMER_SECRET;

        String basicUser = userId;
        String basicPassword = password;
        weibo.setHttpConnectionTimeout(5000);

        weibo.setUserId(basicUser);
        weibo.setPassword(basicPassword);
    }


    public class WeiboPageViewFactory {
        public WeiboPageView createPageView() {
            WeiboPageView pageView = null;

            if (isWeiboMode()) {
                pageView = new WeiboPageView(PageActivity.this);
            } else {
                pageView = new MagzinePageView(PageActivity.this);
            }
            return pageView;
        }

        public WeiboPageView createFirstPage() {
            WeiboPageView pageView = null;
            pageView = new FirstPageView(PageActivity.this, slidingWindows);
            return pageView;
        }

        public WeiboPageView createLastPage() {
            WeiboPageView pageView = null;
            pageView = new LastPageView(PageActivity.this, slidingWindows);

            return pageView;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    boolean enlargedMode = false;

    public void setEnlargedMode(boolean enlargedMode) {
        this.enlargedMode = enlargedMode;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        System.out.println("flipStarted" + flipStarted);
        if (flipStarted)
            return true;
//        if (header.isSourceSelectMode()) {
        if (header.dispatchTouchEvent(event)) {
            return true;
        }
//        }
        if (enlargedMode) {
            current.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                return current.dispatchTouchEvent(event);
            case MotionEvent.ACTION_MOVE: {
                if (!enlargedMode)
                    onTouchEvent(event);
                else
                    current.onTouchEvent(event);

            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("flipStarted" + flipStarted);
        if (flipStarted) {
            return false;
        }
//        if (header.isSourceSelectMode()) {
//            header.onTouchEvent(event);
//        }
        if (enlargedMode) {
            current.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                if (event.getHistorySize() > 0 && !flipStarted) {
                    if (GestureUtil.flipRight(event)) {
                        lastFlipDirection = ACTION_HORIZONTAL;
                        flipPage(false);
                    } else if (GestureUtil.flipUp(event)) {
                        lastFlipDirection = ACTION_VERTICAL;
                        flipPage(false);
                    } else if (GestureUtil.flipLeft(event)) {
                        lastFlipDirection = ACTION_HORIZONTAL;
                        flipPage(true);
                    } else if (GestureUtil.flipDown(event)) {
                        lastFlipDirection = ACTION_VERTICAL;
                        flipPage(true);
                    }

                } else {
//                    if (header.isSourceSelectMode())
                    header.onTouchEvent(event);
                }
                break;
            default:
                break;
        }
        return true;
    }

    Handler handler = new Handler();

    private void flipPage(final boolean forward) {
        flipStarted = true;

        this.previousDirection = this.forward;
        this.forward = forward;

        if (!forward)
            decreasePageNo();

        int nextPageIndex = forward ? currentPageIndex + 1 : currentPageIndex;

        if (currentPageIndex == -1 && forward) {//we are first timer
            currentPageIndex++;
            container.addView(current, pageViewLayoutParamsFront);
            new Thread(new Runnable() {
                public void run() {
                    slideToNextPage();
                }
            }).start();
        } else if (nextPageIndex > 0) {
            if (current.isLastPage() && forward) {
                overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                finish();
            }
            try {
                new Thread(new Runnable() {
                    public void run() {
                        slideToNextPage();
                    }
                }).start();
            } catch (Exception e) {
                executionFailed();
            }
        } else {
            overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
            finish();
        }
    }

    private void slideToNextPage() {

        try {
            if (preparingWindow == null)
                preparingWindow = forward ? slidingWindows.getNextWindow() : slidingWindows.getPreviousWindow();
            else {
                if (previousDirection != forward) {
                    if (previousDirection) {
                        if (currentPageIndex == 0) {
                            overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                            finish();
                            return;
                        }
                        preparingWindow = slidingWindows.getPreviousWindow();
                        preparingWindow = slidingWindows.getPreviousWindow();
                    } else {
                        preparingWindow = slidingWindows.getNextWindow();
                        preparingWindow = slidingWindows.getNextWindow();
                    }
                }
                boolean preparingWindowReady = preparingWindow.isLoaded();
                if (!preparingWindowReady) {
                    preparingWindow.registerOnLoadListener(PageActivity.this);
                    handler.post(new Runnable() {
                        public void run() {
                            current.showLoading();
                        }
                    });
                    return;
                }
            }
            final com.goal98.flipdroid.model.Window nextWindow = preparingWindow;
            preparingWindow = null;
            if (nextWindow.isLoading()) {
                Log.d("SLIDING", "register on load listener...");

                handler.post(new Runnable() {
                    public void run() {
                        nextWindow.registerOnLoadListener(PageActivity.this);
                        current.showLoading();
                    }
                });
                return;
            } else if (nextWindow.isSkip()) {
                Log.d("SLIDING", "slide to next page...");
                current.setLoadingNext(false);
                slideToNextPage();
                return;
            } else {
                if (next.isLastPage()) {
                    System.out.println("lastpage");
                } else {
                    if (forward)
                        next = nextWindow.get();
                    else
                        previous = nextWindow.get();
                }

                handler.post(new Runnable() {
                    public void run() {
                        if (forward)
                            next.removeLoadingIfNecessary();
                        else {
                            if (previous == null)
                                return;
                            previous.removeLoadingIfNecessary();
                        }
                        showAnimation();
                    }
                });
            }
        } catch (LastWindowException e) {
            next = pageViewFactory.createLastPage();
            handler.post(new Runnable() {
                public void run() {
                    showAnimation();
                }
            });
        }
    }

    private void prepareNextPage() {
        try {
            preparingWindow = forward ? slidingWindows.getNextWindow() : slidingWindows.getPreviousWindow();
            System.out.println("preparingWindow" + preparingWindow);
            if (preparingWindow.isLoading()) {
                Log.d("SLIDING", "register on load listener...");

                handler.post(new Runnable() {
                    public void run() {
                        preparingWindow.registerOnLoadListener(new com.goal98.flipdroid.model.Window.OnLoadListener() {

                            public void onWindowLoaded(com.goal98.flipdroid.model.Window window) {
                                if (forward)
                                    next = preparingWindow.get();
                                else {
                                    previous = preparingWindow.get();
                                }
                                prepareFail = false;
                            }

                            public void onWindowSkipped(com.goal98.flipdroid.model.Window pageViewWindow) {
                                prepareNextPage();
                            }
                        });
                    }
                });
                return;
            } else if (preparingWindow.isSkip()) {
                Log.d("SLIDING", "slide to next page...");
                prepareNextPage();
                return;
            } else {
                if (forward)
                    next = preparingWindow.get();
                else
                    previous = preparingWindow.get();

                handler.post(new Runnable() {
                    public void run() {
                        renderNextPageIfNotRendered();
                        if (forward)
                            next.removeLoadingIfNecessary();
                        else
                            previous.removeLoadingIfNecessary();
                    }
                });
                prepareFail = false;
            }
        } catch (LastWindowException e) {
            prepareFail = true;
            e.printStackTrace();
            next = pageViewFactory.createLastPage();
        }
    }

    private void renderNextPageIfNotRendered() {
        WeiboPageView renderingPageView;

        if (forward) {
            renderingPageView = next;
        } else {
            renderingPageView = previous;
        }
        if (renderingPageView.isRendered())
            return;

        for (ArticleView v : renderingPageView.getWeiboViews()) {
            v.renderBeforeLayout();
        }
        renderingPageView.renderBeforeLayout();
        renderingPageView.setVisibility(View.VISIBLE);
    }

    private void decreasePageNo() {
        currentPageIndex--;
    }

    private Animation buildFlipHorizonalAnimation(final boolean forward) {
        final float centerY = container.getHeight() / 2.0f;
        final float centerX = 0;
        long duration = getFlipDurationFromPreference();

        Animation rotation = AnimationFactory.buildHorizontalFlipAnimation(forward, duration, centerX, centerY);


        rotation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                if(forward)
                    container.bringChildToFront(current);
            }

            public void onAnimationEnd(Animation animation) {
                flipStarted = false;
                container.bringChildToFront(forward?next:previous);
                switchViews(forward);
                new Thread(new Runnable() {
                    public void run() {
                        prepareNextPage();
                    }
                }).start();
//                pageInfo.setText(currentPageIndex +"/"+ repo.getTotal()+"");
                pageIndexView.setDot(repo.getTotal(), currentPageIndex);
                header.setPageView(current);
                System.out.println("last page" + current.isLastPage());
                if (current.isLastPage()) {
                    overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                    PageActivity.this.finish();
                }
            }

            public void onAnimationRepeat(Animation animation) {

            }
        });
        return rotation;
    }

    private long getFlipDurationFromPreference() {
        String key = getString(R.string.key_anim_flip_duration_preference);
        return Long.parseLong(preferences.getString(key, "500"));
    }

    private int getBrowseMode() {
        String key = getString(R.string.key_browse_mode_preference);
        return Integer.parseInt(preferences.getString(key, "0"));
    }

    private void switchViews(boolean forward) {
        if (forward) {
            WeiboPageView tmp = current;
            current = next;
            next = tmp;
        } else {
            WeiboPageView tmp = current;
            current = previous;
            previous = tmp;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, CONFIG_ID, 0, R.string.config);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CONFIG_ID:
                startActivity(new Intent(this, ConfigActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void executionFailed() {
        alarmSender.sendInstantMessage(R.string.networkerror, this);
        flipStarted = false;
    }

    private void showAnimation() {

        enlargedMode = false;
        renderNextPageIfNotRendered();
        Log.d("ANI", "start animation");
        container.removeAllViews();
        if (isWeiboMode() || current.isFirstPage() || next.isLastPage() || next.getWrapperViews().size() < 2 || lastFlipDirection==ACTION_HORIZONTAL) {

            Animation rotation = buildFlipHorizonalAnimation(forward);
            if (forward)
                next.setVisibility(View.VISIBLE);
            else
                previous.setVisibility(View.VISIBLE);
            if (forward) {
                currentPageIndex++;

                container.addView(current, pageViewLayoutParamsBack);
                container.addView(next, pageViewLayoutParamsFront);
                current.startAnimation(rotation);
            } else {

                container.addView(current, pageViewLayoutParamsFront);
                container.addView(previous, pageViewLayoutParamsBack);
                previous.startAnimation(rotation);
            }
        } else {
            if (forward) {
                currentPageIndex++;
                container.addView(next, pageViewLayoutParamsFront);
            } else {
                container.addView(previous, pageViewLayoutParamsFront);
            }
            container.addView(current, pageViewLayoutParamsBack);


            if (forward) {
                final LinearLayout currentUpper = current.getWrapperViews().get(0);
                final LinearLayout currentBottom = current.getWrapperViews().get(1);
                final LinearLayout nextUpper = next.getWrapperViews().get(0);
                final LinearLayout nextBottom = next.getWrapperViews().get(1);
                final Animation flipToNext = buildFlipAnimation(currentUpper, currentBottom, nextUpper, nextBottom, true);
                currentBottom.startAnimation(flipToNext);
            } else {
                final LinearLayout currentUpper = current.getWrapperViews().get(0);
                final LinearLayout currentBottom = current.getWrapperViews().get(1);
                final LinearLayout nextUpper = previous.getWrapperViews().get(0);
                final LinearLayout nextBottom = previous.getWrapperViews().get(1);

                final Animation flipToPrevious = buildFlipAnimation(currentUpper, currentBottom, nextUpper, nextBottom, false);
                currentUpper.startAnimation(flipToPrevious);
            }
        }

    }

    private Animation buildFlipAnimation(final LinearLayout currentUpper, final LinearLayout currentBottom, final LinearLayout nextUpper, final LinearLayout nextBottom, final boolean forward) {
//        final float centerY = container.getHeight() / 2;
//        final float centerX = container.getWidth() / 2;

        final float centerY = 240;
        final float centerX = 160;

        long duration = getFlipDurationFromPreference();

        final Animation step1 = forward ? AnimationFactory.buildVerticalFlipAnimation(0, 90, duration, centerX, 0, shadow, shadow2)
                : AnimationFactory.buildVerticalFlipAnimation(0, -90, duration, centerX, centerY, shadow, shadow2);

        final Animation step2 = forward ? AnimationFactory.buildVerticalFlipAnimation(-90, 0, duration, centerX, centerY, shadow, shadow2)
                : AnimationFactory.buildVerticalFlipAnimation(90, 0, duration, centerX, 0, shadow, shadow2);
        step2.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                Log.d("ANI", "upper part animation starts");
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
                params.setMargins(0, 0 - nextUpper.getHeight(), 0, 0);

                (forward ? nextUpper : nextBottom).addView(shadow, params);
                (forward ? currentUpper : currentBottom).addView(shadow2, params);
            }

            public void onAnimationEnd(Animation animation) {
                Log.d("ANI", "upper part animation end");
                Log.d("ANI", "remove shadows");
                (forward ? nextUpper : nextBottom).removeView(shadow);
                (forward ? currentUpper : currentBottom).removeView(shadow2);

                flipStarted = false;

                switchViews(PageActivity.this.forward);
                header.setPageView(current);
                new Thread(new Runnable() {
                    public void run() {
                        prepareNextPage();
                    }
                }).start();
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
        step1.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {

                shadowParams.setMargins(0, (int) (0 - currentBottom.getHeight()), 0, 0);
                Log.d("ANI", "adding shadows " + currentBottom.getHeight() + "," + currentBottom.getChildCount());
//                currentBottom.removeAllViews();

                (forward ? currentBottom : currentUpper).addView(shadow, shadowParams);
                (forward ? nextBottom : nextUpper).addView(shadow2, shadowParams);
//                        nextBottom.setVisibility(View.GONE);
            }

            public void onAnimationEnd(Animation animation) {
                Log.d("ANI", "animate upper part");
                (forward ? currentBottom : currentUpper).removeView(shadow);
                (forward ? nextBottom : nextUpper).removeView(shadow2);
//                        nextBottom.setVisibility(View.VISIBLE);
                container.removeAllViews();
                container.addView(current, pageViewLayoutParamsFront);
                container.addView(forward ? next : previous, pageViewLayoutParamsBack);
                (forward ? nextUpper : nextBottom).startAnimation(step2);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
        Log.d("ANI", "currentBottom");
        return step1;
    }

     public boolean sinaAlreadyBinded() {
        return accountDB.hasAccount(Constants.TYPE_SINA_WEIBO) && preferences.getString(WeiPaiWebViewClient.SINA_ACCOUNT_PREF_KEY, null) != null;
    }

    public static final int PROMPT_OAUTH = 1;

    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch (id) {
            case PROMPT_OAUTH:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.commentneedsinaoauth)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FlipdroidApplications application = (FlipdroidApplications) getApplication();
                                OAuth oauth = new OAuth();
                                application.setOauth(oauth);
                                System.out.println("OAuthHolder.oauth" + application + oauth);
                                oauth.RequestAccessToken(PageActivity.this, "flipdroid://SinaAccountSaver");
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                PageActivity.this.finish();
                            }
                        });
                dialog = builder.create();
                break;

            default:
                dialog = null;
        }
        return dialog;
    }
}
