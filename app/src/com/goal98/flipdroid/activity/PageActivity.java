package com.goal98.flipdroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.animation.*;
import android.widget.*;
import com.goal98.android.WebImageView;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.anim.AnimationFactory;
import com.goal98.flipdroid.client.OAuth;
import com.goal98.flipdroid.db.RSSURLDB;
import com.goal98.flipdroid.db.SourceContentDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.exception.NoMoreStatusException;
import com.goal98.flipdroid.exception.NoSinaAccountBindedException;
import com.goal98.flipdroid.model.*;
import com.goal98.flipdroid.model.cachesystem.CacheSystem;
import com.goal98.flipdroid.model.cachesystem.CachedArticleSource;
import com.goal98.flipdroid.model.cachesystem.SourceCache;
import com.goal98.flipdroid.model.cachesystem.SourceUpdateable;
import com.goal98.flipdroid.model.google.GoogleReaderArticleSource;
import com.goal98.flipdroid.model.rss.RemoteRSSArticleSource;
import com.goal98.flipdroid.model.sina.SinaArticleSource;
import com.goal98.flipdroid.model.sina.SinaToken;
import com.goal98.flipdroid.model.taobao.TaobaoArticleSource;
import com.goal98.flipdroid.multiscreen.MultiScreenSupport;
import com.goal98.flipdroid.util.*;
import com.goal98.flipdroid.view.*;
import com.goal98.tika.common.TikaConstants;
import com.mobclick.android.MobclickAgent;
import weibo4j.WeiboException;

import java.util.concurrent.*;

public class PageActivity extends Activity implements com.goal98.flipdroid.model.Window.OnLoadListener, SourceUpdateable {

    private static final int CONFIG_ID = Menu.FIRST;
    public static final int FADE_OUT_FIRST_PAGE_DURATION = 1000;
    private Animation fadeInPageView;
    private Animation fadeOutPageView;
    private ImageButton contentImageButton;

    private CachedArticleSource cachedArticleSource;
    public static final int PROMPT_INPROGRESS = 3;
    private boolean toLoadImage;
    private Animation rotationForward;
    private Animation rotationBackward;
    private SinaWeiboHelper sinaWeiboHelper;
    private RSSURLDB rssurlDB;
    private SourceContentDB sourceContentDB;

    public ExecutorService getExecutor() {
        return executor;
    }

    private boolean flipStarted = false;
    private boolean forward = false;

    private ViewGroup container;

    public ViewGroup getContainer() {
        return container;
    }

    private ThumbnailViewContainer current;
    private ThumbnailViewContainer next;
    private ThumbnailViewContainer previous;

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

    //    private int animationMode;
    private LinearLayout shadow;
    private LinearLayout shadow2;
    private LinearLayout.LayoutParams shadowParams;
    private FrameLayout.LayoutParams pageViewLayoutParamsFront;
    public com.goal98.flipdroid.model.Window preparingWindow;
    private boolean previousDirection;
    private boolean prepareFail;
    private FrameLayout.LayoutParams pageViewLayoutParamsBack;
    private HeaderView bottomBar;
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
    public ViewGroup currentUpper;
    public ViewGroup currentBottom;
    public ViewGroup nextUpper;
    public ViewGroup nextBottom;
    public Animation verticalAnimationStep2;
    public Dialog dialog;
    private Animation.AnimationListener fadeOutAnimationListener;

    private int mTouchSlop;

    private View tutorial;

    public boolean isFlipStarted() {
        return flipStarted;
    }

    public String getSourceImageURL() {
        return sourceImageURL;
    }

    public String getSourceName() {
        return sourceName;
    }


    public HeaderView getBottomBar() {
        return bottomBar;
    }


