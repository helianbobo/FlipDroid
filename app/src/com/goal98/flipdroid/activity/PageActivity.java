package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent.ShortcutIconResource;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewConfigurationCompat;
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
import com.goal98.flipdroid.model.cachesystem.CacheSystem;
import com.goal98.flipdroid.model.cachesystem.CachedArticleSource;
import com.goal98.flipdroid.model.cachesystem.SourceCache;
import com.goal98.flipdroid.model.cachesystem.SourceUpdateable;
import com.goal98.flipdroid.model.featured.FeaturedArticleSource;
import com.goal98.flipdroid.model.google.GoogleReaderArticleSource;
import com.goal98.flipdroid.model.rss.RSSArticleSource;
import com.goal98.flipdroid.model.sina.SinaArticleSource;
import com.goal98.flipdroid.model.sina.SinaToken;
import com.goal98.flipdroid.model.taobao.TaobaoArticleSource;
import com.goal98.flipdroid.util.*;
import com.goal98.flipdroid.view.*;
import weibo4j.Weibo;
import weibo4j.WeiboException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

public class PageActivity extends Activity implements com.goal98.flipdroid.model.Window.OnLoadListener, SourceUpdateable {

    private static final int CONFIG_ID = Menu.FIRST;
    private Animation fadeInPageView;
    private Animation fadeOutPageView;
    private ImageButton contentImageButton;
    public SinaToken sinaToken;
    private CachedArticleSource cachedArticleSource;

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
    public SourceItemArrayAdapter sourceAdapter;
    private boolean updated;
    private long lastUpdate = -1;
    private float x, y, z;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 900;
    private DeviceInfo deviceInfo;
    //    public SensorManager sm;
//    public Sensor acceleromererSensor;
//    public SensorEventListener acceleromererListener;
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

    private int mTouchSlop;


    public String getSourceImageURL() {
        return sourceImageURL;
    }

    public String getSourceName() {
        return sourceName;
    }


    public HeaderView getHeader() {
        return header;
    }


    public DeviceInfo getDeviceInfoFromApplicationContext() {
        return DeviceInfo.getInstance(this);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ViewConfiguration configuration = ViewConfiguration.get(this);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        GestureUtil.minDelta = mTouchSlop;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        this.deviceInfo = getDeviceInfoFromApplicationContext();


        StopWatch sw = new StopWatch();
        sw.start("create activity");


        createAnimation();
        buildFadeInPageViewAnimation();
        buildFadeOutPageViewAnimation();


        setProgressBarIndeterminateVisibility(false);
        System.out.println("debug on create");

        executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                10L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
        sourceDB = new SourceDB(this);
        alarmSender = new AlarmSender(this);

//        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        acceleromererSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        createShakeListener();


        Cursor sourceCursor = sourceDB.findAll();

        startManagingCursor(sourceCursor);

        sourceAdapter = new SourceItemArrayAdapter<SourceItem>(this, R.layout.source_item, sourceDB, deviceInfo);

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
        ViewSwitcher headerSwitcher = (ViewSwitcher) findViewById(R.id.flipper);
        header = (HeaderView) findViewById(R.id.header);
        contentImageButton = (ImageButton) findViewById(R.id.content);
        contentImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finishActivity();
            }
        });
//        pageInfo = (TextView)header.findViewById(R.id.pageInfo);
        headerText = (TextView) findViewById(R.id.headerText);
        headerImageView = (WebImageView) findViewById(R.id.headerImage);


        pageViewFactory = new WeiboPageViewFactory();

        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        this.browseMode = getBrowseMode();
