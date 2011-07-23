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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.*;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.anim.AnimationFactory;
import com.goal98.flipdroid.client.OAuth;
import com.goal98.flipdroid.client.WeiboExt;
import com.goal98.flipdroid.db.AccountDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.exception.LastWindowException;
import com.goal98.flipdroid.exception.NoMoreStatusException;
import com.goal98.flipdroid.exception.NoSinaAccountBindedException;
import com.goal98.flipdroid.model.*;
import com.goal98.flipdroid.model.google.GoogleReaderArticleSource;
import com.goal98.flipdroid.model.rss.RSSArticleSource;
import com.goal98.flipdroid.model.sina.SinaArticleSource;
import com.goal98.flipdroid.model.sina.SinaToken;
import com.goal98.flipdroid.model.taobao.TaobaoArticleSource;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.util.Constants;
import com.goal98.flipdroid.util.GestureUtil;
import com.goal98.flipdroid.util.SinaAccountUtil;
import com.goal98.flipdroid.view.*;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class PageActivity extends Activity implements com.goal98.flipdroid.model.Window.OnLoadListener {

    static final private int CONFIG_ID = Menu.FIRST;

    public ExecutorService getExecutor() {
        return executor;
    }

    private boolean flipStarted = false;
    private boolean mToggleIndeterminate = false;
    private boolean forward = false;

    private ViewGroup container;
    private WeiboPageView current;
    private WeiboPageView next;
    private WeiboPageView previous;

    private ContentRepo repo;
    private SharedPreferences preferences;
    private AccountDB accountDB;
    private WeiboPagingStrategy weiboPagingStrategy;
    ExecutorService executor;
    private String deviceId;
    private int currentPageIndex = -1;
//    private Page currentSmartPage;


    private AlarmSender alarmSender;


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
    private WebImageView headerImageView;
    private int lastFlipDirection = ACTION_HORIZONTAL;
    private int browseMode;
    public static int ACTION_HORIZONTAL = 1;
    public static int ACTION_VERTICAL = 0;
    //    private TextView pageInfo;
    private PageIndexView pageIndexView;
    private SourceDB sourceDB;
    private LayoutInflater inflater;
    public SimpleCursorAdapter sourceAdapter;

    private long lastUpdate = -1;
    private float x, y, z;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 900;

    public SensorManager sm;
    public Sensor acceleromererSensor;
    public SensorEventListener acceleromererListener;
    public Animation.AnimationListener flipAnimationListener;
    public Animation.AnimationListener fadeInAnimationListener;
    public Animation.AnimationListener verticalAnimationListenerStep1;
    public Animation.AnimationListener verticalAnimationListenerStep2;
    public Animation verticalAnimationStep1;
    public LinearLayout currentUpper;
    public LinearLayout currentBottom;
    public LinearLayout nextUpper;
    public LinearLayout nextBottom;
    public Animation verticalAnimationStep2;
    public Dialog dialog;
    private Animation.AnimationListener fadeOutAnimationListener;


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

        createAnimation();

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);
        System.out.println("debug on create");

        executor = Executors.newCachedThreadPool();
        sourceDB = new SourceDB(this);
        alarmSender = new AlarmSender(this);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acceleromererSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        createShakeListener();

        accountDB = new AccountDB(this);
        registerShakeListener();
        Cursor sourceCursor = sourceDB.findAll();

        startManagingCursor(sourceCursor);

        sourceAdapter = new SimpleCursorAdapter(PageActivity.this, R.layout.source_selection_item, sourceCursor,
                new String[]{Source.KEY_SOURCE_NAME, Source.KEY_SOURCE_DESC, Source.KEY_IMAGE_URL},
                new int[]{R.id.source_name, R.id.source_desc, R.id.source_image});
        sourceAdapter.setViewBinder(new SourceItemViewBinder());

        TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tManager.getDeviceId();
        inflater = LayoutInflater.from(this);
        accountType = (String) getIntent().getExtras().get("type");
        sourceId = (String) getIntent().getExtras().get("sourceId");
        sourceImageURL = (String) getIntent().getExtras().get("sourceImage");
        sourceName = (String) getIntent().getExtras().get("sourceName");
        contentUrl = (String) getIntent().getExtras().get("contentUrl");

        ////System.out.println("sourceImageURL:" + sourceImageURL);
        setContentView(R.layout.main);
        container = (ViewGroup) findViewById(R.id.pageContainer);
        pageIndexView = (PageIndexView) findViewById(R.id.pageIndex);
        header = (HeaderView) findViewById(R.id.header);
//        pageInfo = (TextView)header.findViewById(R.id.pageInfo);
        headerText = (TextView) findViewById(R.id.headerText);
        headerImageView = (WebImageView) findViewById(R.id.headerImage);

        PagingStrategy pagingStrategy = null;
        pageViewFactory = new WeiboPageViewFactory();

        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        this.browseMode = getBrowseMode();
