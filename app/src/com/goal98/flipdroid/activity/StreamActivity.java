package com.goal98.flipdroid.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.goal98.flipdroid.R;
import com.goal98.flipdroid.db.RSSURLDB;
import com.goal98.flipdroid.db.RecommendSourceDB;
import com.goal98.flipdroid.db.SourceContentDB;
import com.goal98.flipdroid.db.SourceDB;
import com.goal98.flipdroid.model.SourceUpdateManager;
import com.goal98.flipdroid.model.cachesystem.CachedArticleSource;
import com.goal98.flipdroid.model.cachesystem.SourceCache;
import com.goal98.flipdroid.model.cachesystem.SourceUpdateable;
import com.goal98.flipdroid.util.AlarmSender;
import com.goal98.flipdroid.view.StreamPagerAdapter;
import com.goal98.tika.common.TikaConstants;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.srz.androidtools.util.DeviceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jleo
 * Date: 12-3-9
 * Time: 下午3:28
 * To change this template use File | Settings | File Templates.
 */
public class StreamActivity extends SherlockActivity implements SourceUpdateable, ActionBar.OnNavigationListener {
    private Handler handler = new Handler();
    private AddSourcePopupViewBuilder addSourcePopupViewBuilder;
    private DeviceInfo deviceInfo;
    private ViewPager mViewPager;
    private View addSourcePopup;
    private PopupWindow mPopupWindow;
    private List<ArticleAdapter> adapters = new ArrayList<ArticleAdapter>();
    private boolean windowOpened;
    private Animation hideaction;
    //    private LinearLayout sourceSelection;
    private ListView listView = null;
    private ArrayAdapter sourceAdapter;
    public static final int THEME = R.style.Theme_Sherlock;
    //    private ViewSwitcher titleSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.

        setContentView(R.layout.stream_by_source);
        deviceInfo = DeviceInfo.getInstance(this);
        addSourcePopupViewBuilder = new AddSourcePopupViewBuilder(StreamActivity.this);

        final Animation showaction = AnimationUtils.loadAnimation(this, R.anim.stay_in);
        showaction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                windowOpened = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
//        //隐藏动画
//        hideaction = AnimationUtils.loadAnimation(this, R.anim.stay_out);
//        hideaction.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                StreamActivity.this.sourceSelection.setVisibility(View.GONE);
//                windowOpened = false;
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });

        HostSetter hostSetter = new HostSetter(this);
        hostSetter.setHost();
        hostSetter = null;

//        StreamActivity.this.sourceSelection = (LinearLayout) findViewById(R.id.source_selection);
//        listView = (ListView) findViewById(R.id.source_list);
        bindSourceSelectionAdapter();

//        titleSwitcher = (ViewSwitcher) findViewById(R.id.source_selection_trigger);
//        titleSwitcher.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!windowOpened) {
//                    StreamActivity.this.sourceSelection.setVisibility(View.VISIBLE);
//                    listView.setSelectionAfterHeaderView();
//                    StreamActivity.this.sourceSelection.startAnimation(showaction);
//                } else {
//                    hideWindow();
//                }
//            }
//        });
//        titleSwitcher.setDisplayedChild(0);

        String from = null;
        setupMainStream(from);
    }


    private void setupMainStream(String from) {
        final List<String> titles = new ArrayList<String>();

        titles.add(getString(R.string.allfeeds));
        titles.add(getString(R.string.myfavorite));

        final List<PullToRefreshListView> ptrs = new ArrayList<PullToRefreshListView>();

        addPage(ptrs, from, null, true, 0, 3);
        addFavoritePage(ptrs, null);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        final PagerAdapter mPagerAdapter = new StreamPagerAdapter(ptrs, titles, null);

        addSourcePopup = addSourcePopupViewBuilder.buildAddSourcePopupView(StreamActivity.this);

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onPageSelected(int i) {
//                animateTitleTo(mPagerAdapter.getPageTitle(i).toString());
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (adapters.size() > 1) {
                    for (int i = 1; i < adapters.size(); i++) {
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
        final PullToRefreshListView ptr = new PullToRefreshListView(this);

        final ArticleLoader loaderService = new ArticleLoader(StreamActivity.this, 10, from, name, inDaysFrom, inDaysTo);

        int noDataView = 0;

        if (isFirst)
            noDataView = R.layout.add_more_source_view;
        else
            noDataView = -1;

        final ArticleAdapter adapter = new ArticleAdapter(this, ptr.getAdapterView(), R.layout.lvloading, R.layout.stream_styled_article_view, loaderService, noDataView, new View.OnClickListener() {
            public void onClick(View view) {
                showSourceType(view);
            }
        });
        adapters.add(adapter);
        ptr.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetDataTask(ptr, adapter, loaderService).execute();
            }
        });
        ptrs.add(ptr);
        if (isFirst)
            adapter.forceLoad();
    }

    private void addFavoritePage(List<PullToRefreshListView> ptrs, String from) {
        final PullToRefreshListView ptr = new PullToRefreshListView(this);

        final ArticleLoader loaderService = new ArticleLoader(StreamActivity.this, 10, from, true);

        int noDataView = -1;

        final ArticleAdapter adapter = new ArticleAdapter(this, ptr.getAdapterView(), R.layout.lvloading, R.layout.stream_styled_article_view, loaderService, noDataView, new View.OnClickListener() {
            public void onClick(View view) {
                showSourceType(view);
            }
        });
        adapters.add(adapter);
        ptr.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetDataTask(ptr, adapter, loaderService).execute();
            }
        });
        ptrs.add(ptr);
    }

    private void showSourceType(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            return;
        }

        mPopupWindow = new PopupWindow(addSourcePopup, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
        if (location[1] > deviceInfo.getHeight() / 2) {
            mPopupWindow.showAsDropDown(view, 0, -view.getHeight());
        } else {
            mPopupWindow.showAsDropDown(view, 0, 0);
        }
    }

    public void notifyUpdating(CachedArticleSource cachedArticleSource) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyHasNew(CachedArticleSource cachedArticleSource) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyNoNew(CachedArticleSource cachedArticleSource) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void notifyUpdateDone(CachedArticleSource cachedArticleSource) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        final SourceItem sourceItem = (SourceItem) sourceAdapter.getItem(itemPosition);
        hideWindow();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupMainStream(sourceItem.getSourceURL());