//        this.animationMode = getAnimationMode();
        refreshingSemaphore = new Semaphore(1, true);
        Log.v("accountType", accountType);
        reload();

    }


    private void createAnimation() {
        fadeOutAnimationListener = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                current.setVisibility(View.VISIBLE);
            }

            public void onAnimationEnd(Animation animation) {

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

            }

            public void onAnimationRepeat(Animation animation) {

            }
        };

        fadeInAnimationListener = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                if (forward)
                    container.bringChildToFront(current);
                next.setVisibility(View.VISIBLE);
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

                if (cachedArticleSource != null && !updated && NetworkUtil.isNetworkAvailable()) {
                    System.out.println("check update");
                    cachedArticleSource.checkUpdate();
                }

                pageIndexView.setDot(repo.getTotal(), currentPageIndex);
                header.setPageView(current);
                if (current.isLastPage()) {
                    finishActivity();
                }
//                current.startAnimation(fadeOutPageView);
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

//    private void createShakeListener() {
//        acceleromererListener = new SensorEventListener() {
//
//            public void onAccuracyChanged(Sensor sensor, int accuracy) {
//            }
//
//            public synchronized void onSensorChanged(SensorEvent event) {
//                long curTime = System.currentTimeMillis();
//                // only allow one update every 100ms.
//                if ((curTime - lastUpdate) > 80) {
//                    long diffTime = (curTime - lastUpdate);
//                    lastUpdate = curTime;
//
//                    x = event.values[SensorManager.DATA_X];
//                    y = event.values[SensorManager.DATA_Y];
//                    z = event.values[SensorManager.DATA_Z];
//
//                    float speed = Math.abs(x + y + z - last_x - last_y - last_z)
//                            / diffTime * 10000;
//
//                    if (speed > SHAKE_THRESHOLD) {
//                        ////System.out.println("sensored" + dialog);
//                        if (dialog == null)
//                            PageActivity.this.showDialog(NAVIGATION);
//                        else {
//                            dialog.dismiss();
//                        }
//                    }
//                    last_x = x;
//                    last_y = y;
//                    last_z = z;
//                }
//
//            }
//
//        };
//    }
//
//
//    private void registerShakeListener() {
//        sm.registerListener(acceleromererListener, acceleromererSensor, SensorManager.SENSOR_DELAY_UI);
//    }

    private int getAnimationMode() {
        String key = getString(R.string.key_animation_mode_preference);
        return Integer.parseInt(preferences.getString(key, "0"));
    }

    private Semaphore refreshingSemaphore;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sourceDB != null)
            sourceDB.close();
    }

    @Override
    protected void onPause() {
        super.onDestroy();
        CacheSystem.getTikaCache(this).shutdown();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
            initSinaWeibo();
        }
        weibo.updateStatus(comment, statusId);
    }

    public void forward(String comment, String url) throws WeiboException, NoSinaAccountBindedException {
        String userId = preferences.getString("sina_account", null);
        if (userId == null)
            throw new NoSinaAccountBindedException();

        if (weibo == null) {
            initSinaWeibo();
        }
        weibo.updateStatus(comment + " " + url);
    }

    private void initSinaWeibo() {
        weibo = new WeiboExt();
        weibo.setHttpConnectionTimeout(5000);
        if (sinaToken == null)
            sinaToken = SinaAccountUtil.getToken(PageActivity.this);

        weibo.setToken(sinaToken.getToken(), sinaToken.getTokenSecret());
    }

    public void notifyHasNew(CachedArticleSource cachedArticleSource) {
        handler.post(new Runnable() {
            public void run() {
                setProgressBarIndeterminateVisibility(false);
                pageIndexView.setHasUpdate(true);
            }
        });
    }

    public void notifyNoNew(CachedArticleSource cachedArticleSource) {
        handler.post(new Runnable() {
            public void run() {
                setProgressBarIndeterminateVisibility(false);
                pageIndexView.setHasUpdate(false);
            }
        });
    }

    public void notifyUpdateDone(CachedArticleSource cachedArticleSource) {

    }

    public void notifyUpdating(CachedArticleSource cachedArticleSource) {
        handler.post(new Runnable() {
            public void run() {
                setProgressBarIndeterminateVisibility(true);
                pageIndexView.setUpdating(true);
            }
        });
    }

    public void reload() {
        currentPageIndex = -1;
        PagingStrategy pagingStrategy = null;
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

            sinaToken = SinaAccountUtil.getToken(PageActivity.this);
            ArticleFilter filter;
            if (isWeiboMode())
                filter = new NullArticleFilter();
            else
                filter = new ContainsLinkFilter(new NullArticleFilter());

            source = new SinaArticleSource(true, sinaToken.getToken(), sinaToken.getTokenSecret(), sourceId, filter);

        } else if (accountType.equals(Constants.TYPE_RSS) || accountType.equals(Constants.TYPE_BAIDUSEARCH)) {
            pagingStrategy = new FixedPagingStrategy(this, 2);
            pagingStrategy.setNoMoreArticleListener(new NoMoreArticleListener() {
                public void onNoMoreArticle() throws NoMoreStatusException {
                    throw new NoMoreStatusException();
                }
            });

            repo = new ContentRepo(pagingStrategy, refreshingSemaphore);
            cachedArticleSource = new CachedArticleSource(new FeaturedArticleSource(contentUrl, sourceName, sourceImageURL), this, SourceCache.getInstance(this));
            cachedArticleSource.loadSourceFromCache();
            source = cachedArticleSource;
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
        } else if (accountType.equals(Constants.TYPE_TAOBAO)) {

            pagingStrategy = new FixedPagingStrategy(this, 2);
            pagingStrategy.setNoMoreArticleListener(new NoMoreArticleListener() {
                public void onNoMoreArticle() throws NoMoreStatusException {
                    throw new NoMoreStatusException();
                }
            });

            repo = new ContentRepo(pagingStrategy, refreshingSemaphore);
            source = new TaobaoArticleSource(sourceName, this.getApplicationContext());
        }

        repo.setArticleSource(source);

        headerText.setText(sourceName);
        if (sourceImageURL != null && sourceImageURL.length() != 0) {
            headerImageView.setImageUrl(sourceImageURL);
            headerImageView.loadImage();
        } else {
            int maxTitle = 7;

            if (sourceName != null && sourceName.length() >= maxTitle)
                headerImageView.setVisibility(View.GONE);
            else
                headerImageView.setVisibility(View.INVISIBLE);
        }

        slidingWindows = new PageViewSlidingWindows(10, repo, pageViewFactory, 3);
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
            pageView = new FirstPageView(PageActivity.this, slidingWindows, executor);
            return pageView;
        }

        public WeiboPageView createLastPage() {
            WeiboPageView pageView = null;
            pageView = new LastPageView(PageActivity.this);

            return pageView;
        }

    }

    boolean enlargedMode = false;

    public void setEnlargedMode(boolean enlargedMode) {
        this.enlargedMode = enlargedMode;
        if (enlargedMode) {
            header.showToolBar();
        } else {
            header.showTitleBar();
        }
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
//        if (pageIndexView.dispatchTouchEvent(event)) {
//            return true;
//        }
//        }
        if (enlargedMode) {
            current.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mLastMotionX = mInitialMotionX = event.getX();
                mLastMotionY = mInitialMotionY = event.getY();
                break;
            }
            case MotionEvent.ACTION_UP:
                Log.v(TAG, "** dispatchTouchEvent() event.getAction()=" + MotionEvent.ACTION_UP);
                return current.dispatchTouchEvent(event);
            case MotionEvent.ACTION_MOVE: {

                mLastMotionX = event.getX();
                mLastMotionY = event.getY();

                if (!enlargedMode) {
                    Log.v(TAG, "** dispatchTouchEvent() event.getAction()=" + MotionEvent.ACTION_MOVE + " enlargedMode=" + enlargedMode + " --> onTouchEvent(event)");
                    onTouchEvent(event);
                } else {
                    Log.v(TAG, "** dispatchTouchEvent() event.getAction()=" + MotionEvent.ACTION_MOVE + " enlargedMode=" + enlargedMode + " --> current.onTouchEvent(event);");
                    current.onTouchEvent(event);
                }

            }
        }

        Log.v("PageActivity", "** dispatchTouchEvent() event.getAction()=" + event.getAction() + " enlargedMode=" + enlargedMode + " --> super.dispatchTouchEvent(event);");
        return super.dispatchTouchEvent(event);
    }

    private final String TAG = "PageActivity";
    private float mLastMotionX, mLastMotionY, mInitialMotionX, mInitialMotionY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ////System.out.println("flipStarted" + flipStarted);

        if (flipStarted) {
            Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> return false;");
            return false;
        }