    public DeviceInfo getDeviceInfoFromApplicationContext() {
        return DeviceInfo.getInstance(this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    LayoutAnimationController fadeinController;

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

        fadeinController = new LayoutAnimationController(AnimationUtils.loadAnimation(PageActivity.this, R.anim.fadein));
        createAnimation();
        buildFadeInPageViewAnimation();
        buildFadeOutPageViewAnimation();


        setProgressBarIndeterminateVisibility(false);
        Log.v(TAG, "debug on create");

        executor = Executors.newFixedThreadPool(10);
        sourceDB = new SourceDB(this);
        rssurlDB = new RSSURLDB(this);
        sourceContentDB = new SourceContentDB(this);

        alarmSender = new AlarmSender(this.getApplicationContext());

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

        ////Log.v(TAG, "sourceImageURL:" + sourceImageURL);
        setContentView(R.layout.main);
        container = (ViewGroup) findViewById(R.id.pageContainer);

        /*container.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {

            }
        });*/
        toLoadImage = NetworkUtil.toLoadImage(PageActivity.this);
        pageIndexView = (PageIndexView) findViewById(R.id.pageIndex);
        bottomBar = (HeaderView) findViewById(R.id.header);
        bottomBar.setLayoutAnimation(fadeinController);
        contentImageButton = (ImageButton) findViewById(R.id.content);
        contentImageButton.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        finishActivity();
                        break;
                    default:
                        break;
                }

                return false;
            }

        });

//        pageInfo = (TextView)bottomBar.findViewById(R.id.pageInfo);
        headerText = (TextView) findViewById(R.id.headerText);
        headerImageView = (WebImageView) findViewById(R.id.headerImage);


        pageViewFactory = new WeiboPageViewFactory();

        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        this.browseMode = getBrowseMode();
//        this.animationMode = getAnimationMode();
        refreshingSemaphore = new Semaphore(1, true);
        Log.v("accountType", accountType);

        initTutorialView();
        rotationForward = buildFlipHorizonalAnimation(true);
        rotationBackward = buildFlipHorizonalAnimation(false);
        sinaWeiboHelper = new SinaWeiboHelper(this);
        reload();


    }

    private void initTutorialView() {
        tutorial = findViewById(R.id.tutorial);
        tutorial.setVisibility(View.GONE);
        tutorial.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                tutorial.setVisibility(View.GONE);
                preferences.edit().putBoolean(Constants.PREFERENCE_TUTORIAL_READ, true).commit();
            }
        });
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
//                new Thread(new Runnable() {
//                    public void run() {
//                        prepareNextPage();
//                    }
//                }).start();
                pageIndexView.setDot(repo.getTotal(), currentPageIndex);
