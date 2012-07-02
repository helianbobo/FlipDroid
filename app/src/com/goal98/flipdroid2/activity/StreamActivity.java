package com.goal98.flipdroid2.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ArrayAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.goal98.flipdroid2.R;
import com.goal98.flipdroid2.db.RSSURLDB;
import com.goal98.flipdroid2.db.RecommendSourceDB;
import com.goal98.flipdroid2.db.SourceContentDB;
import com.goal98.flipdroid2.db.SourceDB;
import com.goal98.flipdroid2.model.SourceUpdateManager;
import com.goal98.flipdroid2.model.cachesystem.CachedArticleSource;
import com.goal98.flipdroid2.model.cachesystem.SourceCache;
import com.goal98.flipdroid2.model.cachesystem.SourceUpdateable;
import com.goal98.flipdroid2.util.AlarmSender;
import com.goal98.flipdroid2.view.StreamPagerAdapter;
import com.goal98.tika.common.TikaConstants;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.srz.androidtools.autoloadlistview.OnNothingLoaded;
import com.srz.androidtools.autoloadlistview.PaginationLoaderAdapter;
import com.srz.androidtools.util.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-3-9
 * Time: 下午3:28
 * To change this template use File | Settings | File Templates.
 */
public class StreamActivity extends SherlockActivity implements SourceUpdateable, ActionBar.OnNavigationListener {
    private Handler handler = new Handler();
    private DeviceInfo deviceInfo;
    private ViewPager mViewPager;
    private List<ArticleAdapter> adapters = new ArrayList<ArticleAdapter>();
    private ArrayAdapter sourceAdapter;
    public static final int THEME = R.style.Theme_Sherlock;
    private ArticleAdapter mainStreamAdapter;
    private ArticleLoader currentLoaderService;
    private boolean backFromSelection;
    private String lastItemName;
    private boolean needRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.stream_by_source);
        deviceInfo = DeviceInfo.getInstance(this);

        HostSetter hostSetter = new HostSetter(this);
        hostSetter.setHost();
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        bindSourceSelectionAdapter();
        if (sourceAdapter.getItem(0) instanceof SourceItem) {
            SourceItem sourceItem = (SourceItem) sourceAdapter.getItem(0);
            if (sourceItem == null)
                return;

            String from = null;
//            if (sourceItem.getSourceType().equals(TikaConstants.TYPE_RSS))
                from = sourceItem.getSourceURL();
//            else
//                from = sourceItem.getCategory();

            String name = sourceItem.getSourceName();
            setupMainStream(from, name);
        }
    }

    private void setupEmptyList() {
        mViewPager.setVisibility(View.GONE);
        findViewById(R.id.vpcontainer).setBackgroundResource(R.drawable.bgnosource);
    }

    private void setupNoneEmptyList() {
        mViewPager.setVisibility(View.VISIBLE);
        findViewById(R.id.vpcontainer).setBackgroundDrawable(null);
    }


    private void setupMainStream(String from, String name) {
        final List<String> titles = new ArrayList<String>();

        titles.add(getString(R.string.allfeeds));
        titles.add(getString(R.string.myfavorite));

        final List<PullToRefreshListView> ptrs = new ArrayList<PullToRefreshListView>();

        addPage(ptrs, from, name, true, 0, 100);
        addFavoritePage(ptrs, null);


        final PagerAdapter mPagerAdapter = new StreamPagerAdapter(ptrs, titles, null);

//        addSourcePopup = addSourcePopupViewBuilder.buildAddSourcePopupView(StreamActivity.this);

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {//normal
                    getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                    getSupportActionBar().setTitle("");
                } else {
                    getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                    getSupportActionBar().setTitle(R.string.favorite);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (adapters.size() >= 1) {
                    for (int i = 0; i < adapters.size(); i++) {
                        final int finalI = i;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapters.get(finalI).forceLoad();
                            }
                        }, (i + 1) * 1000);
                    }
                }
            }
        });
        t.start();
        mPagerAdapter.notifyDataSetChanged();
    }

    private void addPage(List<PullToRefreshListView> ptrs, String from, String name, boolean isFirst, int inDaysFrom, int inDaysTo) {
        final PullToRefreshListView currentPullToRefresh = new PullToRefreshListView(this);

        currentLoaderService = new ArticleLoader(StreamActivity.this, 10, from, name, inDaysFrom, inDaysTo);

        int noDataView = 0;

        if (isFirst)
            noDataView = R.layout.add_more_source_view;
        else
            noDataView = -1;

        OnNothingLoaded nothingLoaded = new NoMoreArticleToLoad(currentPullToRefresh, currentLoaderService, handler);

        mainStreamAdapter = new ArticleAdapter(this, currentPullToRefresh.getAdapterView(), R.layout.lvloading, R.layout.stream_styled_article_view, currentLoaderService, noDataView, new View.OnClickListener() {
            public void onClick(View view) {
//                showSourceType(view);
            }
        }, nothingLoaded);

        currentPullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetDataTask(currentPullToRefresh, mainStreamAdapter, currentLoaderService, handler).execute();
            }
        });
        ptrs.add(currentPullToRefresh);
        if (isFirst)
            mainStreamAdapter.forceLoad();
    }

    private void addFavoritePage(List<PullToRefreshListView> ptrs, String from) {
        final PullToRefreshListView ptr = new PullToRefreshListView(this);

        final ArticleLoader loaderService = new ArticleLoader(StreamActivity.this, 10, from, true);

        int noDataView = R.layout.add_more_favorite_view;

        final ArticleAdapter myAdapter = new ArticleAdapter(this, ptr.getAdapterView(), R.layout.lvloading, R.layout.stream_styled_article_view, loaderService, noDataView, new View.OnClickListener() {
            public void onClick(View view) {
                //showSourceType(view);
            }
        }, null);
        adapters.add(myAdapter);
        ptr.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetDataTask(ptr, myAdapter, loaderService, handler).execute();
            }
        });
        ptrs.add(ptr);
    }