//        if (header.isSourceSelectMode()) {
//            header.onTouchEvent(event);
//        }
        if (enlargedMode) {
            Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " enlargedMode=" + enlargedMode + " -->  current.onTouchEvent(event);");
            current.onTouchEvent(event);
        }


        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN: {
                mLastMotionX = mInitialMotionX = event.getX();
                mLastMotionY = mInitialMotionY = event.getY();
                break;
            }

            case MotionEvent.ACTION_MOVE:

                mLastMotionX = event.getX();
                mLastMotionY = event.getY();

                if (!flipStarted) {

                    final GestureUtil.FlipDirection flipDirection = GestureUtil.checkFlipDirection(mInitialMotionX, mInitialMotionY, mLastMotionX, mLastMotionY);

                    switch (flipDirection) {
                        case RIGHT:
                            Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> RIGHT;");
                            lastFlipDirection = ACTION_HORIZONTAL;
                            flipPage(false);
                            break;
                        case UP:
                            Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> UP;");
                            lastFlipDirection = ACTION_VERTICAL;
                            flipPage(true);
                            break;
                        case LEFT:
                            Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> LEFT;");
                            lastFlipDirection = ACTION_HORIZONTAL;
                            flipPage(true);
                            break;
                        case DOWN:
                            Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> DOWN;");
                            lastFlipDirection = ACTION_VERTICAL;
                            flipPage(false);
                            break;
                        case NONE:
                            break;

                    }

                    Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> No Gesture matched");

                } else {
                    Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " event.getHistorySize()" + event.getHistorySize() + " --> header.onTouchEvent(event);");
