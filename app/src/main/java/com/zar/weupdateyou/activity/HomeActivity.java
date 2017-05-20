package com.zar.weupdateyou.activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kgom.refresh.PullToRefreshBase;
import com.kgom.refresh.PullToRefreshRecyclerView;
import com.kgom.wuy.R;
import com.mopub.nativeads.MoPubNativeAdPositioning;
import com.zar.weupdateyou.adapter.FilterAdapter;
import com.zar.weupdateyou.adapter.NewsRecycleAdapter;
import com.zar.weupdateyou.doc.Constants;
import com.zar.weupdateyou.doc.ENUM;
import com.zar.weupdateyou.doc.UserInfo;
import com.zar.weupdateyou.model.FilterItem;
import com.zar.weupdateyou.model.NewsItem;
import com.zar.weupdateyou.model.NewsModel;
import com.zar.weupdateyou.model.UserModel;
import com.zar.weupdateyou.receiver.AlarmManagerBroadcastReceiver;
import com.zar.weupdateyou.service.HttpServiceManager;
import com.zar.weupdateyou.service.ServiceListener;
import com.zar.weupdateyou.view.AddSourceMenuDialog;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.androidbucket.utils.imageprocess.ABShape;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by KJS on 11/14/2016.
 */
public class HomeActivity extends BaseActivity implements NewsRecycleAdapter.NewsItemListener, ServiceListener, RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {

    final static int SEARCH_ALL = 0;
    final static int SEARCH_TAG = 1;
    final static int SEARCH_SOURCE = 2;
    final static int SORT_TIME =0;
    final static int SORT_SHARES = 1;



    PullToRefreshRecyclerView pullToRefreshRecyclerView;
    MoPubRecyclerAdapter myMoPubAdapter;
    RecyclerView newsList;

    private ArrayList<NewsItem> newsItems;

    private HashMap<String, Integer> tagMap;
    private HashMap<String, Integer> sourceMap;
    private int currentSearchType;
    private NewsRecycleAdapter newsRecycleAdapter;
    private Map<String, Integer> sections;
    private HttpServiceManager serviceManager;
    private ProgressDialog progressDialog = null;
    private UserInfo userInfo;
    private Handler mHandler;

    private DrawerLayout drawer;
    private LinearLayout drawMenu;

    private ListView filterView;
    private ArrayList<FilterItem> filterItems;
    private FilterAdapter filterAdapter;

    private DrawerArrowDrawable drawerArrow;
    //private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;

    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private RapidFloatingActionContentLabelList rfaContent;