//    private void showSourceType(View view) {
//        int[] location = new int[2];
//        view.getLocationOnScreen(location);
//
//        if (mPopupWindow != null && mPopupWindow.isShowing()) {
//            mPopupWindow.dismiss();
//            return;
//        }
//
//        mPopupWindow = new PopupWindow(addSourcePopup, ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
//        mPopupWindow.setOutsideTouchable(true);
//        if (location[1] > deviceInfo.getHeight() / 2) {
//            mPopupWindow.showAsDropDown(view, 0, -view.getHeight());
//        } else {
//            mPopupWindow.showAsDropDown(view, 0, 0);
//        }
//    }

    public void notifyUpdating(CachedArticleSource cachedArticleSource) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyHasNew(CachedArticleSource cachedArticleSource) {
        mainStreamAdapter.forceLoad(false);
    }

    public void notifyNoNew(CachedArticleSource cachedArticleSource) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyUpdateDone(CachedArticleSource cachedArticleSource) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private int lastItemPosition = 0;

    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (backFromSelection && !needRefresh) {
            backFromSelection = false;
            return true;
        }

        if (sourceAdapter.getItem(itemPosition) instanceof SourceItem) {
            final SourceItem sourceItem = (SourceItem) sourceAdapter.getItem(itemPosition);


            if (currentLoaderService != null && sourceItem.getSourceName().equals(currentLoaderService.getName()))
                return true;
            needRefresh = false;
            backFromSelection = false;
            lastItemPosition = itemPosition;
            lastItemName = sourceItem.getSourceName();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String from = null;
//                    if (sourceItem.getSourceType().equals(TikaConstants.TYPE_RSS))
                        from = sourceItem.getSourceURL();
//                    else
//                        from = sourceItem.getCategory();
                    setupMainStream(from, sourceItem.getSourceName());
                }
            }, 300);
            return true;
        }
        return true;
    }


    private void bindSourceSelectionAdapter() {

        SourceDB sourceDB = new SourceDB(this);
        Cursor sourceCursor = sourceDB.findSourceByMultipleType(new String[]{TikaConstants.TYPE_RSS, TikaConstants.TYPE_FEATURED});
        if (sourceCursor.getCount() == 0) {
            sourceCursor.close();
            sourceAdapter = new ArrayAdapter(this, R.layout.nodataitem,
                    R.id.text, new String[]{getString(R.string.nodata)}
            );
            getSupportActionBar().setTitle(R.string.app_name);
            setupEmptyList();
        } else {
            sourceAdapter = new SourceItemArrayAdapter<SourceItem>(this, R.layout.source_item_mini, sourceDB, deviceInfo);
            sourceAdapter.setDropDownViewResource(R.layout.source_item_mini);

            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            getSupportActionBar().setListNavigationCallbacks(sourceAdapter, this);
            getSupportActionBar().setTitle("");

            if (sourceAdapter.getCount() > lastItemPosition) {
                SourceItem item = (SourceItem) sourceAdapter.getItem(lastItemPosition);
                String lastName = item.getSourceName();
                if (lastItemPosition != 0 && lastName.equals(lastItemName)) {
                    getSupportActionBar().setSelectedNavigationItem(lastItemPosition);
                } else {
                    getSupportActionBar().setSelectedNavigationItem(0);
                    needRefresh = true;
                }
            } else {
                getSupportActionBar().setSelectedNavigationItem(0);
                needRefresh = true;
            }


            setupNoneEmptyList();
        }
        sourceDB.close();


    }