//                    if (header.isSourceSelectMode())
                    header.onTouchEvent(event);
//                    pageIndexView.onTouchEvent(event);
                }
                break;
            default:
                Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> default;");
                break;
        }
        Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> return true;");

        return true;
    }

    Handler handler = new Handler();

    private void flipPage(final boolean forward) {
        if (!current.isFirstPage())
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
            slideToNextPageAsynchronized();
        } else if (nextPageIndex > 0) {

            if (current.isLastPage() && forward) {
                finishActivity();
            }
            slideToNextPageAsynchronized();
        } else {
            if (pageIndexView.isHasUpdate()) {
                this.reload();
                return;
            } else {
                finishActivity();
            }
        }
        System.out.println("debug flip done");
    }

    private void finishActivity() {
        overridePendingTransition(android.R.anim.slide_in_left, R.anim.fade);
        finish();
    }

    private void slideToNextPageAsynchronized() {
        new Thread(new Runnable() {
            public void run() {
                slideToNextPage();
            }
        }).start();
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
                            finishActivity();
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

        int animation = forward ? R.anim.left_out : R.anim.left_in;
        Animation result = AnimationUtils.loadAnimation(this, animation);//AnimationFactory.buildHorizontalFlipAnimation(forward, duration, centerX, centerY);

        result.setDuration(duration);
        result.setAnimationListener(flipAnimationListener);
        return result;
    }

    private long getFlipDurationFromPreference() {
        String key = getString(R.string.key_anim_flip_duration_preference);
        return Long.parseLong(preferences.getString(key, "400"));
    }

    private int getBrowseMode() {
        String key = getString(R.string.key_browse_mode_preference);
        return Integer.parseInt(preferences.getString(key, "1"));
    }

    private void switchViews(boolean forward) {
        if (forward) {
            if (previous != null)
                previous.releaseResource();
            WeiboPageView tmp = current;
            previous = current;
            current = next;
            next = tmp;
        } else {
            if (next != null)
                next.releaseResource();
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
            next.setVisibility(View.VISIBLE);
            currentPageIndex++;
            container.addView(current, pageViewLayoutParamsBack);
            container.addView(next, pageViewLayoutParamsFront);


            current.setAnimationCacheEnabled(true);
            current.startAnimation(fadeInPageView);
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

                current.setAnimationCacheEnabled(true);
                current.startAnimation(rotation);
            } else {

                container.addView(current, pageViewLayoutParamsFront);
                container.addView(previous, pageViewLayoutParamsBack);

                previous.setAnimationCacheEnabled(true);
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

    private void buildFadeInPageViewAnimation() {
        fadeInPageView = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        fadeInPageView.setAnimationListener(fadeInAnimationListener);
    }

    private void buildFadeOutPageViewAnimation() {
        fadeOutPageView = AnimationUtils.loadAnimation(this, R.anim.left_in);
        fadeOutPageView.setAnimationListener(fadeOutAnimationListener);
    }

    private Animation buildFlipAnimation(final LinearLayout currentUpper, final LinearLayout currentBottom, final LinearLayout nextUpper, final LinearLayout nextBottom, final boolean forward) {
        final float centerY = getWindowManager().getDefaultDisplay().getHeight()/2;
        final float centerX = 160;

        getWindowManager().getDefaultDisplay().getHeight();

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
        AccountDB accountDB = new AccountDB(this);
        try {
            return accountDB.hasAccount(Constants.TYPE_MY_SINA_WEIBO) && preferences.getString(WeiPaiWebViewClient.SINA_ACCOUNT_PREF_KEY, null) != null;
        } finally {
            accountDB.close();
        }
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
                LayoutInflater li = LayoutInflater.from(this);
                View v = li.inflate(R.layout.dialog_nav_title_view, null);

                // builder.setView(v);
                builder.setCustomTitle(v);


                builder.setAdapter(sourceAdapter, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(PageActivity.this, PageActivity.class);
                        SourceItem cursor = sourceAdapter.getItem(i);
                        intent.putExtra("type", cursor.getSourceType());
                        intent.putExtra("sourceId", cursor.getSourceId());
                        intent.putExtra("sourceImage", cursor.getSourceImage());
                        intent.putExtra("sourceName", cursor.getSourceName());
                        intent.putExtra("contentUrl", cursor.getSourceURL());
                        if (dialog != null)
                            dialog.dismiss();
                        startActivity(intent);
                        finishActivity();
                    }
                });


                this.dialog = builder.create();
                Button btn_addshortcut = (Button) v.findViewById(R.id.btnaddshortcut);
                btn_addshortcut.setText("add shortcut");

                btn_addshortcut.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        addShortcut();
                        dialog.cancel();
                    }
                });

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

    @Override
    public void onBackPressed() {
        if (enlargedMode)
            current.closeEnlargedView();
        else {
            finishActivity();
        }
        return;
    }

    public boolean toLoadImage() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(this.getString(R.string.key_load_image_preference), true);
    }

    private void addShortcut() {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra("duplicate", false);
        ComponentName comp = new ComponentName(this.getPackageName(), "." + this.getLocalClassName());
        Intent intent = new Intent(Intent.ACTION_MAIN).setComponent(comp);
        //Bundle bundle = new Bundle();
        //bundle.putString("info", "infohahaha"+time);
        //bundle.putString("info", "infohahaha");
        //intent.putExtras(bundle);
        intent.putExtra("type", accountType);
        intent.putExtra("sourceId", sourceId);
        intent.putExtra("sourceImage", sourceImageURL);
        intent.putExtra("sourceName", sourceName);
        intent.putExtra("contentUrl", contentUrl);//for rss

        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, sourceName);

        ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.drawable.icon);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        sendBroadcast(shortcut);
    }
}