//                animateTitleTo(sourceItem.getSourceName());
            }
        }, 500);
        return true;
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        private PullToRefreshListView mPullRefreshListView;
        private ArticleAdapter adapter;
        private ArticleLoader loaderService;

        public GetDataTask(PullToRefreshListView mPullRefreshListView, ArticleAdapter adapter, ArticleLoader loaderService) {
            this.mPullRefreshListView = mPullRefreshListView;
            this.adapter = adapter;
            this.loaderService = loaderService;
        }

        int countBeforeUpdate = 0;

        @Override
        protected String[] doInBackground(Void... params) {
            adapter.reset();
            loaderService.reset();

            SourceDB sourceDB = new SourceDB(getApplicationContext());
            RSSURLDB rssurlDB = new RSSURLDB(getApplicationContext());
            countBeforeUpdate = rssurlDB.getCount();

            try {
                SourceUpdateManager updateManager = new SourceUpdateManager(rssurlDB, sourceDB, new SourceCache(new SourceContentDB(StreamActivity.this)), StreamActivity.this, RecommendSourceDB.getInstance(StreamActivity.this));
                if (loaderService.getName() != null) {
                    updateManager.updateContentByName(true, loaderService.getName());
                } else
                    updateManager.updateContent(true);
            } finally {
                sourceDB.close();
                rssurlDB.close();
            }
            adapter.forceLoad(false);
            return null;
        }


        @Override
        protected void onPostExecute(String[] result) {
            RSSURLDB rssurlDB = new RSSURLDB(getApplicationContext());
            try {
                int countAfterUpdate = rssurlDB.getCount();
                final int updatedCount = countAfterUpdate - countBeforeUpdate;

                mPullRefreshListView.onRefreshComplete();

                super.onPostExecute(result);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String updated = StreamActivity.this.getString(R.string.updated);
                        updated = updated.replaceAll("%", "" + updatedCount);
                        new AlarmSender(StreamActivity.this.getApplicationContext()).sendInstantMessage(updated);
                    }
                });
            } finally {
                rssurlDB.close();
            }
        }
    }

    private void bindSourceSelectionAdapter() {

        SourceDB sourceDB = new SourceDB(this);
        Cursor sourceCursor = sourceDB.findAll();
        if (sourceCursor.getCount() == 0) {
            sourceCursor.close();
            Map<String, String> noData = new HashMap<String, String>();
            sourceAdapter = new ArrayAdapter(this, R.layout.nodataitem,
                    R.id.text, new String[]{getString(R.string.nodata)}
            );
        } else {
            sourceAdapter = new SourceItemArrayAdapter<SourceItem>(this, R.layout.source_item_mini, sourceDB, deviceInfo);
        }
        sourceDB.close();
//        listView.setAdapter(sourceAdapter);
//        final ListAdapter finalAdapter = sourceAdapter;
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                final SourceItem sourceItem = (SourceItem) finalAdapter.getItem(i);
//                hideWindow();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        setupMainStream(sourceItem.getSourceURL());
//                        animateTitleTo(sourceItem.getSourceName());
//                    }
//                }, 500);
//
//            }
//        });

//        Context context = getSupportActionBar().getThemedContext();
//        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.locations, R.layout.sherlock_spinner_item);

        sourceAdapter.setDropDownViewResource(R.layout.source_item_mini);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(sourceAdapter, this);

        getSupportActionBar().setTitle("");
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

        SubMenu addSubMenu = menu.addSubMenu("Add");
        addSubMenu.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        addSubMenu.add(R.string.rssfeeds).setIcon(R.drawable.rss);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(this.getString(R.string.rssfeeds))) {
            Intent intent = new Intent(StreamActivity.this, RSSSourceSelectionActivity.class);
            intent.putExtra("type", TikaConstants.TYPE_RSS);
            StreamActivity.this.startActivityForResult(intent, 100);
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            bindSourceSelectionAdapter();
        }
    }
}