//    private void animateTitleTo(String sourceName) {
//        int next = (titleSwitcher.getDisplayedChild() + 1) % 2;
//        ((TextView) titleSwitcher.getChildAt(next)).setText(sourceName);
//
//        titleSwitcher.setDisplayedChild(next);
//    }


    private void hideWindow() {
//        StreamActivity.this.sourceSelection.startAnimation(hideaction);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Used to put dark icons on light action bar
        boolean isLight = THEME == R.style.Theme_Sherlock_Light;

        SubMenu addSubMenu = menu.addSubMenu(R.string.addbtn);
        addSubMenu.setIcon(R.drawable.source_edit);
        addSubMenu.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        addSubMenu.add(R.string.rssfeeds).setIcon(R.drawable.rss);
        addSubMenu.add(R.string.featured).setIcon(R.drawable.icon);

        SubMenu configSubMenu = menu.addSubMenu(R.string.config);
        configSubMenu.setIcon(R.drawable.settings);
        configSubMenu.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

    private String lastSourceName;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(this.getString(R.string.rssfeeds))) {
            Intent intent = new Intent(StreamActivity.this, RSSSourceSelectionActivity.class);
            intent.putExtra("type", TikaConstants.TYPE_RSS);
            StreamActivity.this.startActivityForResult(intent, 100);
        }
        if (item.getTitle().equals(this.getString(R.string.featured))) {
            Intent intent = new Intent(StreamActivity.this, FeaturedSourceSelectionActivity.class);
            StreamActivity.this.startActivityForResult(intent, 100);
        }
        if (item.getTitle().equals(this.getString(R.string.config))) {
            Intent intent = new Intent(StreamActivity.this, ConfigActivity.class);
            StreamActivity.this.startActivity(intent);
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            bindSourceSelectionAdapter();
        }
        backFromSelection = true;
    }
}

class NoMoreArticleToLoad implements OnNothingLoaded {
    private PullToRefreshListView currentPullToRefresh;
    private PaginationLoaderAdapter currentAdapter;
    private ArticleLoader currentLoaderService;
    private Handler handler;

    public NoMoreArticleToLoad(PullToRefreshListView currentPullToRefresh, ArticleLoader currentLoaderService, Handler handler) {
        this.currentPullToRefresh = currentPullToRefresh;
        this.currentLoaderService = currentLoaderService;
        this.handler = handler;
    }


    public void setAdapter(PaginationLoaderAdapter currentAdapter) {
        this.currentAdapter = currentAdapter;
    }

    @Override
    public void onNothingLoaded() {
        currentPullToRefresh.setRefreshing();
    }
}

class GetDataTask extends AsyncTask<Void, Void, String[]> {
    private PullToRefreshListView mPullRefreshListView;
    private PaginationLoaderAdapter adapter;
    private ArticleLoader loaderService;
    private Handler handler;
    private SourceUpdateManager updateManager;
    private SourceDB sourceDB;
    private RSSURLDB rssurlDB;
    private boolean failed;

    public GetDataTask(PullToRefreshListView mPullRefreshListView, PaginationLoaderAdapter adapter, ArticleLoader loaderService, Handler handler) {
        this.mPullRefreshListView = mPullRefreshListView;
        this.adapter = adapter;
        this.loaderService = loaderService;
        this.handler = handler;
        sourceDB = new SourceDB(adapter.getContext());
        rssurlDB = new RSSURLDB(adapter.getContext());
    }

    int countBeforeUpdate = 0;

    @Override
    protected String[] doInBackground(Void... params) {
        adapter.reset();
        loaderService.reset();

        countBeforeUpdate = rssurlDB.getCount();

        updateManager = new SourceUpdateManager(rssurlDB, sourceDB, new SourceCache(new SourceContentDB(adapter.getContext())), (SourceUpdateable) adapter.getContext(), RecommendSourceDB.getInstance(adapter.getContext()));
        try {
            if (loaderService.getName() != null) {
                updateManager.updateContentByName(true, loaderService.getName());
            } else
                updateManager.updateContent(true);
        } catch (Exception e) {
            failed = true;
            sourceDB.close();
            rssurlDB.close();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
        if(failed){
            new AlarmSender(adapter.getContext()).sendInstantMessage(R.string.failed);
            return;
        }

        try {
            int countAfterUpdate = rssurlDB.getCount();//performance
            final int updatedCount = countAfterUpdate - countBeforeUpdate;

            mPullRefreshListView.onRefreshComplete();

            super.onPostExecute(result);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String updated = adapter.getContext().getString(R.string.updated);
                    updated = updated.replaceAll("%", "" + updatedCount);
                    new AlarmSender(adapter.getContext()).sendInstantMessage(updated);
                }
            });
            updateManager.updateSourceList(false);
        } finally {
            sourceDB.close();
            rssurlDB.close();
        }
    }
}