//        this.animationMode = getAnimationMode();
        refreshingSemaphore = new Semaphore(1, true);
        Log.v("accountType",accountType);
        if (accountType.equals(Constants.TYPE_SINA_WEIBO) || accountType.equals(Constants.TYPE_MY_SINA_WEIBO)) {
            ////System.out.println("accountType" + accountType);
            if (isWeiboMode())
                pagingStrategy = new WeiboPagingStrategy(this);
            else
                pagingStrategy = new FixedPagingStrategy(this, 2);

            pagingStrategy.setNoMoreArticleListener(new NoMoreArticleListener() {
                public void onNoMoreArticle() throws NoMoreStatusException {
                    //Log.d("cache system", "no more articles, refreshing repo");
                    repo.refresh(repo.getRefreshingToken());
                }
            });

            repo = new ContentRepo(pagingStrategy, refreshingSemaphore);

            SinaToken sinaToken = SinaAccountUtil.getToken(PageActivity.this);
            ArticleFilter filter = null;
            if (isWeiboMode())
                filter = new NullArticleFilter();
            else
                filter = new ContainsLinkFilter(new NullArticleFilter());

            source = new SinaArticleSource(true, sinaToken.getToken(), sinaToken.getTokenSecret(), sourceId, filter);

        } else if (accountType.equals(Constants.TYPE_RSS) ||accountType.equals(Constants.TYPE_BAIDUSEARCH)  ) {
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
        }else if (accountType.equals(Constants.TYPE_TAOBAO ) ){

            pagingStrategy = new FixedPagingStrategy(this, 2);
            pagingStrategy.setNoMoreArticleListener(new NoMoreArticleListener() {
                public void onNoMoreArticle() throws NoMoreStatusException {
                    throw new NoMoreStatusException();
                }
            });

            repo = new ContentRepo(pagingStrategy, refreshingSemaphore);
            source = new TaobaoArticleSource(sourceName,this.getApplicationContext());
        }

        repo.setArticleSource(source);

        headerText.setText(sourceName);
        if (sourceImageURL != null && sourceImageURL.length()!=0) {
            headerImageView.setImageUrl(sourceImageURL);
            headerImageView.loadImage();
        }else{
            headerImageView.setVisibility(View.GONE);
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

    private void createAnimation() {
        fadeOutAnimationListener = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                current.setVisibility(View.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };

        flipAnimationListener = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                if (forward)
                    container.bringChildToFront(current);
            }

            public void onAnimationEnd(Animation animation) {
                flipStarted = false;

                container.bringChildToFront(forward ? next : previous);
                if (next.isLastPage()) {
                    current.setVisibility(View.INVISIBLE);
                }
                switchViews(forward);
                new Thread(new Runnable() {
                    public void run() {
                        prepareNextPage();
                    }
                }).start();
                pageIndexView.setDot(repo.getTotal(), currentPageIndex);
                header.setPageView(current);
                ////System.out.println("last page" + current.isLastPage());
                if (current.isLastPage()) {
                    overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                    PageActivity.this.finish();
                }
            }

            public void onAnimationRepeat(Animation animation) {

            }
        };

        fadeInAnimationListener = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                if (forward)
                    container.bringChildToFront(current);
                next.setVisibility(View.INVISIBLE);
            }

            public void onAnimationEnd(Animation animation) {
                flipStarted = false;

                container.bringChildToFront(forward ? next : previous);
                current.setVisibility(View.INVISIBLE);
                switchViews(forward);
                new Thread(new Runnable() {
                    public void run() {
                        prepareNextPage();
                    }
                }).start();
                pageIndexView.setDot(repo.getTotal(), currentPageIndex);
                header.setPageView(current);
                ////System.out.println("last page" + current.isLastPage());
                if (current.isLastPage()) {
                    overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                    PageActivity.this.finish();
                }
                Animation fadeOutAnimation = buildFadeOutAnimation(true);
                current.startAnimation(fadeOutAnimation);
            }

            public void onAnimationRepeat(Animation animation) {

            }
        };

        verticalAnimationListenerStep2 = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                //Log.d("ANI", "upper part animation starts");
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
                params.setMargins(0, 0 - nextUpper.getHeight(), 0, 0);

                (forward ? nextUpper : nextBottom).addView(shadow, params);
                (forward ? currentUpper : currentBottom).addView(shadow2, params);
            }

            public void onAnimationEnd(Animation animation) {
                //Log.d("ANI", "upper part animation end");
                //Log.d("ANI", "remove shadows");
                (forward ? nextUpper : nextBottom).removeView(shadow);
                (forward ? currentUpper : currentBottom).removeView(shadow2);

                flipStarted = false;
                pageIndexView.setDot(repo.getTotal(), currentPageIndex);
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
        };

        verticalAnimationListenerStep1 = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {

                shadowParams.setMargins(0, (int) (0 - currentBottom.getHeight()), 0, 0);
                //Log.d("ANI", "adding shadows " + currentBottom.getHeight() + "," + currentBottom.getChildCount());
//                currentBottom.removeAllViews();

                (forward ? currentBottom : currentUpper).addView(shadow, shadowParams);
                (forward ? nextBottom : nextUpper).addView(shadow2, shadowParams);
//                        nextBottom.setVisibility(View.GONE);
            }

            public void onAnimationEnd(Animation animation) {
                //Log.d("ANI", "animate upper part");
                (forward ? currentBottom : currentUpper).removeView(shadow);
                (forward ? nextBottom : nextUpper).removeView(shadow2);
//                        nextBottom.setVisibility(View.VISIBLE);
                container.removeAllViews();
                container.addView(current, pageViewLayoutParamsFront);
                container.addView(forward ? next : previous, pageViewLayoutParamsBack);
                (forward ? nextUpper : nextBottom).startAnimation(verticalAnimationStep2);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        };
    }

    private void createShakeListener() {
        acceleromererListener = new SensorEventListener() {

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            public synchronized void onSensorChanged(SensorEvent event) {
                long curTime = System.currentTimeMillis();
                // only allow one update every 100ms.
                if ((curTime - lastUpdate) > 80) {
                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;

                    x = event.values[SensorManager.DATA_X];
                    y = event.values[SensorManager.DATA_Y];
                    z = event.values[SensorManager.DATA_Z];

                    float speed = Math.abs(x + y + z - last_x - last_y - last_z)
                            / diffTime * 10000;

                    if (speed > SHAKE_THRESHOLD) {
                        ////System.out.println("sensored" + dialog);
                        if (dialog == null)
                            PageActivity.this.showDialog(NAVIGATION);
                        else {
                            dialog.dismiss();
                        }
                    }
                    last_x = x;
                    last_y = y;
                    last_z = z;
                }

            }

        };
    }


    private void registerShakeListener() {
        sm.registerListener(acceleromererListener, acceleromererSensor, SensorManager.SENSOR_DELAY_UI);
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
        sm.unregisterListener(acceleromererListener);
    }

    @Override
    protected void onPause() {
        super.onDestroy();
        accountDB.close();
        sm.unregisterListener(acceleromererListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (accountDB != null)
            accountDB.close();
        if (sourceDB != null)
            sourceDB.close();
    }


    boolean isWeiboMode() {
        if (source == null) {
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
        //Log.d("SLIDING", "on windows loaded");
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
            pageView = new FirstPageView(PageActivity.this, slidingWindows,executor);
            return pageView;
        }

        public WeiboPageView createLastPage() {
            WeiboPageView pageView = null;
            pageView = new LastPageView(PageActivity.this);

            return pageView;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    boolean enlargedMode = false;

    public void setEnlargedMode(boolean enlargedMode) {
        this.enlargedMode = enlargedMode;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        ////System.out.println("flipStarted" + flipStarted);
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
        ////System.out.println("flipStarted" + flipStarted);
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
        System.out.println("debug flip");
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
        System.out.println("debug slide to next");
        try {
            if (preparingWindow == null && !prepareFail)
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
                if (next.isLastPage()) {
                    handler.post(new Runnable() {
                        public void run() {
                            showAnimation();
                        }
                    });
                    return;
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
                //Log.d("SLIDING", "register on load listener...");

                handler.post(new Runnable() {
                    public void run() {
                        nextWindow.registerOnLoadListener(PageActivity.this);
                        current.showLoading();
                    }
                });
                return;
            } else if (nextWindow.isSkip()) {
                //Log.d("SLIDING", "slide to next page...");
                current.setLoadingNext(false);
                slideToNextPage();
                return;
            } else {
                if (next.isLastPage()) {
                    ////System.out.println("lastpage");
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
        } catch (Exception e) {
            e.printStackTrace();
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
            ////System.out.println("preparingWindow" + preparingWindow);
            if (preparingWindow.isLoading()) {
                //Log.d("SLIDING", "register on load listener...");

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
                //Log.d("SLIDING", "slide to next page...");
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


        rotation.setAnimationListener(flipAnimationListener);
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
            previous = current;
            current = next;
            next = tmp;
        } else {
            WeiboPageView tmp = current;
            next = current;
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
        //Log.d("ANI", "start animation");
        container.removeAllViews();
        if (current.isFirstPage()) {
            Animation fadeIn = buildFadeInAnimation(forward);
            next.setVisibility(View.VISIBLE);
            currentPageIndex++;
            container.addView(current, pageViewLayoutParamsBack);
            container.addView(next, pageViewLayoutParamsFront);
            current.startAnimation(fadeIn);
        } else if (isWeiboMode() || next.isLastPage() || next.getWrapperViews().size() < 2 || lastFlipDirection == ACTION_HORIZONTAL) {

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
                currentUpper = current.getWrapperViews().get(0);
                currentBottom = current.getWrapperViews().get(1);
                nextUpper = next.getWrapperViews().get(0);
                nextBottom = next.getWrapperViews().get(1);
                final Animation flipToNext = buildFlipAnimation(currentUpper, currentBottom, nextUpper, nextBottom, true);
                currentBottom.startAnimation(flipToNext);
            } else {
                currentUpper = current.getWrapperViews().get(0);
                currentBottom = current.getWrapperViews().get(1);
                nextUpper = previous.getWrapperViews().get(0);
                nextBottom = previous.getWrapperViews().get(1);

                final Animation flipToPrevious = buildFlipAnimation(currentUpper, currentBottom, nextUpper, nextBottom, false);
                currentUpper.startAnimation(flipToPrevious);
            }
        }

    }

    private Animation buildFadeInAnimation(boolean forward) {
        final Animation fadeoutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade);
        fadeoutAnimation.setAnimationListener(fadeInAnimationListener);
        return fadeoutAnimation;  //To change body of created methods use File | Settings | File Templates.
    }

    private Animation buildFadeOutAnimation(boolean forward) {
        final Animation fadeoutArticle = AnimationUtils.loadAnimation(this, R.anim.fadein);
        fadeoutArticle.setAnimationListener(fadeOutAnimationListener);
        return fadeoutArticle;  //To change body of created methods use File | Settings | File Templates.
    }

    private Animation buildFlipAnimation(final LinearLayout currentUpper, final LinearLayout currentBottom, final LinearLayout nextUpper, final LinearLayout nextBottom, final boolean forward) {
        final float centerY = 240;
        final float centerX = 160;

        long duration = getFlipDurationFromPreference();

        verticalAnimationStep1 = forward ? AnimationFactory.buildVerticalFlipAnimation(0, 90, duration, centerX, 0, shadow, shadow2)
                : AnimationFactory.buildVerticalFlipAnimation(0, -90, duration, centerX, centerY, shadow, shadow2);

        verticalAnimationStep2 = forward ? AnimationFactory.buildVerticalFlipAnimation(-90, 0, duration, centerX, centerY, shadow, shadow2)
                : AnimationFactory.buildVerticalFlipAnimation(90, 0, duration, centerX, 0, shadow, shadow2);

        verticalAnimationStep2.setAnimationListener(verticalAnimationListenerStep2);

        verticalAnimationStep1.setAnimationListener(verticalAnimationListenerStep1);
        //Log.d("ANI", "currentBottom");
        return verticalAnimationStep1;
    }

    public boolean sinaAlreadyBinded() {
        return accountDB.hasAccount(Constants.TYPE_SINA_WEIBO) && preferences.getString(WeiPaiWebViewClient.SINA_ACCOUNT_PREF_KEY, null) != null;
    }

    public static final int PROMPT_OAUTH = 1;
    public static final int NAVIGATION = 2;

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
            case PROMPT_OAUTH:
                builder.setMessage(R.string.commentneedsinaoauth)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FlipdroidApplications application = (FlipdroidApplications) getApplication();
                                OAuth oauth = new OAuth();
                                application.setOauth(oauth);
                                ////System.out.println("OAuthHolder.oauth" + application + oauth);
                                oauth.RequestAccessToken(PageActivity.this, "flipdroid://SinaAccountSaver");
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                this.dialog = builder.create();
                break;
            case NAVIGATION:
                builder.setAdapter(sourceAdapter, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(PageActivity.this, PageActivity.class);
                        Cursor cursor = (Cursor) sourceAdapter.getItem(i);
                        intent.putExtra("type", cursor.getString(cursor.getColumnIndex(Source.KEY_ACCOUNT_TYPE)));
                        intent.putExtra("sourceId", cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_ID)));
                        intent.putExtra("sourceImage", cursor.getString(cursor.getColumnIndex(Source.KEY_IMAGE_URL)));
                        intent.putExtra("sourceName", cursor.getString(cursor.getColumnIndex(Source.KEY_SOURCE_NAME)));
                        intent.putExtra("contentUrl", cursor.getString(cursor.getColumnIndex(Source.KEY_CONTENT_URL)));
                        cursor.close();
                        if (dialog != null)
                            dialog.dismiss();
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
                        finish();
                    }
                });

                this.dialog = builder.create();
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                            if (dialog != null) {
                                dialog.dismiss();
                                return true;
                            }
                        }

                        return false;
                    }
                });
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
        return this.dialog;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {//拦截meu键事件
            showDialog(NAVIGATION);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