//                bottomBar.setPageView(current);

            }

            public void onAnimationRepeat(Animation animation) {

            }
        };

        fadeInAnimationListener = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                if (forward)
                    container.bringChildToFront(current);
                next.setVisibility(View.VISIBLE);
                bottomBar.setVisibility(View.VISIBLE);
                current.setVisibility(View.GONE);


            }

            public void onAnimationEnd(Animation animation) {
                flipStarted = false;

                container.bringChildToFront(forward ? next : previous);

                switchViews(forward);
//                new Thread(new Runnable() {
//                    public void run() {
//                        prepareNextPage();
//                    }
//                }).start();


                if (cachedArticleSource != null && !updated && NetworkUtil.isNetworkAvailable(PageActivity.this)) {
                    Log.v(TAG, "check update");
                    cachedArticleSource.checkUpdate(false);
                }

                pageIndexView.setDot(repo.getTotal(), currentPageIndex);
//                bottomBar.setPageView(current);
                if (current != null && current.isLastPage()) {
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
//                bottomBar.setPageView(current);
//                new Thread(new Runnable() {
//                    public void run() {
//                        prepareNextPage();
//                    }
//                }).start();
            }

            public void onAnimationRepeat(Animation animation) {
            }
        };

        verticalAnimationListenerStep1 = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {

                shadowParams.setMargins(0, 0 - currentBottom.getHeight(), 0, 0);
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

    private void displayTutorial() {
        final boolean tutorial_read = preferences.getBoolean(Constants.PREFERENCE_TUTORIAL_READ, false);
        if (!tutorial_read)
            tutorial.setVisibility(View.VISIBLE);
    }

    private int getAnimationMode() {
        String key = getString(R.string.key_animation_mode_preference);
        return Integer.parseInt(preferences.getString(key, "0"));
    }

    private Semaphore refreshingSemaphore;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
        if (sourceDB != null)
            sourceDB.close();
        if(sourceContentDB!=null){
            sourceContentDB.close();
        }
        if(rssurlDB!=null)
            rssurlDB.close();
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
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

    public void comment(String comment, Article article) throws WeiboException, NoSinaAccountBindedException {

        sinaWeiboHelper.comment(comment, article);
    }

    public void forward(String comment, Article article) throws WeiboException, NoSinaAccountBindedException {


        sinaWeiboHelper.forward(comment, article);
    }

    private void initSinaWeibo() {

        sinaWeiboHelper.initSinaWeibo();
    }

    public void notifyHasNew(CachedArticleSource cachedArticleSource) {
        handler.post(new Runnable() {
            public void run() {
                setProgressBarIndeterminateVisibility(false);
                bottomBar.showUpdate();
                pageIndexView.setUpdating(false);
            }
        });
    }

    public void notifyNoNew(CachedArticleSource cachedArticleSource) {
        handler.post(new Runnable() {
            public void run() {
                setProgressBarIndeterminateVisibility(false);
                pageIndexView.setHasUpdate(false);
                bottomBar.hideUpdate();
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
        current = null;
        next = null;
        previous = null;
        preparingWindow = null;
        bottomBar.hideUpdate();
        currentPageIndex = -1;
        PagingStrategy pagingStrategy = null;
        if (accountType.equals(TikaConstants.TYPE_SINA_WEIBO) || accountType.equals(TikaConstants.TYPE_MY_SINA_WEIBO)) {
            ////Log.v(TAG, "accountType" + accountType);
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
            ArticleFilter filter;
            if (isWeiboMode())
                filter = new NullArticleFilter();
            else
                filter = new ContainsLinkOrImageFilter(new NullArticleFilter());

            source = new SinaArticleSource(true, sinaToken.getToken(), sinaToken.getTokenSecret(), sourceId, filter);

        } else if (accountType.equals(TikaConstants.TYPE_RSS) || accountType.equals(Constants.TYPE_BAIDUSEARCH)) {
            pagingStrategy = new FixedPagingStrategy(this, 2);
            pagingStrategy.setNoMoreArticleListener(new NoMoreArticleListener() {
                public void onNoMoreArticle() throws NoMoreStatusException {
                    throw new NoMoreStatusException();
                }
            });

            repo = new ContentRepo(pagingStrategy, refreshingSemaphore);
            cachedArticleSource = new CachedArticleSource(new RemoteRSSArticleSource(contentUrl, sourceName, sourceImageURL), this, new SourceCache(sourceContentDB), rssurlDB);
            cachedArticleSource.loadSourceFromCache();
            source = cachedArticleSource;
        } else if (accountType.equals(TikaConstants.TYPE_GOOGLE_READER)) {
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
            headerImageView.setRoundImage(true);
            headerImageView.setBackgroundResource(R.drawable.border);
        } else {
            int maxTitle = 7;

            if (sourceName != null && sourceName.length() >= maxTitle)
                headerImageView.setVisibility(View.GONE);
            else
                headerImageView.setVisibility(View.INVISIBLE);
        }

        slidingWindows = new PageViewSlidingWindows(5, repo, pageViewFactory, 2);
        current = pageViewFactory.createFirstPage();
        handler.post(new Runnable() {
            @Override
            public void run() {
                current.renderBeforeLayout();
            }
        });
        shadow = new LinearLayout(PageActivity.this);
        shadow.setBackgroundColor(Color.parseColor("#10999999"));

        shadow2 = new LinearLayout(PageActivity.this);
        shadow2.setBackgroundColor(Color.parseColor("#FFDDDDDD"));

        shadowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        pageViewLayoutParamsFront = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
        pageViewLayoutParamsBack = pageViewLayoutParamsFront;
        flipPage(true);
    }

    public void hideIndexView() {
        this.pageIndexView.hide();
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 440);
        container.setLayoutParams(params1);
    }

    public void showIndexView() {
        this.pageIndexView.show();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 415);
        container.setLayoutParams(params);
    }

    public boolean isToLoadImage() {
        return toLoadImage;
    }


    public class WeiboPageViewFactory {
        public ThumbnailViewContainer createPageView() {
            System.out.println("jleo creating page view");
            ThumbnailViewContainer pageViewContainer = null;

            if (isWeiboMode()) {
                pageViewContainer = new ThumbnailViewContainer(PageActivity.this);
            } else {
                pageViewContainer = new MagzinePageViewContainer(PageActivity.this);
            }
            return pageViewContainer;
        }

        public ThumbnailViewContainer createFirstPage() {
            ThumbnailViewContainer pageViewContainer = null;
            pageViewContainer = new FirstPageViewContainer(PageActivity.this);
            return pageViewContainer;
        }

        public ThumbnailViewContainer createLastPage() {
            ThumbnailViewContainer pageViewContainer = null;
            pageViewContainer = new LastPageViewContainer(PageActivity.this);

            return pageViewContainer;
        }

    }

    boolean enlargedMode = false;

    public void setEnlargedMode(boolean enlargedMode) {
        this.enlargedMode = enlargedMode;
        if (enlargedMode) {
            bottomBar.showToolBar();
        } else {
            bottomBar.showTitleBar();
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        ////Log.v(TAG, "flipStarted" + flipStarted);

        if (tutorial.getVisibility() == View.VISIBLE) {
            tutorial.dispatchTouchEvent(event);
            return true;
        }

        if (flipStarted)
            return true;
//        if (bottomBar.isSourceSelectMode()) {
        if (bottomBar.dispatchTouchEvent(event)) {
            return true;
        }
        if (pageIndexView.dispatchTouchEvent(event)) {
            return true;
        }
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
            /*case MotionEvent.ACTION_UP:
                Log.v(TAG, "** dispatchTouchEvent() event.getAction()=" + MotionEvent.ACTION_UP);
                return current.dispatchTouchEvent(event);*/
            case MotionEvent.ACTION_UP: {

                mLastMotionX = event.getX();
                mLastMotionY = event.getY();

                if (!enlargedMode) {
//                    Log.v(TAG, "** dispatchTouchEvent() event.getAction()=" + MotionEvent.ACTION_MOVE + " enlargedMode=" + enlargedMode + " --> onTouchEvent(event)");
                    if (!onTouchEvent(event))
                        current.onTouchEvent(event);
                } else {
//                    Log.v(TAG, "** dispatchTouchEvent() event.getAction()=" + MotionEvent.ACTION_MOVE + " enlargedMode=" + enlargedMode + " --> current.onTouchEvent(event);");
                    current.onTouchEvent(event);
                }

            }
        }

//        Log.v("PageActivity", "** dispatchTouchEvent() event.getAction()=" + event.getAction() + " enlargedMode=" + enlargedMode + " --> super.dispatchTouchEvent(event);");
        return super.dispatchTouchEvent(event);
    }

    private final String TAG = "PageActivity";
    private float mLastMotionX, mLastMotionY, mInitialMotionX, mInitialMotionY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ////Log.v(TAG, "flipStarted" + flipStarted);

        if (flipStarted) {
//            Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> return false;");
            return false;
        }
//        if (bottomBar.isSourceSelectMode()) {
//            bottomBar.onTouchEvent(event);
//        }
        if (enlargedMode) {
//            Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " enlargedMode=" + enlargedMode + " -->  current.onTouchEvent(event);");
            current.onTouchEvent(event);
            return true;
        }


        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN: {
                mLastMotionX = mInitialMotionX = event.getX();
                mLastMotionY = mInitialMotionY = event.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mLastMotionX = event.getX();
                mLastMotionY = event.getY();


                if (mInitialMotionX == 0 && mInitialMotionY == 0) {
                    System.out.println("it works");
                    mInitialMotionX = event.getX();
                    mInitialMotionY = event.getY();
                }
                if (!flipStarted && !enlargedMode) {

                    final GestureUtil.FlipDirection flipDirection = GestureUtil.checkFlipDirection(mInitialMotionX, mInitialMotionY, mLastMotionX, mLastMotionY);

                    switch (flipDirection) {
                        case RIGHT:
//                            Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> RIGHT;");
                            MobclickAgent.onEvent(this, "FlipPage", "Right");
                            lastFlipDirection = ACTION_HORIZONTAL;
                            flipPage(false);
                            break;
                        case UP:
//                            Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> UP;");
                            MobclickAgent.onEvent(this, "FlipPage", "Up");
                            lastFlipDirection = ACTION_VERTICAL;
                            flipPage(true);
                            break;
                        case LEFT:
//                            Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> LEFT;");
                            MobclickAgent.onEvent(this, "FlipPage", "Left");
                            lastFlipDirection = ACTION_HORIZONTAL;
                            flipPage(true);
                            break;
                        case DOWN:
//                            Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> DOWN;");
                            MobclickAgent.onEvent(this, "FlipPage", "Down");
                            lastFlipDirection = ACTION_VERTICAL;
                            flipPage(false);
                            break;
                        case NONE:
                            return false;

                    }

//                    Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> No Gesture matched");

                }
                break;
            }

            case MotionEvent.ACTION_UP:

                mLastMotionX = event.getX();
                mLastMotionY = event.getY();


                if (!flipStarted && !enlargedMode) {

                    final GestureUtil.FlipDirection flipDirection = GestureUtil.checkFlipDirection(mInitialMotionX, mInitialMotionY, mLastMotionX, mLastMotionY);

                    switch (flipDirection) {
                        case RIGHT:
//                            Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> RIGHT;");
                            MobclickAgent.onEvent(this, "FlipPage", "Right");
                            lastFlipDirection = ACTION_HORIZONTAL;
                            flipPage(false);
                            break;
                        case UP:
//                            Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> UP;");
                            MobclickAgent.onEvent(this, "FlipPage", "Up");
                            lastFlipDirection = ACTION_VERTICAL;
                            flipPage(true);
                            break;
                        case LEFT:
//                            Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> LEFT;");
                            MobclickAgent.onEvent(this, "FlipPage", "Left");
                            lastFlipDirection = ACTION_HORIZONTAL;
                            flipPage(true);
                            break;
                        case DOWN:
//                            Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> DOWN;");
                            MobclickAgent.onEvent(this, "FlipPage", "Down");
                            lastFlipDirection = ACTION_VERTICAL;
                            flipPage(false);
                            break;
                        case NONE:
                            return false;

                    }

//                    Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> No Gesture matched");

                }
                break;

            default:

                break;
        }
//        Log.v(TAG, "** onTouchEvent() event.getAction()=" + event.getAction() + " flipStarted=" + flipStarted + " --> return true;");

        return true;
    }

    Handler handler = new Handler();

    private void flipPage(final boolean forward) {

        this.previousDirection = this.forward;
        this.forward = forward;

        if (!forward)
            decreasePageNo();

        int nextPageIndex = forward ? currentPageIndex + 1 : currentPageIndex;
//        Log.v(TAG, "debug flip");
        if (currentPageIndex == -1 && forward) {//we are first timer
            currentPageIndex++;
            container.addView(current, pageViewLayoutParamsFront);
            slideToNextPageAsynchronized();
        } else if (nextPageIndex > 0) {
            if (repo.getTotal() == currentPageIndex) {
                new AlarmSender(this.getApplicationContext()).sendInstantMessage(R.string.isLastPage);
                flipStarted = false;
                return;
            }
            if (current.isLastPage() && forward) {
                finishActivity();
                return;
            }
            slideToNextPageAsynchronized();
        } else {
            finishActivity();
        }
//        Log.v(TAG, "debug flip done");
    }

    private void finishActivity() {
        finish();
    }

    private void slideToNextPageAsynchronized() {
        flipStarted = true;
        new Thread(new Runnable() {
            public void run() {
                slideToNextPage();
            }
        }).start();
    }

    private void slideToNextPage() {
        Log.v(TAG, "debug slide to next");

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
//                if (next.isLastPage() && forward) {
//                    handler.post(new Runnable() {
//                        public void run() {
//                            showAnimation();
//                        }
//                    });
//                    return;
//                }
                boolean preparingWindowReady = preparingWindow.isLoaded();
                if (!preparingWindowReady && !preparingWindow.isSkip()) {
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
                    ////Log.v(TAG, "lastpage");
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


    private void renderNextPageIfNotRendered() {
        ThumbnailViewContainer renderingPageViewContainer;

        if (forward) {
            renderingPageViewContainer = next;
        } else {
            renderingPageViewContainer = previous;
        }
        renderingPageViewContainer.setVisibility(View.VISIBLE);
        if (renderingPageViewContainer.isRendered())
            return;

        for (ArticleView v : renderingPageViewContainer.getWeiboViews()) {
            v.renderBeforeLayout();
        }
        renderingPageViewContainer.renderBeforeLayout();
    }

    private void decreasePageNo() {
        currentPageIndex--;
    }

    private Animation buildFlipHorizonalAnimation(final boolean forward) {
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
//            if (previous != null)
//                previous.releaseResource();
            ThumbnailViewContainer tmp = current;
            previous = current;
            current = next;
            next = tmp;
        } else {
//            if (next != null)
//                next.releaseResource();
            ThumbnailViewContainer tmp = current;
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




    private void showAnimation() {
//        if (next != null && next.isLastPage() && forward) {
//            AlarmSender.sendInstantMessage(R.string.isLastPage, this);
//            flipStarted= false;
//            return;
//        }
        enlargedMode = false;
        renderNextPageIfNotRendered();
        //Log.d("ANI", "start animation");
        container.removeAllViews();
        if (current.isFirstPage()) {
            WebImageView webImageView = (WebImageView) current.findViewById(R.id.portrait);
            next.setVisibility(View.VISIBLE);
            currentPageIndex++;
            container.addView(current, pageViewLayoutParamsBack);
            container.addView(next, pageViewLayoutParamsFront);

            current.startAnimation(fadeInPageView);
            int translateY = MultiScreenSupport.getInstance(deviceInfo).getFirstPageTranslateY();
            Animation translate = new TranslateAnimation(Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, -80, Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, translateY);
            AnimationSet animationSet = new AnimationSet(true);
            Animation scale = new ScaleAnimation(1, 0.25f, 1, 0.25f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animationSet.setInterpolator(new AccelerateInterpolator());
            animationSet.addAnimation(scale);
            animationSet.addAnimation(translate);


            translate.setDuration(FADE_OUT_FIRST_PAGE_DURATION);
            scale.setDuration((long) (FADE_OUT_FIRST_PAGE_DURATION));
//            animationSet.setInterpolator(new AccelerateInterpolator());
            animationSet.setFillAfter(true);
            webImageView.setAnimation(animationSet);
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    bottomBar.findViewById(R.id.headerImage).setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });
            animationSet.startNow();
//            webImageView.startAnimation(scale);

        } else if (isWeiboMode() || (current.isLastPage() && !forward) || (next.isLastPage() && forward) || (next.getWrapperViews().size() < 2 && forward) || lastFlipDirection == ACTION_HORIZONTAL) {

            if (forward)
                next.setVisibility(View.VISIBLE);
            else
                previous.setVisibility(View.VISIBLE);
            if (forward) {
                currentPageIndex++;

                container.addView(current, pageViewLayoutParamsBack);
                container.addView(next, pageViewLayoutParamsFront);

//                current.setAnimationCacheEnabled(true);
                current.startAnimation(rotationForward);
            } else {

                container.addView(current, pageViewLayoutParamsFront);
                if (previous.getParent() != null)
                    ((ViewGroup) previous.getParent()).removeView(previous);
                container.addView(previous, pageViewLayoutParamsBack);

//                previous.setAnimationCacheEnabled(true);
                previous.startAnimation(rotationBackward);
            }
        } else {

            if (forward) {
                currentPageIndex++;
                container.addView(next, pageViewLayoutParamsFront);
            } else {
                container.addView(previous, pageViewLayoutParamsFront);
            }
            if (current.getParent() != null)
                ((ViewGroup) current.getParent()).removeView(current);
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
        fadeInPageView.setDuration(FADE_OUT_FIRST_PAGE_DURATION);
//        fadeInPageView.setInterpolator();
        fadeInPageView.setAnimationListener(fadeInAnimationListener);
    }

    private void buildFadeOutPageViewAnimation() {
        fadeOutPageView = AnimationUtils.loadAnimation(this, R.anim.left_in);
        fadeOutPageView.setAnimationListener(fadeOutAnimationListener);
    }

    private Animation buildFlipAnimation(final ViewGroup currentUpper, final ViewGroup currentBottom, final ViewGroup nextUpper, final ViewGroup nextBottom, final boolean forward) {
        final float centerY = getWindowManager().getDefaultDisplay().getHeight() / 2;
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


    public void hideBottomBar() {
        bottomBar.hide();
    }

    public void showBottomBar() {
        bottomBar.show();
    }

    public static final int PROMPT_OAUTH = 1;
    public static final int NAVIGATION = 2;

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = null;
        switch (id) {
            case PROMPT_INPROGRESS:
                ProgressDialog dialogProgress = new ProgressDialog(PageActivity.this);
                dialogProgress.setIcon(R.drawable.icon);
                dialogProgress.setMessage(this.getString(R.string.inprogress));
                dialogProgress.setCancelable(false);
                this.dialog = dialogProgress;
                break;
            case PROMPT_OAUTH:
                builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.commentneedsinaoauth)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FlipdroidApplications application = (FlipdroidApplications) getApplication();
                                OAuth oauth = new OAuth();
                                application.setOauth(oauth);
                                ////Log.v(TAG, "OAuthHolder.oauth" + application + oauth);
                                boolean result = oauth.RequestAccessToken(PageActivity.this, "flipdroid://SinaAccountSaver");
                                if (!result) {
                                    new AlarmSender(PageActivity.this.getApplicationContext()).sendInstantMessage(R.string.networkerror);
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                this.dialog = builder.create();

                break;
            case NAVIGATION:
                builder = new AlertDialog.Builder(this);
                LayoutInflater li = LayoutInflater.from(this);
                View v = li.inflate(R.layout.dialog_nav_title_view, null);

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
                btn_addshortcut.setText(R.string.addShortcut);

                btn_addshortcut.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        addShortcut();
                        if (dialog != null)
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
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {//拦截meu键事件
            showDialog(NAVIGATION);
            return true;
        }
        return super.onKeyUp(keyCode, event);
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


    private void addShortcut() {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra("duplicate", false);
        ComponentName comp = new ComponentName(this.getPackageName(), "." + this.getLocalClassName());
        Intent intent = new Intent(Intent.ACTION_MAIN).setComponent(comp);
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