    private int currentSortType = SORT_TIME;
    private int currentFilterType = SEARCH_ALL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setToolBar();
        attachHandle();
        if (savedInstanceState != null) {
            UserModel userModel = new UserModel();
			userInfo = UserInfo.getInstance();
            userModel.mUserName = userInfo.getUserName(this);
            userModel.mPassword = userInfo.getPassword(this);
            userModel.mSecret = userInfo.getSecret(this);
            UserInfo.getInstance().mAccount = userModel;
            UserInfo.getInstance().newsModels = application.GetDB().getNews();
        }
        initialize();
        configFAB();
        setupAlarm();
        notificationClear();
        if (userInfo.newsModels != null && userInfo.newsModels.size() > 0) {
            analysisData();
            updateList(SEARCH_ALL, "");
        } else {
            userInfo.newsModels = application.GetDB().getNews();
            analysisData();
            updateList(SEARCH_ALL, "");
            userInfo.shouldRefresh = false;
           /* final PullToRefreshRecyclerView layout = (PullToRefreshRecyclerView)findViewById(R.id.newsList);
            ViewTreeObserver vto = layout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    loadData();
                    pullToRefreshRecyclerView.setRefreshing();

                }
            });*/
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData();
                    pullToRefreshRecyclerView.setRefreshing();
                }
            },700);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    public void onClick(View v) {
    }

    @Override
    public void onResume() {

        super.onResume();

        final PullToRefreshRecyclerView layout = (PullToRefreshRecyclerView)findViewById(R.id.newsList);
     /*   ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);


            }
        });*/

        if (userInfo.settingChanged) {
            loadData();
            userInfo.settingChanged = false;
            pullToRefreshRecyclerView.setRefreshing();

        } else if( userInfo.shouldRefresh) {
            loadData();
            pullToRefreshRecyclerView.setRefreshing();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_more:
                View menuItemView = findViewById(R.id.action_more);
                onMenuClick(menuItemView);
                break;
            case R.id.action_share:
                shareTextUrl();
                break;
        }
        return true;
    }

    private void setToolBar() {
        toolbar = (Toolbar) findViewById(R.id.Toolbar);
        TextView mTitleFirst = (TextView) toolbar.findViewById(R.id.toolbar_title_first);
        Typeface Holla = Typeface.createFromAsset(getAssets(), "TypographerRotunda.ttf");

        mTitleFirst.setTypeface(Holla);

        TextView mTitleLeft = (TextView) toolbar.findViewById(R.id.toolbar_title_left);
        Typeface Robert = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        mTitleLeft.setTypeface(Robert);
        toolbar.setTitle(R.string.app_title);
        toolbar.setNavigationIcon(R.drawable.menu_ic);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFilterList(SEARCH_ALL);
                if(drawer.isDrawerOpen(drawMenu))
                   drawer.closeDrawer(drawMenu);
                else
                    drawer.openDrawer(drawMenu);

            }
        });
    }

    private void attachHandle() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawMenu = (LinearLayout) findViewById(R.id.left_nav);
        filterView = (ListView) findViewById(R.id.left_nav_list);

        rfaLayout = (RapidFloatingActionLayout) findViewById(R.id.label_list_sample_rfal);
        rfaButton = (RapidFloatingActionButton) findViewById(R.id.label_list_sample_rfab);


        pullToRefreshRecyclerView = (PullToRefreshRecyclerView) findViewById(R.id.newsList);
        newsList = pullToRefreshRecyclerView.getRefreshableView();
        newsList.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        newsList.setLayoutManager(linearLayoutManager);
    }

    private void initialize() {
        userInfo = UserInfo.getInstance();
        serviceManager = HttpServiceManager.getInstance();

        drawerArrow = new DrawerArrowDrawable(this);
        drawerArrow.setColor(getResources().getColor(R.color.color_white));
       /* mDrawerToggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };*/


       // drawer.setDrawerListener(mDrawerToggle);
        //mDrawerToggle.syncState();

        filterItems = new ArrayList<FilterItem>();
        filterAdapter = new FilterAdapter(this, filterItems);
        filterView.setAdapter(filterAdapter);
        filterView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FilterItem item = (FilterItem) filterAdapter.getItem(position);
                if (item.type == 1)
                    return;
                updateList(item.filterType, item.title);
                currentFilterType = item.filterType;
                refreshFAB();
                drawer.closeDrawer(drawMenu);
               // mDrawerToggle.onDrawerClosed(drawMenu);
            }
        });


        //add source section
        FilterItem source = new FilterItem();
        source.type = 1;
        source.title = getString(R.string.source);
        filterItems.add(source);

        //add tag section
        FilterItem tag = new FilterItem();
        tag.type = 1;
        tag.title = getString(R.string.tag);
        filterItems.add(tag);

        newsItems = new ArrayList<NewsItem>();
        sections = new HashMap<String, Integer>();
        tagMap = new HashMap<String, Integer>();
        sourceMap = new HashMap<String, Integer>();

        newsRecycleAdapter = new NewsRecycleAdapter(this, newsItems, sections);

        myMoPubAdapter = new MoPubRecyclerAdapter(this, newsRecycleAdapter, MoPubNativeAdPositioning.clientPositioning().enableRepeatingPositions(5));


        ViewBinder viewBinder = new ViewBinder.Builder(R.layout.facebook_ad_listview_item)
                .iconImageId(R.id.icon_desc)
                .titleId(R.id.nativeAdTitle)
                .textId(R.id.nativeAdBody)
                .callToActionId(R.id.nativeAdCallToAction)
                .build();

        MoPubStaticNativeAdRenderer myRenderer = new MoPubStaticNativeAdRenderer(viewBinder);

        myMoPubAdapter.registerAdRenderer(myRenderer);
        mHandler = new Handler();
        newsRecycleAdapter.setListener(this);
        newsList.setAdapter(myMoPubAdapter);
        pullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullToRefreshRecyclerView.setScrollingWhileRefreshingEnabled(true);
        pullToRefreshRecyclerView.setShowViewWhileRefreshing(true);
        pullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                loadData();

            }
        });
        myMoPubAdapter.loadAds(getString(R.string.unit_id));

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                if(myMoPubAdapter.isAd(position)) {
                    return super.getSwipeDirs(recyclerView, viewHolder);
                }
                position = myMoPubAdapter.getOriginalPosition(position);
                if( newsRecycleAdapter.getItem(position).type != 0)
                    return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
                if(myMoPubAdapter.isAd(position)) {
                    myMoPubAdapter.removeAd(position);
                    return;
                }
                position = myMoPubAdapter.getOriginalPosition(position);
                NewsItem item = newsRecycleAdapter.getItem(position);
                String section = newsRecycleAdapter.getSection(position);
                item.read = section == Constants.READ ? "0" : "1";
                newsRecycleAdapter.moveItem(position, (section == Constants.READ ? Constants.UNREAD : Constants.READ));
                HomeActivity.this.moveItemInModels(item.originalIndex, (section == Constants.READ ? Constants.UNREAD : Constants.READ));
                analysisData();
                serviceManager.serviceSwipeNews(UserInfo.getInstance().mAccount, (section != Constants.READ), item.link, HomeActivity.this);

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(newsList);



    }

    private void configFAB() {
        rfaContent = new RapidFloatingActionContentLabelList(this);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);

        List<RFACLabelItem> items = new ArrayList<RFACLabelItem>();
        String[] strList = getResources().getStringArray(R.array.fab_array);
        int[] resList = getResources().getIntArray(R.array.res_array);
        int index = 0;
        for(String title : strList)
        {
            items.add(new RFACLabelItem<Integer>()
                    .setLabel(title)
                    .setResId(getResourceId(index))
                    .setIconNormalColor(getColorOfItem(index))
                    .setIconPressedColor(getColorOfItem(index))
                    .setLabelColor(Color.WHITE)
                    .setLabelSizeSp(14)
                    .setLabelBackgroundDrawable(ABShape.generateCornerShapeDrawable(getColorOfItem(index), ABTextUtil.dip2px(this, 4)))
                    .setWrapper(1)
            );
            index++;
        }


        rfaContent
                .setItems(items)
                .setIconShadowRadius(ABTextUtil.dip2px(this, 5))
                .setIconShadowColor(0xff000000)
                .setIconShadowDy(ABTextUtil.dip2px(this, 5))
        ;

        rfabHelper = new RapidFloatingActionHelper(
                this,
                rfaLayout,
                rfaButton,
                rfaContent
        ).build();

    }
    private void refreshFAB() {
        for(int i=0; i<rfaContent.getItems().size(); i++) {
            rfaContent.getItems().get(i).setIconNormalColor(getColorOfItem(i));
            rfaContent.getItems().get(i).setIconPressedColor(getColorOfItem(i));
            rfaContent.getItems().get(i).setLabelBackgroundDrawable(ABShape.generateCornerShapeDrawable(getColorOfItem(i), ABTextUtil.dip2px(this, 4)));

        }
        rfaContent.refreshItems();
    }

    private int getResourceId(int index)
    {
        switch (index)
        {
            case 0:
                return R.drawable.ic_tag;
            case 1:
                return R.drawable.ic_sharp;
            case 2:
                return R.drawable.ic_query_builder_white_24dp;
            case 3:
                return R.drawable.ic_share;
            case 4:
                return R.drawable.ic_close_white_24dp;
        }
        return 0;
    }

    private int getColorOfItem(int index)
    {
        switch (index)
        {
            case 0: {
                if (currentFilterType == SEARCH_TAG)
                    return getResources().getColor(R.color.color_green);
            }
                break;
            case 1: {
                if (currentFilterType == SEARCH_SOURCE)
                    return getResources().getColor(R.color.color_green);
            }
                break;
            case 2: {
                if (currentSortType == SORT_TIME)
                    return getResources().getColor(R.color.color_green);
            }
                break;
            case 3: {
                if (currentSortType == SORT_SHARES)
                    return getResources().getColor(R.color.color_green);
            }
                break;

        }
        return getResources().getColor(R.color.fab_back_color);
    }
    protected void onPause() {
        super.onPause();
        UserInfo.getInstance().previousActivity = this.getClass().getSimpleName();


    }

    public void onMenuClick(final View v) {
        // We need to post a Runnable to show the popup to make sure that the PopupMenu is
        // correctly positioned. The reason being that the view may change position before the
        // PopupMenu is shown.
        v.post(new Runnable() {
            @Override
            public void run() {
                showPopupMenu(v);
            }
        });
    }

    // BEGIN_INCLUDE(show_popup)
    private void showPopupMenu(View view) {
        // Retrieve the clicked item from view's tag

        // Create a PopupMenu, giving it the clicked view for an anchor
        PopupMenu popup = new PopupMenu(this, view);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.feedlist_popup_menu, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.menu_manage_source:
                        openManageSource();
                        break;
                    case R.id.menu_sign_out:
                        signOut();
                        break;
                }
                return false;
            }
        });

        // Finally show the PopupMenu
        popup.show();
    }

    private void openAddSourceDialog() {
        final AddSourceMenuDialog dialogView = new AddSourceMenuDialog(this);
        dialogView.setCanceledOnTouchOutside(true);
        dialogView.showDialog();
        dialogView.setListener(new AddSourceMenuDialog.AddSourceMenuListener() {
            @Override
            public void onMenuItemClick(String menuItem) {
                if (menuItem.equals(getString(R.string.news)))
                    openPopularSource();
                if (menuItem.equals(getString(R.string.web)))
                    openWebAdd();
                if (menuItem.equals(getString(R.string.youtub)))
                    openYoutubeAdd();
            }


        });
    }

    private void signOut() {
        userInfo.mAccount.mPassword = "";
        userInfo.mAccount.mUserName = "";
        userInfo.mAccount.mSecret = "";
        userInfo.setPassword(this, "");
        userInfo.setSecret(this, "");
        userInfo.setPassword(this, "");
        userInfo.newsModels.clear();
        application.GetDB().removeAllNews();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void loadData() {
        serviceManager.serviceGetNews(UserInfo.getInstance().mAccount, this);
        userInfo.shouldRefresh = false;
        currentFilterType = SEARCH_ALL;
        refreshFAB();

    }

    private void openEditSource() {
        Intent intent = new Intent(this, SettingListActivity.class);
        intent.putExtra("type", "edit");
        startActivity(intent);
    }

    private void openManageSource(){
        Intent intent = new Intent(this, SettingListActivity.class);
        intent.putExtra("type", "manage");
        startActivity(intent);
    }

    private void openRemoveSource() {
        Intent intent = new Intent(this, SettingListActivity.class);
        intent.putExtra("type", "remove");
        startActivity(intent);
    }

    private void openWebAdd() {
        Intent intent = new Intent(this, SaveSourceActivity.class);
        intent.putExtra("type", "web");
        intent.putExtra("source", "web");
        intent.putExtra("title", "Web");
        intent.putExtra("top", "0");
        startActivity(intent);
    }

    private void openYoutubeAdd() {
        Intent intent = new Intent(this, SaveSourceActivity.class);
        intent.putExtra("type", "youtube");
        intent.putExtra("source", "youtube");
        intent.putExtra("title", "Youtube");
        intent.putExtra("top", "0");
        startActivity(intent);
    }

    private void openPopularSource() {
        Intent intent = new Intent(this, PopularSourceActivity.class);
        startActivity(intent);
    }

    private void openArticle(int index) {

        Glide.with(this)
                .load(userInfo.newsModels.get(index).mImageLink)
                .preload();


        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra("NewsModelIndex", index);
        startActivity(intent);
    }


    @Override
    public void onTagPressed(String tagname) {
        updateList(SEARCH_TAG, tagname);
        //showToast(this, tagname);
        currentFilterType = SEARCH_TAG;
        refreshFAB();
    }

    @Override
    public void onSourcePressed(String sourcename) {
        updateList(SEARCH_SOURCE, sourcename);
        // showToast(this, sourcename);
        currentFilterType = SEARCH_SOURCE;
        refreshFAB();
    }

    @Override
    public void onItemClick(View v) {
        final int pos = newsList.getChildLayoutPosition(v);
        final int position = myMoPubAdapter.getOriginalPosition(pos);
        NewsItem item = newsRecycleAdapter.getItem(position);
        if (item.type == 0) {
            String section = newsRecycleAdapter.getSection(position);
            if (section == Constants.UNREAD) {
                // serviceManager.serviceRead(UserInfo.getInstance().mAccount,item.guid,null);
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        NewsItem item = newsRecycleAdapter.getItem(position);
                        item.read = "1";
                        newsRecycleAdapter.moveItem(position, Constants.READ);
                        HomeActivity.this.moveItemInModels(item.originalIndex, Constants.READ);
                        analysisData();

                    }
                }, 50);
                serviceManager.serviceRead(UserInfo.getInstance().mAccount, item.link, null);

            }

            openArticle(item.originalIndex);
        }
        if(item.type == 2)
        {
            openAddSourceDialog();
        }
    }


    @Override
    public void OnSuccess(Object response, ENUM.SERVICE_TYPE type) {
        if (type == ENUM.SERVICE_TYPE.GET_NEWS) {
            userInfo.newsModels = (ArrayList<NewsModel>) response;
            analysisData();
            updateList(SEARCH_ALL, "");
            userInfo.settingChanged = false;
            syncData();

            UserInfo.getInstance().previousTime = System.currentTimeMillis();


        }
    }


    private void updateList(int searchType, String key) {

        if (userInfo.newsModels == null)
            return;
        //searchStack.add(new SearchItem(searchType, key));
        currentSearchType = searchType;
        ArrayList<Integer> readIndexs = new ArrayList<Integer>();
        ArrayList<Integer> unreadIndexs = new ArrayList<Integer>();
        int index = 0;
        for (NewsModel model : userInfo.newsModels) {
            if (model.mRead.equals("0"))
                unreadIndexs.add(index);
            else if (model.mRead.equals("1"))
                readIndexs.add(index);
            index++;
        }

        newsItems.clear();
        sections.clear();
        newsRecycleAdapter.notifyItemRangeRemoved(0, newsItems.size());
        //add tap to add source
        if(checkIfShowAddSourceTab()) {
            NewsItem addSourceTab = new NewsItem();
            addSourceTab.type = 2;
            newsItems.add(addSourceTab);
        }
        //add unread section
        sections.put(Constants.UNREAD, newsItems.size());

        NewsItem unreadSection = new NewsItem();
        unreadSection.type = 1;
        unreadSection.title = Constants.UNREAD;
        newsItems.add(unreadSection);

        //add unread items
        for (Integer i : unreadIndexs) {

            NewsModel model = userInfo.newsModels.get(i);
            if (searchType == SEARCH_TAG && (!model.mTag.equals(key)))
                continue;
            if (searchType == SEARCH_SOURCE && (!model.mFound.equals(key)))
                continue;

            NewsItem newsItem = new NewsItem();

            newsItem.type = 0;
            newsItem.tag = model.mTag;
            newsItem.title = model.mTitle;
            newsItem.description = model.mDescription;
            newsItem.source = model.mFound;
            newsItem.guid = model.mGuid;
            newsItem.link = model.mLink;
            newsItem.imgLink = model.mLocalImageLink;
            newsItem.shares = model.mShares;
            newsItem.isHot = model.mYourTopStories.equals("true") ? true : false;
            newsItem.timeText = model.mTimeArray.getTimeString();
            newsItem.originalIndex = i;
            newsItem.read = model.mRead;
            newsItem.timeDiff = model.mTimeArray.getTimeDiff();
            newsItems.add(newsItem);
        }

        //add read section
        sections.put(Constants.READ, newsItems.size());

        NewsItem readSection = new NewsItem();
        readSection.type = 1;
        readSection.title = Constants.READ;
        newsItems.add(readSection);

        //add read items
        for (Integer i : readIndexs) {
            NewsModel model = userInfo.newsModels.get(i);
            if (searchType == SEARCH_TAG && (!model.mTag.equals(key)))
                continue;
            if (searchType == SEARCH_SOURCE && (!model.mFound.equals(key)))
                continue;
            NewsItem newsItem = new NewsItem();
            newsItem.type = 0;
            newsItem.tag = model.mTag;
            newsItem.title = model.mTitle;
            newsItem.description = model.mDescription;
            newsItem.source = model.mFound;
            newsItem.guid = model.mGuid;
            newsItem.link = model.mLink;
            newsItem.imgLink = model.mLocalImageLink;
            newsItem.shares = model.mShares;
            newsItem.isHot = model.mYourTopStories.equals("true") ? true : false;
            newsItem.timeText = model.mTimeArray.getTimeString();
            newsItem.originalIndex = i;
            newsItem.read = model.mRead;
            newsItem.timeDiff = model.mTimeArray.getTimeDiff();
            newsItems.add(newsItem);
        }

        newsRecycleAdapter.setItems(newsItems, sections);
        newsRecycleAdapter.notifyItemRangeInserted(0, newsItems.size());
        myMoPubAdapter.refreshAds(getString(R.string.unit_id));
    }

    private boolean checkIfShowAddSourceTab()
    {
        int settingCnt = userInfo.getSourceSettingCount(this);
        if( settingCnt< Constants.SOURCE_MIN)
             return true;
        return false;
    }
    public void syncData() {
        if (userInfo.newsModels == null)
            return;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                application.GetDB().removeAllNews();
                application.GetDB().insertNewsList(userInfo.newsModels);
//                DownloadManager.getInstance().DataSync(HomeActivity.this, userInfo.newsModels);
                //download images.
            }
        }, 0);


    }

    public void analysisData() {
        tagMap.clear();
        sourceMap.clear();
        if (userInfo.newsModels == null)
            return;
        for (NewsModel model : userInfo.newsModels) {
            if (!model.mTag.isEmpty()) {
                if (tagMap.containsKey(model.mTag)) {
                    if (model.mRead.equals("0"))
                        tagMap.put(model.mTag, tagMap.get(model.mTag) + 1);
                } else {
                    if (model.mRead.equals("0"))
                        tagMap.put(model.mTag, 1);
                    else
                        tagMap.put(model.mTag, 0);
                }
            }

            if (!model.mFound.isEmpty()) {
                if (sourceMap.containsKey(model.mFound)) {
                    if (model.mRead.equals("0"))
                        sourceMap.put(model.mFound, sourceMap.get(model.mFound) + 1);
                } else {
                    if (model.mRead.equals("0"))
                        sourceMap.put(model.mFound, 1);
                    else
                        sourceMap.put(model.mFound, 0);
                }
            }
        }
        updateFilterList(SEARCH_ALL);


    }

    private void updateFilterList(int searchType) {
        filterItems.clear();
        if(searchType != SEARCH_SOURCE) {
            //add tag section
            FilterItem tag = new FilterItem();
            tag.type = 1;
            tag.title = getString(R.string.tag);
            filterItems.add(tag);
            List<String> keys = new ArrayList<String>(tagMap.keySet()) ;
            Collections.sort(keys);

            for (String key : keys) {
                FilterItem item = new FilterItem();
                item.title = key;
                item.unreadCount = tagMap.get(key);
                item.type = 0;
                item.filterType = SEARCH_TAG;
                filterItems.add(item);
            }
        }

        //add source section
        if(searchType != SEARCH_TAG) {
            FilterItem source = new FilterItem();
            source.type = 1;
            source.title = getString(R.string.source);
            filterItems.add(source);
            List<String> sourceKeys = new ArrayList<String>(sourceMap.keySet()) ;
            Collections.sort(sourceKeys);
            for (String key : sourceKeys) {
                FilterItem item = new FilterItem();
                item.title = key;
                item.unreadCount = sourceMap.get(key);
                item.type = 0;
                item.filterType = SEARCH_SOURCE;
                filterItems.add(item);
            }
        }


        filterAdapter.setItems(filterItems);
    }


    void sortList(final int type)
    {

        ArrayList<NewsItem> unreadItem = new ArrayList<NewsItem>();
        ArrayList<NewsItem> readItem = new ArrayList<NewsItem>();
        for(NewsItem item : newsItems)
        {
            if(item.read.equals("0"))
                unreadItem.add(item);
            else
            if(item.read.equals("1"))
                readItem.add(item);
        }
        Comparator<NewsItem> comparator = new Comparator<NewsItem>() {
            @Override
            public int compare(NewsItem lhs, NewsItem rhs) {
                if(type == SORT_SHARES)
                {
                        int l = Integer.parseInt(lhs.shares);
                        int r = Integer.parseInt(rhs.shares);
                        return l > r ? -1 : (l == r ? 0 : 1);
                }

                if(type == SORT_TIME)
                {
                    long l = lhs.timeDiff;
                    long r= rhs.timeDiff;
                    return l < r ? -1 : (l == r ? 0 : 1);
                }
                return 0;
            }
        };
        Collections.sort(unreadItem, comparator);
        Collections.sort(readItem,comparator);
        int size = newsItems.size();
        newsItems.clear();
        newsRecycleAdapter.notifyItemRangeRemoved(0, size);

        NewsItem unreadSection = new NewsItem();
        unreadSection.type = 1;
        unreadSection.title = Constants.UNREAD;
        newsItems.add(unreadSection);

        newsItems.addAll(unreadItem);

        NewsItem readSection = new NewsItem();
        readSection.type = 1;
        readSection.title = Constants.READ;
        newsItems.add(readSection);
        newsItems.addAll(readItem);
        newsRecycleAdapter.setItems(newsItems,sections);
        newsRecycleAdapter.notifyItemRangeInserted(0, newsItems.size());
    }
    @Override
    public void OnError(String ErrorMsg, ENUM.SERVICE_TYPE type) {
        if (userInfo.newsModels == null || userInfo.newsModels.size() == 0) {
            userInfo.newsModels = application.GetDB().getNews();
            updateList(SEARCH_ALL, "");
        }
    }

    @Override
    public void OnFaild(ENUM.SERVICE_TYPE type) {
        //showToast(this, "Connection Faild");
        if (userInfo.newsModels == null || userInfo.newsModels.size() == 0) {
            userInfo.newsModels = application.GetDB().getNews();
            updateList(SEARCH_ALL, "");
        }
    }

    @Override
    public void OnFinished(ENUM.SERVICE_TYPE type) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        pullToRefreshRecyclerView.onRefreshComplete();
    }

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Share WeUpdateYou");
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));

        startActivity(Intent.createChooser(share, "Share"));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {

            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

        boolean finishActivity = true;
        if (currentSearchType != SEARCH_ALL) {
            updateList(SEARCH_ALL, "");
            finishActivity = false;
        }
        if(drawer != null && drawMenu != null && drawer.isDrawerOpen(drawMenu))
        {
            drawer.closeDrawer(drawMenu);
            finishActivity = false;
        }
        if(rfaLayout.isExpanded())
        {
            rfaLayout.collapseContent();
            finishActivity = false;
        }

        if(finishActivity)
        finish();
        return;
    }

    private void moveItemInModels(int position, String section) {
        if (userInfo.newsModels == null)
            return;
        userInfo.newsModels.get(position).mRead = (section.equals(Constants.READ) ? "1" : "0");
    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem rfacLabelItem) {

        onFABClick(position, rfacLabelItem);
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem rfacLabelItem) {

        onFABClick(position, rfacLabelItem);
    }

    private void onFABClick(int position, RFACLabelItem rfacLabelItem)
    {
        switch (position)
        {
            case 0:
                updateFilterList(SEARCH_TAG);
                drawer.openDrawer(drawMenu);
                break;
            case 1:

                updateFilterList(SEARCH_SOURCE);
                drawer.openDrawer(drawMenu);
                break;
            case 2:
                sortList(SORT_TIME);
                currentSortType = SORT_TIME;
                refreshFAB();
                break;

            case 3:
                sortList(SORT_SHARES);
                currentSortType = SORT_SHARES;
                refreshFAB();
                break;
            case 4:

                    updateList(SEARCH_ALL, "");
                    currentFilterType = 0;
                    refreshFAB();

                break;
        }
        rfabHelper.toggleContent();
    }
    class SearchItem {
        public int searchType;
        public String key;

        public SearchItem(int type, String key) {
            this.searchType = type;
            this.key = key;
        }
    }

    private void setupAlarm()
    {
        AlarmManagerBroadcastReceiver.setupAlarm(getBaseContext());
      //  startService(new Intent(getApplicationContext(), AlarmService.class));
    }


    private void notificationClear(){

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager Nmang = (NotificationManager) getApplicationContext()
                .getSystemService(ns);
       Nmang.cancel(Constants.NotificationId);
       // Nmang.cancelAll();
    }
}
